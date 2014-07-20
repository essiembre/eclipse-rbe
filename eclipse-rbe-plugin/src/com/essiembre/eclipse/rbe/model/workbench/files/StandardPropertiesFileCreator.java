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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Creates a standard properties file.
 * @author Pascal Essiembre
 */
public class StandardPropertiesFileCreator extends PropertiesFileCreator {

    private String dir;
    private String baseFileName;
    private String extension;
    
    /**
     * Constructor.
     * @param dir directory in wich to create the file
     * @param baseFileName base name of file to create
     * @param extension file extension
     */
    public StandardPropertiesFileCreator(
            String dir, String baseFileName, String extension) {
        super();
        this.dir = dir;
        this.baseFileName = baseFileName;
        this.extension = extension;
    }

    @Override
    protected IPath buildFilePath(Locale locale) {
        
        IPath path = new Path(dir);
        path = path.append(baseFileName);
        if (locale != null) {
            path = new Path(
                    path.toString() + "_" + locale.toString()); 
        }
        return path.addFileExtension(extension);
    }


}
