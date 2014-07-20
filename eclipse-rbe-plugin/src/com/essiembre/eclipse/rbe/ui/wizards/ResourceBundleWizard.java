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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.bundle.PropertiesGenerator;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one or several files with the extension
 * "properties". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */
public class ResourceBundleWizard extends Wizard implements INewWizard {
    private ResourceBundleNewWizardPage page;
    private ISelection selection;

    /**
     * Constructor for ResourceBundleWizard.
     */
    public ResourceBundleWizard() {
        super();
        setNeedsProgressMonitor(true);
    }
    
    /**
     * Adding the page to the wizard.
     */

    public void addPages() {
        page = new ResourceBundleNewWizardPage(selection);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        final String containerName = page.getContainerName();
        final String baseName = page.getFileName();
        final String[] locales = page.getLocaleStrings();
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) 
                    throws InvocationTargetException {
                try {
                    monitor.worked(1);
                    monitor.setTaskName(RBEPlugin.getString(
                            "editor.wiz.creating"));
                    IFile file = null;
                    for (int i = 0; i <  locales.length; i++) {
                        String fileName = baseName;
                        if (locales[i].equals(
                                ResourceBundleNewWizardPage.DEFAULT_LOCALE)) {
                            fileName += ".properties";
                        } else {
                            fileName += "_" + locales[i]
                                     + ".properties";
                        }
                        file = createFile(containerName, fileName, monitor);
                    }
                    final IFile lastFile = file;
                    getShell().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            IWorkbenchPage wbPage = PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getActivePage();
                            try {
                                IDE.openEditor(wbPage, lastFile, true);
                            } catch (PartInitException e) {
                            }
                        }
                    });
                    monitor.worked(1);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), 
                    "Error", realException.getMessage());
            return false;
        }
        return true;
    }
    
    /*
     * The worker method. It will find the container, create the
     * file if missing or just replace its contents, and open
     * the editor on the newly created file.
     */
    /*default*/ IFile createFile(
            String containerName,
            String fileName,
            IProgressMonitor monitor)
            throws CoreException {
        
        monitor.beginTask(RBEPlugin.getString(
                "editor.wiz.creating") + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException("Container \"" + containerName 
                    + "\" does not exist.");
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        try {
            InputStream stream = openContentStream();
            if (file.exists()) {
                file.setContents(stream, true, true, monitor);
            } else {
                file.create(stream, true, monitor);
            }
            stream.close();
        } catch (IOException e) {
        }
        return file;
    }
    
    /*
     * We will initialize file contents with a sample text.
     */
    private InputStream openContentStream() {
        String contents = "";
        if (RBEPreferences.getShowGenerator()) {
            contents = PropertiesGenerator.GENERATED_BY;
        }
        return new ByteArrayInputStream(contents.getBytes());
    }

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, 
                "com.essiembre.eclipse.i18n.resourcebundle", 
                IStatus.OK, message, null);
        throw new CoreException(status);
    }

    /**
     * We will accept the selection in the workbench to see if
     * we can initialize from it.
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(
            IWorkbench workbench, IStructuredSelection structSelection) {
        this.selection = structSelection;
    }
}