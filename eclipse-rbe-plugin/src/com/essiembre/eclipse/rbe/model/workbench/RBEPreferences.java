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
package com.essiembre.eclipse.rbe.model.workbench;

import org.eclipse.jface.preference.IPreferenceStore;

import com.essiembre.eclipse.rbe.RBEPlugin;

/**
 * Application preferences, relevant to the resource bundle editor plugin.
 * @author Pascal Essiembre
 */
public final class RBEPreferences {

    /** Key group separator. */
    public static final String KEY_GROUP_SEPARATOR =
            "keyGroupSeparator";

    /** Should key tree be hiearchical by default. */
    public static final String KEY_TREE_HIERARCHICAL =
            "keyTreeHierarchical";
    /** Should key tree be expanded by default. */
    public static final String KEY_TREE_EXPANDED =
            "keyTreeExpanded";

    /** Should "Generated by" line be added to files. */
    public static final String SHOW_GENERATOR = "showGenerator";

    /** Should Eclipse "nl" directory structure be supported. */
    public static final String SUPPORT_NL = "supportNL";

    /** Should resources also be loaded from fragments. */
    public static final String SUPPORT_FRAGMENTS = "supportFragments";
    /**
     * Load only fragment resources when loading from fragments.
     * The default bundle is mostly located in the host plug-in.
     */
    public static final String LOAD_ONLY_FRAGMENT_RESOURCES =
            "loadOnlyFragmentResources";

    /** Should tab characters be inserted when tab key pressed on text field. */
    public static final String FIELD_TAB_INSERTS =
            "fieldTabInserts";

    /** Should equal signs be aligned. */
    public static final String ALIGN_EQUAL_SIGNS =
            "alignEqualSigns";
    /** Should spaces be put around equal signs. */
    public static final String SPACES_AROUND_EQUAL_SIGNS =
            "spacesAroundEqualSigns";

    /** Should keys be grouped. */
    public static final String GROUP_KEYS = "groupKeys";
    /** How many level deep should keys be grouped. */
    public static final String GROUP_LEVEL_DEPTH =
            "groupLevelDeep";
    /** How many line breaks between key groups. */
    public static final String GROUP_LINE_BREAKS =
            "groupLineBreaks";
    /** Should equal signs be aligned within groups. */
    public static final String GROUP_ALIGN_EQUAL_SIGNS =
            "groupAlignEqualSigns";

    /** Should lines be wrapped. */
    public static final String WRAP_LINES = "wrapLines";
    /** Maximum number of character after which we should wrap. */
    public static final String WRAP_CHAR_LIMIT = "wrapCharLimit";
    /** Align subsequent lines with equal signs. */
    public static final String WRAP_ALIGN_EQUAL_SIGNS =
            "wrapAlignEqualSigns";
    /** Number of spaces to indent subsequent lines. */
    public static final String WRAP_INDENT_SPACES =
            "wrapIndentSpaces";

    /** Should unicode values be converted to their encoded equivalent. */
    public static final String CONVERT_UNICODE_TO_ENCODED =
            "convertUnicodeToEncoded";
    /** Should unicode values be converted to their encoded equivalent. */
    public static final String CONVERT_UNICODE_TO_ENCODED_UPPER =
            "convertUnicodeToEncodedUppercase";
    /** Should encoded values be converted to their unicode equivalent. */
    public static final String CONVERT_ENCODED_TO_UNICODE =
            "convertEncodedToUnicode";

    /** Impose a given new line type. */
    public static final String FORCE_NEW_LINE_TYPE =
            "forceNewLineType";
    /** How new lines are represented in resource bundle. */
    public static final String NEW_LINE_TYPE = "newLineType";
    /** Should new lines character produce a line break in properties files. */
    public static final String NEW_LINE_NICE = "newLineNice";

    /** New Line Type: UNIX. */
    public static final int NEW_LINE_UNIX = 0;
    /** New Line Type: Windows. */
    public static final int NEW_LINE_WIN = 1;
    /** New Line Type: Mac. */
    public static final int NEW_LINE_MAC = 2;

    /** Report missing values. */
    public static final String REPORT_MISSING_VALUES = "detectMissingValues";
    /** Report duplicate values. */
    public static final String REPORT_DUPL_VALUES = "reportDuplicateValues";
    /** Report similar values. */
    public static final String REPORT_SIM_VALUES = "reportSimilarValues";
    /** Report similar values: word compare. */
    public static final String REPORT_SIM_VALUES_WORD_COMPARE =
            "reportSimilarValuesWordCompare";
    /** Report similar values: levensthein distance. */
    public static final String REPORT_SIM_VALUES_LEVENSTHEIN =
            "reportSimilarValuesLevensthein";
    /** Report similar values: precision. */
    public static final String REPORT_SIM_VALUES_PRECISION =
            "reportSimilarValuesPrecision";

    /** Don't show the tree within the editor. */
    public static final String NO_TREE_IN_EDITOR = "noTreeInEditor";

    /** Keep empty fields. */
    public static final String KEEP_EMPTY_FIELDS = "keepEmptyFields";

    public static final String MIN_HEIGHT = "minHeight";
//    public static final String AUTO_ADJUST = "autoAdjust";


    public static final String PREFERENCE_ID = "eclipse-rbe-preferences";

    /** RBEPreferences. */
    private static final IPreferenceStore PREFS =
            RBEPlugin.getDefault().getPreferenceStore();

    /** API-Key for Google translate. */
    public static final String TRANSLATION_API_KEY = "translationApiKey";

    /** Target language to be used as default language */
    public static final String TRANSLATION_DEFAULT_LANG = "translationDefaultLang";

    /**
     * Constructor.
     */
    private RBEPreferences() {
        super();
    }


    //--- General --------------------------------------------------------------
    /**
     * Gets whether key tree should be displayed in hiearchical way by default.
     * @return <code>true</code> if hierarchical
     */
    public static boolean getKeyTreeHierarchical() {
        return PREFS.getBoolean(KEY_TREE_HIERARCHICAL);
    }
    /**
     * Gets whether key tree should be show expaned by default.
     * @return <code>true</code> if expanded
     */
    public static boolean getKeyTreeExpanded() {
        return PREFS.getBoolean(KEY_TREE_EXPANDED);
    }
    /**
     * Gets whether to support Eclipse NL directory structure.
     * @return <code>true</code> if supported
     */
    public static boolean getSupportNL() {
        return PREFS.getBoolean(SUPPORT_NL);
    }
    /**
     * Gets whether to support resources found in fragments.
     * @return <code>true</code> if supported
     */
    public static boolean getLoadOnlyFragmentResources() {
        return PREFS.getBoolean(LOAD_ONLY_FRAGMENT_RESOURCES);
    }
    /**
     * Gets whether to support resources found in fragments.
     * @return <code>true</code> if supported
     */
    public static boolean getSupportFragments() {
        return PREFS.getBoolean(SUPPORT_FRAGMENTS);
    }
    /**
     * Gets whether to convert encoded strings to unicode characters when
     * reading file.
     * @return <code>true</code> if converting
     */
    public static boolean getConvertEncodedToUnicode() {
        return PREFS.getBoolean(CONVERT_ENCODED_TO_UNICODE);
    }
    /**
     * Gets whether a tree shall be displayed within the editor or not.
     * @return <code>true</code> A tree shall not be displayed.
     */
    public static boolean getNoTreeInEditor() {
        return PREFS.getBoolean(NO_TREE_IN_EDITOR);
    }
//    public static boolean getAutoAdjust() {
//        return PREFS.getBoolean(AUTO_ADJUST);
//    }

    public static int getMinHeight() {
        return PREFS.getInt(MIN_HEIGHT);
    }


    //--- Formatting -----------------------------------------------------------

    /**
     * Gets key group separator.
     * @return key group separator.
     */
    public static String getKeyGroupSeparator() {
        return PREFS.getString(KEY_GROUP_SEPARATOR);
    }
    /**
     * Gets whether pressing tab inserts a tab in a field.
     * @return <code>true</code> if pressing tab inserts a tab in a field
     */
    public static boolean getFieldTabInserts() {
        return PREFS.getBoolean(FIELD_TAB_INSERTS);
    }
    /**
     * Gets whether equals signs should be aligned when generating file.
     * @return <code>true</code> if equals signs should be aligned
     */
    public static boolean getAlignEqualSigns() {
        return PREFS.getBoolean(ALIGN_EQUAL_SIGNS);
    }
    /**
     * Gets whether there should be spaces around equals signs when generating
     * file.
     * @return <code>true</code> there if should be spaces around equals signs
     */
    public static boolean getSpacesAroundEqualSigns() {
        return PREFS.getBoolean(SPACES_AROUND_EQUAL_SIGNS);
    }
    /**
     * Gets whether keys should be grouped when generating file.
     * @return <code>true</code> if keys should be grouped
     */
    public static boolean getGroupKeys() {
        return PREFS.getBoolean(GROUP_KEYS);
    }
    /**
     * Gets how many level deep keys should be grouped when generating file.
     * @return how many level deep
     */
    public static int getGroupLevelDepth() {
        return PREFS.getInt(GROUP_LEVEL_DEPTH);
    }
    /**
     * Gets how many blank lines should separate groups when generating file.
     * @return how many blank lines between groups
     */
    public static int getGroupLineBreaks() {
        return PREFS.getInt(GROUP_LINE_BREAKS);
    }
    /**
     * Gets whether equal signs should be aligned within each groups when
     * generating file.
     * @return <code>true</code> if equal signs should be aligned within groups
     */
    public static boolean getGroupAlignEqualSigns() {
        return PREFS.getBoolean(GROUP_ALIGN_EQUAL_SIGNS);
    }
    /**
     * Gets whether to print "Generated By..." comment when generating file.
     * @return <code>true</code> if we print it
     */
    public static boolean getShowGenerator() {
        return PREFS.getBoolean(SHOW_GENERATOR);
    }
    /**
     * Gets the number of character after which lines should be wrapped when
     * generating file.
     * @return number of characters
     */
    public static int getWrapCharLimit() {
        return PREFS.getInt(WRAP_CHAR_LIMIT);
    }
    /**
     * Gets the number of spaces to use for indentation of wrapped lines when
     * generating file.
     * @return number of spaces
     */
    public static int getWrapIndentSpaces() {
        return PREFS.getInt(WRAP_INDENT_SPACES);
    }

    /**
     * Gets whether lines should be wrapped if too big when generating file.
     * @return <code>true</code> if wrapped
     */
    public static boolean getWrapLines() {
        return PREFS.getBoolean(WRAP_LINES);
    }
    /**
     * Gets whether wrapped lines should be aligned with equal sign when
     * generating file.
     * @return <code>true</code> if aligned
     */
    public static boolean getWrapAlignEqualSigns() {
        return PREFS.getBoolean(WRAP_ALIGN_EQUAL_SIGNS);
    }
    /**
     * Gets whether to escape unicode characters when generating file.
     * @return <code>true</code> if escaping
     */
    public static boolean getConvertUnicodeToEncoded() {
        return PREFS.getBoolean(CONVERT_UNICODE_TO_ENCODED);
    }
    /**
     * Gets whether escaped unicode "alpha" characters should be uppercase
     * when generating file.
     * @return <code>true</code> if uppercase
     */
    public static boolean getConvertUnicodeToEncodedUpper() {
        return PREFS.getBoolean(CONVERT_UNICODE_TO_ENCODED_UPPER);
    }
    /**
     * Gets the new line type to use when overwriting system (or Eclipse)
     * default new line type when generating file.  Use constants to this
     * effect.
     * @return new line type
     */
    public static int getNewLineType() {
        return PREFS.getInt(NEW_LINE_TYPE);
    }
    /**
     * Gets whether new lines are escaped or printed as is when generating file.
     * @return <code>true</code> if printed as is.
     */
    public static boolean getNewLineNice() {
        return PREFS.getBoolean(NEW_LINE_NICE);
    }
    /**
     * Gets whether to keep empty fields.
     * @return <code>true</code> if empty fields are to be kept.
     */
    public static boolean getKeepEmptyFields() {
        return PREFS.getBoolean(KEEP_EMPTY_FIELDS);
    }

    /**
     * Gets whether we want to overwrite system (or Eclipse) default new line
     * type when generating file.
     * @return <code>true</code> if overwriting
     */
    public static boolean getForceNewLineType() {
        return PREFS.getBoolean(FORCE_NEW_LINE_TYPE);
    }


    //--- Reporting/Performance ------------------------------------------------

    /**
     * Gets whether to report keys with missing values.
     * @return <code>true</code> if reporting
     */
    public static boolean getReportMissingValues() {
        return PREFS.getBoolean(REPORT_MISSING_VALUES);
    }
    /**
     * Gets whether to report keys with duplicate values.
     * @return <code>true</code> if reporting
     */
    public static boolean getReportDuplicateValues() {
        return PREFS.getBoolean(REPORT_DUPL_VALUES);
    }
    /**
     * Gets whether to report keys with similar values.
     * @return <code>true</code> if reporting
     */
    public static boolean getReportSimilarValues() {
        return PREFS.getBoolean(REPORT_SIM_VALUES);
    }
    /**
     * Gets whether to use the "word compare" method when reporting similar
     * values.
     * @return <code>true</code> if using "word compare" method
     */
    public static boolean getReportSimilarValuesWordCompare() {
        return PREFS.getBoolean(REPORT_SIM_VALUES_WORD_COMPARE);
    }
    /**
     * Gets whether to use the Levensthein method when reporting similar
     * values.
     * @return <code>true</code> if using Levensthein method
     */
    public static boolean getReportSimilarValuesLevensthein() {
        return PREFS.getBoolean(REPORT_SIM_VALUES_LEVENSTHEIN);
    }
    /**
     * Gets the minimum precision level to use for determining when to report
     * similarities.
     * @return precision
     */
    public static double getReportSimilarValuesPrecision() {
        return PREFS.getDouble(REPORT_SIM_VALUES_PRECISION);
    }

    /**
     * Returns the translation API key that is used to log in to google cloud translation service.
     * @return the api key.
     */
    public static String getTranslationApiKey() {
        return PREFS.getString(TRANSLATION_API_KEY);
    }

    /**
     * Returns the language that should be used to fill the default language field.
     * @return the language.
     */
    public static String getTranslationDefaultLanguage() {
        return PREFS.getString(TRANSLATION_DEFAULT_LANG);
    }
}
