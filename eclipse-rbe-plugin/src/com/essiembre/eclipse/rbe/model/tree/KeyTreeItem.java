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
package com.essiembre.eclipse.rbe.model.tree;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Leaf (tree) representation of one or several resource bundle entries sharing
 * the same key.
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public class KeyTreeItem implements Comparable<KeyTreeItem>, IKeyTreeVisitable {

    /** Parent key tree. */
    private KeyTree keyTree;
    /** Unique identifier (e.g., full key). */
    private String id;
    /** Item name (e.g., last segment / display name). */
    private String name;
    /** Parent item. */
    private Object parent;
    /** Child items. */
    private final SortedSet<KeyTreeItem> children = new TreeSet<KeyTreeItem>();
    
    /**
     * Constructor.
     * @param keyTree associated key tree
     * @param id unique identifier
     * @param name name
     */
    public KeyTreeItem(KeyTree keyTree, String id, String name) {
        super();
        this.keyTree = keyTree;
        this.id = id;
        this.name = name;
    }
    
    /**
     * Returns true if this item is a leaf.
     * 
     * @return    true <=> This item is a leaf.
     */
    public boolean isLeaf() {
        return (children.isEmpty());
    }
    
    /**
     * Gets the "parent" attribute.
     * @return Returns the parent.
     */
    public Object getParent() {
        return parent;
    }
    /**
     * Sets the "parent" attribute.
     * @param parent The parent to set.
     */
    public void setParent(Object parent) {
        this.parent = parent;
    }
    /**
     * Gets the "children" attribute.
     * @return Returns the children.
     */
    public SortedSet<KeyTreeItem> getChildren() {
        return children;
    }
    /**
     * Gets the "id" attribute.
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    /**
     * Gets the "keyTree" attribute.
     * @return Returns the keyTree.
     */
    public KeyTree getKeyTree() {
        return keyTree;
    }
    /**
     * Gets the "name" attribute.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all children of this item, from all available level.
     * @return collection of <code>KeyTreeItem</code> objects
     */
    public Set<KeyTreeItem> getNestedChildren() {
        Set<KeyTreeItem> nestedChildren = new TreeSet<KeyTreeItem>();
        nestedChildren.addAll(children);
        for (KeyTreeItem item : children) {
            nestedChildren.addAll(item.getNestedChildren());
        } 
        return nestedChildren;
    }
   
    /**
     * Adds a child to this item.
     * @param item child to add
     */
    public void addChildren(KeyTreeItem item) {
        children.add(item);
    }
    /**
     * Removes a child from this item.
     * @param item child to remove
     */
    public void removeChildren(KeyTreeItem item) {
        children.remove(item);
    }
    
    /**
     * @see java.lang.Object#toString()
     */    
    public String toString() {
        return id;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(KeyTreeItem o) {
        // TODO consider leaving this out to be configurable
        return this.id.compareTo((o).getId());
    }
    
    /**
     * @see com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitable#accept(
     *         com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor,
     *         java.lang.Object)
     */
    public void accept(IKeyTreeVisitor visitor, Object passAlongArgument) {
        visitor.visitKeyTreeItem(this, passAlongArgument);
    }

    /**
     * Returns a hash code of this node.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Returns whether this node is equal to the given object.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final KeyTreeItem other = (KeyTreeItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    private boolean visible = true;
    
    /**
     * Returns whether this node while be visible under the given 
     * <code>filter</code> string. To determine this, 
     * a simple substring-check on the id is applied.<br />
     * A node is marked visible if either its <code>id</code> contains the 
     * filter string or at least one of its child
     * nodes is visible. The visibility information is saved until the next 
     * call to {@link #applyFilter(String)} and can
     * be retrieved using {@link #isVisible()}.
     * 
     * @param filter The string to be searched in the id. 
     * @return A boolean indicating whether this node will be visible under 
     * the given filter.
     */
    public boolean applyFilter(String filter) {
        visible = false;
        if (id.indexOf(filter) != -1) {
            visible = true;
        }
        for (KeyTreeItem child : children) {
            if (child.applyFilter(filter)) {
                visible = true;
            }
        }
        return visible;
    }
    
    /**
     * Returns whether this node is visible under the current filter, that was 
     * applied using {@link #applyFilter(String)}.
     * @return A boolean indicating this node is visible under the current 
     * filter, that was applied using {@link #applyFilter(String)}.
     */
    public boolean isVisible() {
        return visible;
    }
}
