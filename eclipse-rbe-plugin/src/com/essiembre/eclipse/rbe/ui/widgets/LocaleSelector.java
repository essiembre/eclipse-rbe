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
package com.essiembre.eclipse.rbe.ui.widgets;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;

/**
 * Composite for dynamically selecting a locale from a list of available
 * locales.
 * @author Pascal Essiembre
 */
public class LocaleSelector extends Composite {

    private static final String DEFAULT_LOCALE = 
            "[" + RBEPlugin.getString("editor.default") + "]";
    
    /*default*/ Locale[] availableLocales;

    /*default*/ Combo localesCombo;
    /*default*/ Text langText;
    /*default*/ Text countryText;
    /*default*/ Text variantText;

    
    /**
     * Constructor.
     * @param parent parent composite
     */
    public LocaleSelector(Composite parent) {
        super(parent, SWT.NONE);

        // Init available locales
        availableLocales = Locale.getAvailableLocales();
        Arrays.sort(availableLocales, new Comparator<Locale>() {
            public int compare(Locale locale1, Locale locale2) {
                return Collator.getInstance().compare(
                        locale1.getDisplayName(),
                        locale2.getDisplayName());
            }
        });
        
        // This layout
        GridLayout layout = new GridLayout();
        setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 20;
        
        // Group settings
        Group selectionGroup = new Group(this, SWT.NULL);
        layout = new GridLayout(3, false);
        selectionGroup.setLayout(layout);
        selectionGroup.setText(RBEPlugin.getString(
                "selector.title"));
        
        // Set locales drop-down
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        localesCombo = new Combo(selectionGroup, SWT.READ_ONLY);
        localesCombo.setLayoutData(gd);
        localesCombo.add(DEFAULT_LOCALE);
        for (int i = 0; i < availableLocales.length; i++) {
            localesCombo.add(availableLocales[i].getDisplayName());
        }
        localesCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = localesCombo.getSelectionIndex();
                if (index == 0) { // default
                    langText.setText("");
                    countryText.setText("");
                } else {
                    Locale locale = availableLocales[index -1];
                    langText.setText(locale.getLanguage());
                    countryText.setText(locale.getCountry());
                }
                variantText.setText("");
            }
        });

        // Language field
        gd = new GridData();
        langText = new Text(selectionGroup, SWT.BORDER);
        langText.setTextLimit(3);
        gd.widthHint = UIUtils.getWidthInChars(langText, 4);
        langText.setLayoutData(gd);
        langText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                langText.setText(langText.getText().toLowerCase());
                setLocaleOnlocalesCombo();
            }
        });

        // Country field
        gd = new GridData();
        countryText = new Text(selectionGroup, SWT.BORDER);
        countryText.setTextLimit(2);
        gd.widthHint = UIUtils.getWidthInChars(countryText, 4);
        countryText.setLayoutData(gd);
        countryText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                countryText.setText(
                        countryText.getText().toUpperCase());
                setLocaleOnlocalesCombo();
            }
        });

        // Variant field
        gd = new GridData();
        variantText = new Text(selectionGroup, SWT.BORDER);
        gd.widthHint = UIUtils.getWidthInChars(variantText, 4);
        variantText.setLayoutData(gd);
        variantText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                setLocaleOnlocalesCombo();
            }
        });
        
        // Labels
        gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        Label lblLang = new Label(selectionGroup, SWT.NULL);
        lblLang.setText(RBEPlugin.getString("selector.language"));
        lblLang.setLayoutData(gd);

        gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        Label lblCountry = new Label(selectionGroup, SWT.NULL);
        lblCountry.setText(RBEPlugin.getString(
                "selector.country"));
        lblCountry.setLayoutData(gd);

        gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        Label lblVariant = new Label(selectionGroup, SWT.NULL);
        lblVariant.setText(RBEPlugin.getString(
                "selector.variant"));
        lblVariant.setLayoutData(gd);
    }

    /**
     * Gets the selected locale.  Default locale is represented by a 
     * <code>null</code> value.
     * @return selected locale
     */
    public Locale getSelectedLocale() {
        String lang = langText.getText().trim();
        String country = countryText.getText().trim();
        String variant = variantText.getText().trim();
        
        if (lang.length() > 0 && country.length() > 0 && variant.length() > 0) {
            return new Locale(lang, country, variant);
        } else if (lang.length() > 0 && country.length() > 0) {
            return new Locale(lang, country);
        } else if (lang.length() > 0) {
            return new Locale(lang);
        } else {
            return null;
        }
    }

    /**
     * Sets an available locale on the available locales combo box.
     */
    /*default*/ void setLocaleOnlocalesCombo() {
        Locale locale = new Locale(
                langText.getText(),
                countryText.getText(),
                variantText.getText());
        int index = -1;
        for (int i = 0; i < availableLocales.length; i++) {
            Locale availLocale = availableLocales[i];
            if (availLocale.equals(locale)) {
                index = i + 1;
            }
        }
        if (index >= 1) {
            localesCombo.select(index);
        } else {
            localesCombo.clearSelection();
        }
    }
    
    /**
     * Adds a modify listener.
     * @param listener modify listener
     */
    public void addModifyListener(final ModifyListener listener) {
        langText.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                listener.modifyText(e);
            }
        });
        countryText.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                listener.modifyText(e);
            }
        });
        variantText.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                listener.modifyText(e);
            }
        });
    }
}
