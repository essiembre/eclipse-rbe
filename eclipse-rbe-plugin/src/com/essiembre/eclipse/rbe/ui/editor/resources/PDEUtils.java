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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.osgi.framework.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class that helps to find fragment and plugin projects.
 * 
 * @author Uwe Voigt
 */
public class PDEUtils {
    
    /** Bundle manifest name */
    public static final String OSGI_BUNDLE_MANIFEST = "META-INF/MANIFEST.MF";
    /** Plugin manifest name */
    public static final String PLUGIN_MANIFEST = "plugin.xml";
    /** Fragment manifest name */
    public static final String FRAGMENT_MANIFEST = "fragment.xml";

    /**
     * Returns the plugin-id of the project if it is a plugin project. Else
     * null is returned.
     * 
     * @param project the project
     * @return the plugin-id or null
     */
    public static String getPluginId(IProject project) {
        if (project == null)
            return null;
        IResource manifest = project.findMember(OSGI_BUNDLE_MANIFEST);
        String id = getManifestEntryValue(
                manifest, Constants.BUNDLE_SYMBOLICNAME);
        if (id != null)
            return id;
        manifest = project.findMember(PLUGIN_MANIFEST);
        if (manifest == null)
            manifest = project.findMember(FRAGMENT_MANIFEST);
        if (manifest instanceof IFile) {
            InputStream in = null;
            try {
                DocumentBuilder builder = DocumentBuilderFactory
                        .newInstance().newDocumentBuilder();
                in = ((IFile) manifest).getContents();
                Document document = builder.parse(in);
                Node node = getXMLElement(document, "plugin");
                if (node == null)
                    node = getXMLElement(document, "fragment");
                if (node != null)
                    node = node.getAttributes().getNamedItem("id");
                if (node != null)
                    return node.getNodeValue();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Returns a project containing plugin/fragment of the specified
     * project. The first project found is returned. If the specified project
     * itself is a fragment, then this is returned.
     *  
     * @param pluginProject the plugin project
     * @return a project containing a fragment or null if none
     */
    public static IProject lookupFragment(IProject pluginProject) {
        String pluginId = PDEUtils.getPluginId(pluginProject);
        if (pluginId == null)
            return null;
        String fragmentId = getFragmentId(
                pluginProject, getPluginId(getFragmentHost(pluginProject)));
        if (fragmentId != null)
            return pluginProject;
        IProject[] projects = 
                pluginProject.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < projects.length; i++) {
            IProject project = projects[i];
            if (!project.isOpen())
                continue;
            if (getFragmentId(project, pluginId) == null)
                continue;
            return project;
        }
        return null;
    }

    /**
     * Returns the fragment-id of the project if it is a fragment project with
     * the specified host plugin id as host. Else null is returned.
     * 
     * @param project the project
     * @param hostPluginId the host plugin id
     * @return the plugin-id or null
     */
    public static String getFragmentId(IProject project, String hostPluginId) {
        IResource manifest = project.findMember(FRAGMENT_MANIFEST);
        Node fragmentNode = getXMLElement(getXMLDocument(manifest), "fragment");
        if (fragmentNode != null) {
            Node hostNode = fragmentNode.getAttributes().getNamedItem(
                    "plugin-id");
            if (hostNode != null && 
                    hostNode.getNodeValue().equals(hostPluginId)) {
                Node idNode = fragmentNode.getAttributes().getNamedItem("id");
                if (idNode != null)
                    return idNode.getNodeValue();
            }
        }
        manifest = project.findMember(OSGI_BUNDLE_MANIFEST);
        String hostId = getManifestEntryValue(
                manifest, Constants.FRAGMENT_HOST);
        if (hostId != null && hostId.equals(hostPluginId))
            return getManifestEntryValue(
                    manifest, Constants.BUNDLE_SYMBOLICNAME);
        return null;
    }
    
    /**
     * Returns the host plugin project of the specified project if it contains a fragment.
     *  
     * @param fragment the fragment project
     * @return the host plugin project or null
     */
    public static IProject getFragmentHost(IProject fragment) {
        IResource manifest = fragment.findMember(FRAGMENT_MANIFEST);
        Node fragmentNode = getXMLElement(
                getXMLDocument(manifest), "fragment");
        if (fragmentNode != null) {
            Node hostNode = 
                    fragmentNode.getAttributes().getNamedItem("plugin-id");
            if (hostNode != null)
                return fragment.getWorkspace().getRoot().getProject(
                        hostNode.getNodeValue());
        }
        manifest = fragment.findMember(OSGI_BUNDLE_MANIFEST);
        String hostId = getManifestEntryValue(
                manifest, Constants.FRAGMENT_HOST);
        if (hostId != null)
            return fragment.getWorkspace().getRoot().getProject(hostId);
        return null;
    }

    /**
     * Returns the file content as UTF8 string.
     * 
     * @param file
     * @param charset 
     * @return
     */
    public static String getFileContent(IFile file, String charset) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = file.getContents(true);
            byte[] buf = new byte[8000];
            for (int count; (count = in.read(buf)) != -1;)
                outputStream.write(buf, 0, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
        try {
            return outputStream.toString(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return outputStream.toString();
        }
    }

    private static String getManifestEntryValue(
            IResource manifest, String entryKey) {
        if (manifest instanceof IFile) {
            String content = getFileContent((IFile) manifest, "UTF8");
            int index = content.indexOf(entryKey);
            if (index != -1) {
                StringTokenizer st = new StringTokenizer(content.substring(index
                        + entryKey.length()), ";:\r\n");
                return st.nextToken().trim();
            }
        }
        return null;
    }

    private static Document getXMLDocument(IResource resource) {
        if (!(resource instanceof IFile))
            return null;
        InputStream in = null;
        try {
            DocumentBuilder builder = 
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
            in = ((IFile) resource).getContents();
            return builder.parse(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static Node getXMLElement(Document document, String name) {
        if (document == null)
            return null;
        NodeList list = document.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            if (name.equals(node.getNodeName()))
                return node;
        }
        return null;
    }
    
}
