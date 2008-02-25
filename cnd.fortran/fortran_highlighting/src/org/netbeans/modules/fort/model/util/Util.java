
package org.netbeans.modules.fort.model.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * utility class
 * @author Andrey Gubichev
 */
public class Util {
   /**
    * read string from file f
    */
   public static String readFile(File f) {   
       
       BufferedReader reader = null;
       StringBuilder res = new StringBuilder();
       
       try {
           try {
               reader = new BufferedReader(new FileReader(f));            

               String t;

               while ((t = reader.readLine()) != null) {
                   res.append(t).append('\n');
               }
           }
           finally {
               if (reader != null)
                 reader.close();
           }
       }
       catch(IOException ex) {
           res.setLength(0);
       }              
         
       
       return res.toString();
   }
    
}

