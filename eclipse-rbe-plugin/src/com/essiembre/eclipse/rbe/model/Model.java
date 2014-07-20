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
package com.essiembre.eclipse.rbe.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.eclipse.core.internal.runtime.ListenerList; >= Eclipse 3.2
//import org.eclipse.core.runtime.ListenerList;           < Eclipse 3.2

/**
 * Base class for core model objects.
 * @author Pascal Essiembre
 */
public abstract class Model {

    /* The holder for listeners was changed from ListenerList to ArrayList
     * to support both Eclipse 3.1 and 3.2.  The ListenerList location changed
     * from 3.1 to 3.2 as described here:
     * https://bugs.eclipse.org/bugs/show_bug.cgi?format=multiple&id=94156
     */
    /** Listeners for this object. */
    private final List<IDeltaListener> listeners = new ArrayList<>();
    
    /**
     * Fires an "add" event.
     * @param added object added
     */
    protected void fireAdd(Object added) {
        for (Iterator<IDeltaListener> iter = 
                listeners.iterator(); iter.hasNext();) {
            IDeltaListener listener = iter.next();
            listener.add(new DeltaEvent(added));
        }
    }

    /**
     * Fires a "remove" event.
     * @param removed object removed
     */
    protected void fireRemove(Object removed) {
        for (Iterator<IDeltaListener> iter = 
                listeners.iterator(); iter.hasNext();) {
            IDeltaListener listener = iter.next();
            listener.remove(new DeltaEvent(removed));
        }
    }
    
    /**
     * Fires a "modify" event.
     * @param modified object modified
     */
    protected void fireModify(Object modified) {
        for (Iterator<IDeltaListener> iter = 
                listeners.iterator(); iter.hasNext();) {
            IDeltaListener listener = iter.next();
            listener.modify(new DeltaEvent(modified));
        }
    }
    
    /**
     * Fires a "select" event.
     * @param selected the selected object.
     */
    protected void fireSelect(Object selected) {
        for (Iterator<IDeltaListener> iter = 
                listeners.iterator(); iter.hasNext();) {
            IDeltaListener listener = iter.next();
            listener.select(new DeltaEvent(selected));
        }
    }

    /**
     * Adds a listener to this instance.
     * @param listener listener to add
     */
    public void addListener(IDeltaListener listener) {
        listeners.add(0, listener);
    }
    /**
     * Removes a listener from this instance.
     * @param listener listener to remove
     */
    public void removeListener(IDeltaListener listener) {
        listeners.remove(listener);
    }
}
