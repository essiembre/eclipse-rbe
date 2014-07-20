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
package com.essiembre.eclipse.rbe.model.tree;

/**
 * Objects implementing this interface can act as a visitor to any 
 * key tree-related resource implementing <code>IKeyTreeVisitable</code>.
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public interface IKeyTreeVisitor {
    /**
     * Visits a key tree.
     * @param keyTree key tree to visit
     * @param passAlongArgument an optional argument
     */
    public void visitKeyTree(KeyTree keyTree, Object passAlongArgument);
    /**
     * Visits a key tree item.
     * @param item key tree item to visit
     * @param passAlongArgument an optional argument
     */
    public void visitKeyTreeItem(KeyTreeItem item, Object passAlongArgument);
}
