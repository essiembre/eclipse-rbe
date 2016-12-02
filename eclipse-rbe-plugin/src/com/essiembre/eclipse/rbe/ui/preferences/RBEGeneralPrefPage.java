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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;


/**
 * Plugin generic preference page.
 * @author Pascal Essiembre
 * @author cuhiodtick
 */
public class RBEGeneralPrefPage extends AbstractRBEPrefPage {

   /* Preference fields. */
   private Text   keyGroupSeparator;

   private Button convertEncodedToUnicode;

   private Button supportNL;
   private Button supportFragments;
   private Button loadOnlyFragmentResources;

   private Button keyTreeHierarchical;
   private Button keyTreeExpanded;

   private Button fieldTabInserts;

   private Button noTreeInEditor;

//   private Button autoAdjust;
   private Text   _minHeight;

//   private Font   _boldFont;

   private Button alphaNumericSortForKeys;
   private Button ignoreCaseForKeys;


   /**
    * Constructor.
    */
   public RBEGeneralPrefPage() {
      super();
   }

   @Override
   public void dispose() {
      super.dispose();
//      _boldFont.dispose();
   }

   @Override
   public boolean performOk() {
      IPreferenceStore prefs = getPreferenceStore();
      prefs.setValue(RBEPreferences.KEY_GROUP_SEPARATOR, 
              keyGroupSeparator.getText());
      prefs.setValue(RBEPreferences.CONVERT_ENCODED_TO_UNICODE, 
              convertEncodedToUnicode.getSelection());
      prefs.setValue(RBEPreferences.SUPPORT_NL, supportNL.getSelection());
      prefs.setValue(RBEPreferences.SUPPORT_FRAGMENTS, supportFragments.getSelection());
      prefs.setValue(RBEPreferences.LOAD_ONLY_FRAGMENT_RESOURCES, loadOnlyFragmentResources.getSelection());
      prefs.setValue(RBEPreferences.KEY_TREE_HIERARCHICAL, 
              keyTreeHierarchical.getSelection());
      prefs.setValue(RBEPreferences.KEY_TREE_EXPANDED, 
              keyTreeExpanded.getSelection());
      prefs.setValue(RBEPreferences.FIELD_TAB_INSERTS, 
              fieldTabInserts.getSelection());
      prefs.setValue(RBEPreferences.NO_TREE_IN_EDITOR, 
              noTreeInEditor.getSelection());
      prefs.setValue(RBEPreferences.MIN_HEIGHT, _minHeight.getText());
//      prefs.setValue(RBEPreferences.AUTO_ADJUST, autoAdjust.getSelection());
      prefs.setValue(RBEPreferences.ALPHA_NUMERIC_SORT_FOR_KEYS, alphaNumericSortForKeys.getSelection());
      prefs.setValue(RBEPreferences.IGNORE_CASE_FOR_KEYS, ignoreCaseForKeys.getSelection());

      refreshEnabledStatuses();
      return super.performOk();
   }

   /**
    * @see org.eclipse.jface.preference.PreferencePage
    *         #createContents(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createContents( Composite parent ) {
      IPreferenceStore prefs = getPreferenceStore();
      Composite field = null;
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(1, false));

      // Key group separator
      field = createFieldComposite(composite);
      new Label(field, SWT.NONE).setText(RBEPlugin.getString("prefs.groupSep"));
      keyGroupSeparator = new Text(field, SWT.BORDER);
      keyGroupSeparator.setText(
              prefs.getString(RBEPreferences.KEY_GROUP_SEPARATOR));
      keyGroupSeparator.setTextLimit(2);

      // Convert encoded to unicode?
      field = createFieldComposite(composite);
      convertEncodedToUnicode = new Button(field, SWT.CHECK);
      convertEncodedToUnicode.setSelection(
              prefs.getBoolean(RBEPreferences.CONVERT_ENCODED_TO_UNICODE));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.convertEncoded"));

      // Support "NL" localization structure
      field = createFieldComposite(composite);
      supportNL = new Button(field, SWT.CHECK);
      supportNL.setSelection(prefs.getBoolean(RBEPreferences.SUPPORT_NL));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.supportNL"));

      // Support loading resources from fragment 
      field = createFieldComposite(composite);
      supportFragments = new Button(field, SWT.CHECK);
      supportFragments.setSelection(prefs.getBoolean(RBEPreferences.SUPPORT_FRAGMENTS));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.supportFragments"));

      // Support loading resources from fragment 
      field = createFieldComposite(composite);
      loadOnlyFragmentResources = new Button(field, SWT.CHECK);
      loadOnlyFragmentResources.setSelection(prefs.getBoolean(RBEPreferences.LOAD_ONLY_FRAGMENT_RESOURCES));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.loadOnlyFragmentResources"));

      // Default key tree mode (tree vs flat)
      field = createFieldComposite(composite);
      keyTreeHierarchical = new Button(field, SWT.CHECK);
      keyTreeHierarchical.setSelection(
              prefs.getBoolean(RBEPreferences.KEY_TREE_HIERARCHICAL));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.keyTree.hierarchical"));

      // Default key tree expand status (expanded vs collapsed)
      field = createFieldComposite(composite);
      keyTreeExpanded = new Button(field, SWT.CHECK);
      keyTreeExpanded.setSelection(
              prefs.getBoolean(RBEPreferences.KEY_TREE_EXPANDED));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.keyTree.expanded"));

      // Default tab key behaviour in text field
      field = createFieldComposite(composite);
      fieldTabInserts = new Button(field, SWT.CHECK);
      fieldTabInserts.setSelection(
              prefs.getBoolean(RBEPreferences.FIELD_TAB_INSERTS));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.fieldTabInserts"));

      field = createFieldComposite(composite);
      noTreeInEditor = new Button(field, SWT.CHECK);
      noTreeInEditor.setSelection(
              prefs.getBoolean(RBEPreferences.NO_TREE_IN_EDITOR));
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.noTreeInEditor"));

//      Label desc = new Label(composite, SWT.READ_ONLY);
//      desc.setText(RBEPlugin.getString("prefs.properties-desc"));
//      _boldFont = UIUtils.createFont(desc, SWT.BOLD);
//      desc.setFont(_boldFont);

//      field = createFieldComposite(composite);
////      autoAdjust = new Button(field, SWT.CHECK);
////      autoAdjust.setSelection(prefs.getBoolean(RBEPreferences.AUTO_ADJUST));
//      new Label(field, SWT.NONE).setText(
//              RBEPlugin.getString("prefs.autoAdjust"));
//      autoAdjust.addSelectionListener(new SelectionAdapter() {
//         public void widgetSelected(SelectionEvent event) {
//             refreshEnabledStatuses();
//         }
//     });

      field = createFieldComposite(composite);
      alphaNumericSortForKeys = new Button(field, SWT.CHECK);
      alphaNumericSortForKeys.setSelection(prefs.getBoolean(RBEPreferences.ALPHA_NUMERIC_SORT_FOR_KEYS));
      new Label(field, SWT.NONE).setText(RBEPlugin.getString("prefs.alphaNumericSortForKeys")); //$NON-NLS-1$

      field = createFieldComposite(composite);
      ignoreCaseForKeys = new Button(field, SWT.CHECK);
      ignoreCaseForKeys.setSelection(prefs.getBoolean(RBEPreferences.IGNORE_CASE_FOR_KEYS));
      new Label(field, SWT.NONE).setText(RBEPlugin.getString("prefs.ignoreCaseForKeys")); //$NON-NLS-1$

      field = createFieldComposite(composite);
      new Label(field, SWT.NONE).setText(
              RBEPlugin.getString("prefs.minHeight"));
      _minHeight = new Text(field, SWT.BORDER);
      _minHeight.setText(prefs.getString(RBEPreferences.MIN_HEIGHT));
      _minHeight.setTextLimit(3);
      setWidthInChars(_minHeight, 3);
      _minHeight.addKeyListener(new IntTextValidatorKeyListener(
              RBEPlugin.getString("prefs.minHeight.error")));        

      refreshEnabledStatuses();
      return composite;
   }

   
   @Override
   protected void performDefaults() {
      IPreferenceStore prefs = getPreferenceStore();
      keyGroupSeparator.setText(
              prefs.getDefaultString(RBEPreferences.KEY_GROUP_SEPARATOR));
      convertEncodedToUnicode.setSelection(prefs.getDefaultBoolean(
              RBEPreferences.CONVERT_ENCODED_TO_UNICODE));
      supportNL.setSelection(
              prefs.getDefaultBoolean(RBEPreferences.SUPPORT_NL));
      keyTreeHierarchical.setSelection(
              prefs.getDefaultBoolean(RBEPreferences.KEY_TREE_HIERARCHICAL));
      keyTreeHierarchical.setSelection(
              prefs.getDefaultBoolean(RBEPreferences.KEY_TREE_EXPANDED));
      fieldTabInserts.setSelection(
              prefs.getDefaultBoolean(RBEPreferences.FIELD_TAB_INSERTS));
//      autoAdjust.setSelection(
//              prefs.getDefaultBoolean(RBEPreferences.AUTO_ADJUST));
      _minHeight.setText(
              prefs.getDefaultString(RBEPreferences.MIN_HEIGHT));

      refreshEnabledStatuses();
      super.performDefaults();
   }

   private void refreshEnabledStatuses() {
//      boolean isAutoAdjust = autoAdjust.getSelection();
//      _minHeight.setEnabled(!isAutoAdjust);
   }

}
