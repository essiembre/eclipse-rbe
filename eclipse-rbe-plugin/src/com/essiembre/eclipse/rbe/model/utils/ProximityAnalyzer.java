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

/**
 * Analyze the proximity of two objects (i.e., how similar they are) and return
 * a proximity level between zero and one.  The higher the return value is, 
 * the closer the two objects are to each other.  "One" does not need to mean 
 * "identical", but it has to be the closest match and analyser can 
 * potentially acheive.
 * @author Pascal Essiembre
 */
public interface ProximityAnalyzer {
    /**
     * Analyzes two objects and return the proximity level.
     * @param obj1 first object to analyze
     * @param obj2 second object to analyze
     * @return proximity level
     */
    double analyse(Object obj1, Object obj2);
}
