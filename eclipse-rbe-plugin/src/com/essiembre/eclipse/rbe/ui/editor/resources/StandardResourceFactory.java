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
package com.essiembre.eclipse.rbe.ui.editor.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.model.workbench.files.PropertiesFileCreator;
import com.essiembre.eclipse.rbe.model.workbench.files.StandardPropertiesFileCreator;

/**
 * Responsible for creating resources related to a standard
 * directory structure.
 * @author Pascal Essiembre
 * @author Alexander Bieber
 * @author wolfgang-ch
 */
public class StandardResourceFactory extends ResourceFactory {

    private PropertiesFileCreator fileCreator;

    @Override
    public boolean isResponsible(IFile file) throws CoreException {
        return true;
    }
    
    /**
     * Constructor.
     * @param site editor site
     * @param file file used to open all related files
     * @throws CoreException problem creating factory
     */
    public void init(IEditorSite site, IFile file) 
             throws CoreException {
        setSite(site);
        String bundleName = getBundleName(file);
//        String regex = ResourceFactory.getPropertiesFileRegEx(file);
        IResource[] resources = StandardResourceFactory.getResources(file);

        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
//            String resourceName = resource.getName();
            // Build local title
            Locale locale = parseBundleName(resource);            
            SourceEditor sourceEditor = 
                    createEditor(site, resource, locale);
            if (sourceEditor != null) {
                addSourceEditor(sourceEditor.getLocale(), sourceEditor);
            }
        }
        fileCreator = new StandardPropertiesFileCreator(
                file.getParent().getFullPath().toString(),
                bundleName,
                file.getFileExtension());
        setDisplayName(getDisplayName(file));
    }
    
    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.ResourceFactory
     *         #getPropertiesFileCreator()
     */
    public PropertiesFileCreator getPropertiesFileCreator() {
        return fileCreator;
    }

    protected static IFile[] getResources(IFile file)
        throws PartInitException {
        
        String regex = ResourceFactory.getPropertiesFileRegEx(file);
        IResource[] resources = null;
        try {
            resources = file.getParent().members();
        } catch (CoreException e) {
            throw new PartInitException(
                   "Can't initialize resource bundle editor.", e);
        }
        ResourceFilter resourceFilter = new ResourceFilter();
        Collection<IResource> validResources = new ArrayList<>();
        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
            String resourceName = resource.getName();
            
			if (resource instanceof IFile
				&& resourceName.matches(regex)
				&& resourceFilter.isResourceDisplayed(resourceName, regex)) {
				
                validResources.add(resource);
            }
        }
        return (IFile[]) validResources.toArray(new IFile[]{});
    }
}
