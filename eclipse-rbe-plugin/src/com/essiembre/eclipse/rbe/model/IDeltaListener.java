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

/**
 * Generic listener for additions, removals, modifications and selections.
 * @author Pascal Essiembre
 */
public interface IDeltaListener {
    /**
     * Adds an "add" event to this listener.
     * @param event "add" event
     */
    public void add(DeltaEvent event);
    /**
     * Adds an "remove" event to this listener.
     * @param event "remove" event
     */
    public void remove(DeltaEvent event);
    /**
     * Adds an "modify" event to this listener.
     * @param event "modify" event
     */
    public void modify(DeltaEvent event);
    /**
     * Adds a "select" event to this listener.
     * @param event "select" event
     */
    public void select(DeltaEvent event);
}
