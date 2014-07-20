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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.FindNextAction;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;

public class I18nPageEditor extends AbstractTextEditor {

    private I18nPage i18nPage;
    private ResourceManager resourceMediator;

    private FindReplaceAction findReplaceAction;
    private FindNextAction findNextAction;
    private FindNextAction findPreviousAction;

    public I18nPageEditor(ResourceManager resourceMediator) {
        this.resourceMediator = resourceMediator;
    }

    public I18nPage getI18nPage() {
        return i18nPage;
    }

    @Override
    public void createPartControl(Composite parent) {
        i18nPage = new I18nPage(parent, SWT.NONE, resourceMediator);

        findReplaceAction = new FindReplaceAction(RBEPlugin.getDefault()
                .getResourceBundle(), null, i18nPage.getShell(),
                i18nPage.getReplaceTarget()) {
            @Override
            public void run() {
                i18nPage.findActionStart();
                super.run();
            }
        };

        findNextAction = new FindNextAction(RBEPlugin.getDefault()
                .getResourceBundle(), null, this, true) {
            @Override
            public void run() {
                i18nPage.findActionStart();
                super.run();
            }
        };
        findNextAction.setActionDefinitionId(
                IWorkbenchActionDefinitionIds.FIND_NEXT);

        findPreviousAction = new FindNextAction(RBEPlugin.getDefault()
                .getResourceBundle(), null, this, false) {
            @Override
            public void run() {
                i18nPage.findActionStart();
                super.run();
            }
        };
        findPreviousAction.setActionDefinitionId(
                IWorkbenchActionDefinitionIds.FIND_PREVIOUS);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class required) {
        if (required.equals(IFindReplaceTarget.class)) {
            return i18nPage.getReplaceTarget();
        }
        return super.getAdapter(required);
    }

    public IAction getFindReplaceAction() {
        return findReplaceAction;
    }

    public IAction getFindNextAction() {
        return findNextAction;
    }

    public IAction getFindPreviousAction() {
        return findPreviousAction;
    }
}
