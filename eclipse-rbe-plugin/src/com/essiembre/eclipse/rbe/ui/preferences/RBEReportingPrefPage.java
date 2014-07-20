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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
public class RBEReportingPrefPage extends AbstractRBEPrefPage {
    
    /* Preference fields. */
    private Button reportMissingVals;
    private Button reportDuplVals;
    private Button reportSimVals;
    private Text reportSimPrecision;
    private Button[] reportSimValsMode = new Button[2];

    /**
     * Constructor.
     */
    public RBEReportingPrefPage() {
        super();
    }

    @Override
    protected Control createContents(Composite parent) {
        IPreferenceStore prefs = getPreferenceStore();
        Composite field = null;
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        
        new Label(composite, SWT.NONE).setText(
                RBEPlugin.getString("prefs.perform.intro1"));
        new Label(composite, SWT.NONE).setText(
                RBEPlugin.getString("prefs.perform.intro2"));
        new Label(composite, SWT.NONE).setText(" ");
        
        // Report missing values?
        field = createFieldComposite(composite);
        reportMissingVals = new Button(field, SWT.CHECK);
        reportMissingVals.setSelection(
                prefs.getBoolean(RBEPreferences.REPORT_MISSING_VALUES));
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.perform.missingVals"));

        // Report duplicate values?
        field = createFieldComposite(composite);
        reportDuplVals = new Button(field, SWT.CHECK);
        reportDuplVals.setSelection(
                prefs.getBoolean(RBEPreferences.REPORT_DUPL_VALUES));
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.perform.duplVals"));
        
        // Report similar values?
        field = createFieldComposite(composite);
        reportSimVals = new Button(field, SWT.CHECK);
        reportSimVals.setSelection(
                prefs.getBoolean(RBEPreferences.REPORT_SIM_VALUES));
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.perform.simVals"));
        reportSimVals.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                refreshEnabledStatuses();
            }
        });
        
        Composite simValModeGroup = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = indentPixels;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        simValModeGroup.setLayout(gridLayout);
        
        // Report similar values: word count
        reportSimValsMode[0] = new Button(simValModeGroup, SWT.RADIO);
        reportSimValsMode[0].setSelection(prefs.getBoolean(
                RBEPreferences.REPORT_SIM_VALUES_WORD_COMPARE));
        new Label(simValModeGroup, SWT.NONE).setText(RBEPlugin.getString(
                "prefs.perform.simVals.wordCount"));
        
        // Report similar values: Levensthein
        reportSimValsMode[1] = new Button(simValModeGroup, SWT.RADIO);
        reportSimValsMode[1].setSelection(prefs.getBoolean(
                RBEPreferences.REPORT_SIM_VALUES_LEVENSTHEIN));
        new Label(simValModeGroup, SWT.NONE).setText(RBEPlugin.getString(
                "prefs.perform.simVals.levensthein"));
        
        // Report similar values: precision level
        field = createFieldComposite(composite, indentPixels);
        new Label(field, SWT.NONE).setText(RBEPlugin.getString(
                "prefs.perform.simVals.precision"));
        reportSimPrecision = new Text(field, SWT.BORDER);
        reportSimPrecision.setText(
                prefs.getString(RBEPreferences.REPORT_SIM_VALUES_PRECISION));
        reportSimPrecision.setTextLimit(6);
        setWidthInChars(reportSimPrecision, 6);
        reportSimPrecision.addKeyListener(new DoubleTextValidatorKeyListener(
                RBEPlugin.getString(
                        "prefs.perform.simVals.precision.error"),
                0, 1));
        
        refreshEnabledStatuses();
        
        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore prefs = getPreferenceStore();
        prefs.setValue(RBEPreferences.REPORT_MISSING_VALUES,
                reportMissingVals.getSelection());
        prefs.setValue(RBEPreferences.REPORT_DUPL_VALUES,
                reportDuplVals.getSelection());
        prefs.setValue(RBEPreferences.REPORT_SIM_VALUES,
                reportSimVals.getSelection());
        prefs.setValue(RBEPreferences.REPORT_SIM_VALUES_WORD_COMPARE,
                reportSimValsMode[0].getSelection());
        prefs.setValue(RBEPreferences.REPORT_SIM_VALUES_LEVENSTHEIN,
                reportSimValsMode[1].getSelection());
        prefs.setValue(RBEPreferences.REPORT_SIM_VALUES_PRECISION,
                Double.parseDouble(reportSimPrecision.getText()));
        refreshEnabledStatuses();
        return super.performOk();
    }
    
    
    @Override
    protected void performDefaults() {
        IPreferenceStore prefs = getPreferenceStore();
        reportMissingVals.setSelection(prefs.getDefaultBoolean(
                RBEPreferences.REPORT_MISSING_VALUES));
        reportDuplVals.setSelection(prefs.getDefaultBoolean(
                RBEPreferences.REPORT_DUPL_VALUES));
        reportSimVals.setSelection(prefs.getDefaultBoolean(
                RBEPreferences.REPORT_SIM_VALUES));
        reportSimValsMode[0].setSelection(prefs.getDefaultBoolean(
                RBEPreferences.REPORT_SIM_VALUES_WORD_COMPARE));
        reportSimValsMode[1].setSelection(prefs.getDefaultBoolean(
                RBEPreferences.REPORT_SIM_VALUES_LEVENSTHEIN));
        reportSimPrecision.setText(Double.toString(prefs.getDefaultDouble(
                RBEPreferences.REPORT_SIM_VALUES_PRECISION)));
        refreshEnabledStatuses();
        super.performDefaults();
    }

    /*default*/ void refreshEnabledStatuses() {
        boolean isReportingSimilar = reportSimVals.getSelection();

        for (int i = 0; i < reportSimValsMode.length; i++) {
            reportSimValsMode[i].setEnabled(isReportingSimilar);
        }
        reportSimPrecision.setEnabled(isReportingSimilar);
    }
    
}
