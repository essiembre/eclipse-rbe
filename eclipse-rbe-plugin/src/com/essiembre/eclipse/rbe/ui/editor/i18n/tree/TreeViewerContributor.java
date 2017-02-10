/*
 * Copyright (C) 2003-2014  Pascal Essiembre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.essiembre.eclipse.rbe.ui.editor.i18n.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.updater.FlatKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.IncompletionUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.KeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;

/**
 * Helper class which is used to provide menu functions to the used TreeViewer
 * instances (outline and the treeview within the editor).
 */
public class TreeViewerContributor {

	public static final int KT_FLAT = 0; // 0th bit unset
	public static final int KT_HIERARCHICAL = 1; // 0th bit set
	public static final int KT_INCOMPLETE = 2; // 1th bit set

	public static final int MENU_NEW = 0;
	public static final int MENU_RENAME = 1;
	public static final int MENU_DELETE = 2;
	public static final int MENU_COPY = 3;
	public static final int MENU_MOVE = 4;
	public static final int MENU_COMMENT = 5;
	public static final int MENU_UNCOMMENT = 6;
	public static final int MENU_EXPAND = 7;
	public static final int MENU_COLLAPSE = 8;
	private static final int MENU_COUNT = 9;

	/** the tree which is controlled through this manager. */
	private KeyTree tree;

	/** the component which displays the tree. */
	private TreeViewer treeviewer;

	private Separator separator;

	/** actions for the context menu. */
	private Action[] actions;

	/** the updater which is used for structural information. */
	private KeyTreeUpdater structuralupdater;

	/** holds the information about the current state. */
	private int mode;

	/** some cursors to indicate progress */
	private Cursor waitcursor;
	private Cursor defaultcursor;

	/**
	 * Initializes this contributor using the supplied model structure and the
	 * viewer which is used to access the model.
	 * 
	 * @param keytree
	 *            Out tree model.
	 * @param viewer
	 *            The viewer used to display the supplied model.
	 */
	public TreeViewerContributor(KeyTree keytree, TreeViewer viewer) {
		tree = keytree;
		treeviewer = viewer;
		actions = new Action[MENU_COUNT];
		mode = KT_HIERARCHICAL;
		waitcursor = UIUtils.createCursor(SWT.CURSOR_WAIT);
		defaultcursor = UIUtils.createCursor(SWT.CURSOR_ARROW);
		if (RBEPreferences.getKeyTreeHierarchical()) {
			structuralupdater = new GroupedKeyTreeUpdater(RBEPreferences.getKeyGroupSeparator());
		} else {
			structuralupdater = new FlatKeyTreeUpdater();
		}
	}

	private void buildActions() {
		actions[MENU_NEW] = new Action() {
			@Override
			public void run() {
				newKey();
			}
		};
		actions[MENU_NEW].setText(RBEPlugin.getString("key.new"));

		actions[MENU_RENAME] = new Action() {
			@Override
			public void run() {
				renameKeyOrGroup();
			}
		};
		actions[MENU_RENAME].setText(RBEPlugin.getString("key.rename"));

		actions[MENU_DELETE] = new Action() {
			@Override
			public void run() {
				deleteKeyOrGroup();
			}
		};
		actions[MENU_DELETE].setText(RBEPlugin.getString("key.delete"));

		actions[MENU_COPY] = new Action() {
			@Override
			public void run() {
				copyKeyOrGroup();
			}
		};
		actions[MENU_COPY].setText(RBEPlugin.getString("key.duplicate"));

		actions[MENU_MOVE] = new Action() {
			@Override
			public void run() {
				moveKeyOrGroup();
			}
		};
		actions[MENU_MOVE].setText(RBEPlugin.getString("key.move"));

		actions[MENU_COMMENT] = new Action() {
			@Override
			public void run() {
				commentKey();
			}
		};
		actions[MENU_COMMENT].setText(RBEPlugin.getString("key.comment"));

		actions[MENU_UNCOMMENT] = new Action() {
			@Override
			public void run() {
				uncommentKey();
			}
		};
		actions[MENU_UNCOMMENT].setText(RBEPlugin.getString("key.uncomment"));

		separator = new Separator();

		actions[MENU_EXPAND] = new Action() {
			@Override
			public void run() {
				treeviewer.expandAll();
			}
		};
		actions[MENU_EXPAND].setText(RBEPlugin.getString("key.expandAll"));

		actions[MENU_COLLAPSE] = new Action() {
			@Override
			public void run() {
				treeviewer.collapseAll();
			}
		};
		actions[MENU_COLLAPSE].setText(RBEPlugin.getString("key.collapseAll"));
	}

	private void fillMenu(IMenuManager manager) {
		KeyTreeItem selectedItem = getSelection();
		manager.add(actions[MENU_NEW]);
		manager.add(actions[MENU_RENAME]);
		actions[MENU_RENAME].setEnabled(selectedItem != null);
		manager.add(actions[MENU_DELETE]);
		actions[MENU_DELETE].setEnabled(selectedItem != null);
		manager.add(actions[MENU_COPY]);
		actions[MENU_COPY].setEnabled(selectedItem != null);
		manager.add(actions[MENU_COMMENT]);
		actions[MENU_COMMENT].setEnabled(selectedItem != null);
		manager.add(actions[MENU_UNCOMMENT]);
		actions[MENU_UNCOMMENT].setEnabled(selectedItem != null);
		manager.add(separator);
		manager.add(actions[MENU_EXPAND]);
		manager.add(actions[MENU_COLLAPSE]);
		manager.add(actions[MENU_MOVE]);
		actions[MENU_MOVE].setEnabled(selectedItem != null);
	}

	/**
	 * Creates the menu contribution for the supplied parental component.
	 * 
	 * @param parent
	 *            The component which is receiving the menu.
	 */
	public void createControl(Composite parent) {
		buildActions();
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillMenu(manager);
			}
		});

		treeviewer.getTree().setMenu(menuManager.createContextMenu(parent));

	}

	/**
	 * Gets the selected key tree item.
	 * 
	 * @return key tree item
	 */
	public KeyTreeItem getSelection() {
		IStructuredSelection selection = (IStructuredSelection) treeviewer.getSelection();
		return (KeyTreeItem) selection.getFirstElement();
	}

	/**
	 * Creates a new key in case it isn't existing yet.
	 */
	protected void newKey() {
		KeyTreeItem selectedItem = getSelection();
		String key = selectedItem != null ? selectedItem.getId() : "";
		String msgHead = RBEPlugin.getString("dialog.new.head");
		String msgBody = RBEPlugin.getString("dialog.new.body", key);
		InputDialog dialog = new InputDialog(getShell(), msgHead, msgBody, key, null);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK) {
			String newKey = dialog.getValue();
			BundleGroup bundleGroup = tree.getBundleGroup();
			if (!bundleGroup.containsKey(newKey)) {
				bundleGroup.addKey(newKey);
			}
		}
	}

	/**
	 * Moves a key or group of key to another properties file.
	 */
	private class ChooseResourceManagerDialog extends Dialog {
				
		public ChooseResourceManagerDialog(Shell parentShell) {
			super(parentShell);
		}
			
		@Override
		protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		    if (id == IDialogConstants.OK_ID) {
		    	//we do not wont OK button
		    	return null;		    	
		    }
		    return super.createButton(parent, id, label, defaultButton);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			for (final ResourceManager manager : ResourceManager.getAvailaibleManagers()) {
				Button button = new Button(container, SWT.PUSH);
				button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				button.setText(manager.getFileName());
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						BundleGroup bundleGroup = tree.getBundleGroup();
						Collection<KeyTreeItem> items = new ArrayList<>();
						KeyTreeItem selectedItem = getSelection();
						if (selectedItem != null) {
							
							items.add(selectedItem);
							items.addAll(selectedItem.getNestedChildren());
							for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
								KeyTreeItem item = (KeyTreeItem) iter.next();
								//now iterate over localization in target and add BundleEntry for localization if it exists in source 
								boolean added = false;
								for (Locale locale: manager.getBundleGroup().getLocales()) {
									BundleEntry entry = bundleGroup.getBundleEntry(locale, item.getId());
									if(entry != null) {
										if (!added) {
											//add new key to target only if we will need it..
											manager.getBundleGroup().addKey(item.getId());						
											added = true;
										}
										manager.getBundleGroup().addBundleEntry(locale, entry);									
									}
								}
							}
							//now remove  all keys from source
							for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
								KeyTreeItem item = (KeyTreeItem) iter.next();
								bundleGroup.removeKey(item.getId());
							}
						}
						//and close dialog
						ChooseResourceManagerDialog.this.close();
					}
				});
			}
			if (ResourceManager.getAvailaibleManagers().isEmpty()) {
				Label label = new Label(container, SWT.NONE);
				label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				label.setText(RBEPlugin.getString("dialog.move.noeditors"));
			}
			return container;
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Select target file");
		}

		@Override
		protected Point getInitialSize() {
			return new Point(650, 300);
		}

		@Override
		protected boolean isResizable() {
			return true;
		}
		
	}

	protected void moveKeyOrGroup() {
		ChooseResourceManagerDialog dlg = new ChooseResourceManagerDialog(getShell());
		dlg.open();
	}

	/**
	 * Renames a key or group of key.
	 */
	protected void renameKeyOrGroup() {
		KeyTreeItem selectedItem = getSelection();
		String key = selectedItem.getId();
		String msgHead = null;
		String msgBody = null;
		if (selectedItem.getChildren().size() == 0) {
			msgHead = RBEPlugin.getString("dialog.rename.head.single");
			msgBody = RBEPlugin.getString("dialog.rename.body.single", key);
		} else {
			msgHead = RBEPlugin.getString("dialog.rename.head.multiple");
			msgBody = RBEPlugin.getString("dialog.rename.body.multiple", selectedItem.getName());
		}
		// Rename single item
		InputDialog dialog = new InputDialog(getShell(), msgHead, msgBody, key, null);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK) {
			String newKey = dialog.getValue();
			BundleGroup bundleGroup = tree.getBundleGroup();
			Collection<KeyTreeItem> items = new ArrayList<>();
			items.add(selectedItem);
			items.addAll(selectedItem.getNestedChildren());
			for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
				KeyTreeItem item = (KeyTreeItem) iter.next();
				String oldItemKey = item.getId();
				if (oldItemKey.startsWith(key)) {
					String newItemKey = newKey + oldItemKey.substring(key.length());
					bundleGroup.renameKey(oldItemKey, newItemKey);
				}
			}
		}
	}

	/**
	 * Uncomments a key or group of key.
	 */
	protected void uncommentKey() {
		KeyTreeItem selectedItem = getSelection();
		BundleGroup bundleGroup = tree.getBundleGroup();
		Collection<KeyTreeItem> items = new ArrayList<>();
		items.add(selectedItem);
		items.addAll(selectedItem.getNestedChildren());
		for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
			KeyTreeItem item = (KeyTreeItem) iter.next();
			bundleGroup.uncommentKey(item.getId());
		}
	}

	/**
	 * Deletes a key or group of key.
	 */
	protected void deleteKeyOrGroup() {
		KeyTreeItem selectedItem = getSelection();
		String key = selectedItem.getId();
		String msgHead = null;
		String msgBody = null;
		if (selectedItem.getChildren().size() == 0) {
			msgHead = RBEPlugin.getString("dialog.delete.head.single");
			msgBody = RBEPlugin.getString("dialog.delete.body.single", key);
		} else {
			msgHead = RBEPlugin.getString("dialog.delete.head.multiple");
			msgBody = RBEPlugin.getString("dialog.delete.body.multiple", selectedItem.getName());
		}
		MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
		msgBox.setMessage(msgBody);
		msgBox.setText(msgHead);
		if (msgBox.open() == SWT.OK) {
			BundleGroup bundleGroup = tree.getBundleGroup();
			Collection<KeyTreeItem> items = new ArrayList<>();
			items.add(selectedItem);
			items.addAll(selectedItem.getNestedChildren());
			for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
				KeyTreeItem item = iter.next();
				bundleGroup.removeKey(item.getId());
			}
		}
	}

	/**
	 * Comments a key or group of key.
	 */
	protected void commentKey() {
		KeyTreeItem selectedItem = getSelection();
		BundleGroup bundleGroup = tree.getBundleGroup();
		Collection<KeyTreeItem> items = new ArrayList<>();
		items.add(selectedItem);
		items.addAll(selectedItem.getNestedChildren());
		for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
			KeyTreeItem item = (KeyTreeItem) iter.next();
			bundleGroup.commentKey(item.getId());
		}

	}

	/**
	 * Copies a key or group of key.
	 */
	protected void copyKeyOrGroup() {
		KeyTreeItem selectedItem = getSelection();
		String key = selectedItem.getId();
		String msgHead = null;
		String msgBody = null;
		if (selectedItem.getChildren().size() == 0) {
			msgHead = RBEPlugin.getString("dialog.duplicate.head.single");
			msgBody = RBEPlugin.getString("dialog.duplicate.body.single", key);
		} else {
			msgHead = RBEPlugin.getString("dialog.duplicate.head.multiple");
			msgBody = RBEPlugin.getString("dialog.duplicate.body.multiple", selectedItem.getName());
		}
		// Rename single item
		InputDialog dialog = new InputDialog(getShell(), msgHead, msgBody, key, null);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK) {
			String newKey = dialog.getValue();
			BundleGroup bundleGroup = tree.getBundleGroup();
			Collection<KeyTreeItem> items = new ArrayList<>();
			items.add(selectedItem);
			items.addAll(selectedItem.getNestedChildren());
			for (Iterator<KeyTreeItem> iter = items.iterator(); iter.hasNext();) {
				KeyTreeItem item = (KeyTreeItem) iter.next();
				String origItemKey = item.getId();
				if (origItemKey.startsWith(key)) {
					String newItemKey = newKey + origItemKey.substring(key.length());
					bundleGroup.copyKey(origItemKey, newItemKey);
				}
			}
		}
	}

	/**
	 * Returns the currently used Shell instance.
	 * 
	 * @return The currently used Shell instance.
	 */
	private Shell getShell() {
		return (RBEPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
	}

	/**
	 * Modifies the current filter according to a selected activity.
	 * 
	 * @param action
	 *            One of the KT_??? constants declared above.
	 * @param activate
	 *            true <=> Enable this activity.
	 */
	public void update(int action, boolean activate) {
		treeviewer.getTree().setCursor(waitcursor);
		if (action == KT_INCOMPLETE) {
			if (activate) {
				// we're setting a filter which uses the structural updater
				tree.setUpdater(new IncompletionUpdater(tree.getBundleGroup(), structuralupdater));
				mode = mode | KT_INCOMPLETE;
			} else {
				// disabled, so we can reuse the structural updater
				tree.setUpdater(structuralupdater);
				mode = mode & (~KT_INCOMPLETE);
			}
			if (structuralupdater instanceof GroupedKeyTreeUpdater) {
				if (RBEPreferences.getKeyTreeExpanded()) {
					treeviewer.expandAll();
				}
			}
		} else if (action == KT_FLAT) {
			structuralupdater = new FlatKeyTreeUpdater();
			if ((mode & KT_INCOMPLETE) != 0) {
				// we need to activate the filter
				tree.setUpdater(new IncompletionUpdater(tree.getBundleGroup(), structuralupdater));
			} else {
				tree.setUpdater(structuralupdater);
			}
			mode = mode & (~KT_HIERARCHICAL);
		} else if (action == KT_HIERARCHICAL) {
			structuralupdater = new GroupedKeyTreeUpdater(RBEPreferences.getKeyGroupSeparator());
			if ((mode & KT_INCOMPLETE) != 0) {
				// we need to activate the filter
				tree.setUpdater(new IncompletionUpdater(tree.getBundleGroup(), structuralupdater));
			} else {
				tree.setUpdater(structuralupdater);
			}
			if (RBEPreferences.getKeyTreeExpanded()) {
				treeviewer.expandAll();
			}
			mode = mode | KT_HIERARCHICAL;
		}
		treeviewer.getTree().setCursor(defaultcursor);
	}

	/**
	 * Returns the currently used mode.
	 * 
	 * @return The currently used mode.
	 */
	public int getMode() {
		return (mode);
	}
}
