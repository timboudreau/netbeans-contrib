/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.contrib.generate.project.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Tim Boudreau
 */
public class ProjectScanner {

    private final Path pom;
    private static final ThreadLocal<Document> DOCUMENT = new ThreadLocal<>();

    public ProjectScanner(Path pom) {
        this.pom = pom;
        assert Files.exists(pom);
    }

    public <T> T enter(DocConsumer<T> cons) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Document old = DOCUMENT.get();
        Document doc = getDocument();
        DOCUMENT.set(doc);
        try {
            return cons.withDocument(doc, this);
        } finally {
            DOCUMENT.set(old);
        }
    }

    public String getProperty(String prop) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findArtifactId = xpath.compile(
                "/project/properties/" + prop);
        Node n = (Node) findArtifactId.evaluate(d, XPathConstants.NODE);
        return n == null ? null : n.getTextContent();
    }

    public Path projectPath() {
        return pom.getParent();
    }

    public Set<Path> getModules() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Set<Path> result = new HashSet<>();
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findModules = xpath.compile(
                "/project/modules/module");
        NodeList nl = (NodeList) findModules.evaluate(d, XPathConstants.NODESET);
        int max = nl.getLength();
        for (int i = 0; i < max; i++) {
            String s = nl.item(i).getTextContent().trim();
            if (!s.isEmpty()) {
                result.add(Paths.get(nl.item(i).getTextContent().trim()));
            }
        }
        return result;
    }

    public Document getDocument() throws ParserConfigurationException, SAXException, IOException {
        Document doc = DOCUMENT.get();
        if (doc == null) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(pom.toFile());
            doc.getDocumentElement().normalize();
        }
        return doc;
    }

    public ProjectInfo toProjectInfo() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        return enter(this::_projectInfo);
    }

    private ProjectInfo _projectInfo(Document doc, ProjectScanner scanner) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        return new ProjectInfo(getName(), getVersion(), getDescription(), getArtifactId(), projectPath(), getProperty("module.display.category"),
                getPackaging(), getDevelopers());
    }

    public interface DocConsumer<T> {

        T withDocument(Document doc, ProjectScanner scanner) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException;
    }

    public String getParentVersion() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findParentVersion = xpath.compile(
                "/project/parent/version");
        Element n = (Element) findParentVersion.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            return null;
        }
        return n.getTextContent().trim();
    }

    public String getPackaging() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findPackaging = xpath.compile(
                "/project/packaging");
        Node n = (Node) findPackaging.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            return "jar";
        }
        return n.getTextContent().trim();
    }

    public String getArtifactId() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findArtifactId = xpath.compile(
                "/project/artifactId");
        Element n = (Element) findArtifactId.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            throw new IOException("No artifactId node in " + pom);
        }
        return n.getTextContent().trim();
    }

    public Set<String> getDevelopers() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findDevelopers = xpath.compile(
                "/project/developers/developer/name");
        NodeList nl = (NodeList) findDevelopers.evaluate(d, XPathConstants.NODESET);
        int max = nl.getLength();
        if (max == 0) {
            return Collections.emptySet();
        }
        Set<String> result = new TreeSet<>();
        for (int i = 0; i < max; i++) {
            Node n = nl.item(i);
            String name = n.getTextContent().trim();
            Element par = (Element) n.getParentNode();
            NodeList urls = par.getElementsByTagName("url");
            if (urls.getLength() > 0) {
                name = '[' + name + "](" + urls.item(0).getTextContent() + ")";
            } else if (par.getElementsByTagName("email").getLength() > 0) {
                NodeList emails = par.getElementsByTagName("email");
                if (emails.getLength() > 0) {
                    name = '[' + name + "](mailto:" + emails.item(0).getTextContent() + ")";
                }
            } else {
                String nm = name.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                int oix = nm.indexOf('<');
                int eix = nm.indexOf('>');
                if (oix > 0 && eix > 0 && eix > oix+1) {
                    String baseName = name.substring(0, oix).trim();
                    String email = name.substring(oix + 1, eix).trim();
                    name = '[' + baseName + "](" + email + ")";
                }
            }
            if (!name.isEmpty()) {
                result.add(name);
            }
        }
        return result;
    }

    public String getName() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findName = xpath.compile(
                "/project/name");
        Element n = (Element) findName.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            return null;
        }
        return n.getTextContent().trim();
    }

    public String getDescription() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findDescription = xpath.compile(
                "/project/description");
        Element n = (Element) findDescription.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            return null;
        }
        return n.getTextContent().trim();
    }

    public String getGroupId() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findArtifactId = xpath.compile(
                "/project/groupId");
        Element n = (Element) findArtifactId.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            findArtifactId = xpath.compile(
                    "/project/parent/groupId");
            n = (Element) findArtifactId.evaluate(d, XPathConstants.NODE);
            if (n == null) {
                throw new IOException("No group id in " + pom);
            }
        }
        return n.getTextContent();
    }

    public String getVersion() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
        Document d = getDocument();
        XPathFactory fac = XPathFactory.newInstance();
        XPath xpath = fac.newXPath();
        XPathExpression findVersion = xpath.compile(
                "/project/version");
        Node n = (Node) findVersion.evaluate(d, XPathConstants.NODE);
        if (n == null) {
            return getParentVersion();
        }
        return n.getTextContent();
    }
}
