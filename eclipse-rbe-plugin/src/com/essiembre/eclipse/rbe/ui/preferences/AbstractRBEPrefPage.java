/*
 * Copyright (C) 2003, 2004  Pascal Essiembre
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package com.essiembre.eclipse.rbe.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;

/**
 * Plugin preference page.
 * @author Pascal Essiembre
 */
public abstract class AbstractRBEPrefPage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /** Number of pixels per field indentation  */
    protected final int indentPixels = 20;
    
    /** Controls with errors in them. */
    protected final Map<Text, String> errors = new HashMap<>();
    
    /**
     * Constructor.
     */
    public AbstractRBEPrefPage() {
        super();
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(
                RBEPlugin.getDefault().getPreferenceStore());
    }

    protected Composite createFieldComposite(Composite parent) {
        return createFieldComposite(parent, 0);
    }
    protected Composite createFieldComposite(Composite parent, int indent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = indent;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        composite.setLayout(gridLayout);
        return composite;
    }

    protected class IntTextValidatorKeyListener extends KeyAdapter {
        
        private String errMsg = null;
        
        /**
         * Constructor.
         * @param errMsg error message
         */
        public IntTextValidatorKeyListener(String errMsg) {
            super();
            this.errMsg = errMsg;
        }
        @Override
        public void keyReleased(KeyEvent event) {
            Text text = (Text) event.widget;
            String value = text.getText(); 
            event.doit = value.matches("^\\d*$");
            if (event.doit) {
                errors.remove(text);
                if (errors.isEmpty()) {
                    setErrorMessage(null);
                    setValid(true);
                } else {
                    setErrorMessage(
                            (String) errors.values().iterator().next());
                }
            } else {
                errors.put(text, errMsg);
                setErrorMessage(errMsg);
                setValid(false);
            }
        }
    }

    protected class DoubleTextValidatorKeyListener extends KeyAdapter {
        
        private String errMsg;
        private double minValue;
        private double maxValue;
        
        /**
         * Constructor.
         * @param errMsg error message
         */
        public DoubleTextValidatorKeyListener(String errMsg) {
            super();
            this.errMsg = errMsg;
        }
        /**
         * Constructor.
         * @param errMsg error message
         * @param minValue minimum value (inclusive)
         * @param maxValue maximum value (inclusive)
         */
        public DoubleTextValidatorKeyListener(
                String errMsg, double minValue, double maxValue) {
            super();
            this.errMsg = errMsg;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
        
        @Override
        public void keyReleased(KeyEvent event) {
            Text text = (Text) event.widget;
            String value = text.getText(); 
            boolean valid = value.length() > 0;
            if (valid) {
                valid = value.matches("^\\d*\\.?\\d*$");
            }
            if (valid && minValue != maxValue) {
                double doubleValue = Double.parseDouble(value);
                valid = doubleValue >= minValue && doubleValue <= maxValue;
            }
            event.doit = valid;
            if (event.doit) {
                errors.remove(text);
                if (errors.isEmpty()) {
                    setErrorMessage(null);
                    setValid(true);
                } else {
                    setErrorMessage(
                            (String) errors.values().iterator().next());
                }
            } else {
                errors.put(text, errMsg);
                setErrorMessage(errMsg);
                setValid(false);
            }
        }
    }
    
    protected void setWidthInChars(Control field, int widthInChars) {
        GridData gd = new GridData();
        gd.widthHint = UIUtils.getWidthInChars(field, widthInChars);
        field.setLayoutData(gd);
    }
}
