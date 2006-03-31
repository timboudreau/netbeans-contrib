/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.netbeans.api.wizard.WizardDisplayer;
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

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

        if (args.length > 0 && "reset".equals(args[0])) {
            Preferences.userNodeForPackage(
                    InstructionsPage.class).putBoolean(
                    InstructionsPage.KEY_SHOW_INSTRUCTIONS, true);
        }
        
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
        
        Wizard wiz = WizardPage.createWizard(pages, new WRP());
        File f = (File) WizardDisplayer.showWizard (wiz);
        if (f != null) {
            System.err.println("Created " + f.getPath());
        }
    }
 
    private static class WRP implements WizardResultProducer {
        public Object finish(Map map) throws WizardException {
//            outMap (map);
            
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
//                throw new WizardException ("Couldn't write to " + destFolder);
                throw new IllegalArgumentException ();
            }
            File f = new File (outDir, destFileName);
            if (f.exists()) {
                f.delete();
            }
            try {
                if (!f.createNewFile()) {
                    throw new IllegalArgumentException ("Couldn't create file");
                }
                
                char[] cname = codeName.toCharArray();
                for (int i=0; i < cname.length; i++) {
                    if (cname[i] == '.') {
                        cname[i] = '-';
                    }
                }
                String moduleJarName = new String (cname) + ".jar";

                String jarFileNameSimple = new File (jarFileName).getName();
                
                NbmFileModel nbm = new NbmFileModel (f.getPath());
                ModuleModel module = new ModuleModel ("netbeans/modules/" + moduleJarName, codeName, description, version, displayName, minJDK);
                ModuleInfoModel infoXml = new ModuleInfoModel (module, homepage, author, license);
                
                nbm.add (module);
                nbm.add (infoXml);
                
                JarToCopyModel libJar = new JarToCopyModel ("netbeans/libs/" + jarFileNameSimple, jarFileName);
                nbm.add (libJar);

                String srcFileNameSimple = null;
                if (sourceJar != null && !"".equals(sourceJar)) {
                    srcFileNameSimple = new File (sourceJar).getName();
                    JarToCopyModel srcJarMdl = new JarToCopyModel ("netbeans/sources/" + srcFileNameSimple, sourceJar);
                    nbm.add (srcJarMdl);
                }
                String docsJarNameSimple = null;
                if (docsJar != null && !"".equals(docsJar)) {
                    docsJarNameSimple = new File (docsJar).getName();
                    JarToCopyModel docsJarMdl = new JarToCopyModel ("netbeans/docs/" + docsJarNameSimple, docsJar);
                    nbm.add (docsJarMdl);
                }
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
                
                layer.addLibraryName(companyNameUnderscores);
                
                LibDescriptorModel libDesc = new LibDescriptorModel (codeNameSlashes + "/" + companyNameUnderscores + ".xml", companyNameUnderscores, codeName, jarFileNameSimple, srcFileNameSimple, docsJarNameSimple);
                module.addFileEntry(libDesc);
                module.addFileDisplayName("org-netbeans-api-project-libraries/Libraries/" + companyNameUnderscores + ".xml", displayName);
                module.addFileDisplayName(companyNameUnderscores, displayName);

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
                module.addFileDisplayName(codeNameSlashes + "/" + companyNameUnderscores, displayName);
                
                ModuleXMLModel mxml = new ModuleXMLModel (codeName,version);
                nbm.add(mxml);
                
                OutputStream out = new BufferedOutputStream (new FileOutputStream (f));
                nbm.write(out);
                out.close();
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            
            return f;
        }
    }
    
    private static final void outMap (Map wizardData) {
        for (Iterator i = wizardData.keySet().iterator(); i.hasNext();) {
            Object key = (Object) i.next();
            Object val = wizardData.get(key);
            System.out.println(key + "=" + val);
        }
    }
    
}
