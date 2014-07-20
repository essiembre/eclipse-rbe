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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.essiembre.eclipse.rbe.model.bundle.PropertiesGenerator;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * Creates a properties file.
 * @author Pascal Essiembre
 */
public abstract class PropertiesFileCreator {

    /**
     * Creates a propertiles file.
     * @param locale locale representing properties file
     * @return the properties file
     * @throws CoreException problem creating file
     * @throws IOException problem creating file
     */
    public IFile createPropertiesFile(Locale locale)
            throws CoreException, IOException {
        IPath filePath = buildFilePath(locale);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile file = root.getFile(filePath);
        if (file.exists()) {
            //TODO internationalize.
            throw new IOException("File already exists: " + file.getName());
        }
        String contents = "";
        if (RBEPreferences.getShowGenerator()) {
            contents = PropertiesGenerator.GENERATED_BY;
        }
        InputStream stream = 
            new ByteArrayInputStream(contents.getBytes());
        file.create(stream, true, null);
        stream.close();
        return file;
    }
    
    protected abstract IPath buildFilePath(Locale locale) throws CoreException;
}
