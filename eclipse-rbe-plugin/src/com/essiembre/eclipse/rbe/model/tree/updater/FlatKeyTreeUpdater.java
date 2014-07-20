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
 * Contains update instructions on how to update a "flat" key tree
 * (no children).
 * @author Pascal Essiembre
 */
public class FlatKeyTreeUpdater extends KeyTreeUpdater {

    /**
     * Constructor.
     */
    public FlatKeyTreeUpdater() {
        super();
    }

    @Override
    public void addKey(KeyTree keyTree, String key) {
        Map<String, KeyTreeItem> keyCache = keyTree.getKeyItemsCache();
        if (!keyCache.containsKey(key)) {
            KeyTreeItem item = new KeyTreeItem(keyTree, key, key);
            item.setParent(keyTree);
            keyTree.getRootKeyItems().add(item);
            keyCache.put(key, item);
        }
    }
}
