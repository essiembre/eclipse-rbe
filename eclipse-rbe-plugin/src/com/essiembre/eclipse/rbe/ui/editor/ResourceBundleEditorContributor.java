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
package com.essiembre.eclipse.rbe.ui.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.essiembre.eclipse.rbe.ui.editor.i18n.I18nPageEditor;

/**
 * Manages the installation/deinstallation of global actions for multi-page 
 * editors. Responsible for the redirection of global actions to the active 
 * editor.
 * Multi-page contributor replaces the contributors for the individual editors
 * in the multi-page editor.
 */
public class ResourceBundleEditorContributor 
        extends MultiPageEditorActionBarContributor {
    private IEditorPart activeEditorPart;
    /**
     * Creates a multi-page contributor.
     */
    public ResourceBundleEditorContributor() {
        super();
        createActions();
    }
    /**
     * Returns the action registed with the given text editor.
     * @param editor eclipse text editor
     * @param actionID action id
     * @return IAction or null if editor is null.
     */
    protected IAction getAction(ITextEditor editor, String actionID) {
        return (editor == null ? null : editor.getAction(actionID));
    }

    @Override
    public void dispose() {
        activeEditorPart = null;
    }

    @Override
    public void setActivePage(IEditorPart part) {
        if (activeEditorPart == part)
            return;

        activeEditorPart = part;

        IActionBars actionBars = getActionBars();
        if (actionBars != null) {

            ITextEditor editor = (part instanceof ITextEditor) 
                               ? (ITextEditor) part : null;
                               
             if(editor instanceof I18nPageEditor) {
                actionBars.clearGlobalActionHandlers();
                
                actionBars.setGlobalActionHandler(
                   ActionFactory.FIND.getId(),
                   ((I18nPageEditor)editor).getFindReplaceAction());
                actionBars.setGlobalActionHandler(
                   IWorkbenchActionDefinitionIds.FIND_NEXT,
                   ((I18nPageEditor)editor).getFindNextAction());
                actionBars.setGlobalActionHandler(
                   IWorkbenchActionDefinitionIds.FIND_PREVIOUS,
                   ((I18nPageEditor)editor).getFindPreviousAction());
                   
                actionBars.updateActionBars();
                return;
             }

            actionBars.setGlobalActionHandler(
                ActionFactory.DELETE.getId(),
                getAction(editor, ITextEditorActionConstants.DELETE));
            actionBars.setGlobalActionHandler(
                ActionFactory.UNDO.getId(),
                getAction(editor, ITextEditorActionConstants.UNDO));
            actionBars.setGlobalActionHandler(
                ActionFactory.REDO.getId(),
                getAction(editor, ITextEditorActionConstants.REDO));
            actionBars.setGlobalActionHandler(
                ActionFactory.CUT.getId(),
                getAction(editor, ITextEditorActionConstants.CUT));
            actionBars.setGlobalActionHandler(
                ActionFactory.COPY.getId(),
                getAction(editor, ITextEditorActionConstants.COPY));
            actionBars.setGlobalActionHandler(
                ActionFactory.PASTE.getId(),
                getAction(editor, ITextEditorActionConstants.PASTE));
            actionBars.setGlobalActionHandler(
                ActionFactory.SELECT_ALL.getId(),
                getAction(editor, ITextEditorActionConstants.SELECT_ALL));
            actionBars.setGlobalActionHandler(
               ActionFactory.FIND.getId(),
               getAction(editor, ITextEditorActionConstants.FIND));
            actionBars.setGlobalActionHandler(
               IWorkbenchActionDefinitionIds.FIND_NEXT,
               getAction(editor, ITextEditorActionConstants.FIND_NEXT));
            actionBars.setGlobalActionHandler(
               IWorkbenchActionDefinitionIds.FIND_PREVIOUS,
               getAction(editor, ITextEditorActionConstants.FIND_PREVIOUS));
            actionBars.setGlobalActionHandler(
                IDEActionFactory.BOOKMARK.getId(),
                getAction(editor, IDEActionFactory.BOOKMARK.getId()));
            actionBars.updateActionBars();
        }
    }
    private void createActions() {
//        sampleAction = new Action() {
//            public void run() {
//                MessageDialog.openInformation(null,
//        "ResourceBundle Editor Plug-in", "Sample Action Executed");
//            }
//        };
//        sampleAction.setText("Sample Action");
//        sampleAction.setToolTipText("Sample Action tool tip");
//        sampleAction.setImageDescriptor(
//        PlatformUI.getWorkbench().getSharedImages().
//                getImageDescriptor(IDE.SharedImages.IMG_OBJS_TASK_TSK));
    }
    @Override
    public void contributeToMenu(IMenuManager manager) {
//        IMenuManager menu = new MenuManager("Editor &Menu");
//        manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
//        menu.add(sampleAction);
    }
    @Override
    public void contributeToToolBar(IToolBarManager manager) {
//        manager.add(new Separator());
//        manager.add(sampleAction);
    }
}
