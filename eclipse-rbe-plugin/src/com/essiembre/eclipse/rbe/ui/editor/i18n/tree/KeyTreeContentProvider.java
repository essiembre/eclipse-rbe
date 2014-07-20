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
package com.essiembre.eclipse.rbe.ui.editor.i18n.tree;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;

/**
 * Content provider for key tree viewer.
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public class KeyTreeContentProvider implements 
        ITreeContentProvider, IDeltaListener {

    /** Represents empty objects. */
    private static Object[] EMPTY_ARRAY = new Object[0];
    /** Viewer this provided act upon. */
    protected TreeViewer treeViewer;

    @Override
    public void dispose() {}

    
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.treeViewer = (TreeViewer) viewer;
        if(oldInput != null) {
            ((KeyTree) oldInput).removeListener(this);
        }
        if(newInput != null) {
            ((KeyTree) newInput).addListener(this);
        }
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if(parentElement instanceof KeyTree) {
            return ((KeyTree) parentElement).getRootKeyItems().toArray(); 
        } else if (parentElement instanceof KeyTreeItem) {
            return ((KeyTreeItem) parentElement).getChildren().toArray(); 
        }
        return EMPTY_ARRAY;
    }
    
    @Override
    public Object getParent(Object element) {
        if(element instanceof KeyTreeItem) {
            return ((KeyTreeItem) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public void add(DeltaEvent event) {
        treeViewer.refresh(true);
    }

    @Override
    public void remove(DeltaEvent event) {
        treeViewer.refresh(true);
    }

    @Override
    public void select(DeltaEvent event) {
        KeyTreeItem treeItem = (KeyTreeItem) event.receiver();
        if (treeItem != null) {
            KeyTreeItem currentSelection = getTreeSelection();
            if ((currentSelection == null) || (!treeItem.getId().endsWith(
                    currentSelection.getId()))) {
                StructuredSelection selection = 
                        new StructuredSelection(treeItem);
                treeViewer.setSelection(selection);
            }
        }
    }
    
    
    /**
     * Gets the selected key tree item.
     * @return key tree item
     */
    private KeyTreeItem getTreeSelection() {
        IStructuredSelection selection = 
                (IStructuredSelection) treeViewer.getSelection();
        return ((KeyTreeItem) selection.getFirstElement());
    }
    
    
    @Override
    public void modify(DeltaEvent event) {
        //TODO how to make sure many changes could do a "batch" refresh on tree?
        KeyTreeItem treeItem = (KeyTreeItem) event.receiver();
        Object parentTreeItem = treeItem.getParent();
        treeViewer.refresh(parentTreeItem, true);
    }
}
