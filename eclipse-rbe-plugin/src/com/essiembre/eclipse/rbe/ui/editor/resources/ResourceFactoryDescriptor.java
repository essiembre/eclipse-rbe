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
package com.essiembre.eclipse.rbe.ui.editor.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * The extension point descriptor for the resource factory extension point.
 *  
 * @author Uwe Voigt
 */
public class ResourceFactoryDescriptor {

    private static final String EXTENSION_POINT_ID = 
            "com.essiembre.eclipse.rbe.resourceFactory";
    private static final String TAG_FACTORY = "factory";
    private IConfigurationElement fElement;

    private ResourceFactoryDescriptor(IConfigurationElement element) {
        fElement = element;
    }

    /**
     * Returns new instances of the contributed resource factories order by 
     * their contributed order value.
     * 
     * @return
     * @throws CoreException
     */
    public static IResourceFactory[] getContributedResourceFactories() 
            throws CoreException {
        ResourceFactoryDescriptor[] descriptors = 
                getContributedResourceFactoryDescriptors();
        SortedMap<Integer, Object> factories = new TreeMap<>();
        for (int i = 0, lastOrder = 0; i < descriptors.length; i++) {
            Object factory = descriptors[i].fElement.createExecutableExtension(
                    "class");
            String attribute = descriptors[i].fElement.getAttribute("order");
            Integer order = null;
            try {
                order = new Integer(attribute);
            } catch (Exception e) {
                order = new Integer(++lastOrder);
            }
            while (factories.containsKey(order))
                order = new Integer(lastOrder = order.intValue());
            factories.put(order, factory);
        }
        return (IResourceFactory[]) factories.values().toArray(
                new IResourceFactory[factories.values().size()]);
    }

    private static ResourceFactoryDescriptor[] 
            getContributedResourceFactoryDescriptors() {
        IConfigurationElement[] elements = Platform.getExtensionRegistry()
                    .getConfigurationElementsFor(EXTENSION_POINT_ID);
        return createDescriptors(elements);
    }

    private static ResourceFactoryDescriptor[] createDescriptors(
            IConfigurationElement[] elements) {
        List<ResourceFactoryDescriptor> list = new ArrayList<>(elements.length);
        for (int i = 0; i < elements.length; i++) {
            IConfigurationElement element = elements[i];
            if (TAG_FACTORY.equals(element.getName())) {
                ResourceFactoryDescriptor descriptor = 
                        new ResourceFactoryDescriptor(element);
                list.add(descriptor);
            }
        }
        return (ResourceFactoryDescriptor[]) list.toArray(
                new ResourceFactoryDescriptor[list.size()]);
    }

}
