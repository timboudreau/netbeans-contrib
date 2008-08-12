/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package buildhelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.netbeans.xml.schema.productdescription.*;

/**
 *
 * @author lm153972
 */
public class BuildHelper {

    String xmlFileName;
    String guiDirectory;
    String toolchainDirectory;

    Product product;

    Installation gui;
    Installation toolchain;

    public BuildHelper(String xmlFileName, String guiDirectory, String toolchainDirectory) {
        this.xmlFileName = xmlFileName;
        this.guiDirectory = guiDirectory;
        this.toolchainDirectory = toolchainDirectory;
        try {
            readXML();
        } catch (Exception e) {
            System.out.println("Exception .... ");
            e.printStackTrace();
        }
    }

    void readXML() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Product.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        product = (Product)unmarshaller.unmarshal(new File(xmlFileName));
        for (Installation installation : product.getInstallation()) {
            if("toolchain".equals(installation.getName())) {
                toolchain = installation;
            } else {
                gui = installation;
            }
        }
    }



    
    void generateToolchain() throws Exception {

        Map<Platform, PrintWriter> files = new HashMap<Platform, PrintWriter>(3);

        for (Platform platform : Platform.values()) {
            String platformName = platform.equals(platform.LINUX) ? "intel-Linux" :
                        platform.equals(platform.SOLARIS_X_86) ? "intel-S2" : "sparc-S2";
            File list  = new File(toolchainDirectory + "/" + "package-list."
                    + platformName);
            System.out.println("Creating " + list.getPath());
            if (list.exists()) {
                list.delete();
            }
            files.put(platform, new PrintWriter(list));
        }

        for(Componentref ref : toolchain.getComponentref()) {
            Component component = (Component) ref.getUid();
            System.out.println("ref = " + component.getName());
            for (Block block : component.getBlock()) {                
                for (Unit unit : block.getNbmOrSvrOrRpm()) {
                    /*if (!(unit instanceof )) {
                        throw new Exception("Type " + unit.getClass().getName()
                                + " is not supported in toolchain installer.");
                    }*/
                    files.get(block.getPlatform()).write(unit.getSource() + "\n");

                }
            }
        }
        for (PrintWriter writer : files.values()) {
            writer.close();
        }
    }

    void generateGUI() throws Exception{
        for (Group group : gui.getGroup()) {
            generateComponentCL(group.getName(),
                    group.getUid(),
                    "", true);
            generateInfra(group);
        }
        PrintWriter writer = new PrintWriter(new File(guiDirectory + "/infra/native.lst"));
        for (Component component : product.getComponent()) {
            //Component component = (Component) ref.getUid();
            generateComponentCL(component.getName(),
                    component.getUid() ,
                    component.getDescription(), false);
           generateInfra(component);
           writer.print(component.getUid() + ",1.0.0.0.0\n");
        }
        writer.close();
    }


    void generateInfra(Component component) throws Exception{
        copyDir(guiDirectory + "/infra/products/native/native", guiDirectory + "/infra/products/native/"
                + component.getUid());
         Properties properties = new Properties();
                /* Create a component description */
                if (properties.getProperty("product.uid") == null) {
                    properties.setProperty("product.uid", component.getUid());
                }
                properties.setProperty("release.parent.uid", "");
                properties.setProperty("cvs.path", "components/products/" +
                    component.getUid());
                properties.setProperty("basedir", "${nbi.netbeans.dir}/infra/components/infra/native/"
                        + component.getUid());
              
                for (Block block : component.getBlock()) {
                    Platform platform = block.getPlatform();
                    String pkg = platform.equals(platform.LINUX) ? "rpm" : "svr";
                    String platformName = platform.equals(platform.LINUX) ? "intel-Linux" :
                        platform.equals(platform.SOLARIS_X_86) ? "intel-S2" : "sparc-S2";
                    String path = "${installed.bits.dir}/" + platformName;                    
                    properties.setProperty("product.data.length." + platform.value(),
                            String.valueOf(block.getNbmOrSvrOrRpm().size()));
                    int k = 0;
                    for (Unit unit : block.getNbmOrSvrOrRpm()) {
                        k++;
                        String unitName = unit.getSource().replaceAll("VERSION", "12.0-1");
                        properties.setProperty("product.data." + String.valueOf(k) + ".uri." + platform.value(), path + "/" + unitName);
                        properties.setProperty("product.data." + String.valueOf(k) + ".zip", "false");
                    }
                }
                new File(guiDirectory + "/infra/products/native/"
                        + component.getUid()).mkdirs();
                properties.store(new FileOutputStream(
                        new File(guiDirectory + "/infra/products/native/"
                        + component.getUid()
                        + "/build.properties")), "");
    }

    void generateInfra(Group group) throws Exception {
        copyDir(guiDirectory + "/infra/groups/native/", guiDirectory + "/infra/groups/"
                + group.getUid());
        Properties properties = new Properties();
        properties.load(new FileInputStream(
                        new File(guiDirectory + "/infra/groups/"
                        + group.getUid()
                        + "/build.properties")));
                /* Create a component description */                
                properties.setProperty("group.uid", group.getUid());
                properties.setProperty("basedir", "${nbi.netbeans.dir}/infra/groups/" 
                        + group.getUid());
                properties.setProperty("cvs.path", "components/groups/" +
                    group.getUid()  );

                new File(guiDirectory + "/infra/groups/"
                        + group.getUid()).mkdirs();
                properties.store(new FileOutputStream(
                        new File(guiDirectory + "/infra/groups/"
                        + group.getUid()
                        + "/build.properties")), "");
    }
        
    void generateComponentCL(String name, String dir, String description, boolean isGroup) throws Exception {
        String type = isGroup ? "group" : "product";
        File wd = new File(guiDirectory + "/components/" + type + "s");
//            FileUtils .copyFile(new File(wd, "native"), new File(wd, dir), true);
        copyDir(wd.getAbsolutePath() + "/native", wd.getAbsolutePath() + "/" + dir);
        Properties properties = new Properties();
        properties.setProperty(type + ".description", description);
        properties.setProperty(type + ".display.name", name);
        properties.store(new FileOutputStream(new File(wd + File.separator + dir + File.separator + "data", "Bundle.properties")), "");
    }

    static void copyDir(String src, String dst) {
        try {
             Process p = Runtime.getRuntime().exec("/bin/rm -rf " + dst);
             if (p.waitFor() != 0 ) {
                 System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA ");
             }
             p = Runtime.getRuntime().exec("/bin/cp -r " + src + " " + dst);
             if (p.waitFor() != 0 ) {
                 System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb ");
             }
        } catch (Exception ex) {
            System.out.println("Exception during copy ...........");
            ex.printStackTrace();
        }
    }

    void generate() {
        try {
            generateGUI();
            generateToolchain();
        } catch (Exception e) {
            System.out.println("Unexpected Exception during generation......");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.exit(-1);
        }
        new BuildHelper(args[0], args[1], args[2]).generate();
    }
}
