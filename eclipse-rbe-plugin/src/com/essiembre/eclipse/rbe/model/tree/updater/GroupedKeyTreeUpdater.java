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
import java.util.StringTokenizer;

import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;

/**
 * Contains update instructions on how to update a "grouped" key tree.
 * @author Pascal Essiembre
 */
public class GroupedKeyTreeUpdater extends KeyTreeUpdater {

    /** Key group separator. */
    private String separator;
    
    /**
     * Constructor.
     * @param keyGroupSeparator key group separator
     */
    public GroupedKeyTreeUpdater(String keyGroupSeparator) {
        super();
        this.separator = keyGroupSeparator;
    }

    @Override
    public void addKey(KeyTree keyTree, String key) {
        Map<String, KeyTreeItem> keyCache = keyTree.getKeyItemsCache();
        if (!keyCache.containsKey(key)) {
            StringBuffer idBuf = new StringBuffer();
            Object parent = keyTree;
            for (StringTokenizer tokens = new StringTokenizer(key, separator);
                    tokens.hasMoreTokens();) {
                String name = tokens.nextToken();
                if (!(parent instanceof KeyTree)) {
                    idBuf.append(separator);
                }
                idBuf.append(name);
                String id = idBuf.toString();
                if (!keyCache.containsKey(id)) {
                    KeyTreeItem item = new KeyTreeItem(keyTree, id, name);
                    item.setParent(parent);
                    if (parent instanceof KeyTree) {
                        keyTree.getRootKeyItems().add(item);
                    } else {
                        ((KeyTreeItem) parent).addChildren(item);
                    }
                    keyCache.put(id, item);
                    parent = item;
                } else {
                    parent = keyCache.get(id);
                }
            }
        }
    }

    /**
     * Gets the key group separator.
     * @return key group separator.
     */
    public String getSeparator() {
        return separator;
    }
    /**
     * Sets the key group separator.
     * @param separator key group separator
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
