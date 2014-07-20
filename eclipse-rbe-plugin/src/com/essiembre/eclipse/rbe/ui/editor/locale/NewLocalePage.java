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
package com.essiembre.eclipse.rbe.ui.editor.locale;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.widgets.LocaleSelector;

/**
 * Page for adding a new locale (new localized properties file).
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public class NewLocalePage extends Composite {

    private Font fontBoldBig = UIUtils.createFont(this, SWT.BOLD, 5);
    private Font fontBold = UIUtils.createFont(this, SWT.BOLD, 1);
    
    /**
     * Constructor.
     * @param parent parent component.
     * @param resourceManager resource manager 
     */
    public NewLocalePage(
            final Composite parent, 
            final ResourceManager resourceManager,
            final ResourceBundleEditor editor) {
        super(parent, SWT.NONE);
        
        setLayout(new GridLayout());

        Composite block = new Composite(this, SWT.NONE);
        block.setLayout(new GridLayout());
        
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        block.setLayoutData(gridData);
        
        // Title label
        Label label = new Label(block, SWT.NONE);
        label.setText(RBEPlugin.getString("editor.new.title"));
        label.setFont(fontBoldBig);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        label.setLayoutData(gridData);

        // Locale selector
        final LocaleSelector localeSelector = 
                new LocaleSelector(block);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        localeSelector.setLayoutData(gridData);
        
        // Create button
        Button createButton = new Button(block, SWT.NULL);
        createButton.setText(RBEPlugin.getString(
                "editor.new.create"));
        createButton.setFont(fontBold);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        createButton.setLayoutData(gridData);
        createButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                final Locale locale = localeSelector.getSelectedLocale();
                try {
                    // Create the new file
                    try {
                        //TODO add "newPropertiesFile" method to seGroup.
                        final IFile file = 
                                resourceManager.createPropertiesFile(locale);
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                editor.addResource(file, locale);
                            }
                        });
//                        
//                        final IWorkbenchPage page = PlatformUI.getWorkbench()
//                                .getActiveWorkbenchWindow().getActivePage();
//                        // Open new editor with new locale
//                        getShell().getDisplay().asyncExec(new Runnable() {
//                            public void run() {
//                                try {
//                                    IDE.openEditor(page, file, true);
//                                } catch (PartInitException e) {
//                                    UIUtils.showErrorDialog(getShell(), e,
//                                     "error.newfile.cannotCreate");
//                                }
//                            }
//                        });
//                        // Close active editor (prior adding locale)
//                        page.closeEditor(page.getActiveEditor(), true);
                    } catch (NullPointerException e) {
                        UIUtils.showErrorDialog(getShell(), e, 
                                "error.newfile.cannotCreate");
                        throw e;
                    }
                } catch (CoreException e) {
                    UIUtils.showErrorDialog(getShell(), e, 
                            "error.newfile.cannotCreate");
                } catch (IOException e) {
                    UIUtils.showErrorDialog(getShell(), e, 
                            "error.newfile.cannotCreate");
                }
            }
        });
        this.layout();
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        fontBold.dispose();
        fontBoldBig.dispose();
        super.dispose();
    }
}
