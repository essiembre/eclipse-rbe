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
package com.essiembre.eclipse.rbe.model.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Compares two strings (case insensitive) and returns a proximity level
 * based on how many words there are, and how many words are the same 
 * in both strings.  Non-string objects are converted to strings using
 * the <code>toString()</code> method.
 * @author Pascal Essiembre
 */
public class WordCountAnalyzer implements ProximityAnalyzer {

    private static final ProximityAnalyzer INSTANCE = new WordCountAnalyzer();
    private static final String WORD_SPLIT_PATTERN = "\r\n|\r|\n|\\s";

    /**
     * Constructor.
     */
    private WordCountAnalyzer() {
        //TODO add case sensitivity?
        super();
    }

    /**
     * Gets the unique instance.
     * @return a proximity analyzer
     */
    public static ProximityAnalyzer getInstance() {
        return INSTANCE;
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.utils.ProximityAnalyzer
     *         #analyse(java.lang.Object, java.lang.Object)
     */
    public double analyse(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) {
            return 0;
        }
        
        Collection<String> str1 = new ArrayList<String>(
                Arrays.asList(obj1.toString().split(WORD_SPLIT_PATTERN)));
        Collection<String> str2 = new ArrayList<String>(
                Arrays.asList(obj2.toString().split(WORD_SPLIT_PATTERN)));
        
        int maxWords = Math.max(str1.size(), str2.size());
        if (maxWords == 0) {
            return 0;
        }
        
        int matchedWords = 0;
        for (Iterator<String> iter = str1.iterator(); iter.hasNext();) {
            String str = (String) iter.next();
            if (str2.remove(str)) {
                matchedWords++;
            }
        }

        return (double) matchedWords / (double) maxWords;
    }

}
