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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.editor.i18n.tree.KeyTreeComposite;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;

/**
 * Internationalization page where one can edit all resource bundle entries at
 * once for all supported locales.
 *
 * @author Pascal Essiembre
 * @author cuhiodtick
 */
public class I18nPage extends ScrolledComposite {

    private final ResourceManager resourceMediator;
    private final KeyTreeComposite keysComposite;
    private final List<BundleEntryComposite> entryComposites = new ArrayList<>();
    private final LocalBehaviour localBehaviour = new LocalBehaviour();
    private final ScrolledComposite editingComposite;

    /* default */BundleEntryComposite activeEntry;
    /* default */BundleEntryComposite lastActiveEntry;

    private AutoMouseWheelAdapter _autoMouseWheelAdapter;
//    boolean _autoAdjustNeeded;
    private Composite _rightComposite;
    private Button pushbutton;

    /**
     * Constructor.
     *
     * @param parent
     *            parent component.
     * @param style
     *            style to apply to this component
     * @param resourceMediator
     *            resource manager
     */
    public I18nPage(Composite parent, int style,
            final ResourceManager resourceMediator) {
        super(parent, style);
        this.resourceMediator = resourceMediator;

        if (RBEPreferences.getNoTreeInEditor()) {
            keysComposite = null;
            editingComposite = this;
            createEditingPart(this);
        } else {
            // Create screen
            SashForm sashForm = new SashForm(this, SWT.NONE);

            setContent(sashForm);

            keysComposite = new KeyTreeComposite(sashForm,
                    resourceMediator.getKeyTree());
            keysComposite.getTreeViewer().addSelectionChangedListener(
                    localBehaviour);

            editingComposite = new ScrolledComposite(sashForm, SWT.V_SCROLL
                    | SWT.H_SCROLL);
            editingComposite.getVerticalBar().setIncrement(10);
            editingComposite.getVerticalBar().setPageIncrement(100);
            editingComposite.setShowFocusedControl(true);
            createSashRightSide();

            sashForm.setWeights(new int[] { 25, 75 });

        }

        setExpandHorizontal(true);
        setExpandVertical(true);
        setMinWidth(400);
//        setMinHeight(600);

        resourceMediator.getKeyTree().addListener(localBehaviour);

        _autoMouseWheelAdapter = new AutoMouseWheelAdapter(parent);

//        if (RBEPreferences.getAutoAdjust()) {
//            // performance optimization: we only auto-adjust every 50 ms
//            getShell().getDisplay().timerExec(50, new Runnable() {
//
//                @Override
//                public void run() {
//                    if (_autoAdjustNeeded) {
//                        _autoAdjustNeeded = false;
//                        Point newMinSize = _rightComposite.computeSize(
//                                editingComposite.getClientArea().width,
//                                SWT.DEFAULT);
//                        editingComposite.setMinSize(newMinSize);
//                        editingComposite.layout();
//                    }
//
//                    if (!isDisposed())
//                        getShell().getDisplay().timerExec(50, this);
//                }
//            });
//        }
    }

    /**
     * Gets selected key.
     *
     * @return selected key
     */
    private String getSelectedKey() {
        return (resourceMediator.getKeyTree().getSelectedKey());
    }

    /**
     * Creates right side of main sash form.
     *
     * @param sashForm
     *            parent sash form
     */
    private void createSashRightSide() {
        editingComposite.setExpandHorizontal(true);
        editingComposite.setExpandVertical(true);
        editingComposite.setSize(SWT.DEFAULT, 100);
        createEditingPart(editingComposite);
    }

    /**
     * Creates the editing parts which are display within the supplied parental
     * ScrolledComposite instance.
     *
     * @param parent
     *            A container to collect the bundle entry editors.
     */
    private void createEditingPart(ScrolledComposite parent) {
        Control[] children = parent.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].dispose();
        }
        _rightComposite = new Composite(parent, SWT.BORDER);
        parent.setContent(_rightComposite);
//        if (!RBEPreferences.getAutoAdjust()) {
            parent.setMinSize(_rightComposite.computeSize(
                    SWT.DEFAULT,
                    resourceMediator.getLocales().size()
                            * RBEPreferences.getMinHeight()));
//        }
        _rightComposite.setLayout(new GridLayout(1, false));
        if (!GoogleTranslationCaller.empty(RBEPreferences.getTranslationApiKey())) {
            pushbutton = new Button(_rightComposite, SWT.PUSH);
            pushbutton.setText("Translate!");
            pushbutton.addSelectionListener(new GoogleTranslationCaller(entryComposites));
        }
        entryComposites.clear();
        for (Iterator<Locale> iter = resourceMediator.getLocales().iterator();
                iter.hasNext();) {
            Locale locale = (Locale) iter.next();
            BundleEntryComposite entryComposite = new BundleEntryComposite(
                    _rightComposite, resourceMediator, locale, this);
            entryComposite.addFocusListener(localBehaviour);
            entryComposites.add(entryComposite);
        }
    }

    /**
     * This method focusses the {@link BundleEntryComposite} corresponding to
     * the given {@link Locale}. If no such composite exists or the locale is
     * null, nothing happens.
     *
     * @param locale
     *            The locale whose {@link BundleEntryComposite} is to be
     *            focussed.
     */
    public void focusBundleEntryComposite(Locale locale) {
        for (BundleEntryComposite bec : entryComposites) {
            if ((bec.getLocale() == null) && (locale == null)
                    || (locale != null && locale.equals(bec.getLocale()))) {
                bec.focusTextBox();
            }
        }
    }

    /**
     * Focusses the next {@link BundleEntryComposite}.
     */
    public void focusNextBundleEntryComposite() {
        int index = entryComposites.indexOf(activeEntry);
        BundleEntryComposite nextComposite;
        if (index < entryComposites.size() - 1)
            nextComposite = entryComposites.get(++index);
        else
            nextComposite = entryComposites.get(0);

        if (nextComposite != null)
            focusComposite(nextComposite);
    }

    /**
     * Focusses the previous {@link BundleEntryComposite}.
     */
    public void focusPreviousBundleEntryComposite() {
        int index = entryComposites.indexOf(activeEntry);
        BundleEntryComposite nextComposite;
        if (index > 0)
            nextComposite = entryComposites.get(--index);
        else
            nextComposite = entryComposites.get(entryComposites.size() - 1);

        if (nextComposite != null)
            focusComposite(nextComposite);
    }

    /**
     * Focusses the given {@link BundleEntryComposite} and scrolls the
     * surrounding {@link ScrolledComposite} in order to make it visible.
     *
     * @param comp
     *            The {@link BundleEntryComposite} to be focussed.
     */
    private void focusComposite(BundleEntryComposite comp) {
        Point compPos = comp.getLocation();
        Point compSize = comp.getSize();
        Point size = editingComposite.getSize();
        Point origin = editingComposite.getOrigin();
        if (compPos.y + compSize.y > size.y + origin.y)
            editingComposite.setOrigin(origin.x, origin.y
                    + (compPos.y + compSize.y) - (origin.y + size.y) + 5);
        else if (compPos.y < origin.y)
            editingComposite.setOrigin(origin.x, compPos.y);
        comp.focusTextBox();
    }

    public IFindReplaceTarget getReplaceTarget() {
        return new FindReplaceTarget();
    }

    /**
     * Selects the next entry in the {@link KeyTree}.
     */
    public void selectNextTreeEntry() {
        activeEntry.updateBundleOnChanges();
        String nextKey = resourceMediator.getBundleGroup().getNextKey(
                getSelectedKey());
        if (nextKey == null)
            return;

        Locale currentLocale = activeEntry.getLocale();
        resourceMediator.getKeyTree().selectKey(nextKey);
        focusBundleEntryComposite(currentLocale);
    }

    /**
     * Selects the previous entry in the {@link KeyTree}.
     */
    public void selectPreviousTreeEntry() {
        activeEntry.updateBundleOnChanges();
        String prevKey = resourceMediator.getBundleGroup().getPreviousKey(
                getSelectedKey());
        if (prevKey == null)
            return;

        Locale currentLocale = activeEntry.getLocale();
        resourceMediator.getKeyTree().selectKey(prevKey);
        focusBundleEntryComposite(currentLocale);
    }

    /**
     * Refreshes the editor associated with the active text box (if any) if it
     * has changed.
     */
    public void refreshEditorOnChanges() {
        if (activeEntry != null) {
            activeEntry.updateBundleOnChanges();
        }
    }

    /**
     * Refreshes all value-holding text boxes in this page.
     */
    public void refreshTextBoxes() {
        String key = getSelectedKey();
        for (Iterator<BundleEntryComposite> iter = entryComposites.iterator();
                iter.hasNext();) {
            BundleEntryComposite entryComposite = iter.next();
            entryComposite.refresh(key);
        }
    }

    /**
     * Refreshes the tree and recreates the editing part.
     */
    public void refreshPage() {
        if (keysComposite != null)
            keysComposite.getTreeViewer().refresh(true);
        createEditingPart(editingComposite);
        editingComposite.layout(true, true);
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        if (keysComposite != null) {
            keysComposite.dispose();
        }
        for (Iterator<BundleEntryComposite> iter = entryComposites.iterator();
                iter.hasNext();) {
            iter.next().dispose();
        }
        _autoMouseWheelAdapter.dispose();
        super.dispose();
    }

//    void setAutoAdjustNeeded(boolean b) {
//        _autoAdjustNeeded = b;
//    }
//
    void findActionStart() {
        if (!keysComposite.getFilter().isEmpty()) {
            keysComposite.setFilter("");
        }
    }

    /**
     * Implementation of custom behaviour.
     */
    private class LocalBehaviour implements FocusListener, IDeltaListener,
            ISelectionChangedListener {

        /**
         * {@inheritDoc}
         */
        public void focusGained(FocusEvent event) {
            activeEntry = (BundleEntryComposite) event.widget;
            lastActiveEntry = activeEntry;
        }

        /**
         * {@inheritDoc}
         */
        public void focusLost(FocusEvent event) {
            activeEntry = null;
        }

        /**
         * {@inheritDoc}
         */
        public void selectionChanged(SelectionChangedEvent event) {
            refreshTextBoxes();
            String selected = getSelectedKey();
            if (selected != null) {
                resourceMediator.getKeyTree().selectKey(selected);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void add(DeltaEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        public void remove(DeltaEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        public void modify(DeltaEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        public void select(DeltaEvent event) {
            KeyTreeItem item = (KeyTreeItem) event.receiver();
            if (keysComposite != null) {
                if (item != null) {
                    keysComposite.getTreeViewer().setSelection(
                            new StructuredSelection(item));
                }
            } else {
                refreshTextBoxes();
            }
        }

    } /* ENDCLASS */

    private class FindReplaceTarget implements IFindReplaceTarget,
            IFindReplaceTargetExtension3 {

        @Override
        public int findAndSelect(int widgetOffset, String findString,
                boolean searchForward,
                boolean caseSensitive, boolean wholeWord) {
            // replaced by findAndSelect(.,.)
            return -1;
        }

        @Override
        public int findAndSelect(int offset, String findString,
                boolean searchForward, boolean caseSensitive,
                boolean wholeWord, boolean regExSearch) {
            if (lastActiveEntry != null) {
                StyledText textWidget = lastActiveEntry.getTextViewer()
                        .getTextWidget();
                String text = textWidget.getText();
                IRegion region = find(text, findString,
                        textWidget.getSelection().x + (searchForward ? 1 : -1),
                        searchForward, caseSensitive, wholeWord, regExSearch);
                if (region != null) {
                    focusBundleEntryComposite(lastActiveEntry.locale);
                    textWidget.setSelection(region.getOffset(),
                            region.getOffset() + region.getLength());
                    return region.getOffset();
                }
            }
            BundleGroup bundleGroup = resourceMediator.getBundleGroup();
            ArrayList<String> keys = new ArrayList<String>(
                    bundleGroup.getKeys());
            String activeKey = lastActiveEntry != null ?
                    lastActiveEntry.activeKey : keys.get(0);
            int activeKeyIndex = Math.max(keys.indexOf(activeKey), 0);

            List<Locale> locales = resourceMediator.getLocales();
            Locale activeLocale = lastActiveEntry != null
                    ? lastActiveEntry.locale
                    : locales.get(0);
            int activeLocaleIndex = locales.indexOf(activeLocale)
                    + (searchForward ? 1 : -1);

            for (int i = 0, length = keys.size(); i < length; i++) {
                String key = keys
                        .get((activeKeyIndex + (searchForward ? i : -i))
                                % length);
                int j = (i == 0 ? activeLocaleIndex : (searchForward ? 0
                        : locales.size() - 1));
                while (j < locales.size() && j >= 0) {
                    Locale locale = locales.get(j);
                    Bundle bundle = bundleGroup.getBundle(locale);
                    BundleEntry value = bundle.getEntry(key);
                    if (value != null && value.getValue() != null) {
                        IRegion region = find(value.getValue(), findString,
                                searchForward ? 0
                                        : value.getValue().length() - 1,
                                searchForward, caseSensitive, wholeWord,
                                regExSearch);
                        if (region != null) {
                            keysComposite.selectKeyTreeItem(key);
                            focusBundleEntryComposite(locale);
                            StyledText textWidget = activeEntry.getTextViewer()
                                    .getTextWidget();
                            textWidget.setSelection(region.getOffset(),
                                    region.getOffset() + region.getLength());
                            return region.getOffset();
                        }
                    }
                    if (searchForward)
                        ++j;
                    else
                        --j;
                }
            }
            return -1;
        }

        private IRegion find(String text, String findString, int offset,
                boolean searchForward, boolean caseSensitive,
                boolean wholeWord, boolean regExSearch) {
            Document document = new Document(text);
            FindReplaceDocumentAdapter documentAdapter =
                    new FindReplaceDocumentAdapter(document);
            try {
                return documentAdapter.find(offset, findString, searchForward,
                        caseSensitive, wholeWord, regExSearch);
            } catch (BadLocationException argh) {
                return null;
            }
        }

        @Override
        public void replaceSelection(String text) {
            // replaced by replaceSelection(.,.)
        }

        @Override
        public void replaceSelection(String text, boolean regExReplace) {

        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public String getSelectionText() {
            return activeEntry != null ? activeEntry.getTextViewer()
                    .getTextWidget().getSelectionText() : "";
        }

        @Override
        public Point getSelection() {
            return activeEntry != null ? activeEntry.getTextViewer()
                    .getSelectedRange() : new Point(0, 0);
        }

        @Override
        public boolean canPerformFind() {
            return true;
        }

    }
}
