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

/**
 * Objects implementing this interface can act as a visitor to any 
 * bundle-related resource implementing <code>IBundleVisitable</code>.
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public interface IBundleVisitor {
    /**
     * Visits a bundle group.
     * @param group bundle group
     * @param passAlongArgument an optional argument
     */
    public void visitBundleGroup(BundleGroup group, Object passAlongArgument);
    /**
     * Visits a bundle.
     * @param bundle bundle
     * @param passAlongArgument an optional argument
     */
    public void visitBundle(Bundle bundle, Object passAlongArgument);
    /**
     * Visits a bundle entry.
     * @param entry bundle entry
     * @param passAlongArgument an optional argument
     */
    public void visitBundleEntry(BundleEntry entry, Object passAlongArgument);
}
