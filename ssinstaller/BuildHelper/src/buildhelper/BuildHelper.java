/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buildhelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
    String distrs;

    int OFFSET_STEP = 1000;

    public BuildHelper(String xmlFileName, String guiDirectory, String toolchainDirectory) {
        this.xmlFileName = xmlFileName;
        this.guiDirectory = guiDirectory;
        this.toolchainDirectory = toolchainDirectory;
        this.distrs = System.getenv("DISTRS");
        if (distrs == null) {
            distrs = "intel-S2 sparc-S2 intel-Linux";
        }
        System.out.println("Generating data for " + distrs);
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
        product = (Product) unmarshaller.unmarshal(new File(xmlFileName));
        for (Installation installation : product.getInstallation()) {
            if ("toolchain".equals(installation.getName())) {
                toolchain = installation;
            } else {
                gui = installation;
            }
        }
    }

    void generateToolchain() throws Exception {

        Map<Platform, PrintWriter> files = new HashMap<Platform, PrintWriter>(3);

        for (Platform platform : Platform.values()) {
            String platformName = platform.equals(platform.LINUX) ? "intel-Linux" : platform.equals(platform.SOLARIS_X_86) ? "intel-S2" : "sparc-S2";     
            File list = new File(toolchainDirectory + "/" + "package-list." + platformName);
            System.out.println("Creating " + list.getPath());
            if (list.exists()) {
                list.delete();
            }
            files.put(platform, new PrintWriter(list));
        }

        for (Componentref ref : toolchain.getComponentref()) {
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

    void generateGUI() throws Exception {
        List<Component> toGenerate = new ArrayList();
        for (Group group : gui.getGroup()) {
            generateComponentCL(group.getName(),
                    group.getUid(),
                    "", true);
            generateInfra(group);
            for(Componentref ref : group.getComponentref()) {
                toGenerate.add((Component)ref.getUid());
            }
        }
        for(Componentref ref : gui.getComponentref()) {
            toGenerate.add((Component)ref.getUid());
        }
        
        copyDir(guiDirectory + "/infra/products/native/common.properties", guiDirectory + "/infra/products/build/common.properties");
        PrintWriter writer = new PrintWriter(new File(guiDirectory + "/infra/native.lst"));
        for (Component component : toGenerate) {
            //Component component = (Component) ref.getUid();
            generateComponentCL(component.getName(),
                    component.getUid(),
                    component.getDescription(), false);
            generateInfra(component);
            writer.print(component.getUid() + ",1.0.0.0.0\n");
        }
        writer.close();
    }

     int offset = 1000;
    void generateInfra(Component component) throws Exception {
        copyDir(guiDirectory + "/infra/products/native/native", guiDirectory + "/infra/products/build/" + component.getUid());

        Properties properties = new Properties();
        /* Create a component description */
        if (properties.getProperty("product.uid") == null) {
            properties.setProperty("product.uid", component.getUid());
        }
        String parent = "";
        for (Group group : gui.getGroup()) {
            for (Componentref ref : group.getComponentref()) {
                if (component.equals(ref.getUid())) {
                    parent = group.getUid();
                }
            }
        }
        properties.setProperty("release.parent.uid", parent);
        properties.setProperty("product.offset", offset + "");
        offset += OFFSET_STEP;
        properties.setProperty("cvs.path", "components/products/" +
                component.getUid());
        properties.setProperty("basedir", "${nbi.netbeans.dir}/infra/components/infra/native/" + component.getUid());


            properties.setProperty("product.requirements.length", "1");
            properties.setProperty("product.requirements.1.uid", "ss-base");
            properties.setProperty("product.requirements.1.version-lower", "1.0.0.0.0");
            properties.setProperty("product.requirements.1.version-upper", "1.0.0.0.0");

        if (component.getDependency() != null) {
            String uid = ((Component)component.getDependency()).getUid();

            properties.setProperty("product.requirements.length", "2");
            properties.setProperty("product.requirements.2.uid", ((Component)component.getDependency()).getUid());
            properties.setProperty("product.requirements.2.version-lower", "1.0.0.0.0");
            properties.setProperty("product.requirements.2.version-upper", "1.0.0.0.0");
        }
  
    for (Block block : component.getBlock()) {
            Platform platform = block.getPlatform();
            String pkg = platform.equals(platform.LINUX) ? "rpm" : "svr";
            String platformName = platform.equals(platform.LINUX) ? "intel-Linux" : platform.equals(platform.SOLARIS_X_86) ? "intel-S2" : "sparc-S2";
             if  (!distrs.contains(platformName)) {
                continue;
            }
        
            String path = "${installed.bits.dir}/" + platformName;
            properties.setProperty("product.data.length" ,
                    String.valueOf(block.getNbmOrSvrOrRpm().size()));
            int k = 0;
            for (Unit unit : block.getNbmOrSvrOrRpm()) {
                k++;
                String unitName = unit.getSource().replaceAll("VERSION", "12.0-1");
                properties.setProperty("product.data." + String.valueOf(k) + ".uri."  + platform.value() , path + "/" + unitName);
                properties.setProperty("product.data." + String.valueOf(k) + ".zip", "false");
            }
        }
        new File(guiDirectory + "/infra/products/build/" + component.getUid()).mkdirs();
        properties.store(new FileOutputStream(
                new File(guiDirectory + "/infra/products/build/" + component.getUid() + "/build.properties")), "");
    }

    void generateInfra(Group group) throws Exception {
        copyDir(guiDirectory + "/infra/groups/native/", guiDirectory + "/infra/groups/" + group.getUid());
        Properties properties = new Properties();
        properties.load(new FileInputStream(
                new File(guiDirectory + "/infra/groups/" + group.getUid() + "/build.properties")));
        /* Create a component description */
        properties.setProperty("group.uid", group.getUid());
        properties.setProperty("basedir", "${nbi.netbeans.dir}/infra/groups/" + group.getUid());
        properties.setProperty("cvs.path", "components/groups/" +
                group.getUid());

        new File(guiDirectory + "/infra/groups/" + group.getUid()).mkdirs();
        properties.store(new FileOutputStream(
                new File(guiDirectory + "/infra/groups/" + group.getUid() + "/build.properties")), "");
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
            if (p.waitFor() != 0) {
                System.out.println("Could not remove dst : " + dst);
            }
            new File(dst).getParentFile().mkdirs();
            p = Runtime.getRuntime().exec("/bin/cp -r " + src + " " + dst);
            if (p.waitFor() != 0) {
                System.out.println("Could not copy " + src + " to " + dst);
            }
        } catch (Exception ex) {
            System.out.println("Exception during copy " + src + " to " + dst);
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
            System.out.println("Error during run BuildHelper.");
            System.exit(-1);
        }
        new BuildHelper(args[0], args[1], args[2]).generate();
    }
}
