/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.tools.ant.module.loader.AntProjectDataLoader;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.SharedClassObject;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author Jan Lahoda
 */
public class ProjectUpgradeTest extends NbTestCase {
    
    /** Creates a new instance of ProjectUpgradeTest */
    public ProjectUpgradeTest(String name) {
        super(name);
    }
    
    public void setUp() throws IOException, SAXException, PropertyVetoException {
        UnitUtilities.initLookup();
        UnitUtilities.prepareTest(new String[] {"/org/netbeans/modules/latex/guiproject/resources/mf-layer.xml", "/org/netbeans/modules/latex/resources/mf-layer.xml", "/org/apache/tools/ant/module/resources/AntModuleLayer.xml"}, new Object[] {
            new LaTeXGUIProjectFactory(),
            new LaTeXGUIProjectFactorySourceFactory(),
            new LaTeXFileOwnerQuery(),
            SharedClassObject.findObject(AntProjectDataLoader.class, true),
        });
    }
    
    public void testDoUpgrade() throws IOException, SAXException {
        System.setProperty(LaTeXGUIProjectUpgrader.UPGRADE_OPTION, LaTeXGUIProjectUpgrader.UPGRADE_FORCE);
        doUpgradeProject("prj1");
    }
    
    public void testIgnoreUpgrade() throws IOException, SAXException {
        System.setProperty(LaTeXGUIProjectUpgrader.UPGRADE_OPTION, LaTeXGUIProjectUpgrader.UPGRADE_IGNORE);
        doUpgradeProject("prj2");
    }
    
    public void testGVExists() throws IOException, SAXException {
        System.setProperty(LaTeXGUIProjectUpgrader.UPGRADE_OPTION, LaTeXGUIProjectUpgrader.UPGRADE_FORCE);
        doUpgradeProject("prj3");
    }
    
    public void testNoVersionProperty() throws IOException, SAXException {
        System.setProperty(LaTeXGUIProjectUpgrader.UPGRADE_OPTION, LaTeXGUIProjectUpgrader.UPGRADE_FORCE);
        doUpgradeProject("prj4");
    }
    
    private void doUpgradeProject(String name) throws IOException, SAXException {
        FileObject testDir = UnitUtilities.makeScratchDir(this);
        File       testDirFile = FileUtil.toFile(testDir);
        
        prepareProject10("/org/netbeans/modules/latex/guiproject/data/upgrade/upgrade1to11/" + name, name, testDirFile);
        
        File destinationDir = new File(testDirFile, name);
        File projectFile = new File(destinationDir, "nbproject-" + name + ".tex");
        
        assertTrue(projectFile.exists());
        assertTrue(projectFile.isDirectory());
        
        FileObject project = FileUtil.toFileObject(projectFile);
        
        assertNotNull(project);
        
        Project p = ProjectManager.getDefault().findProject(project);
        
        assertTrue(p instanceof LaTeXGUIProject);
        
        //performe upgrade:
        LaTeXGUIProjectUpgrader.getUpgrader().upgrade((LaTeXGUIProject) p);
        
        //upgrade should be done. test it:
        checkProperties("/org/netbeans/modules/latex/guiproject/data/upgrade/upgrade1to11/golden-" + name + "/nbproject-" + name + ".tex/build-settings.properties", new File(new File(destinationDir, "nbproject-" + name + ".tex"), "build-settings.properties"));
        checkBuildScript("/org/netbeans/modules/latex/guiproject/data/upgrade/upgrade1to11/golden-" + name + "/nbproject-" + name + ".tex/build.xml", new File(new File(destinationDir, "nbproject-" + name + ".tex"), "build.xml"));
    }
    
    private void checkBuildScript(String goldenResource, File build) throws IOException, SAXException {
        InputStream golden = null;
        InputStream file   = null;
        
        try {
            golden = ProjectUpgradeTest.class.getResourceAsStream(goldenResource);
            file   = new FileInputStream(build);
            
            Document goldenDocument = XMLUtil.parse(new InputSource(golden), false, false, null, null);
            Document fileDocument   = XMLUtil.parse(new InputSource(file), false, false, null, null);
            
            assertTrue(compareDOMs(goldenDocument, fileDocument));
        } finally {
            if (golden != null) {
                try {
                    golden.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
    }
    
    private void checkProperties(String goldenResource, File properties) throws IOException {
        InputStream golden = null;
        InputStream file   = null;
        
        try {
            golden = ProjectUpgradeTest.class.getResourceAsStream(goldenResource);
            
            EditableProperties goldenProperties = new EditableProperties();
            
            goldenProperties.load(golden);
            golden.close();
            golden = null;
            
            file   = new FileInputStream(properties);
            
            EditableProperties fileProperties   = new EditableProperties();
            
            fileProperties.load(file);
            file.close();
            file = null;
            
            StringBuffer errors = new StringBuffer();
            
            for (Iterator g = goldenProperties.keySet().iterator(); g.hasNext(); ) {
                String key = (String) g.next();
                String goldenValue = goldenProperties.getProperty(key);
                String fileValue = fileProperties.getProperty(key);
                
                fileProperties.remove(key);
                
                if (goldenValue == null && fileValue == null)
                    continue;
                
                if (goldenValue == null) {
                    errors.append("key=" + key + ", golden=null, file=" + fileValue);
                    errors.append(System.getProperty("line.separator"));
                    continue;
                }
                
                if (!goldenValue.equals(fileValue)) {
                    boolean matchesPattern = "mainfile".equals(key);
                    
                    if (matchesPattern) {
                        try {
                            matchesPattern = Pattern.matches(goldenValue, fileValue);
                        } catch (PatternSyntaxException e) {
                            //Effectivelly ignore the exception:
                            e.printStackTrace(getLog());
                        }
                    }
                        
                    if (!matchesPattern) {
                        errors.append("key=" + key + ", golden=" + goldenValue + ", file=" + fileValue);
                        errors.append(System.getProperty("line.separator"));
                    }
                    continue;
                }
            }
            
            for (Iterator f = fileProperties.keySet().iterator(); f.hasNext(); ) {
                String key = (String) f.next();
                String fileValue = fileProperties.getProperty(key);
                
                errors.append("key=" + key + " not in golden, file=" + fileValue);
                errors.append(System.getProperty("line.separator"));
            }
            
            assertTrue(errors.toString(), errors.length() == 0);
        } finally {
            if (golden != null) {
                try {
                    golden.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
    }
    
    private boolean compareDOMs(Document left, Document right) {
        return compareElements(left.getDocumentElement(), right.getDocumentElement());
    }
    
    private boolean compareElements(Element left, Element right) {
        String leftName = left.getNodeName();
        String rightName = right.getNodeName();
        
        if (!leftName.equals(rightName)) {
            return false;
        }
        
        NamedNodeMap leftAttributes = left.getAttributes();
        NamedNodeMap rightAttributes = right.getAttributes();
        
        if (!compareAttributes(leftAttributes, rightAttributes))
            return false;
        
        if (!compareAttributes(rightAttributes, leftAttributes))
            return false;
        
        NodeList leftChilds = left.getChildNodes();
        NodeList rightChilds = right.getChildNodes();
        
        int leftIndex = 0;
        int rightIndex = 0;
        
        while (true) {
            while (leftIndex < leftChilds.getLength() && leftChilds.item(leftIndex).getNodeType() != Document.ELEMENT_NODE)
                leftIndex++;
            
            while (rightIndex < rightChilds.getLength() && rightChilds.item(rightIndex).getNodeType() != Document.ELEMENT_NODE)
                rightIndex++;
            
            if (leftIndex >= leftChilds.getLength() && rightIndex >= rightChilds.getLength())
                break;
            
            if (leftIndex >= leftChilds.getLength())
                return false;
            
            if (rightIndex >= rightChilds.getLength())
                return false;
            
            if (!compareElements((Element) leftChilds.item(leftIndex), (Element) rightChilds.item(rightIndex)))
                return false;
            
            leftIndex++;
            rightIndex++;
        }
        
        return true;
    }
    
    private boolean compareAttributes(NamedNodeMap leftAttributes, NamedNodeMap rightAttributes) {
        for (int cntr = 0; cntr < leftAttributes.getLength(); cntr++) {
            Node leftAttr = leftAttributes.item(cntr);
            
            assert leftAttr.getNodeType() == Node.ATTRIBUTE_NODE;
            
            Node rightAttr = rightAttributes.getNamedItem(leftAttr.getNodeName());
            
            if (rightAttr == null)
                return false;
            
            assert rightAttr.getNodeType() == Node.ATTRIBUTE_NODE;
            
            if (!leftAttr.getNodeValue().equals(rightAttr.getNodeValue()))
                return false;
        }
        
        return true;
    }
    
    private static void prepareProject10(String dir, String name, File temporaryDir) throws IOException {
        File destinationDir = new File(temporaryDir, name);
        
        copyFiles(dir, destinationDir);
        absolutizeMainFile(name, destinationDir);
    }
    
    //the following methods are only support methods for prepareProject
    //and should not be called directly:
    private static void copyFile(String resource, File toCreate) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        
        try {
            in = ProjectUpgradeTest.class.getResourceAsStream(resource);
            
            assertNotNull(in);
            
            out = new FileOutputStream(toCreate);
            
            int read;
            
            while ((read = in.read()) != (-1)) {
                out.write(read);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
    }
    
    private static void copyFiles(String dir, File destinationDir) throws IOException {
        InputStream files = null;
        BufferedReader r = null;
        
        try {
            files = ProjectUpgradeTest.class.getResourceAsStream(dir + "/files");
            r = new BufferedReader(new InputStreamReader(files));
            
            String file = null;
            
            while ((file = r.readLine()) != null) {
                //Copy the file:
                File toCreate = new File(destinationDir, file);
                
                toCreate.getParentFile().mkdirs();
                
                copyFile(dir + "/" + file, toCreate);
            }
        } finally {
            if (r != null) {
                try {
                    r.close();
                    files = null;
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (files != null) {
                try {
                    files.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
    }
    
    private static void absolutizeMainFile(String name, File destinationDir) throws IOException {
        //absolutize the main file name:
        InputStream  ins  = null;
        OutputStream out  = null;
        
        try {
            EditableProperties properties = new EditableProperties();
            File propertiesFile = new File(new File(destinationDir, "nbproject-" + name + ".tex"), "build-settings.properties");
            
            if (propertiesFile == null) {
                //strange...
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "During checking for upgrade of project data, properties file not found!");
                return ;
            }
            
            ins = new FileInputStream(propertiesFile);
            properties.load(ins);
            ins.close();
            ins = null;
            
            String mainFilePath = (String) properties.getProperty("mainfile");
            File mainFile = new File(propertiesFile.getParentFile(), mainFilePath);
            
            properties.put("mainfile", FileUtil.normalizeFile(mainFile).getAbsolutePath());
            
            out  = new FileOutputStream(propertiesFile);
            
            properties.store(out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
        }
        
    }
}
