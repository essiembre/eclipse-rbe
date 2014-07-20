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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeVisitorAdapter;

/**
 * Visitor for finding keys starting with the <code>passAlongArgument</code>,
 * which must be a <code>String</code>.
 * @author Pascal Essiembre
 */
public class KeysStartingWithVisitor extends KeyTreeVisitorAdapter {

    /** Holder for matching keys. */
    List<KeyTreeItem> items = new ArrayList<>();
    
    /**
     * Constructor.
     */
    public KeysStartingWithVisitor() {
        super();
    }

    @Override
    public void visitKeyTreeItem(KeyTreeItem item, Object passAlongArgument) {
        String keyStart = (String) passAlongArgument;
        if (item.getId().startsWith(keyStart)) {
            items.add(item);
        }
    }

    /**
     * Gets matching key tree items.
     * @return matching key tree items
     */
    public Collection<KeyTreeItem> getKeyTreeItems() {
        return items;
    }
    
    /**
     * Gets the first item matched.
     * @return first item matched, or <code>null</code> if none was found
     */
    public KeyTreeItem getKeyTreeItem() {
        if (items.size() > 0) {
            return (KeyTreeItem) items.get(0);
        }
        return null;
    }
}
