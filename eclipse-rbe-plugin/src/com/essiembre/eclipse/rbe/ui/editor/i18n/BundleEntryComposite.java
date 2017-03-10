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
package com.essiembre.eclipse.rbe.ui.editor.i18n;

import java.awt.ComponentOrientation;
import java.awt.GraphicsEnvironment;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.bundle.visitors.DuplicateValuesVisitor;
import com.essiembre.eclipse.rbe.model.bundle.visitors.SimilarValuesVisitor;
import com.essiembre.eclipse.rbe.model.utils.LevenshteinDistanceAnalyzer;
import com.essiembre.eclipse.rbe.model.utils.ProximityAnalyzer;
import com.essiembre.eclipse.rbe.model.utils.WordCountAnalyzer;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.editor.resources.SourceEditor;

/**
 * Represents a data entry section for a bundle entry.
 * @author Pascal Essiembre
 * @author cuhiodtick
 */
public class BundleEntryComposite extends Composite {

   /*default*/ final ResourceManager resourceManager;
    /*default*/ final Locale locale;
    /*default*/ final I18nPage page;
    private final Font boldFont;
    private final Font smallFont;

//    /*default*/ Text textBox;
    private ITextViewer textViewer;
    private IUndoManager undoManager;
    
    private Button commentedCheckbox;
    private Button gotoButton;
    private Button duplButton;
    private Button simButton;

    /*default*/ String activeKey;
    /*default*/ String textBeforeUpdate;

    /*default*/ DuplicateValuesVisitor duplVisitor;
    /*default*/ SimilarValuesVisitor similarVisitor;
    
   
    private FocusListener internalFocusListener = new FocusListener() {
        public void focusGained(FocusEvent e) {
            e.widget = BundleEntryComposite.this;
            for (FocusListener listener : focusListeners)
                listener.focusGained(e);
        }
        public void focusLost(FocusEvent e) {
            e.widget = BundleEntryComposite.this;
            for (FocusListener listener : focusListeners)
                listener.focusLost(e);
            //textViewer.setSelectedRange(0, 0);
        }
    };


    /**
     * Constructor.
     * @param parent parent composite
     * @param resourceManager resource manager
     * @param locale locale for this bundle entry
     */
    public BundleEntryComposite(
            final Composite parent, 
            final ResourceManager resourceManager, 
            final Locale locale,
            final I18nPage page) {

        super(parent, SWT.NONE);
        this.resourceManager = resourceManager;
        this.locale = locale;
        this.page = page;

        this.boldFont = UIUtils.createFont(this, SWT.BOLD, 0);
        this.smallFont = UIUtils.createFont(SWT.NONE, -1);

        GridLayout gridLayout = new GridLayout(1, false);        
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 2;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;

        createLabelRow();
//        createTextRow();
        createTextViewerRow();

        setLayout(gridLayout);
        GridData gd = new GridData(GridData.FILL_BOTH);
        setLayoutData(gd);
    }

    /**
     * Update bundles if the value of the active key changed.
     */
    public void updateBundleOnChanges(){
        if (activeKey != null) {
            BundleGroup bundleGroup = resourceManager.getBundleGroup();
            BundleEntry entry = bundleGroup.getBundleEntry(locale, activeKey);
            boolean commentedSelected = commentedCheckbox.getSelection();
            String textBoxValue = textViewer.getDocument().get();

            if (entry == null || !textBoxValue.equals(entry.getValue())
                    || entry.isCommented() != commentedSelected) {
                String comment = null;
                if (entry != null) {
                    comment = entry.getComment();
                }
                bundleGroup.addBundleEntry(locale, new BundleEntry(
                        activeKey, 
                        textViewer.getDocument().get(), 
                        comment, 
                        commentedSelected));
            }
        }
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        super.dispose();
        boldFont.dispose();
        smallFont.dispose();

        //Addition by Eric Fettweis
        for(Iterator<Font> it = swtFontCache.values().iterator();it.hasNext();){
            Font font = (Font) it.next();
            font.dispose();
        }
        swtFontCache.clear();
    }

    /**
     * Gets the locale associated with this bundle entry
     * @return a locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets a selection in the text box.
     * @param start starting position to select
     * @param end ending position to select
     */
    public void setTextSelection(int start, int end) {
        textViewer.setSelectedRange(start, end-start);
    }
    
    
   public ITextViewer getTextViewer() {
      return textViewer;
   }

    /**
     * Refreshes the text field value with value matching given key.
     * @param key key used to grab value
     */
    public void refresh(String key) {
        activeKey = key;
        BundleGroup bundleGroup = resourceManager.getBundleGroup();
        StyledText textBox = textViewer.getTextWidget();
        
        IDocument document = new Document();
        
        if (key != null && bundleGroup.isKey(key)) {
            BundleEntry bundleEntry = bundleGroup.getBundleEntry(locale, key);
            SourceEditor sourceEditor = resourceManager.getSourceEditor(locale);
            if (bundleEntry == null) {
                document.set("");
                commentedCheckbox.setSelection(false);
            } else {
                commentedCheckbox.setSelection(bundleEntry.isCommented());
                String value = bundleEntry.getValue();
                document.set(value);
            }
            commentedCheckbox.setEnabled(!sourceEditor.isReadOnly());
            textBox.setEnabled(!sourceEditor.isReadOnly());
            textBox.setEditable(true);
//            textBox.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
            gotoButton.setEnabled(true);
            if (RBEPreferences.getReportDuplicateValues()) {
                findDuplicates(bundleEntry);
            } else {
                duplVisitor = null;
            }
            if (RBEPreferences.getReportSimilarValues()) {
                findSimilar(bundleEntry);
            } else {
                similarVisitor = null;
            }
        } else {
            commentedCheckbox.setSelection(false);
            commentedCheckbox.setEnabled(false);
            document.set("");
            textBox.setEnabled(false);
            gotoButton.setEnabled(false);
            duplButton.setVisible(false);
            simButton.setVisible(false);
            textBox.setEditable(false);
//            textBox.setBackground(new Color(getDisplay(), 245, 245, 245));
        }
        
        textViewer.setDocument(document);        
        resetCommented();
    }

    private void findSimilar(BundleEntry bundleEntry) {
        ProximityAnalyzer analyzer;
        if (RBEPreferences.getReportSimilarValuesLevensthein()) {
            analyzer = LevenshteinDistanceAnalyzer.getInstance();
        } else {
            analyzer = WordCountAnalyzer.getInstance();
        }
        BundleGroup bundleGroup = resourceManager.getBundleGroup();
        if (similarVisitor == null) {
            similarVisitor = new SimilarValuesVisitor();
        }
        similarVisitor.setProximityAnalyzer(analyzer);
        similarVisitor.clear();
        bundleGroup.getBundle(locale).accept(similarVisitor, bundleEntry);
        if (duplVisitor != null) {
            similarVisitor.getSimilars().removeAll(duplVisitor.getDuplicates());
        }
        simButton.setVisible(similarVisitor.getSimilars().size() > 0);
    }

    private void findDuplicates(BundleEntry bundleEntry) {
        BundleGroup bundleGroup = resourceManager.getBundleGroup();
        if (duplVisitor == null) {
            duplVisitor = new DuplicateValuesVisitor();
        }
        duplVisitor.clear();
        bundleGroup.getBundle(locale).accept(duplVisitor, bundleEntry);
        duplButton.setVisible(duplVisitor.getDuplicates().size() > 0);
    }


    /**
     * Creates the text field label, icon, and commented check box.
     */
    private void createLabelRow() {
        Composite labelComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        gridLayout.horizontalSpacing = 5;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        labelComposite.setLayout(gridLayout);
        labelComposite.setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));

        // Locale text
        Label txtLabel = new Label(labelComposite, SWT.NONE);
        txtLabel.setText(" " + 
                UIUtils.getDisplayName(locale) + " ");
        txtLabel.setFont(boldFont);
        GridData gridData = new GridData();

        // Similar button
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.grabExcessHorizontalSpace = true;
        simButton = new Button(labelComposite, SWT.PUSH | SWT.FLAT);
        simButton.setImage(UIUtils.getImage("similar.gif"));
        simButton.setLayoutData(gridData);
        simButton.setVisible(false);
        simButton.setToolTipText(
                RBEPlugin.getString("value.similar.tooltip"));
        simButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String head = RBEPlugin.getString(
                        "dialog.similar.head");
                String body = RBEPlugin.getString(
                        "dialog.similar.body", activeKey,
                        UIUtils.getDisplayName(locale));
                body += "\n\n";
                for (Iterator<BundleEntry> iter = 
                        similarVisitor.getSimilars().iterator();
                iter.hasNext();) {
                    body += "        " + iter.next().getKey() + "\n";
                }
                MessageDialog.openInformation(getShell(), head, body); 
            }
        });

        // Duplicate button
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        duplButton = new Button(labelComposite, SWT.PUSH | SWT.FLAT);
        duplButton.setImage(UIUtils.getImage("duplicate.gif"));
        duplButton.setLayoutData(gridData);
        duplButton.setVisible(false);
        duplButton.setToolTipText(
                RBEPlugin.getString("value.duplicate.tooltip"));

        duplButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String head = RBEPlugin.getString(
                        "dialog.identical.head");
                String body = RBEPlugin.getString(
                        "dialog.identical.body", activeKey,
                        UIUtils.getDisplayName(locale));
                body += "\n\n";
                for (Iterator<BundleEntry> iter = 
                        duplVisitor.getDuplicates().iterator();
                iter.hasNext();) {
                    body += "        " + iter.next().getKey() + "\n";
                }
                MessageDialog.openInformation(getShell(), head, body); 
            }
        });

        // Commented checkbox
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        //gridData.grabExcessHorizontalSpace = true;
        commentedCheckbox = new Button(
                labelComposite, SWT.CHECK);
        commentedCheckbox.setText("#");
        commentedCheckbox.setFont(smallFont);
        commentedCheckbox.setLayoutData(gridData);
        commentedCheckbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                resetCommented();
                updateBundleOnChanges();
            }
        });
        commentedCheckbox.setEnabled(false);

        // Country flag
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        Label imgLabel = new Label(labelComposite, SWT.NONE);
        imgLabel.setLayoutData(gridData);
        imgLabel.setImage(loadCountryIcon(locale));

        // Goto button
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gotoButton = new Button(
                labelComposite, SWT.ARROW | SWT.RIGHT);
        gotoButton.setToolTipText(
                RBEPlugin.getString("value.goto.tooltip"));
        gotoButton.setEnabled(false);
        gotoButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ITextEditor editor = resourceManager.getSourceEditor(
                        locale).getEditor();
                Object activeEditor = 
                    editor.getSite().getPage().getActiveEditor();
                if (activeEditor instanceof ResourceBundleEditor) {
                    ((ResourceBundleEditor) activeEditor).setActivePage(locale);
                }
            }
        });
        gotoButton.setLayoutData(gridData);
    }
    
    private Collection<FocusListener> focusListeners = 
            new LinkedList<FocusListener>();
    @Override
    public void addFocusListener(FocusListener listener) {
        if (!focusListeners.contains(listener))
            focusListeners.add(listener);
    }
//    /**
//     * Creates the text row.
//     */
//    private void createTextRow() {
//        textBox = new Text(this, SWT.MULTI | SWT.WRAP | 
//                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
//        textBox.setEnabled(false);
//        //Addition by Eric FETTWEIS
//        //Note that this does not seem to work... It would however be usefull 
//      // for arabic and some other languages  
//        textBox.setOrientation(getOrientation(locale));
//
//        GridData gridData = new GridData();
//        gridData.verticalAlignment = GridData.FILL;
//        gridData.grabExcessVerticalSpace = true;
//        gridData.horizontalAlignment = GridData.FILL;
//        gridData.grabExcessHorizontalSpace = true;
//        gridData.heightHint = UIUtils.getHeightInChars(textBox, 3);
//        textBox.setLayoutData(gridData);
//        textBox.addFocusListener(new FocusListener() {
//            public void focusGained(FocusEvent event) {
//                textBeforeUpdate = textBox.getText();
//            }
//            public void focusLost(FocusEvent event) {
//                updateBundleOnChanges();
//            }
//        });
//        //TODO add a preference property listener and add/remove this listener
//        textBox.addTraverseListener(new TraverseListener() {
//            public void keyTraversed(TraverseEvent event) {
//                if (event.character == SWT.TAB 
//                      && !RBEPreferences.getFieldTabInserts()) {
//                    event.doit = true;
//                    event.detail = SWT.TRAVERSE_NONE;
//                    if (event.stateMask == 0)
//                        page.focusNextBundleEntryComposite();
//                    else if (event.stateMask == SWT.SHIFT)
//                        page.focusPreviousBundleEntryComposite();
//                } else if (event.character == SWT.CR) {
//                    if (event.stateMask == SWT.CTRL) {
//                        event.doit = false;                        
//                    } else if (event.stateMask == 0) {
//                        event.doit = true;
//                        event.detail = SWT.TRAVERSE_NONE;
//                        page.selectNextTreeEntry();
//                    } else if (event.stateMask == SWT.SHIFT) {
//                        event.doit = true;
//                        event.detail = SWT.TRAVERSE_NONE;
//                        page.selectPreviousTreeEntry();
//                    }
//                }
////                } else if (event.keyCode == SWT.ARROW_DOWN 
////                        && event.stateMask == SWT.CTRL) {
////                    event.doit = true;
////                    event.detail = SWT.TRAVERSE_NONE;
////                    page.selectNextTreeEntry();
////                } else if (event.keyCode == SWT.ARROW_UP 
////                        && event.stateMask == SWT.CTRL) {
////                    event.doit = true;
////                    event.detail = SWT.TRAVERSE_NONE;
////                    page.selectPreviousTreeEntry();
////                } 
//
//            }
//        });
//        textBox.addKeyListener(new KeyAdapter() {
//            public void keyReleased(KeyEvent event) {
//                Text eventBox = (Text) event.widget;
//                final ITextEditor editor = resourceManager.getSourceEditor(
//                        locale).getEditor();
//                // Text field has changed: make editor dirty if not already
//                if (textBeforeUpdate != null 
//                        && !textBeforeUpdate.equals(eventBox.getText())) {
//                    // Make the editor dirty if not already.  If it is, 
//                    // we wait until field focus lost (or save) to 
//                    // update it completely.
//                    if (!editor.isDirty()) {
//                        int caretPosition = eventBox.getCaretPosition();
//                        updateBundleOnChanges();
//                        eventBox.setSelection(caretPosition);
//                    }
//                    //autoDetectRequiredFont(eventBox.getText());
//                }
//            }
//        });
//        // Eric Fettweis : new listener to automatically change the font 
//        textBox.addModifyListener(new ModifyListener() {
//
//            public void modifyText(ModifyEvent e) {
//                String text = textBox.getText();
//                Font f = textBox.getFont();
//                String fontName = getBestFont(
//                  f.getFontData()[0].getName(), text);
//                if(fontName!=null){
//                    f = getSWTFont(f, fontName);
//                    textBox.setFont(f);
//                }
//            }
//
//        });
//        
//        textBox.addFocusListener(internalFocusListener);
//    }
    
    /**
     * Creates the text row.
     */
    private void createTextViewerRow() {
//       int vscroll = RBEPreferences.getAutoAdjust() ? 0 : SWT.V_SCROLL;
       textViewer = new TextViewer(this, SWT.MULTI | SWT.WRAP | SWT.H_SCROLL 
               | SWT.V_SCROLL | SWT.BORDER);

        textViewer.setDocument(new Document());
        undoManager = new TextViewerUndoManager(20);
        textViewer.setUndoManager(undoManager);
        textViewer.activatePlugins();
        final StyledText textBox = textViewer.getTextWidget();
        
        textBox.setEnabled(false);
        // Addition by Eric FETTWEIS
        // Note that this does not seem to work... It would however be useful
        // for arabic and some other languages  
        textBox.setOrientation(getOrientation(locale));
        
        FontRegistry fontRegistry = PlatformUI.getWorkbench().getThemeManager()
                .getCurrentTheme().getFontRegistry();
        Font font = fontRegistry.get(
                "com.essiembre.eclipse.rbe.ui.preferences.fontDefinition");
        if ( font != null ) {
           textBox.setFont(font);
        }
        
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
//        gridData.heightHint = UIUtils.getHeightInChars(textBox, 3);
        textBox.setLayoutData(gridData);
        textBox.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                textBeforeUpdate = textBox.getText();
            }
            public void focusLost(FocusEvent event) {
                updateBundleOnChanges();
            }
        });

        textBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent event) {
                if (event.character == SWT.TAB
                        && !RBEPreferences.getFieldTabInserts()) {
                    event.doit = true;
                    event.detail = SWT.TRAVERSE_NONE;
                    if (event.stateMask == 0) {
                        textViewer.setSelectedRange(0, 0);
                        page.focusNextBundleEntryComposite();
                    } else if (event.stateMask == SWT.SHIFT) {
                        textViewer.setSelectedRange(0, 0);
                        page.focusPreviousBundleEntryComposite();
                    }
                } else if (event.keyCode == SWT.ARROW_DOWN
                        && event.stateMask == SWT.CTRL) {
                    event.doit = true;
                    event.detail = SWT.TRAVERSE_NONE;
                    page.selectNextTreeEntry();
                } else if (event.keyCode == SWT.ARROW_UP
                        && event.stateMask == SWT.CTRL) {
                    event.doit = true;
                    event.detail = SWT.TRAVERSE_NONE;
                    page.selectPreviousTreeEntry();
                }                
            }
        });

        textBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (isKeyCombination(event, SWT.CTRL, 'z')) {
                    undoManager.undo();
                } else if (isKeyCombination(event, SWT.CTRL, 'y')) {
                    undoManager.redo();
                } else if (isKeyCombination(event, SWT.CTRL, 'a')) {
                    textViewer.setSelectedRange(
                            0, textViewer.getDocument().getLength());
                } else {                    
                    StyledText eventBox = (StyledText) event.widget;
                    final ITextEditor editor = 
                            resourceManager.getSourceEditor(locale).getEditor();
                    // Text field has changed: make editor dirty if not already
                    if (textBeforeUpdate != null 
                            && !textBeforeUpdate.equals(eventBox.getText())) {
                        // Make the editor dirty if not already.  If it is, 
                        // we wait until field focus lost (or save) to 
                        // update it completely.
                        if (!editor.isDirty()) {
                            int caretPosition = eventBox.getCaretOffset();
                            updateBundleOnChanges();
                            eventBox.setSelection(caretPosition);
                        }
                        //autoDetectRequiredFont(eventBox.getText());
                    }
                }
            }
        });
        // Eric Fettweis : new listener to automatically change the font 
        textViewer.addTextListener(new ITextListener() {
           
           String _oldText=null;

           @Override
           public void textChanged( TextEvent event ) {
              String text = textBox.getText();
              if(text.equals(_oldText)) return;
              _oldText=text;
              
              Font f = textBox.getFont();
              String fontName = getBestFont(f.getFontData()[0].getName(), text);
              if ( fontName != null ) {
                 f = getSWTFont(f, fontName);
                 textBox.setFont(f);
              }

//              ScrolledComposite scrolledComposite = 
//                      (ScrolledComposite)getParent().getParent();
//              Point newMinSize = getParent().computeSize(
//                      scrolledComposite.getClientArea().width, SWT.DEFAULT);
//              if ( !newMinSize.equals(new Point(
//                      scrolledComposite.getMinWidth(), 
//                      scrolledComposite.getMinHeight())) ) {
//                 page.setAutoAdjustNeeded(true);
//              }
           }
        });
        
        textBox.addFocusListener(internalFocusListener);
    }
    
    private static boolean isKeyCombination(
            KeyEvent event, int modifier1, int keyCode) {
        return (event.keyCode == keyCode && event.stateMask == modifier1);
    }
    
    /**
     * Loads country icon based on locale country.
     * @param countryLocale the locale on which to grab the country
     * @return an image, or <code>null</code> if no match could be made
     */
    private Image loadCountryIcon(Locale countryLocale) {
        Image image = null;
        String countryCode = null;
        if (countryLocale != null && countryLocale.getCountry() != null) {
            countryCode = countryLocale.getCountry().toLowerCase();
        }
        if (countryCode != null && countryCode.length() > 0) {
            String imageName = "countries/" +
            countryCode.toLowerCase() + ".gif";
            image = UIUtils.getImage(imageName);
            
		} else if (countryLocale != null
				&& countryLocale.getLanguage() != null
				&& countryLocale.getLanguage().length() > 0) {

			// show language image when country image is not available

			String countryLanguage = countryLocale.getLanguage();
			String imageName = "countries/" + countryLanguage.toLowerCase() + ".gif";

			image = UIUtils.getImage(imageName);
		}
        if (image == null) {
            image = UIUtils.getImage("countries/blank.gif");
        }
        return image;
    }

    /*default*/ void resetCommented() {
        final StyledText textBox = textViewer.getTextWidget();
        if (commentedCheckbox.getSelection()) {
            commentedCheckbox.setToolTipText(
                    RBEPlugin.getString("value.uncomment.tooltip"));
            textBox.setForeground(
                    getDisplay().getSystemColor(SWT.COLOR_GRAY));
        } else {
            commentedCheckbox.setToolTipText(
                    RBEPlugin.getString("value.comment.tooltip"));
            textBox.setForeground(null);
        }
    }

    public void focusTextBox() {
        StyledText textBox = textViewer.getTextWidget();
        textBox.setFocus();
        textViewer.setSelectedRange(0, textViewer.getDocument().getLength());
    }
    
    /** Additions by Eric FETTWEIS */
    /*private void autoDetectRequiredFont(String value) {
    Font f = getFont();
    FontData[] data = f.getFontData();
    boolean resetFont = true;
    for (int i = 0; i < data.length; i++) {
        java.awt.Font test = new java.awt.Font(data[i].getName(), 
        java.awt.Font.PLAIN, 12);
        if(test.canDisplayUpTo(value)==-1){
            resetFont = false;
            break;
        }
    }
    if(resetFont){
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getAvailableFontFamilyNames();
        for (int i = 0; i < fonts.length; i++) {
            java.awt.Font fnt = new java.awt.Font(
            fonts[i],java.awt.Font.PLAIN,12);
            if(fnt.canDisplayUpTo(value)==-1){
                textBox.setFont(createFont(fonts[i]));
                break;
            }
        }
    }
}*/
    /**
     * Holds swt fonts used for the textBox. 
     */
    private Map<String, Font> swtFontCache = new HashMap<>();

    /**
     * Gets a font by its name. The resulting font is build based on the 
     * baseFont parameter.
     * The font is retrieved from the swtFontCache, or created if needed.
     * @param baseFont the current font used to build the new one. 
     * Only the name of the new font will differ fromm the original one. 
     * @parama baseFont a font
     * @param name the new font name
     * @return a font with the same style and size as the original.
     */
    private Font getSWTFont(Font baseFont, String name){
        Font font = (Font) swtFontCache.get(name);
        if(font==null){
            font = createFont(baseFont, getDisplay(), name);
            swtFontCache.put(name, font);
        }
        return font;
    }    
    /**
     * Gets the name of the font which will be the best to display a String.
     * All installed fonts are searched. If a font can display the entire 
     * string, then it is retuned immediately.
     * Otherwise, the font returned is the one which can display properly the 
     * longest substring possible from the argument value. 
     * @param baseFontName a font to be tested before any other. It will be 
     * the current font used by a widget.
     * @param value the string to be displayed.
     * @return a font name
     */
    private static String getBestFont(String baseFontName, String value){
        if(canFullyDisplay(baseFontName, value)) return baseFontName;
        String[] fonts = getAvailableFontNames();
        String fontName=null;
        int currentScore = 0;
        for (int i = 0; i < fonts.length; i++) {
            int score = canDisplayUpTo(fonts[i], value);
            if(score==-1){//no need to loop further
                fontName=fonts[i];
                break;
            }
            if(score>currentScore){
                fontName=fonts[i];
                currentScore = score;
            }
        }

        return fontName;
    }

   private static String[] getAvailableFontNames() {
      if ( _fontFamilyNames == null ) {
         String[] fontFamilyNames = GraphicsEnvironment
                 .getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
         _fontFamilyNames = fontFamilyNames;
      }
      return _fontFamilyNames;
   }

   /**
     * A cache holding an instance of every AWT font tested.
     */
    private static Map<String, java.awt.Font> awtFontCache = new HashMap<>();
    private static String[] _fontFamilyNames;

    /**
     * Creates a variation from an original font, by changing the face name.
     * @param baseFont the original font
     * @param display the current display
     * @param name the new font face name
     * @return a new Font
     */
    private static Font createFont(Font baseFont, Display display, String name){
        FontData[] fontData = baseFont.getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setName(name);
        }
        return new Font(display, fontData);
    }
    /**
     * Can a font display correctly an entire string ?
     * @param fontName the font name
     * @param value the string to be displayed
     * @return 
     */
    private static boolean canFullyDisplay(String fontName, String value){
        return canDisplayUpTo(fontName, value)==-1;
    }

    /**
     * Test the number of characters from a given String that a font can 
     * display correctly.
     * @param fontName the name of the font
     * @param value the value to be displayed
     * @return the number of characters that can be displayed, or -1 if the 
     * entire string can be displayed successfuly.
     * @see java.aw.Font#canDisplayUpTo(String)
     */
    private static int canDisplayUpTo(String fontName, String value){
        java.awt.Font font = getAWTFont(fontName);
        return font.canDisplayUpTo(value);
    }
    /**
     * Returns a cached or new AWT font by its name.
     * If the font needs to be created, its style will be Font.PLAIN and its 
     * size will be 12.
     * @param name teh font name
     * @return an AWT Font
     */
    private static java.awt.Font getAWTFont(String name){
        java.awt.Font font = (java.awt.Font) awtFontCache.get(name);
        if(font==null){
            font = new java.awt.Font(name, java.awt.Font.PLAIN, 12);
            awtFontCache.put(name, font);
        }
        return font;
    }

    /**
     * Gets the orientation suited for a given locale.
     * @param locale the locale
     * @return <code>SWT.RIGHT_TO_LEFT</code> or <code>SWT.LEFT_TO_RIGHT</code>
     */
    private static int getOrientation(Locale locale){
        if(locale!=null){
            ComponentOrientation orientation = 
                    ComponentOrientation.getOrientation(locale);
            if(orientation==ComponentOrientation.RIGHT_TO_LEFT){
                return SWT.RIGHT_TO_LEFT;
            }
        }
        return SWT.LEFT_TO_RIGHT;
    }
}


