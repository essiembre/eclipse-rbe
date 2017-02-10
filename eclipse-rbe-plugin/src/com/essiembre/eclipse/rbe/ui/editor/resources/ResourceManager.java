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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.bundle.PropertiesGenerator;
import com.essiembre.eclipse.rbe.model.bundle.PropertiesParser;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.updater.FlatKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.KeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor;

/**
 * Mediator holding instances of commonly used items, dealing with 
 * important interactions within themselves.
 * @author Pascal Essiembre
 * @author Alexander Bieber
 */
public class ResourceManager {

    private IResourceFactory resourcesFactory;
    private final BundleGroup bundleGroup;
    private final KeyTree keyTree;
    /** key=Locale;value=SourceEditor */
    /*default*/ final Map<Locale, SourceEditor> sourceEditors = new HashMap<>();
    private final List<Locale> locales = new ArrayList<>();
    //contains all ResourceManagers which can take moved keys
    private static List <ResourceManager> availaibleManagers = new ArrayList<ResourceManager>(); 
    private String fileName;
    
    /**
     * Constructor.
     * @param site eclipse editor site
     * @param file file used to create manager
     * @throws CoreException problem creating resource manager
     */
    public ResourceManager(final IEditorSite site, final IFile file)
            throws CoreException {
        super();
        fileName = file.getFullPath().toOSString();
        resourcesFactory = ResourceFactory.createFactory(site, file);
        bundleGroup = new BundleGroup();
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            SourceEditor sourceEditor = editors[i];
            Locale locale = sourceEditor.getLocale();
            sourceEditors.put(locale, sourceEditor);
            locales.add(locale);
            bundleGroup.addBundle(
                    locale, PropertiesParser.parse(sourceEditor.getContent()));            
        }
        bundleGroup.addListener(new IDeltaListener() {
            public void add(DeltaEvent event) {}    // do nothing
            public void remove(DeltaEvent event) {} // do nothing
            public void modify(DeltaEvent event) {
                final Bundle bundle = (Bundle) event.receiver();
                final SourceEditor editor = 
                        (SourceEditor) sourceEditors.get(bundle.getLocale());
                String editorContent = PropertiesGenerator.generate(bundle);
                editor.setContent(editorContent);
            }
            public void select(DeltaEvent event) {
            }
        });
        
        KeyTreeUpdater treeUpdater = null;
        if (RBEPreferences.getKeyTreeHierarchical()) {
            treeUpdater = new GroupedKeyTreeUpdater(
                    RBEPreferences.getKeyGroupSeparator());
        } else {
            treeUpdater = new FlatKeyTreeUpdater();
        }
        this.keyTree = new KeyTree(bundleGroup, treeUpdater);
        site.getPage().addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart arg0) {
				//after new ResourceBundleEditor is opened we have to add it to all availables list
				if (arg0 instanceof ResourceBundleEditor) {
					if (!availaibleManagers.contains(((ResourceBundleEditor)arg0).getResourceManager())) {
						availaibleManagers.add(((ResourceBundleEditor)arg0).getResourceManager());						
					};
				}
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart arg0) {
				//if ResourceBundleEditor is deactivated its mean that it is ready to take.. so we have to add it to all availables list
				if (arg0 instanceof ResourceBundleEditor) {
					if (!availaibleManagers.contains(((ResourceBundleEditor)arg0).getResourceManager())) {
						availaibleManagers.add(((ResourceBundleEditor)arg0).getResourceManager());
					}
				}
			}
			
			@Override
			public void partClosed(IWorkbenchPart arg0) {
				//after close we remove it from list
				if (arg0 instanceof ResourceBundleEditor) {
					if (availaibleManagers.contains(((ResourceBundleEditor)arg0).getResourceManager())) {
						availaibleManagers.remove(((ResourceBundleEditor)arg0).getResourceManager());
					}
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart arg0) {
			}
			
			@Override
			public void partActivated(IWorkbenchPart arg0) {
				//if ResourceBundleEditor is activated its mean it cannot receive keys from its self so we have to remove it from all availables list
				if (arg0 instanceof ResourceBundleEditor) {
					if (availaibleManagers.contains(((ResourceBundleEditor)arg0).getResourceManager())) {
						availaibleManagers.remove(((ResourceBundleEditor)arg0).getResourceManager());		
					}
				}
			}
		});
    }

    public String getFileName() {
		return fileName;
	}
    
    public static List<ResourceManager> getAvailaibleManagers() {
		return availaibleManagers;
	}
    
    /**
     * Gets a bundle group.
     * @return bundle group
     */
    public BundleGroup getBundleGroup() {
        return bundleGroup;
    }
    /**
     * Gets all locales in this bundle.
     * @return locales
     */
    public List<Locale> getLocales() {
        return locales;
    }
    /**
     * Gets the key tree for this bundle.
     * @return key tree
     */
    public KeyTree getKeyTree() {
        return keyTree;
    }
    /**
     * Gets the source editors.
     * @return source editors.
     */
    public SourceEditor[] getSourceEditors() {
        return resourcesFactory.getSourceEditors();
    }
    
    /**
     * Save all dirty editors.
     * @param monitor progress monitor
     */
    public void save(IProgressMonitor monitor) {
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            editors[i].getEditor().doSave(monitor);
        }
    }
        
    /**
     * Gets the multi-editor display name.
     * @return display name
     */
    public String getEditorDisplayName() {
        return resourcesFactory.getEditorDisplayName();
    }

    /**
     * Returns whether a given file is known to the resource manager (i.e.,
     * if it is part of a resource bundle).
     * @param file file to test
     * @return <code>true</code> if a known resource
     */
    public boolean isResource(IFile file) {
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            if (editors[i].getFile().equals(file)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates a properties file.
     * @param locale a locale
     * @return the newly created file
     * @throws CoreException problem creating file
     * @throws IOException problem creating file
     */
    public IFile createPropertiesFile(Locale locale) 
            throws CoreException, IOException {
        return resourcesFactory.getPropertiesFileCreator().createPropertiesFile(
                locale);
    }
    
    /**
     * Gets the source editor matching the given locale.
     * @param locale locale matching requested source editor
     * @return source editor or <code>null</code> if no match
     */
    public SourceEditor getSourceEditor(Locale locale) {
        return (SourceEditor) sourceEditors.get(locale);
    }
    
    public SourceEditor addSourceEditor(IFile resource, Locale locale) 
            throws PartInitException {
        SourceEditor sourceEditor = resourcesFactory.addResource(
                resource, locale);
        sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
        locales.add(locale);
        bundleGroup.addBundle(
                locale, PropertiesParser.parse(sourceEditor.getContent())); 
        return sourceEditor;
    }
    /**
     * Reloads the properties files (parse them).
     */
    public void reloadProperties() {
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            SourceEditor editor = editors[i];
            if (editor.isCacheDirty()) {
                bundleGroup.addBundle(
                        editor.getLocale(),
                        PropertiesParser.parse(editor.getContent()));
                editor.resetCache();
            }
        }
    }

}
