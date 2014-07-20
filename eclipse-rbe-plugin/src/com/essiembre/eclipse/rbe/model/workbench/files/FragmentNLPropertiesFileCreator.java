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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.essiembre.eclipse.rbe.ui.editor.resources.NLResourceFactory;
import com.essiembre.eclipse.rbe.ui.editor.resources.PDEUtils;

/**
 * PropertiesFileCreator used when loaded resources from an nl structure in 
 * fragments.  
 * 
 * @author Alexander Bieber
 */
public class FragmentNLPropertiesFileCreator extends NLPropertiesFileCreator {

    private IProject fragment;
    private String fragmentNlDir;
    private String hostNlDir;
    
    /**
     * @param nlDir
     * @param fileName
     */
    public FragmentNLPropertiesFileCreator(IProject fragment, String fileName) {
        super(NLResourceFactory.lookupNLDir(fragment).toString(), fileName);
        this.fragment = fragment;
        this.fragmentNlDir = NLResourceFactory.lookupNLDir(
                fragment).getFullPath().toString();
        IProject host = PDEUtils.getFragmentHost(fragment);
        if (host != null) {
            this.hostNlDir = NLResourceFactory.lookupNLDir(
                    host).getFullPath().toString();
        }
    }
    
    @Override
    protected IPath buildFilePath(Locale locale) throws CoreException {
        if (FragmentPropertiesFileCreator.shouldFileBeCreatedInFragment(
                fragment)) {
            setNlDir(fragmentNlDir);
        } else {
            setNlDir(hostNlDir);
        }
        return super.buildFilePath(locale);
    }

}
