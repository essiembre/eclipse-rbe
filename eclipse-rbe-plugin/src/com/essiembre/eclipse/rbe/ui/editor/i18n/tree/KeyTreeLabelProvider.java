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
package com.essiembre.eclipse.rbe.ui.editor.i18n.tree;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.visitors.IsCommentedVisitor;
import com.essiembre.eclipse.rbe.model.tree.visitors.IsMissingValueVisitor;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.OverlayImageIcon;
import com.essiembre.eclipse.rbe.ui.UIUtils;

/**
 * Label provider for key tree viewer.
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public class KeyTreeLabelProvider 
        extends LabelProvider implements IFontProvider, IColorProvider {    
    
    private static final int KEY_DEFAULT = 1 << 1;
    private static final int KEY_COMMENTED = 1 << 2;
    private static final int KEY_NOT = 1 << 3;
    private static final int WARNING = 1 << 4;
    private static final int WARNING_GREY = 1 << 5;

    /** Registry instead of UIUtils one for image not keyed by file name. */
    private static ImageRegistry imageRegistry = new ImageRegistry();
    
//    private Color colorInactive = UIUtils.getSystemColor(SWT.COLOR_GRAY);
    private Color colorCommented = UIUtils.getSystemColor(SWT.COLOR_GRAY);

    /** Group font. */
    private Font keyFont = UIUtils.createFont(SWT.NORMAL);
    private Font groupFontKey = UIUtils.createFont(SWT.NORMAL);
    private Font groupFontNoKey = UIUtils.createFont(SWT.NORMAL);

    
    /**
     * @see ILabelProvider#getImage(Object)
     */
    public Image getImage(Object element) {
        KeyTreeItem treeItem = ((KeyTreeItem) element);
        
        int iconFlags = 0;

        // Figure out background icon
        if (treeItem.getKeyTree().getBundleGroup().isKey(treeItem.getId())) {
            IsCommentedVisitor commentedVisitor = new IsCommentedVisitor();
            treeItem.accept(commentedVisitor, null);
            if (commentedVisitor.hasOneCommented()) {
                iconFlags += KEY_COMMENTED;
            } else {
                iconFlags += KEY_DEFAULT;
            }
        } else {
            iconFlags += KEY_NOT;
        }
        
        // Maybe add warning icon        
        if (RBEPreferences.getReportMissingValues()) {
            IsMissingValueVisitor misValVisitor = new IsMissingValueVisitor();
            treeItem.accept(misValVisitor, null);
            if (misValVisitor.isMissingValue()) {
                iconFlags += WARNING;
            } else if (misValVisitor.isMissingChildValueOnly()) {
                iconFlags += WARNING_GREY;
            }
        }

        return generateImage(iconFlags);
    }

    @Override
    public String getText(Object element) {
        return ((KeyTreeItem) element).getName(); 
    }

    @Override
    public void dispose() {
        groupFontKey.dispose();
        groupFontNoKey.dispose();
        keyFont.dispose();
        colorCommented.dispose();
    }

    @Override
    public Font getFont(Object element) {
        KeyTreeItem item = (KeyTreeItem) element; 
        if (item.getChildren().size() > 0) {
            if (item.getKeyTree().getBundleGroup().isKey(item.getId())) {
                return groupFontKey;
            }
            return groupFontNoKey;
        }
        return keyFont;
    }

    @Override
    public Color getForeground(Object element) {
        KeyTreeItem treeItem = (KeyTreeItem) element; 
//        // No key
//        if (!treeItem.getKeyTree().getBundleGroup().isKey(treeItem.getId())) {
//            return colorInactive;
//        }

        // Commented
        IsCommentedVisitor commentedVisitor = new IsCommentedVisitor();
        treeItem.accept(commentedVisitor, null);
        if (commentedVisitor.hasOneCommented()) {
            return colorCommented;
        }

        return null;
    }

    @Override
    public Color getBackground(Object element) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Generates an image based on icon flags. 
     * @param iconFlags
     * @return generated image
     */
    private Image generateImage(int iconFlags) {
        Image image = imageRegistry.get("" + iconFlags);
        if (image == null) {
            // Figure background image
            if ((iconFlags & KEY_COMMENTED) != 0) {
                image = getRegistryImage("keyCommented.gif");
            } else if ((iconFlags & KEY_NOT) != 0) {
                image = getRegistryImage("keyCommented.gif");
            } else {
                image = getRegistryImage("key.gif");
            }
            
            // Add warning icon
            if ((iconFlags & WARNING) != 0) {
                image = overlayImage(image, "warning.gif",
                        OverlayImageIcon.BOTTOM_RIGHT, iconFlags);
            } else if ((iconFlags & WARNING_GREY) != 0) {
                image = overlayImage(image, "warningGrey.gif",
                        OverlayImageIcon.BOTTOM_RIGHT, iconFlags);
            }
        }
        return image;
    }

    private Image overlayImage(
            Image baseImage, String imageName, int location, int iconFlags) {
        /* To obtain a unique key, we assume here that the baseImage and 
         * location are always the same for each imageName and keyFlags 
         * combination.
         */
        String imageKey = imageName + iconFlags;
        Image image = imageRegistry.get(imageKey);
        if (image == null) {
            image = new OverlayImageIcon(baseImage, getRegistryImage(
                    imageName), location).createImage();
            imageRegistry.put(imageKey, image);
        }
        return image;
    }

    private Image getRegistryImage(String imageName) {
        Image image = imageRegistry.get(imageName);
        if (image == null) {
            image = RBEPlugin.getImageDescriptor(imageName).createImage();
            imageRegistry.put(imageName, image);
        }
        return image;
    }
}
