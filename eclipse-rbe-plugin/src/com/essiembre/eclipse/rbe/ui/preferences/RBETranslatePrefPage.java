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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * Plugin preference page for reporting/performance options.
 * @author Pascal Essiembre
 */
public class RBETranslatePrefPage extends AbstractRBEPrefPage {

    private Text apiKey;
    private Text defaultLang;

    /**
     * Constructor.
     */
    public RBETranslatePrefPage() {
        super();
    }

    @Override
    protected Control createContents(Composite parent) {
        IPreferenceStore prefs = getPreferenceStore();
        Composite field = null;
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        new Label(composite, SWT.NONE).setText(
                RBEPlugin.getString("prefs.translate.intro"));
        new Label(composite, SWT.NONE).setText(" ");

        // Report missing values?
        field = createFieldComposite(composite);
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.translate.apiKey"));
        apiKey = new Text(field, SWT.BORDER);
        apiKey.setText(prefs.getString(RBEPreferences.TRANSLATION_API_KEY));
        setWidthInChars(apiKey, 40);
        field = createFieldComposite(composite);
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.translate.defaultLang"));
        defaultLang = new Text(field, SWT.BORDER);
        defaultLang.setText(prefs.getString(RBEPreferences.TRANSLATION_DEFAULT_LANG));
        defaultLang.setTextLimit(2);
        setWidthInChars(defaultLang, 2);
        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore prefs = getPreferenceStore();
        prefs.setValue(RBEPreferences.TRANSLATION_API_KEY, apiKey.getText());
        prefs.setValue(RBEPreferences.TRANSLATION_DEFAULT_LANG, defaultLang.getText());
        return super.performOk();
    }


    @Override
    protected void performDefaults() {
        IPreferenceStore prefs = getPreferenceStore();
        apiKey.setText(prefs.getDefaultString(RBEPreferences.TRANSLATION_API_KEY));
        defaultLang.setText(prefs.getString(RBEPreferences.TRANSLATION_DEFAULT_LANG));
        super.performDefaults();
    }

}
