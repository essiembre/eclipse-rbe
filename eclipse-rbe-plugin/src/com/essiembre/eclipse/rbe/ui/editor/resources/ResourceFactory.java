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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.essiembre.eclipse.rbe.model.workbench.files.PropertiesFileCreator;
import com.essiembre.eclipse.rbe.ui.UIUtils;

/**
 * Responsible for creating resources related to a given file structure.
 * <p>
 * This class is also the abstract base class for implementations of 
 * a {@link ResourceFactory} as well as static entry point to access
 * the responsible one.
 * </p>
 * @author Pascal Essiembre
 * @author cuhiodtick
 */
public abstract class ResourceFactory implements IResourceFactory {

    /** Class name of Properties file editor (Eclipse 3.1). */
    protected static final String PROPERTIES_EDITOR_CLASS_NAME = 
            "org.eclipse.jdt.internal.ui.propertiesfileeditor."
          + "PropertiesFileEditor";

    /** Token to replace in a regular expression with a bundle name. */
    private static final String TOKEN_BUNDLE_NAME = "BUNDLENAME";
    /** Token to replace in a regular expression with a file extension. */
    private static final String TOKEN_FILE_EXTENSION = 
            "FILEEXTENSION";
    /** Regex to match a properties file. */
    private static final String PROPERTIES_FILE_REGEX = 
            "^(" + TOKEN_BUNDLE_NAME + ")"
          + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
          + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
          + TOKEN_FILE_EXTENSION + ")$";
    

    /*
     * Common members of ResourceFactories
     */
    
    /**
     * A sorted map of {@link SourceEditor}s.
     * Sorted by key (Locale).
     */
    private Map<Locale, SourceEditor> sourceEditors = 
            new TreeMap<>(new UIUtils.LocaleComparator());
        
    /**
     * The {@link PropertiesFileCreator} used to create new files.
     */
    private PropertiesFileCreator propertiesFileCreator;
    /**
     * The associated editor site.
     */
    private IEditorSite site;
    /**
     * The displayname
     */
    private String displayName;

    @Override
    public String getEditorDisplayName() {
        return displayName;
    }
    /**
     * Sets the editor display name of this factory.
     * @param displayName The display name to set.
     * @see #getEditorDisplayName()
     */
    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public SourceEditor[] getSourceEditors() {
        SourceEditor[] editors = new SourceEditor[sourceEditors.values().size()];
        int i = 0;
        for (Iterator<SourceEditor> it = sourceEditors.values().iterator(); 
                it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof SourceEditor) {
                editors[i] = (SourceEditor) obj;
            }
            i++;
        }
        return editors;
    }

    @Override
    public SourceEditor addResource(IResource resource, Locale locale) throws PartInitException {
        if (sourceEditors.containsKey(locale))
            throw new IllegalArgumentException("ResourceFactory already contains a resource for locale "+locale);
        SourceEditor editor = createEditor(site, resource, locale);
        addSourceEditor(editor.getLocale(), editor);
        return editor;
    }
    
    protected void addSourceEditor(Locale locale, SourceEditor sourceEditor) {
        sourceEditors.put(locale, sourceEditor);
    }

    protected void setSite(IEditorSite site) {
        this.site = site;
    }
    protected IEditorSite getSite() {
        return site;
    }
    @Override
    public PropertiesFileCreator getPropertiesFileCreator() {
        return propertiesFileCreator;
    }
    protected void setPropertiesFileCreator(PropertiesFileCreator fileCreator) {
        this.propertiesFileCreator = fileCreator;
    }
    
    @Override
    public abstract boolean isResponsible(IFile file) throws CoreException;
    
    @Override
    public abstract void init(IEditorSite site, IFile file) throws CoreException;
    
    
//    /**
//     * Creates a resource factory based on given arguments.
//     * @param site eclipse editor site
//     * @param file file used to create factory
//     * @return resource factory
//     * @throws CoreException problem creating factory
//     */
//    public static ResourceFactory createFactory(IEditorSite site, IFile file)
//            throws CoreException {
//        if (isNLResource(file)) {
//            return new NLResourceFactory(site, file);
//        }
//        return new StandardResourceFactory(site, file);
//    }
//
    
    
    /**
     * Creates a resource factory based on given arguments.
     * @param site eclipse editor site
     * @param file file used to create factory
     * @return An initialized resource factory, or <code>null</code> 
     * if no responsible one could be found
     * @throws CoreException problem creating factory
     */
    public static IResourceFactory createFactory(IEditorSite site, IFile file)
            throws CoreException {
        IResourceFactory[] factories = 
                ResourceFactoryDescriptor.getContributedResourceFactories();
        for (int i = 0; i < factories.length; i++) {
            IResourceFactory factory = factories[i];
            if (factory.isResponsible(file)) {
                factory.init(site, file);
                return factory;
            }
        }
        return new StandardResourceFactory();
    }
    
    /**
     * Creates a resource factory based on given arguments and excluding
     * factories of the given class.
     * <p>
     * This might be used to get the {@link SourceEditor}s from 
     * other factories while initializing an other factory.
     * </p>
     * @param site eclipse editor site
     * @param file file used to create factory
     * @param childFactoryClass The class of factory to exclude.
     * @return An initialized resource factory, or <code>null</code> if 
     * no responsible one could be found
     * @throws CoreException problem creating factory
     */
    public static IResourceFactory createParentFactory(IEditorSite site, 
            IFile file, Class<?> childFactoryClass)
            throws CoreException {
        IResourceFactory[] factories = 
                ResourceFactoryDescriptor.getContributedResourceFactories();
        for (int i = 0; i < factories.length; i++) {
            IResourceFactory factory = factories[i];
            if (!factory.getClass().equals(childFactoryClass) 
                    && factory.isResponsible(file)) {
                factory.init(site, file);
                return factory;
            }
        }
        return null;
    }
    
    /**
     * Parses the specified bundle name and returns the locale.
     * @param resource the resource
     * @return the locale or null if none
     */
    protected static Locale parseBundleName(IResource resource) {
        // Build local title
        String regex = ResourceFactory.getPropertiesFileRegEx(resource);
        String localeText = resource.getName().replaceFirst(regex, "$2");
        StringTokenizer tokens = new StringTokenizer(localeText, "_");
        List<String> localeSections = new ArrayList<>();
        while (tokens.hasMoreTokens()) {
            localeSections.add(tokens.nextToken());
        }
        Locale locale = null;
        switch (localeSections.size()) {
        case 1:
            locale = new Locale(localeSections.get(0));
            break;
        case 2:
            locale = new Locale(
                    localeSections.get(0),
                    localeSections.get(1));
            break;
        case 3:
            locale = new Locale(
                    localeSections.get(0),
                    localeSections.get(1),
                    localeSections.get(2));
            break;
        default:
            break;
        }
        return locale;
    }
    
    protected SourceEditor createEditor(
            IEditorSite site, IResource resource, Locale locale)
            throws PartInitException {
        
        ITextEditor textEditor = null;
        if (resource != null && resource instanceof IFile) {
            IEditorInput newEditorInput = 
                    new FileEditorInput((IFile) resource);
            try {
                // Use PropertiesFileEditor if available
                textEditor = (TextEditor) Class.forName(
                        PROPERTIES_EDITOR_CLASS_NAME).newInstance();
            } catch (Exception e) {
                // Use default editor otherwise
                textEditor = new TextEditor();
            }
            textEditor.init(site, newEditorInput);
            
            try {
               /* ugly fix for a memory leak: 
                * ITextEditor.init(.) Javadoc states: 
                * "Clients must not call this method."
                * but we do in ResourceFactory.createEditor(.), and the way we 
                * set-up everything, we have to.
                * Since duplicate calls to init(.) create a memory leak, due 
                * to a zombie ActivationListener registered in 
                * AbstractTextEditor, we dispose the first ActivationListener 
                * we just unintentionally created */
               Field field = AbstractTextEditor.class.getDeclaredField(
                       "fActivationListener");
               // enable access to the method - ...hackity hack
               field.setAccessible(true);
               Object activationListener = field.get(textEditor);
               Method disposeMethod = 
                       activationListener.getClass().getMethod("dispose");
               disposeMethod.setAccessible(true);
               disposeMethod.invoke(activationListener);
            }
            catch(Exception e) {
               System.err.println("Failed to apply memory leak work around");
            }
        }
        if (textEditor != null) {
            return new SourceEditor(textEditor, locale, (IFile) resource);
        }
        return null;
    }

    
//    private static boolean isNLResource(IFile file) 
//            throws PartInitException {
//        /*
//         * Check if NL is supported.
//         */
//        if (!RBEPreferences.getSupportNL()) {
//            return false;
//        }
//
//        /*
//         * Check if there is an NL directory
//         */
//        IContainer container = file.getParent();
//        IResource nlDir = null;
//        while (container != null 
//                && (nlDir == null || !(nlDir instanceof Folder))) {
//            nlDir = container.findMember("nl");
//            container = container.getParent();
//        }
//        if (nlDir == null || !(nlDir instanceof Folder)) {
//            return false;
//        }
//
//        /*
//         * Ensures NL directory is part of file path, or that file dir
//         * is parent of NL directory.
//         */
//        IPath filePath = file.getFullPath();
//        IPath nlDirPath = nlDir.getFullPath();
//        if (!nlDirPath.isPrefixOf(filePath)
//                && !filePath.removeLastSegments(1).isPrefixOf(nlDirPath)) {
//            return false;
//        }
//        
//        /*
//         * Ensure that there are no other files which could make a standard
//         * resource bundle.
//         */
//        if (StandardResourceFactory.getResources(file).length > 1) {
//             return false;
//        }
//        return true;
//    }
    
    protected static String getBundleName(IResource file) {
        String name = file.getName();
        String regex = "^(.*?)"
                + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
                + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
                + file.getFileExtension() + ")$";
        return name.replaceFirst(regex, "$1");
    }
    
    protected static String getDisplayName(IResource file) {
        if (file instanceof IFile)
            return getBundleName(file) + "[...]." + file.getFileExtension();
        else
            return getBundleName(file);
    }
    
    protected static String getPropertiesFileRegEx(IResource file) {
        String bundleName = getBundleName(file);
        return PROPERTIES_FILE_REGEX.replaceFirst(
                TOKEN_BUNDLE_NAME, bundleName).replaceFirst(
                        TOKEN_FILE_EXTENSION, file.getFileExtension());
    }

    /**
     * Returns the resource bundle file resources that match the specified file
     * name.
     * @param file the file to match
     * @return array of file resources, empty if none matches
     * @throws CoreException
     */
    protected static IFile[] getResources(IFile file) throws CoreException {
        
        String regex = ResourceFactory.getPropertiesFileRegEx(file);
        IResource[] resources = file.getParent().members();
        Collection<IResource> validResources = new ArrayList<>();
        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
            String resourceName = resource.getName();
            if (resource instanceof IFile && resourceName.matches(regex)) {
                validResources.add(resource);
            }
        }
        return (IFile[]) validResources.toArray(new IFile[]{});
    }
    
}
