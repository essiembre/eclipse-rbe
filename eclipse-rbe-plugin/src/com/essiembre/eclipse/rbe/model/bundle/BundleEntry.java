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

import java.util.Locale;

/**
 * Represents an entry in a properties file.
 * @author Pascal Essiembre
 */
public final class BundleEntry implements IBundleVisitable {

    /** Entry Locale. */
    private Locale locale;
    /** Entry unique identifier. */
    private String key;
    /** Entry comment. */
    private String comment;
    /** Whehter this entry is commented out or not. */
    private boolean commented;
    /** Entry value. */
    private String value;
    /** Associated bundle (parent). */
    private Bundle bundle;

    /**
     * Constructor.  Keys and value are <code>null</code> safe.
     * @param key unique identifier within bundle
     * @param value entry value
     * @param comment entry comment
     * @param commented if this whole entry is considered commented out
     */
    public BundleEntry(
            String key, String value, String comment, boolean commented) {
        super();
        this.key = key;
        this.value = value;
        this.comment = comment;
        if (key == null) {
            this.key = "";
        }
        if (value == null) {
            this.value = "";
        }
        this.commented = commented;
    }

    
    /**
     * Constructor.  Keys and value are <code>null</code> safe.
     * @param key unique identifier within bundle
     * @param value entry value
     * @param comment entry comment
     */
    public BundleEntry(String key, String value, String comment) {
        this(key, value, comment, false);
    }

    /**
     * @see IBundleVisitable#accept(IBundleVisitor, Object)
     */
    public void accept(IBundleVisitor visitor, Object passAlongArgument) {
        visitor.visitBundleEntry(this, passAlongArgument);
        visitor.visitBundle(bundle, passAlongArgument);
        if (bundle != null) {
            visitor.visitBundleGroup(
                    bundle.getBundleGroup(), passAlongArgument);
        }
    }

    /**
     * Gets the "comment" attribute.
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }
    /**
     * Gets the "key" attribute.
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }
        
    /**
     * Gets the "value" attribute.
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the "commented" attribute.
     * @return <code>true</code> if this entry is commented out.
     */
    public boolean isCommented() {
        return commented;
    }

    /**
     * Gets associated bundle (parent).
     * @return parent bundle
     */
    public Bundle getBundle() {
        return bundle;
    }
    /**
     * Sets associated bundle (parent).
     * @param bundle the parent bundle
     */
    protected void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    
    /**
     * Gets associated locale.
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }
    /**
     * Gets associated locale.
     * @param locale the entry locale
     */
    protected void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof BundleEntry)) {
            return false;
        }
        BundleEntry entry = (BundleEntry) obj;
        return key.equals(entry.getKey())
                && commented == entry.isCommented()
                && value.equals(entry.getValue())
                && (comment == null && entry.getComment() == null
                        || comment != null && comment.equals(
                                entry.getComment()));
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString() 
                + "[[key=" + key
                + "][value=" + value
                + "][comment=" + comment
                + "][commented=" + commented
                + "][locale=" + locale + "]]";
    }
}
