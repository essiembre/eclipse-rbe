/*
 * Copyright (C) 2003-2017  Pascal Essiembre
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
package com.essiembre.eclipse.rbe;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main resource bundle editor plugin class to be used in the desktop.
 * @author Pascal Essiembre
 */
public class RBEPlugin extends AbstractUIPlugin {

    /** Plugin unique id. */
    public static final String ID = "com.essiembre.eclipse.rbe";
    
    //The shared instance.
    private static RBEPlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;
    
    /**
     * The constructor.
     */
    public RBEPlugin() {
        super();
    }
    
    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        resourceBundle = Platform.getResourceBundle(getBundle());
        plugin = this;
    }
    
    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * @return this plugin
     */
    public static RBEPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @return localized string corresponding to key
     */
    public static String getString(String key) {
        ResourceBundle bundle = 
                RBEPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param args runtime arguments to replace in key value 
     * @return localized string corresponding to key
     */
    public static String getString(String key, Object... args) {
        return MessageFormat.format(getString(key), args);
    }
    
    /**
     * Returns the plugin's resource bundle.
     * @return resource bundle
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    /**
     * Gets an image descriptor.
     * @param name image name
     * @return image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/";
        try {
            URL installURL = RBEPlugin.getDefault().getBundle().getEntry("/");
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            // should not happen
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
    
}
