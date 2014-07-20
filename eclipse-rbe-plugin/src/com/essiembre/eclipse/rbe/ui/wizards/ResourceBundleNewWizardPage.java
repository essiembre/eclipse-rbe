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
package com.essiembre.eclipse.rbe.ui.wizards;

import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.widgets.LocaleSelector;

/**
 * The "New" wizard page allows setting the container for
 * the new bundle group as well as the bundle group common base name. The page
 * will only accept file name without the extension.
 * @author Pascal Essiembre
 */
public class ResourceBundleNewWizardPage extends WizardPage {

    static final String DEFAULT_LOCALE = "["
            + RBEPlugin.getString("editor.default")
            + "]";
    
    private Text containerText;
    private Text fileText;
    private ISelection selection;
    
    /*default*/ Button addButton;
    /*default*/ Button removeButton;
    
    /*default*/ List bundleLocalesList;
    
    private LocaleSelector localeSelector;

    /**
     * Constructor for SampleNewWizardPage.
     * @param selection workbench selection
     */
    public ResourceBundleNewWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle(RBEPlugin.getString("editor.wiz.title"));
        setDescription(RBEPlugin.getString("editor.wiz.desc"));
        this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 20;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        // Bundle name + location        
        createTopComposite(container);

        // Locales        
        createBottomComposite(container);
        
                
        initialize();
        dialogChanged();
        setControl(container);
    }


    /**
     * Creates the bottom part of this wizard, which is the locales to add.
     * @param parent parent container
     */
    private void createBottomComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        // Available locales
        createBottomAvailableLocalesComposite(container);

        // Buttons
        createBottomButtonsComposite(container);
    
        // Selected locales
        createBottomSelectedLocalesComposite(container);
    }

    /**
     * Creates the bottom part of this wizard where selected locales 
     * are stored.
     * @param parent parent container
     */
    private void createBottomSelectedLocalesComposite(Composite parent) {

        // Selected locales Group
        Group selectedGroup = new Group(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout = new GridLayout();
        layout.numColumns = 1;
        selectedGroup.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_BOTH);
        selectedGroup.setLayoutData(gd);
        selectedGroup.setText(RBEPlugin.getString(
                "editor.wiz.selected"));
        bundleLocalesList = 
                new List(selectedGroup, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
        gd = new GridData(GridData.FILL_BOTH);
        bundleLocalesList.setLayoutData(gd);
        bundleLocalesList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                removeButton.setEnabled(
                        bundleLocalesList.getSelectionIndices().length != 0);
                setAddButtonState();
            }
        });
    }
    
    /**
     * Creates the bottom part of this wizard where buttons to add/remove
     * locales are located.
     * @param parent parent container
     */
    private void createBottomButtonsComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);

        addButton = new Button(container, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        addButton.setLayoutData(gd);
        addButton.setText(RBEPlugin.getString(
                "editor.wiz.add"));
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                bundleLocalesList.add(getSelectedLocaleAsString());
                setAddButtonState();
            }
        });

        removeButton = new Button(container, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        removeButton.setLayoutData(gd);
        removeButton.setText(RBEPlugin.getString(
                "editor.wiz.remove"));
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                bundleLocalesList.remove(
                        bundleLocalesList.getSelectionIndices());
                removeButton.setEnabled(false);
                setAddButtonState();
            }
        });
    }
        
    /**
     * Creates the bottom part of this wizard where locales can be chosen
     * or created
     * @param parent parent container
     */
    private void createBottomAvailableLocalesComposite(Composite parent) {

        localeSelector = 
                new LocaleSelector(parent);
        localeSelector.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                setAddButtonState();
            }
        });
    }
    
    /**
     * Creates the top part of this wizard, which is the bundle name
     * and location.
     * @param parent parent container
     */
    private void createTopComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        // Folder
        Label label = new Label(container, SWT.NULL);
        label.setText(RBEPlugin.getString(
                "editor.wiz.folder"));

        containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        Button button = new Button(container, SWT.PUSH);
        button.setText(RBEPlugin.getString(
                "editor.wiz.browse"));
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        
        // Bundle name
        label = new Label(container, SWT.NULL);
        label.setText(RBEPlugin.getString(
                "editor.wiz.bundleName"));

        fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        label = new Label(container, SWT.NULL);
        label.setText("[locale].properties");
    }
    
    
    /**
     * Tests if the current workbench selection is a suitable
     * container to use.
     */
    private void initialize() {
        if (selection!=null && selection.isEmpty()==false
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection)selection;
            if (ssel.size()>1) return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer)obj;
                else
                    container = ((IResource)obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
        fileText.setText("ApplicationResources");
    }
    
    /**
     * Uses the standard container selection dialog to
     * choose the new value for the container field.
     */

    /*default*/ void handleBrowse() {
        ContainerSelectionDialog dialog =
            new ContainerSelectionDialog(
                getShell(),
                ResourcesPlugin.getWorkspace().getRoot(),
                false,
                RBEPlugin.getString(
                        "editor.wiz.selectFolder"));
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerText.setText(((Path)result[0]).toOSString());
            }
        }
    }
    
    /**
     * Ensures that both text fields are set.
     */
    /*default*/ void dialogChanged() {
        String container = getContainerName();
        String fileName = getFileName();

        if (container.length() == 0) {
            updateStatus(RBEPlugin.getString(
                    "editor.wiz.error.container"));
            return;
        }
        if (fileName.length() == 0) {
            updateStatus(RBEPlugin.getString(
                    "editor.wiz.error.bundleName"));
            return;
        }
        int dotLoc = fileName.lastIndexOf('.');
        if (dotLoc != -1) {
            updateStatus(RBEPlugin.getString(
                    "editor.wiz.error.extension"));
            return;
        }
        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    /**
     * Gets the container name.
     * @return container name
     */
    public String getContainerName() {
        return containerText.getText();
    }
    /** 
     * Gets the file name.
     * @return file name
     */
    public String getFileName() {
        return fileText.getText();
    }
    
    /**
     * Sets the "add" button state. 
     */
    /*default*/ void setAddButtonState() {
        addButton.setEnabled(bundleLocalesList.indexOf(
                getSelectedLocaleAsString()) == -1);
    }
    
    /**
     * Gets the user selected locales.
     * @return locales
     */
    /*default*/ String[] getLocaleStrings() {
        return bundleLocalesList.getItems();
    }
    
    /**
     * Gets a string representation of selected locale.
     * @return string representation of selected locale
     */
    /*default*/ String getSelectedLocaleAsString() {
        Locale selectedLocale = localeSelector.getSelectedLocale();
        if (selectedLocale != null) {
            return selectedLocale.toString();
        }
        return DEFAULT_LOCALE;
    }
}