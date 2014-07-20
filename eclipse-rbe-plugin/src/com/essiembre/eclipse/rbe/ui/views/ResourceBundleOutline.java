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
package com.essiembre.eclipse.rbe.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.i18n.tree.KeyTreeContentProvider;
import com.essiembre.eclipse.rbe.ui.editor.i18n.tree.KeyTreeLabelProvider;
import com.essiembre.eclipse.rbe.ui.editor.i18n.tree.TreeViewerContributor;

/**
 * This outline provides a view for the property keys coming with with a
 * ResourceBundle
 */
public class ResourceBundleOutline extends ContentOutlinePage {

    private KeyTree tree;
    private KeyTreeContentProvider contentprovider;
    private ToggleAction filterincomplete;
    private ToggleAction flataction;
    private ToggleAction hierarchicalaction;
    private boolean hierarchical;
    private TreeViewerContributor contributor;

    /**
     * Initializes this outline while using the mediator which provides all
     * necessary informations.
     * 
     * @param mediator
     *            The mediator which comes with all necessary informations.
     */
    public ResourceBundleOutline(KeyTree keytree) {
        super();
        tree = keytree;
        contentprovider = new KeyTreeContentProvider();
        hierarchical = RBEPreferences.getKeyTreeHierarchical();
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        getTreeViewer().setContentProvider(contentprovider);
        getTreeViewer().setLabelProvider(new KeyTreeLabelProvider());
        getTreeViewer().setUseHashlookup(true);
        getTreeViewer().setInput(tree);
        if (RBEPreferences.getKeyTreeExpanded()) {
            ((Tree) getTreeViewer().getControl()).setRedraw(false);
            getTreeViewer().expandAll();
            ((Tree) getTreeViewer().getControl()).setRedraw(true);
        }
        contributor = new TreeViewerContributor(tree, getTreeViewer());
        contributor.createControl(parent);
        LocalBehaviour localbehaviour = new LocalBehaviour();
        getTreeViewer().addSelectionChangedListener(localbehaviour);
        getTreeViewer().getTree().addMouseListener(localbehaviour);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // contributor.dispose();
        super.dispose();
    }

    /**
     * Gets the selected key tree item.
     * 
     * @return key tree item
     */
    public KeyTreeItem getTreeSelection() {
        IStructuredSelection selection = (IStructuredSelection) getTreeViewer()
                .getSelection();
        return ((KeyTreeItem) selection.getFirstElement());
    }

    /**
     * Gets selected key.
     * 
     * @return selected key
     */
    private String getSelectedKey() {
        String key = null;
        KeyTreeItem item = getTreeSelection();
        if (item != null) {
            key = item.getId();
        }
        return (key);
    }

    /**
     * {@inheritDoc}
     */
    public void setActionBars(IActionBars actionbars) {
        super.setActionBars(actionbars);
        filterincomplete = new ToggleAction(UIUtils.IMAGE_INCOMPLETE_ENTRIES);
        flataction = new ToggleAction(UIUtils.IMAGE_LAYOUT_FLAT);
        hierarchicalaction = 
                new ToggleAction(UIUtils.IMAGE_LAYOUT_HIERARCHICAL);
        flataction.setToolTipText(RBEPlugin.getString("key.layout.flat"));
        hierarchicalaction.setToolTipText(RBEPlugin
                .getString("key.layout.tree"));
        filterincomplete.setToolTipText(RBEPlugin
                .getString("key.filter.incomplete"));
        flataction.setChecked(!hierarchical);
        hierarchicalaction.setChecked(hierarchical);
        actionbars.getToolBarManager().add(flataction);
        actionbars.getToolBarManager().add(hierarchicalaction);
        actionbars.getToolBarManager().add(filterincomplete);
    }

    /**
     * Invokes this functionality according to the toggled action.
     * 
     * @param action
     *            The action that has been toggled.
     */
    private void update(ToggleAction action) {
        int actioncode = 0;
        if (action == filterincomplete) {
            actioncode = TreeViewerContributor.KT_INCOMPLETE;
        } else if (action == flataction) {
            actioncode = TreeViewerContributor.KT_FLAT;
        } else if (action == hierarchicalaction) {
            actioncode = TreeViewerContributor.KT_HIERARCHICAL;
        }
        contributor.update(actioncode, action.isChecked());
        flataction.setChecked((contributor.getMode() 
                & TreeViewerContributor.KT_HIERARCHICAL) == 0);
        hierarchicalaction.setChecked((contributor.getMode() 
                & TreeViewerContributor.KT_HIERARCHICAL) != 0);
    }

    /**
     * Simple toggle action which delegates it's invocation to the method
     * {@link #update(ToggleAction)}.
     */
    private class ToggleAction extends Action {

        /**
         * Initializes this action using the supplied icon.
         * 
         * @param icon
         *            The icon which shall be displayed.
         */
        public ToggleAction(String icon) {
            super(null, IAction.AS_CHECK_BOX);
            setImageDescriptor(RBEPlugin.getImageDescriptor(icon));
        }

        @Override
        public void run() {
            update(this);
        }
    }

    /**
     * Implementation of custom behaviour.
     */
    private class LocalBehaviour extends MouseAdapter implements
            IDeltaListener, ISelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            String selected = getSelectedKey();
            if (selected != null) {
                tree.selectKey(selected);
            }
        }

        @Override
        public void add(DeltaEvent event) {
        }

        @Override
        public void remove(DeltaEvent event) {
        }

        @Override
        public void modify(DeltaEvent event) {
        }

        @Override
        public void select(DeltaEvent event) {
            KeyTreeItem item = (KeyTreeItem) event.receiver();
            if (item != null) {
                getTreeViewer().setSelection(new StructuredSelection(item));
            }
        }

        @Override
        public void mouseDoubleClick(MouseEvent event) {
            Object element = getSelection();
            if (getTreeViewer().isExpandable(element)) {
                if (getTreeViewer().getExpandedState(element)) {
                    getTreeViewer().collapseToLevel(element, 1);
                } else {
                    getTreeViewer().expandToLevel(element, 1);
                }
            }
        }

    }

}
