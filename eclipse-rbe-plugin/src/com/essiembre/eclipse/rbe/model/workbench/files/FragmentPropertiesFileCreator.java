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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.editor.resources.PDEUtils;

/**
 * This is a property file creator for files within a fragment project contributing
 * translations.
 *
 * @author Uwe Voigt
 */
public class FragmentPropertiesFileCreator extends PropertiesFileCreator {
    private final IProject fragment;
    private final String targetDir;
    private final String baseFileName;
    private final String extension;
    
    /**
     * Creates an instance.
     * 
     * @param fragment the fragment project
     * @param targetDir the target directory
     * @param baseFileName the base bundle name
     * @param extension the file extension
     */
    public FragmentPropertiesFileCreator(IProject fragment, String targetDir, String baseFileName, String extension) {
        super();
        this.fragment = fragment;
        this.targetDir = targetDir;
        this.baseFileName = baseFileName;
        this.extension = extension;
    }

    @Override
    protected IPath buildFilePath(final Locale locale) throws CoreException {
        /*
         * Check where to create the file
         */
        IProject project = null;
        if (!shouldFileBeCreatedInFragment(fragment)) {
            project = PDEUtils.getFragmentHost(fragment);
        }
        if (project == null) {
            project = fragment;
        }
        /*
         * create the resource parent paths if necessary 
         */
        IResource resource = project.findMember(targetDir);
        if (resource == null || !resource.exists()) {
            final IPath path = new Path(targetDir);            
            final List<IPath> paths = new ArrayList<>();
            IPath parent = path;
            do {
                paths.add(parent);
                parent = parent.uptoSegment(parent.segmentCount() - 1);
                resource = project.findMember(parent);
            } while (resource == null || !resource.exists());
            for (int i = paths.size() - 1; i >= 0; i--) {
                project.getFolder(((IPath) paths.get(i))).create(true, true, null);
            }
        }

        /*
         * build the resource path according to the requested language
         */
        IPath filePath = new Path(baseFileName);
        if (locale != null) {
            filePath = new Path(filePath.toString() + '_' + locale.toString());
            filePath = filePath.addFileExtension(extension);
        }
        return project.getFullPath().append(targetDir).append(filePath);
    }
    
    /**
     * Ask the user where to create the new file 
     * the fragment or the host plugin.
     * @return Whether the user decided to create the file in the fragment.
     */
    public static boolean shouldFileBeCreatedInFragment(IProject fragment) {
        if (PDEUtils.getFragmentHost(fragment) == null) {
            return true; // there is no host plugin, can not create something there
        }
        if (RBEPreferences.getLoadOnlyFragmentResources())
            return true;
        // TODO externalize/translate this messages
        MessageDialog dialog = new MessageDialog(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                "File creation", null, // accept
                "Resources where loaded from both the host and the fragment "
                + "plugin. Where do you want to create the new bundle?", 
                MessageDialog.QUESTION, 
                // Fragment is the default
                new String[] {"Fragment", "Host plugin"}, 0); 
        int result = dialog.open();
        return result == 0;
    }
}
