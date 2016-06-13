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
package com.essiembre.eclipse.rbe.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.i18n.I18nPage;
import com.essiembre.eclipse.rbe.ui.editor.i18n.I18nPageEditor;
import com.essiembre.eclipse.rbe.ui.editor.locale.NewLocalePage;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.editor.resources.SourceEditor;
import com.essiembre.eclipse.rbe.ui.views.ResourceBundleOutline;

/**
 * Multi-page editor for editing resource bundles.
 * @author Pascal Essiembre
 */
public class ResourceBundleEditor extends MultiPageEditorPart
        implements IGotoMarker {

    /** Editor ID, as defined in plugin.xml. */
    public static final String EDITOR_ID = 
       "com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor";
    
    private ResourceManager resourceMediator;
    private I18nPage i18nPage;
    /** New locale page. */
    private NewLocalePage newLocalePage;
    
    /** the outline which additionally allows to navigate through the keys. */
    private ResourceBundleOutline outline;
    
    private ResourceChangeListener resourceChangeListener = 
            new ResourceChangeListener();
    private List<IPath> paths = new ArrayList<IPath>();
    
    
    /**
     * Creates a multi-page editor example.
     */
    public ResourceBundleEditor() {
        super();
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    @Override
    public void init(IEditorSite site, IEditorInput editorInput)
            throws PartInitException {
        super.init(site, editorInput);
        if (editorInput instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) editorInput).getFile();
            try {
                resourceMediator = new ResourceManager(site, file);
            } catch (CoreException e) {
                UIUtils.showErrorDialog(
                        site.getShell(), e, "error.init.ui");
                return;
            }
            
            ResourcesPlugin.getWorkspace().addResourceChangeListener(
                    resourceChangeListener, IResourceChangeEvent.POST_CHANGE);

            setPartName(resourceMediator.getEditorDisplayName());
            setContentDescription(
                    RBEPlugin.getString("editor.content.desc")
                  + resourceMediator.getEditorDisplayName() + ".");
            setTitleImage(UIUtils.getImage(UIUtils.IMAGE_RESOURCE_BUNDLE));
            closeIfAreadyOpen(site, file);
        } else {
            throw new PartInitException(
                    "Invalid Input: Must be IFileEditorInput");
        }
    }
    
    /**
     * Gets the resource manager.
     * @return the resource manager
     */
    public ResourceManager getResourceManager() {
        return resourceMediator;
    }
    
    /**
     * Creates the pages of the multi-page editor.
     */
    @Override
    protected void createPages() {
       // Create I18N page
        int index;
        try {
           I18nPageEditor i18PageEditor = new I18nPageEditor(resourceMediator);
           index = addPage(i18PageEditor, null);
           i18nPage = i18PageEditor.getI18nPage();
           setPageText(index, RBEPlugin.getString("editor.properties"));
           setPageImage(index, UIUtils.getImage(UIUtils.IMAGE_RESOURCE_BUNDLE));
        }
        catch (PartInitException argh) {
           ErrorDialog.openError(getSite().getShell(), 
                   "Error creating i18PageEditor page.",
                           null, argh.getStatus());
        }
        
        // Create text editor pages for each locales
        try {
            SourceEditor[] sourceEditors = resourceMediator.getSourceEditors();
            for (int i = 0; i < sourceEditors.length; i++) {
                SourceEditor sourceEditor = sourceEditors[i];
                index = addPage(
                   sourceEditor.getEditor(), 
                   sourceEditor.getEditor().getEditorInput());
                setPageText(index, UIUtils.getDisplayName(
                        sourceEditor.getLocale()));
                setPageImage(index, 
                        UIUtils.getImage(UIUtils.IMAGE_PROPERTIES_FILE));
                
                paths.add( sourceEditor.getFile().getFullPath() );
            }
            outline = new ResourceBundleOutline(resourceMediator.getKeyTree());
            
            
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), 
                "Error creating text editor page.",
                null, e.getStatus());
        }
        
        // Add "new locale" page
        newLocalePage = new NewLocalePage(
                getContainer(), resourceMediator, this);
        index = addPage(newLocalePage);
        setPageText(index, RBEPlugin.getString("editor.new.tab"));
        setPageImage(
                index, UIUtils.getImage(UIUtils.IMAGE_NEW_PROPERTIES_FILE));
    }
    
    public void addResource(IFile resource, Locale locale) {
        try {            
            SourceEditor sourceEditor = 
                    resourceMediator.addSourceEditor(resource, locale);
            int index = getPageCount() - 1;
            addPage(index,
                    sourceEditor.getEditor(), 
                    sourceEditor.getEditor().getEditorInput());
            setPageText(index, UIUtils.getDisplayName(
                    sourceEditor.getLocale()));
            setPageImage(index, 
                    UIUtils.getImage(UIUtils.IMAGE_PROPERTIES_FILE));
            i18nPage.refreshPage();
            setActivePage(0);
            // re-set the content to trigger dirty state 
            sourceEditor.setContent(sourceEditor.getContent()); 
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), 
                    "Error creating resource mediator.",
                    null, e.getStatus());
        }
    }


    
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        Object obj = super.getAdapter(adapter);
        if (obj == null) {
            if (IContentOutlinePage.class.equals(adapter)) {
                return (outline);
            }
        }
        return (obj);
    }
    
    
    /**
     * Saves the multi-page editor's document.
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        KeyTree keyTree = resourceMediator.getKeyTree();
        String key = keyTree.getSelectedKey();

        i18nPage.refreshEditorOnChanges();
        resourceMediator.save(monitor);
        
        keyTree.setUpdater(keyTree.getUpdater());
        if (key != null)
            keyTree.selectKey(key);
    }
    
    @Override
    public void doSaveAs() {
        // Save As not allowed.
    }
    
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Change current page based on locale.  If there is no editors associated
     * with current locale, do nothing.
     * @param locale locale used to identify the page to change to
     */
    public void setActivePage(Locale locale) {
        SourceEditor[] editors = resourceMediator.getSourceEditors();
        int index = -1;
        for (int i = 0; i < editors.length; i++) {
            SourceEditor editor = editors[i];
            Locale editorLocale = editor.getLocale();
            if (editorLocale != null && editorLocale.equals(locale)
                    || editorLocale == null && locale == null) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            setActivePage(index + 1);
        }
    }

    @Override
    public void gotoMarker(IMarker marker) {
        IPath markerPath = marker.getResource().getProjectRelativePath();
        SourceEditor[] sourceEditors = resourceMediator.getSourceEditors();
        for (int i = 0; i < sourceEditors.length; i++) {
            SourceEditor editor = sourceEditors[i];
            IPath editorPath = editor.getFile().getProjectRelativePath();
            if (markerPath.equals(editorPath)) {
                setActivePage(editor.getLocale());
                IDE.gotoMarker(editor.getEditor(), marker);
                break;
            }
        }
    }
    
    private SourceEditor lastEditor;

    /**
     * Calculates the contents of page GUI page when it is activated.
     * @param newPageIndex new page index
     */
    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        KeyTree keyTree = resourceMediator.getKeyTree();
        
        if (lastEditor != null) {
            String lastEditorKey = lastEditor.getCurrentKey();
            if (lastEditorKey != null)
                keyTree.selectKey(lastEditor.getCurrentKey());
        }
        
        if (newPageIndex == 0) {  // switched to first page
            resourceMediator.reloadProperties();
            i18nPage.refreshTextBoxes();
            lastEditor = null; // reset lastEditor
            return;
        }
        
        if (newPageIndex == getPageCount()-1) // switched to last page
            return;
        
        int editorIndex = newPageIndex - 1; // adjust because first page is tree page        
        if (editorIndex >= 0 && editorIndex < resourceMediator.getSourceEditors().length) {
            lastEditor = resourceMediator.getSourceEditors()[editorIndex];
            if (keyTree.getSelectedKey() != null)
                lastEditor.selectKey(keyTree.getSelectedKey());
        }
    }

    
    /**
     * Is the given file a member of this resource bundle.
     * @param file file to test
     * @return <code>true</code> if file is part of bundle
     */
    public boolean isBundleMember(IFile file) {
        return resourceMediator.isResource(file);
    }

    private void closeIfAreadyOpen(final IEditorSite site, final IFile file) {
        IWorkbenchPage[] pages = site.getWorkbenchWindow().getPages();
        for (int i = 0; i < pages.length; i++) {
            final IWorkbenchPage page = pages[i];
            IEditorReference[] editors = page.getEditorReferences();
            for (int j = 0; j < editors.length; j++) {
                final IEditorPart editor = editors[j].getEditor(false);
                if (editor instanceof ResourceBundleEditor) {
                    ResourceBundleEditor rbe = (ResourceBundleEditor) editor;
                    if (rbe.isBundleMember(file)) {
                        // putting the close operation into the queue
                        // closing during opening caused errors.
                        Display.getDefault().asyncExec(new Runnable() {
                        	@Override
							public void run() {
                                page.closeEditor(editor, true);
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        
        if (i18nPage != null) {
            i18nPage.dispose();
        }
        if (newLocalePage != null) {
            newLocalePage.dispose();
        }
        
        /* fix for a weird memory leak: unless we remove the selectionProvider 
         * from our editor, nothing get's GCed. */
        getSite().setSelectionProvider(null);
        SourceEditor[] sourceEditors = resourceMediator.getSourceEditors();
        for ( int i = 0; i < sourceEditors.length; i++ ) {
           SourceEditor editor = sourceEditors[i];
           editor.getEditor().getSite().setSelectionProvider(null);
        }

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(
                resourceChangeListener);
        super.dispose();
    }

    private class ResourceChangeListener implements IResourceChangeListener {
      @Override
      public void resourceChanged( IResourceChangeEvent event ) {
         boolean deltaFound = false;
         for ( IPath path : paths ) {
            IResourceDelta delta = event.getDelta().findMember(path);
            deltaFound |= delta!= null;
         }
         if ( deltaFound ) {
            resourceMediator.reloadProperties();
            i18nPage.refreshTextBoxes();
         }
      }
   }
}
