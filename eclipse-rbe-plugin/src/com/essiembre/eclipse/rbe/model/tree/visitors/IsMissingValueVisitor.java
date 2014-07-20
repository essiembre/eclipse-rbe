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
package com.essiembre.eclipse.rbe.model.tree.visitors;

import java.util.Collection;

import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeVisitorAdapter;

/**
 * Visitor for finding if a key has at least one corresponding bundle entry
 * with a missing value.
 * @author Pascal Essiembre
 */
public class IsMissingValueVisitor extends KeyTreeVisitorAdapter {

    /** Whether corresponding bundle entries are missing a value. */
    boolean isMissingValue = false;
    /** Whether a corresponding bundle entries children are missing a value. */
    boolean isMissingChildValueOnly = false;
    
    /**
     * Constructor.
     */
    public IsMissingValueVisitor() {
        super();
    }

    @Override
    public void visitKeyTreeItem(KeyTreeItem item, Object passAlongArgument) {
        // passed item
        isMissingValue = isItemMissingValue(item);
        
        // chidren items
        if (!isMissingValue) {
            for (KeyTreeItem childItem : item.getNestedChildren()) {
                isMissingChildValueOnly = isItemMissingValue(childItem);
                if (isMissingChildValueOnly) {
                    return;
                }
            }
        }
    }

    /**
     * Checks whether the corresponding entries do not miss any values, but have
     * at least one child missing a value.
     * @return <code>true</code> if child missing a value
     */
    public boolean isMissingChildValueOnly() {
        return isMissingChildValueOnly;
    }
    /**
     * Sets whether the corresponding entries do not miss any values, but have
     * at least one child missing a value.
     * @param isMissingChildValueOnly <code>true</code> if child missing value
     */
    public void setMissingChildValueOnly(boolean isMissingChildValueOnly) {
        this.isMissingChildValueOnly = isMissingChildValueOnly;
    }

    /**
     * Checks whether the corresponding entries are missing any values.
     * @return <code>true</code> if missing a value
     */
    public boolean isMissingValue() {
        return isMissingValue;
    }
    /**
     * Sets whether the corresponding entries are missing any values.
     * @param isMissingValue <code>true</code> if missing a value
     */
    public void setMissingValue(boolean isMissingValue) {
        this.isMissingValue = isMissingValue;
    }
    
    /**
     * Checks if the given item is missing a value.
     * @param item the item to check
     * @return <code>true</code> if item is missing a value
     */
    private boolean isItemMissingValue(KeyTreeItem item) {
        String key = item.getId();
        BundleGroup bundleGroup = item.getKeyTree().getBundleGroup();
        if (bundleGroup.isKey(key)) {
            Collection<BundleEntry> entries = bundleGroup.getBundleEntries(key);
            if (entries.size() != bundleGroup.getSize()) {
                return true;
            }
            for (BundleEntry entry : entries) {
                if (entry == null || entry.getValue().length() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
