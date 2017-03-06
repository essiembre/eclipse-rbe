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
package com.essiembre.eclipse.rbe.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceFilter;

/**
 * Initializes default preferences.
 * 
 * @author Pascal Essiembre
 * @author cuhiodtick
 * @author wolfgang-ch
 */
public class RBEPreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * Constructor.
     */
    public RBEPreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefs = RBEPlugin.getDefault().getPreferenceStore();

        // General
        prefs.setDefault(RBEPreferences.CONVERT_ENCODED_TO_UNICODE, true);
        prefs.setDefault(RBEPreferences.FIELD_TAB_INSERTS, false);
        prefs.setDefault(RBEPreferences.KEY_TREE_HIERARCHICAL, true);
        prefs.setDefault(RBEPreferences.KEY_TREE_EXPANDED, true);
        prefs.setDefault(RBEPreferences.SUPPORT_FRAGMENTS, true);
        prefs.setDefault(RBEPreferences.LOAD_ONLY_FRAGMENT_RESOURCES, false);

        //locales filter: by default: don't filter locales.
        prefs.setDefault(RBEPreferences.FILTER_LOCALES_STRING_MATCHERS, "*"); //$NON-NLS-1$
        prefs.addPropertyChangeListener(ResourceFilter.getInstance());

        // Formatting
        prefs.setDefault(RBEPreferences.CONVERT_UNICODE_TO_ENCODED, true);
        prefs.setDefault(RBEPreferences.CONVERT_UNICODE_TO_ENCODED_UPPER, true);

        prefs.setDefault(RBEPreferences.SPACES_AROUND_EQUAL_SIGNS, true);

        prefs.setDefault(RBEPreferences.KEY_GROUP_SEPARATOR, ".");
        prefs.setDefault(RBEPreferences.ALIGN_EQUAL_SIGNS, true);
        prefs.setDefault(RBEPreferences.SHOW_GENERATOR, true);
        prefs.setDefault(RBEPreferences.KEY_TREE_HIERARCHICAL, true);

        prefs.setDefault(RBEPreferences.GROUP_KEYS, true);
        prefs.setDefault(RBEPreferences.GROUP_LEVEL_DEPTH, 1);
        prefs.setDefault(RBEPreferences.GROUP_LINE_BREAKS, 1);
        prefs.setDefault(RBEPreferences.GROUP_ALIGN_EQUAL_SIGNS, true);

        prefs.setDefault(RBEPreferences.WRAP_CHAR_LIMIT, 80);
        prefs.setDefault(RBEPreferences.WRAP_INDENT_SPACES, 8);

        prefs.setDefault(RBEPreferences.NEW_LINE_TYPE,
                RBEPreferences.NEW_LINE_UNIX);

        prefs.setDefault(RBEPreferences.KEEP_EMPTY_FIELDS, false);

        // Reporting/Performance
        prefs.setDefault(RBEPreferences.REPORT_MISSING_VALUES, true);
        prefs.setDefault(RBEPreferences.REPORT_DUPL_VALUES, true);
        prefs.setDefault(RBEPreferences.REPORT_SIM_VALUES_WORD_COMPARE, true);
        prefs.setDefault(RBEPreferences.REPORT_SIM_VALUES_PRECISION, 0.75d);

        prefs.setDefault(RBEPreferences.NO_TREE_IN_EDITOR, false);

        prefs.setDefault(RBEPreferences.MIN_HEIGHT, 80);
//        prefs.setDefault(RBEPreferences.AUTO_ADJUST, true);

    }

}
