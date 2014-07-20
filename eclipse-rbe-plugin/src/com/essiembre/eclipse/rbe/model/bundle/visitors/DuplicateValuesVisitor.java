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
package com.essiembre.eclipse.rbe.model.bundle.visitors;

import java.util.ArrayList;
import java.util.Collection;

import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleVisitorAdapter;

/**
 * Finds bundle entries having values identical to the bundle entry given
 * as the pass-along argument.
 * @author Pascal Essiembre
 */
public class DuplicateValuesVisitor extends BundleVisitorAdapter {

    /** Holder for bundle entries having duplicate values. */
    private final Collection<BundleEntry> duplicates = new ArrayList<>();
    
    /**
     * Constructor.
     */
    public DuplicateValuesVisitor() {
        super();
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.bundle.IBundleVisitor
     *         #visitBundleEntry(
     *                 com.essiembre.eclipse.rbe.model.bundle.BundleEntry,
     *                 java.lang.Object)
     */
    public void visitBundleEntry(BundleEntry entry, Object passAlongArgument) {
        
        BundleEntry entryToMatch = (BundleEntry) passAlongArgument;
        if (entry != entryToMatch
                && entry != null && entryToMatch != null
                && entry.getValue().length() > 0
                && entry.getValue().equals(entryToMatch.getValue())) {
            duplicates.add(entry);
        }
    }
    
    /**
     * Gets a collection of duplicate <code>BundleEntry</code> instance.
     * @return bundle entries with duplicate values
     */
    public Collection<BundleEntry> getDuplicates() {
        return duplicates;
    }
    
    /**
     * Clears the list of duplicate values.
     */
    public void clear() {
        duplicates.clear();
    }
    
}