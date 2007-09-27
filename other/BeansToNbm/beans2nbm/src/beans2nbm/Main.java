/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package beans2nbm;

import beans2nbm.gen.JarInfo;
import beans2nbm.gen.JarToCopyModel;
import beans2nbm.gen.LayerFileModel;
import beans2nbm.gen.LibDescriptorModel;
import beans2nbm.gen.ModuleInfoModel;
import beans2nbm.gen.ModuleModel;
import beans2nbm.gen.ModuleXMLModel;
import beans2nbm.gen.NbmFileModel;
import beans2nbm.gen.PaletteItemFileModel;
import beans2nbm.ui.AuthorInfoPage;
import beans2nbm.ui.BeanItem;
import beans2nbm.ui.InstructionsPage;
import beans2nbm.ui.LibDataPage;
import beans2nbm.ui.LocateJarPage;
import beans2nbm.ui.OutputLocationPage;
import beans2nbm.ui.SelectBeansPage;
import java.io.BufferedOutputStream;
import java.io.CharConversionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

/**
 *
 * @author Tim Boudreau
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && "reset".equals(args[0])) {
            Preferences.userNodeForPackage(
                    InstructionsPage.class).putBoolean(
                    InstructionsPage.KEY_SHOW_INSTRUCTIONS, true);
        }
        
        setLookAndFeel();
        
//        go (getPageList());

        WizardDisplayer.showWizard(new Brancher(new WRP()).createWizard());
    }

    static Class[] getPageList() {
        Class[] pages = InstructionsPage.shouldShowInstructions() ?
            new Class[] {
                InstructionsPage.class,
                LocateJarPage.class, 
                SelectBeansPage.class,
                LibDataPage.class, 
                AuthorInfoPage.class,
                OutputLocationPage.class,
        } : new Class[] {
                LocateJarPage.class, 
                SelectBeansPage.class,
                LibDataPage.class, 
                AuthorInfoPage.class,
                OutputLocationPage.class,
        };
        return pages;
    }
    
    public static void go (Class[] pages) {
        Wizard wiz = WizardPage.createWizard(pages, new WRP());
        File f = (File) WizardDisplayer.showWizard (wiz);
        if (f != null) {
            System.out.println("Created " + f.getPath());
        }
    }
    
    public static class BackgroundBuilder extends DeferredWizardResult {
        public BackgroundBuilder() {
            super (false);
        }
        
        public void start(final Map map, final ResultProgressHandle progress) {
            final int total = 15;
            progress.setProgress("Building NBM", 1, total);
            
            String destFolder = (String) map.get ("destFolder");
            String destFileName = (String) map.get ("destFileName");
            String description = (String) map.get ("description");
            String version = (String) map.get ("libversion");
            String homepage = (String) map.get ("homepage");
            JarInfo jarInfo = (JarInfo) map.get ("jarInfo");
            String codeName = (String) map.get ("codeName");
            String jarFileName = (String) map.get ("jarFileName");
            String author = (String) map.get ("author");
            String docsJar = (String) map.get ("docsJar");
            String sourceJar = (String) map.get ("sourceJar");
            String displayName = (String) map.get ("displayName");
            String license = (String) map.get ("license");
            String minJDK = (String) map.get ("javaVersion");

            
            
            File outDir = new File (destFolder);
            if (!outDir.isDirectory()) {
                throw new IllegalArgumentException (destFolder + " does not " +
                        "exist or is not a directory");
            }
            File f = new File (outDir, destFileName);
            if (f.exists()) {
                f.delete();
            }
            try {
                if (!f.createNewFile()) {
                    progress.failed("Could not create " + f.getPath(), true);
                    return;
                }
                progress.setProgress(2, total);
                char[] cname = codeName.toCharArray();
                for (int i=0; i < cname.length; i++) {
                    if (cname[i] == '.') {
                        cname[i] = '-';
                    }
                }
                String moduleJarName = new String (cname) + ".jar";

                String jarFileNameSimple = new File (jarFileName).getName();
                progress.setProgress(3, 10);
                NbmFileModel nbm = new NbmFileModel (f.getPath());
                progress.setProgress (3, total);
                
                ModuleModel module = new ModuleModel ("netbeans/modules/" + moduleJarName, codeName, description, version, displayName, minJDK);
                progress.setProgress(4, total);
                ModuleInfoModel infoXml = new ModuleInfoModel (module, homepage, author, license);
                progress.setProgress(5, total);
                
                nbm.add (module);
                nbm.add (infoXml);
                
                JarToCopyModel libJar = new JarToCopyModel ("netbeans/libs/" + jarFileNameSimple, jarFileName);
                progress.setProgress (7, total);
                nbm.add (libJar);

                String srcFileNameSimple = null;
                if (sourceJar != null && !"".equals(sourceJar)) {
                    srcFileNameSimple = new File (sourceJar).getName();
                    JarToCopyModel srcJarMdl = new JarToCopyModel ("netbeans/sources/" + srcFileNameSimple, sourceJar);
                    nbm.add (srcJarMdl);
                }
                progress.setProgress(8, total);
                String docsJarNameSimple = null;
                if (docsJar != null && !"".equals(docsJar)) {
                    docsJarNameSimple = new File (docsJar).getName();
                    JarToCopyModel docsJarMdl = new JarToCopyModel ("netbeans/docs/" + docsJarNameSimple, docsJar);
                    nbm.add (docsJarMdl);
                }
                progress.setProgress(9, total);
                cname = codeName.toCharArray();
                for (int i=0; i < cname.length; i++) {
                    if (cname[i] == '.') {
                        cname[i] = '/';
                    }
                }
                String codeNameSlashes = new String (cname);
                
                char[] c = displayName.toCharArray();
                for (int i=0; i < c.length; i++) {
                    if (Character.isWhitespace(c[i])) {
                        c[i] = '_';
                    }
                }
                
                String companyNameUnderscores = new String (c);
                
                LayerFileModel layer = new LayerFileModel (codeNameSlashes + "/layer.xml", companyNameUnderscores, codeName);
                module.addFileEntry(layer);
                progress.setProgress (10, total);
                
                layer.addLibraryName(companyNameUnderscores);
                
                LibDescriptorModel libDesc = new LibDescriptorModel (codeNameSlashes + "/" + companyNameUnderscores + ".xml", companyNameUnderscores, codeName, jarFileNameSimple, srcFileNameSimple, docsJarNameSimple);
                module.addFileEntry(libDesc);
                module.addFileDisplayName("org-netbeans-api-project-libraries/Libraries/" + companyNameUnderscores + ".xml", displayName);
                module.addFileDisplayName(companyNameUnderscores, displayName);
                progress.setProgress (11, total);
                
                if (jarInfo != null) {
                    for (Iterator i=jarInfo.getBeans().iterator(); i.hasNext();) {
                        String pathInBeanJar = (String) i.next();
                        BeanItem bi = new BeanItem (pathInBeanJar);
                        String beanClassName = bi.getClassName();
                        String beanSimpleName = bi.getSimpleName();
                        String paletteItemPathInLayer = "FormDesignerPalette/" + companyNameUnderscores + "/" + beanSimpleName + ".palette_item";
                        PaletteItemFileModel palMdl = new PaletteItemFileModel (codeNameSlashes + "/" + beanSimpleName + "_paletteItem.xml", companyNameUnderscores, beanClassName);
                        module.addFileEntry(palMdl);
                        layer.addBeanEntry(companyNameUnderscores, beanSimpleName);
                        module.addFileDisplayName (paletteItemPathInLayer, beanClassName);
                    }
                }
                progress.setProgress (11, total);
                module.addFileDisplayName(codeNameSlashes + "/" + companyNameUnderscores, displayName);
                
                ModuleXMLModel mxml = new ModuleXMLModel (codeName,version);
                nbm.add(mxml);
                progress.setProgress (12, total);
                
                OutputStream out = new BufferedOutputStream (new FileOutputStream (f));
                progress.setProgress (13, total);
                nbm.write(out);
                progress.setProgress (14, total);
                
                out.close();
                progress.setProgress (15, total);
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
                progress.failed(ioe.getMessage(), true);
            }
            String summaryInfo = "Created " + 
                    f.getPath() + " successfully.\n\nYou can also generate " +
                    "new versions of this module via an Ant script, using the " +
                    "JAR file you are running in order to see this wizard, e.g:\n\n" 
                    + getAntInstructions(map) + "\n\nThe sourceJar and " +
                    "docsJar attributes are optional.  License may be any of " +
                    "the ones named in this wizard (apache, artistic, bsd," +
                    "cddl, gpl, lgpl), or a path to a file containing a " +
                    "license, or the license text itself.\n\nRemember always to " +
                    "increment the version number when you release a new " +
                    "version.";
            
            //May not be present
            summaryInfo = summaryInfo.replace("sourceJar=\"\"" , "");
            summaryInfo = summaryInfo.replace("docsJar=\"\"", "");
            
            progress.finished(Summary.create (summaryInfo, f));
        }
    }
    
    private static String getAntInstructions (Map map) {
        String s = BASE;
        map = new HashMap (map);
        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            Object val = map.get(key);
            if (val instanceof String) {
                try {
                    val = XMLUtil.toAttributeValue(val.toString());
                    map.put (key, val);
                } catch (CharConversionException cce) {
                    continue;
                }
            }
        }
        String destFolder = (String) map.get ("destFolder");
        String destFileName = (String) map.get ("destFileName");
        String description = (String) map.get ("description");
        String version = (String) map.get ("libversion");
        String homepage = (String) map.get ("homepage");
        String codeName = (String) map.get ("codeName");
        String jarFileName = (String) map.get ("jarFileName");
        String author = (String) map.get ("author");
        String docsJar = (String) map.get ("docsJar");
        String sourceJar = (String) map.get ("sourceJar");
        String displayName = (String) map.get ("displayName");
        String license = (String) map.get ("license");
        String minJDK = (String) map.get ("javaVersion");
        String licenseFile = (String) map.get ("licenseFile");
        String licenseName = (String) map.get ("licenseName");
        if (licenseName != null) {
            license = licenseName;
        } else if (licenseFile != null) {
            license = licenseFile;
        } else {
            license = "path/to/license.txt";
        }

        s = s.replace("#DEST_FOLDER", destFolder);
        s = s.replace("#NBM_NAME", destFileName);
        s = s.replace("#DESCRIPTION", description);
        s = s.replace("#CODENAME", codeName);
        s = s.replace("#JARFILENAME", jarFileName);
        s = s.replace("#AUTHOR", author);
        s = s.replace("#MIN_JDK", minJDK);
        s = s.replace("#SOURCE_JAR", sourceJar == null ? "sourceJar.jar" : sourceJar);
        s = s.replace("#DOCS_JAR", docsJar == null ? "docsJar.jar" : docsJar);
        s = s.replace("#DISPLAY_NAME", displayName);
        s = s.replace("#LICENSE", license);
        s = s.replace("#CODENAME", codeName);
        s = s.replace("#VERSION", version);
        s = s.replace("#HOMEPAGE", homepage);
        return s;
    }
    
    private static final String BASE = 
        "<target name=\"nbm\" description=\"Generate a NetBeans Module\">\n" +
        "  <taskdef classpath=\"lib/beans2nbm.jar\" classname=\"beans2nbm.ant.GenNbmTask\"\n" +
        "  name=\"nbm\"/>\n\n" +
        "  <nbm destFolder=\"#DEST_FOLDER\" destFileName=\"#NBM_NAME\"\n" + 
        "    description=\"#DESCRIPTION\" version=\"1.0\" homepage=\"#HOMEPAGE\"\n" +
        "    codeName=\"#CODENAME\" jarFileName=\"#JARFILENAME\" \n"+
        "    author=\"#AUTHOR\" displayName=\"#DISPLAY_NAME\" license=\"#LICENSE\"\n" +
        "    minJDK=\"#MIN_JDK\" sourceJar=\"#SOURCE_JAR\" docsJar=\"#DOCS_JAR\"/>\n</target>";
 
    private static class WRP implements WizardResultProducer {
        public Object finish(Map map) throws WizardException {
            return new BackgroundBuilder();
//            outMap (map);
        }

        public boolean cancel (Map m) {
            System.exit(0);
            return true;
        }
    }
    
    private static final void outMap (Map wizardData) {
        for (Iterator i = wizardData.keySet().iterator(); i.hasNext();) {
            Object key = (Object) i.next();
            Object val = wizardData.get(key);
        }
    }
    
}
