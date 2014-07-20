package com.essiembre.eclipse.rbe.ui.editor.resources;

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.model.workbench.files.PropertiesFileCreator;

/**
 * An {@link IResourceFactory} is responsible for creating resources 
 * related to a given file structure.
 */
public interface IResourceFactory {

    /**
     * Gets the editor display name.
     * @return editor display name
     */
    public abstract String getEditorDisplayName();

    /**
     * Get the {@link SourceEditor}s of this factory,
     * that reference all message/properties files
     * this factory knows of.
     * <p>
     * This method should only be called after 
     * {@link #init(IEditorSite, IFile)} was called.
     * </p>
     * @return All {@link SourceEditor}s of this factory.
     */
    public abstract SourceEditor[] getSourceEditors();

    /**
     * Adds the given resource to the factory.
     * 
     * @param resource The resource to add.
     * @param locale The locale of the resource.
     */
    public abstract SourceEditor addResource(IResource resource, Locale locale)
            throws PartInitException;

    /**
     * Gets a properties file creator.
     * @return properties file creator
     */
    public abstract PropertiesFileCreator getPropertiesFileCreator();

    /**
     * Returns true if the resource factory is responsible for
     * the specified file resource.
     * 
     * @param file the file to check
     * @return if responsible
     * @throws CoreException
     */
    public abstract boolean isResponsible(IFile file) throws CoreException;

    /**
     * A factory should initialize its {@link SourceEditor}s
     * in this method.
     * 
     * @param site the editor site
     * @param file the file resource
     * @throws CoreException
     */
    public abstract void init(IEditorSite site, IFile file)
            throws CoreException;

}