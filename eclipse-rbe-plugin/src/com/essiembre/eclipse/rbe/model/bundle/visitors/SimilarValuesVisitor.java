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
package com.essiembre.eclipse.rbe.model.bundle.visitors;

import java.util.ArrayList;
import java.util.Collection;

import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleVisitorAdapter;
import com.essiembre.eclipse.rbe.model.utils.ProximityAnalyzer;
import com.essiembre.eclipse.rbe.model.utils.WordCountAnalyzer;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * Finds bundle entries having values similar (case insensitive) to the bundle 
 * entry given as the pass-along argument.  If no proximity analyzer is set,
 * <code>WordCountAnalyser</code> is used.
 * @author Pascal Essiembre
 */
public class SimilarValuesVisitor extends BundleVisitorAdapter {

    /** Holder for bundle entries having similar values. */
    private final Collection<BundleEntry> similars = new ArrayList<>();

    /** Proximity analyzer used to find similarities. */
    private ProximityAnalyzer analyzer = WordCountAnalyzer.getInstance();
    
    /**
     * Constructor.
     */
    public SimilarValuesVisitor() {
        super();
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.bundle.IBundleVisitor
     *         #visitBundleEntry(
     *                 com.essiembre.eclipse.rbe.model.bundle.BundleEntry,
     *                 java.lang.Object)
     */
    public void visitBundleEntry(BundleEntry entry, Object passAlongArgument) {
        
        BundleEntry entryToMatch = (BundleEntry) passAlongArgument;
        if (entry != entryToMatch
                && entry != null && entryToMatch != null
                && entry.getValue().length() > 0
                && analyzer.analyse(
                        entry.getValue().toLowerCase(), 
                        entryToMatch.getValue().toLowerCase())
                        >= RBEPreferences.getReportSimilarValuesPrecision()) {
            similars.add(entry);
        }
    }
    
    
    
    /**
     * Gets the proximity analyzer.
     * @return Returns the proximity analyzer.
     */
    public ProximityAnalyzer getProximityAnalyzer() {
        return analyzer;
    }
    /**
     * Sets the proximity analyzer.
     * @param analyzer proximity analyzer
     */
    public void setProximityAnalyzer(ProximityAnalyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    /**
     * Gets a collection of similar <code>BundleEntry</code> instance.
     * @return bundle entries with similar values
     */
    public Collection<BundleEntry> getSimilars() {
        return similars;
    }
    
    /**
     * Clears the list of duplicate values.
     */
    public void clear() {
        similars.clear();
    }
    
}