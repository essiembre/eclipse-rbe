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
package com.essiembre.eclipse.rbe.model.bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.essiembre.eclipse.rbe.model.Model;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;


/**
 * Representation of a properties file, specific to ResourceBundle editor.
 * @author Pascal Essiembre
 */
public class Bundle extends Model implements IBundleVisitable {

    /** Bundle head comment. */
    private String comment;
    /** Bundle locale. */
    private Locale locale;
    /** Bundle entries (key=key value=BundleEntry). */
    private final Map<String, BundleEntry> entries = new LinkedHashMap<>();
    /** Bundle group (parent). */
    private BundleGroup bundleGroup;
    
    /**
     * Constructor.
     */
    public Bundle() {
        super();
    }
    
    /**
     * @see IBundleVisitable#accept(IBundleVisitor, Object)
     */
    public void accept(IBundleVisitor visitor, Object passAlongArgument) {
        for (Iterator<BundleEntry> iter = 
                entries.values().iterator(); iter.hasNext();) {
            visitor.visitBundleEntry(iter.next(), passAlongArgument);
        }
        visitor.visitBundle(this, passAlongArgument);
        visitor.visitBundleGroup(bundleGroup, passAlongArgument);
    }
    
    /**
     * Gets the "comment" attribute.
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }
    /**
     * Sets the "comment" attribute.
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the "locale" attribute.
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return locale;
    }
    /**
     * Sets the "locale" attribute.
     * @param locale The locale to set.
     */
    protected void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    /**
     * Gets the bundle entry matcing the given key.
     * @param key a bundle entry key
     * @return the matching bundle entry, or <code>null</code>
     */
    public BundleEntry getEntry(String key) {
        return (BundleEntry) entries.get(key);    
    }
    
    /**
     * Adds a bundle entry to this bundle.
     * @param entry the bundle entry to add
     */
    protected void addEntry(BundleEntry entry) {
        BundleEntry oldEntry = (BundleEntry) entries.get(entry.getKey());
        if (oldEntry != null) {
            if (!oldEntry.equals(entry)) {
                entries.put(entry.getKey(), entry);
                entry.setBundle(this);
                entry.setLocale(locale);
                fireModify(oldEntry);
            }
        } else if (entry.getKey().trim().length() > 0) {
            entries.put(entry.getKey(), entry);
            entry.setBundle(this);
            entry.setLocale(locale);
            fireAdd(entry);
        }
    }
    
    /**
     * Removes a bundle entry from this bundle.
     * @param entry the bundle entry to remove
     */
    protected void removeEntry(BundleEntry entry) {
        BundleEntry removedEntry = (BundleEntry) entries.get(entry.getKey());
        entries.remove(entry.getKey());
        fireRemove(removedEntry);
    }
    
    /**
     * Renames a bundle entry key.
     * @param oldKey the bundle entry key to rename
     * @param newKey the new name for the bundle entry
     */
    protected void renameKey(String oldKey, String newKey) {
        BundleEntry oldEntry = (BundleEntry) entries.get(oldKey);
        if (oldEntry != null) {
            BundleEntry newEntry = new BundleEntry(
                    newKey, oldEntry.getValue(), oldEntry.getComment());
            removeEntry(oldEntry);
            addEntry(newEntry);
        }
    }

    /**
     * Comments a bundle entry.
     * @param key key of bundle entry to be commented
     */
    protected void commentKey(String key) {
        BundleEntry entry = (BundleEntry) entries.get(key);
        if (entry != null) {
            BundleEntry newEntry = new BundleEntry(
                    key, entry.getValue(), entry.getComment(), true, 0);
            addEntry(newEntry);
        }
    }
    /**
     * Uncomments a bundle entry.
     * @param key key of bundle entry to be uncommented
     */
    protected void uncommentKey(String key) {
        BundleEntry entry = (BundleEntry) entries.get(key);
        if (entry != null) {
            BundleEntry newEntry = new BundleEntry(
                    key, entry.getValue(), entry.getComment(), false, 0);
            addEntry(newEntry);
        }
    }
    
    /**
     * Copies a bundle entry under a different key.
     * @param origKey key of bundle entry to be copied
     * @param newKey key for the copied bundle entry
     */
    protected void copyKey(String origKey, String newKey) {
        BundleEntry origEntry = (BundleEntry) entries.get(origKey);
        if (origEntry != null) {
            BundleEntry newEntry = new BundleEntry(
                    newKey, origEntry.getValue(), origEntry.getComment());
            addEntry(newEntry);
        }
    }
    
    /**
     * Iterates through the <code>BundleEntry</code> objects in this bundle.
     * @return an iterator
     */
    public Iterator<BundleEntry> iterator() {
        return entries.values().iterator();
    }

    /**
     * Gets the bundle group (parent) associated with this bundle.
     * @return a bundle group
     */
    public BundleGroup getBundleGroup() {
        return bundleGroup;
    }
    /**
     * Sets the bundle group (parent) associated with this bundle.
     * @param bundleGroup a bundle group
     */
    protected void setBundleGroup(BundleGroup bundleGroup) {
        this.bundleGroup = bundleGroup;
    }
    
    /**
     * Gets sorted resource bundle keys for this bundle.
     * @return resource bundle keys
     */
    public Set<String> getKeys() {
        final Set<String> keys;
        if (RBEPreferences.getKeepOriginalKeyOrder()) {
        	keys = new LinkedHashSet<String>();
        } else {
        	keys = new TreeSet<String>();
        }
        keys.addAll(entries.keySet());
        return keys;
        //        return Collections.unmodifiableSet(keys);
    }

    /**
     * Copies values from given bundle into this bundle.
     * @param bundle bundle to copy to this bundle.
     */    
    protected void copyFrom(Bundle bundle) {
        setComment(bundle.getComment());
        // Remove deleted entries
        synchronized (entries) {
            List<BundleEntry> entriesToRemove = new ArrayList<>();
            for (Iterator<BundleEntry> iter = iterator(); iter.hasNext();) {
                BundleEntry localEntry = iter.next();
                if (bundle.getEntry(localEntry.getKey()) == null) {
                    entriesToRemove.add(localEntry);
                }
            }    
            for (Iterator<BundleEntry> iter = 
                    entriesToRemove.iterator(); iter.hasNext();) {
                BundleEntry entry = iter.next();
                removeEntry(entry);
            }        
        }
        
        // Add existing/new entries
        for (Iterator<BundleEntry> iter = bundle.iterator(); iter.hasNext();) {
            BundleEntry entry = iter.next();
            addEntry(entry);
        }
    }
}