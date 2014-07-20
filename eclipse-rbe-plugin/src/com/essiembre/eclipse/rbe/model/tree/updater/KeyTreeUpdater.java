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
package com.essiembre.eclipse.rbe.model.tree.updater;

import java.util.Map;

import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;

/**
 * Contains update instructions on how to update a key tree.
 * @author Pascal Essiembre
 */
public abstract class KeyTreeUpdater {

    /**
     * Constructor.
     */
    public KeyTreeUpdater() {
        super();
    }
    
    /**
     * Adds a key to the key tree.
     * @param keyTree key tree on which to add the key
     * @param key key to add
     */
    public abstract void addKey(KeyTree keyTree, String key);

    /**
     * Removes a key from the key tree.
     * @param keyTree key tree from which to remove the key
     * @param key key to remove
     */
    public void removeKey(KeyTree keyTree, String key) {
        Map<String, KeyTreeItem> keyCache = keyTree.getKeyItemsCache();
        KeyTreeItem item = keyCache.get(key);
        if (item != null) {
            Object parent = item.getParent();
            if (parent instanceof KeyTree) {
                ((KeyTree) parent).getRootKeyItems().remove(item);
            } else {
                ((KeyTreeItem) parent).removeChildren(item);
            }
            keyCache.remove(key);

            // remove parents with no children having invalid keys. 
            if (parent instanceof KeyTreeItem) {
                KeyTreeItem parentItem = (KeyTreeItem) parent;
                boolean isKey = 
                        keyTree.getBundleGroup().isKey(parentItem.getId());
                boolean hasChildren = parentItem.getChildren().size() > 0;
                if (!isKey && ! hasChildren) {
                    removeKey(keyTree, parentItem.getId());
                }
            }
        }
    }
}
