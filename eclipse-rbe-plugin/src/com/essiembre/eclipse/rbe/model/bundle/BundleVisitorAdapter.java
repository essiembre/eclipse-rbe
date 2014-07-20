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
 * Convenience implementation of <code>IBundleVisitor</code> allowing to 
 * override only required methods.
 * @author Pascal Essiembre
 */
public class BundleVisitorAdapter implements IBundleVisitor {

    /**
     * Constructor.
     */
    public BundleVisitorAdapter() {
        super();
    }

    @Override
    public void visitBundleGroup(BundleGroup group, Object passAlongArgument) {
    }

    @Override
    public void visitBundle(Bundle bundle, Object passAlongArgument) {
    }

    @Override
    public void visitBundleEntry(BundleEntry entry, Object passAlongArgument) {
    }
}
