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
 * that is commented out.
 * @author Pascal Essiembre
 */
public class IsCommentedVisitor extends KeyTreeVisitorAdapter {

    /** Whether corresponding bundle entries have one commented. */
    boolean hasOneCommented = false;
    /** Whether corresponding bundle entries are all commented. */
    boolean areAllCommented = false;
    
    /**
     * Constructor.
     */
    public IsCommentedVisitor() {
        super();
    }

    @Override
    public void visitKeyTreeItem(KeyTreeItem item, Object passAlongArgument) {
        String key = item.getId();
        BundleGroup bundleGroup = item.getKeyTree().getBundleGroup();
        if (bundleGroup.isKey(key)) {
            Collection<BundleEntry> entries = bundleGroup.getBundleEntries(key);
            int commentedCount = 0;
            for (BundleEntry entry : entries) {
                if (entry != null && entry.isCommented()) {
                    hasOneCommented = true;
                    commentedCount++;
                }
            }
            if (commentedCount == entries.size()) {
                areAllCommented = true;
            }
        }
    }
    
    /**
     * Gets the "areAllCommented" attribute.
     * @return Returns the areAllCommented.
     */
    public boolean areAllCommented() {
        return areAllCommented;
    }
    /**
     * Gets the "hasOneCommented" attribute.
     * @return Returns the hasOneCommented.
     */
    public boolean hasOneCommented() {
        return hasOneCommented;
    }
}
