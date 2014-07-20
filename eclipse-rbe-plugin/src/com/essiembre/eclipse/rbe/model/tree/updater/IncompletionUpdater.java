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


import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;

import java.util.Collection;
import java.util.Iterator;

/**
 * An update which filters entries where at least one isn't available.
 */
public class IncompletionUpdater extends KeyTreeUpdater {
    
    private KeyTreeUpdater delegation;
    private BundleGroup bundlegroup;
    
    /**
     * Initialises this BundleGroup instance which allows to access
     * the bundles keeping the i18n information. 
     * 
     * @param group     A container for the i18n information.
     * @param delegate  The update which will be used for delegation. Mainly
     *                  intended for structural information.
     */
    public IncompletionUpdater(BundleGroup group, KeyTreeUpdater delegate) {
        delegation  = delegate;
        bundlegroup = group;
    }
    

    @Override
    public void addKey(KeyTree keytree, String key) {
        Collection<BundleEntry> entries  = bundlegroup.getBundleEntries(key);
        int        count    = 0;
        Iterator<BundleEntry>   iterator = entries.iterator();
        while(iterator.hasNext()) {
            BundleEntry entry = iterator.next();
            String      value = entry.getValue();
            if((value != null) && (value.length() > 0)) {
                count++;
            }
        }
        // we only delegate entries in case there are some incomplete ones
        if (count < bundlegroup.getBundleCount()) {
            delegation.addKey(keytree, key);
        }
    }
}
