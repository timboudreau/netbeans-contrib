/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buildhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Main {

    private static final String GROUP = "group";
    private static final String COMPONENT = "component";
    private static final String PRODUCT = "product";
    private static final String NAME = "name";
    private static final String PKGUNIT = "pkgunit";
    private static final String PKGNAME = "pkgName";
    private static final String UID = "uid";
    
       private static int size(String packageName) throws Exception {
        String pkgRoot = "/usr/bin";        
        try {
             Process p = new ProcessBuilder(pkgRoot + "/pkginfo", "-d",                  
                    packageName, "-l").start();
            if (p.waitFor() != 0) {
                String line;
                StringBuffer message = new StringBuffer();
                message.append("Error = ");
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = input.readLine()) != null) {
                    message.append(line);
                }
                message.append("\n Output = ");
                input =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    message.append(line);
                }
                throw new Exception("Error native. " + message);
            } else {                 
                String line, size = "";
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    if (line.matches(".*used.*")) {                        
                       size = line.trim().split(" ")[0];
                    }
                }                
                return Integer.valueOf(size);
            }          
        } catch (Throwable ex) {
            return 0;        
        }
    }


    public static void main(String[] args) throws Exception {
        DocumentBuilder builder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(args[0]);
        
        String resultDir = args[1];

        NodeList products = document.getElementsByTagName(PRODUCT);

        for (int pc = 0; pc < products.getLength(); pc++) {
            Element product = (Element) products.item(pc);
            String platform = product.getAttribute("platform");
           
           
            NodeList components = product.getElementsByTagName(COMPONENT);
            for (int i = 0; i < components.getLength(); i++) {
                
                Element component = (Element) components.item(i);
                String productName = component.getAttribute(NAME);                
                String dirName = productName.substring(15).replace(' ', '_');
                String pckgsHome = product.getAttribute("source");
                File dir = new File(resultDir + File.separator + dirName);
                dir.mkdirs();
                NodeList list = component.getElementsByTagName(PKGUNIT);
                String uid = component.getAttribute(UID);
                System.out.println("name = " + productName);
                Properties properties = new Properties();
                try {
                properties.load(new FileInputStream(
                        new File(dir, "build.properties")));
                } catch (FileNotFoundException fnfe) {
                    
                }
                /* Create a component description */    
                if (properties.getProperty("product.uid") == null) {
                    properties.setProperty("product.uid", uid);
                }
                properties.setProperty("cvs.path", "components/products/" + productName.substring(15).replace(' ', '_'));
                properties.setProperty("product.data.length." + platform,  String.valueOf(list.getLength()));
                int sizeModificator = 0;
                for (int j = 0; j < list.getLength(); j++) {
                    Element unit = (Element) list.item(j);
                    String unitName = unit.getAttribute(PKGNAME).replaceAll("VERSION", "12.0-1");
                    System.out.println(unitName);
                    String path =  pckgsHome;                    
                    properties.setProperty("product.data."  + String.valueOf(j + 1) + ".uri." + platform , path + "/" + unitName);
                    properties.setProperty("product.data." + String.valueOf(j + 1) + ".zip", "false");
                    String filename = pckgsHome + "/" + unitName;
                    if (new File(filename).exists()) {
                        sizeModificator += size(pckgsHome + "/" + unitName);
                    }
                }
               // properties.setProperty("product.disk.space.modificator" ,  String.valueOf(sizeModificator * 512));
                properties.store(new FileOutputStream(
                        new File(dir, "build.properties")), "");


             //   System.out.println(" <component uid=\"" + uid + "\" version=\"1.0.0.0.0\"/>");

                
             /*   
            properties.clear();               
            properties.setProperty("product.display.name", productName.substring(15));
            properties.setProperty("product.description", productName.substring(15));
            properties.store(new FileOutputStream(new File(dir, "Bundle.properties")), "");
*/

            }

        }
    }
}
