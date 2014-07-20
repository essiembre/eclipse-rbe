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
package com.essiembre.eclipse.rbe.model.workbench.files;

import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Creates a properties file under an "NL" structure.
 * @author Pascal Essiembre
 * @author Alexander Bieber
 */
public class NLPropertiesFileCreator extends PropertiesFileCreator {

    private String nlDir;
    private String fileName;
    
    /**
     * Constructor.
     * @param nlDir NL directory name
     * @param fileName file name
     */
    public NLPropertiesFileCreator(String nlDir, String fileName) {
        super();
        this.nlDir = nlDir;
        this.fileName = fileName;
    }

    /**
     * @return The currently set nlDir.
     */
    protected String getNlDir() {
        return nlDir;
    }
    /**
     * Set the nlDir.
     * @param nlDir The nlDir to set.
     */
    protected void setNlDir(String nlDir) {
        this.nlDir = nlDir;
    }
    
    /**
     * @throws CoreException
     * @see com.essiembre.eclipse.rbe.model.workbench.files
     *      .PropertiesFileCreator#buildFilePath(java.util.Locale)
     */
    protected IPath buildFilePath(Locale locale) throws CoreException {
        String folderPath = "";
        IWorkspaceRoot root = 
                ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(getNlDir());
        IContainer container = (IContainer) resource;

        if (locale != null) {
            if (locale.getLanguage().length() > 0) {
                folderPath += locale.getLanguage() + "/"; 
                IFolder folder = container.getFolder(new Path(folderPath));
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
            }
            if (locale.getCountry().length() > 0) {
                folderPath += locale.getCountry() + "/"; 
                IFolder folder = container.getFolder(new Path(folderPath));
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
            }
            if (locale.getVariant().length() > 0) {
                folderPath += locale.getVariant() + "/"; 
                IFolder folder = container.getFolder(new Path(folderPath));
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
            }
            folderPath = getNlDir() + "/" + folderPath;
        } else {
            folderPath = getNlDir().substring(
                    0, getNlDir().length() - "/nl".length()) + "/" + folderPath;
        }
        return new Path(folderPath + fileName);
    }
}
