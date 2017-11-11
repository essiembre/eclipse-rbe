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
package com.essiembre.eclipse.rbe.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.essiembre.eclipse.rbe.RBEPlugin;


/**
 * Utility methods related to application UI.
 * @author Pascal Essiembre
 * @author Tobias Langner
 */
public final class UIUtils {

    public static final class LocaleComparator implements Comparator<Locale> {
		@Override
		public int compare(Locale o1, Locale o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 != null && o2 == null) return 1;
			if (o1 == null && o2 != null) return -1;
			Collator c = Collator.getInstance();
			c.setStrength(Collator.PRIMARY);
			return c.compare(o1.getDisplayName(), o2.getDisplayName());
		}
	}

	/** Name of resource bundle image. */
    public static final String IMAGE_RESOURCE_BUNDLE = 
            "resourcebundle.gif"; 
    /** Name of properties file image. */
    public static final String IMAGE_PROPERTIES_FILE = 
            "propertiesfile.gif"; 
    /** Name of new properties file image. */
    public static final String IMAGE_NEW_PROPERTIES_FILE = 
            "newpropertiesfile.gif"; 
    /** Name of hierarchical layout image. */
    public static final String IMAGE_LAYOUT_HIERARCHICAL =
            "hierarchicalLayout.gif"; 
    /** Name of flat layout image. */
    public static final String IMAGE_LAYOUT_FLAT = 
            "flatLayout.gif"; 
    
    public static final String IMAGE_INCOMPLETE_ENTRIES =
            "incomplete.gif"; 
    
    /** Image registry. */
    private static final ImageRegistry imageRegistry = new ImageRegistry();
    
    /**
     * Constructor.
     */
    private UIUtils() {
        super();
    }

    /**
     * Creates a font by altering the font associated with the given control
     * and applying the provided style (size is unaffected).
     * @param control control we base our font data on
     * @param style   style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(Control control, int style) {
        //TODO consider dropping in favor of control-less version?
        return createFont(control, style, 0);
    }

    
    /**
     * Creates a font by altering the font associated with the given control
     * and applying the provided style and relative size.
     * @param control control we base our font data on
     * @param style   style to apply to the new font
     * @param relSize size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(Control control, int style, int relSize) {
        //TODO consider dropping in favor of control-less version?
        FontData[] fontData = control.getFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(style);
        }
        return new Font(control.getDisplay(), fontData);
    }

    /**
     * Creates a font by altering the system font
     * and applying the provided style and relative size.
     * @param style   style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(int style) {
        return createFont(style, 0);
    }
    
    /**
     * Creates a font by altering the system font
     * and applying the provided style and relative size.
     * @param style   style to apply to the new font
     * @param relSize size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(int style, int relSize) {
        Display display = RBEPlugin.getDefault().getWorkbench().getDisplay();
        FontData[] fontData = display.getSystemFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(style);
        }
        return new Font(display, fontData);
    }

    /**
     * Creates a cursor matching given style.
     * @param style   style to apply to the new font
     * @return newly created cursor
     */
    public static Cursor createCursor(int style) {
        Display display = RBEPlugin.getDefault().getWorkbench().getDisplay();
        return new Cursor(display, style);
    }
    
    /**
     * Gets a system color.
     * @param colorId SWT constant
     * @return system color
     */
    public static Color getSystemColor(int colorId) {
        return RBEPlugin.getDefault().getWorkbench()
                .getDisplay().getSystemColor(colorId);
    }
    
    /**
     * Gets the approximate width required to display a given number of
     * characters in a control.
     * @param control the control on which to get width
     * @param numOfChars the number of chars
     * @return width
     */    
    public static int getWidthInChars(Control control, int numOfChars) {
        GC gc = new GC(control);
        Point extent = gc.textExtent("W");//$NON-NLS-1$
        gc.dispose();
        return numOfChars * extent.x;
    }

    /**
     * Gets the approximate height required to display a given number of
     * characters in a control, assuming, they were laid out vertically.
     * @param control the control on which to get height
     * @param numOfChars the number of chars
     * @return height
     */    
    public static int getHeightInChars(Control control, int numOfChars) {
        GC gc = new GC(control);
        Point extent = gc.textExtent("W");//$NON-NLS-1$
        gc.dispose();
        return numOfChars * extent.y;
    }
    
    /**
     * Shows an error dialog based on the supplied arguments.
     * @param shell the shell
     * @param exception the core exception
     * @param msgKey key to the plugin message text
     */
    public static void showErrorDialog(
            Shell shell, CoreException exception, String msgKey) {
        exception.printStackTrace();
        ErrorDialog.openError(
                shell,
                RBEPlugin.getString(msgKey),
                exception.getLocalizedMessage(),
                exception.getStatus());
    }
    
    /**
     * Shows an error dialog based on the supplied arguments.
     * @param shell the shell
     * @param exception the core exception
     * @param msgKey key to the plugin message text
     */
    public static void showErrorDialog(
            Shell shell, Exception exception, String msgKey) {
        exception.printStackTrace();
        IStatus status = new Status(
                IStatus.ERROR, 
                RBEPlugin.ID,
                0, 
                RBEPlugin.getString(msgKey) + " "
                        + RBEPlugin.getString("error.seeLogs"),
                exception);
        ErrorDialog.openError(
                shell,
                RBEPlugin.getString(msgKey),
                exception.getLocalizedMessage(),
                status);
    }
    
    /**
     * Gets a locale, null-safe, display name.
     * @param locale locale to get display name
     * @return display name
     */
    public static String getDisplayName(Locale locale) {
        if (locale == null) {
            return RBEPlugin.getString("editor.default");
        }
        return locale.getDisplayName();
    }
   
    /**
     * Sorts given list of locales based on the {@link Locale#getDisplayName()} using 
     * {@link Collator} in the current locale.
     * null argument will be always first
     * 
     * @param locales to be sorted
     * @return sorted locales
     */
    public static List<Locale> sortLocales(Collection<Locale> locales) {
    	ArrayList<Locale> result = new ArrayList<>(locales);
    	result.sort(new LocaleComparator());
    	return result;
    }
    
    /**
     * Gets an image.
     * @param imageName image name
     * @return image
     */
    public static Image getImage(String imageName) {
        Image image = imageRegistry.get(imageName);
        if (image == null) {
            image = RBEPlugin.getImageDescriptor(imageName).createImage();
            imageRegistry.put(imageName, image);
        }
        return image;
    }
}
