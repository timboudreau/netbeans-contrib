/*
 *
 * 
 * PropertySupport.java -- synopsis.
 * 
 * 
 *  April 13, 2000
 *  <<Revision>>
 * 
 *  SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 * 
 *  Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 *  Use is subject to license terms.
 */
 
package support;

import java.util.*;
import org.openide.nodes.*;

/** Property Support
 *
 * @author  Marek Fukala
 * @version 1.0
 */
public class PropertySupport extends Object {

  /** Creates new PropertySupport */
  public PropertySupport() {
  }
  
    /**
  This method get you node's property value. You must put property name into method's
  arguments in following format:
  
      "Property List"/"Property Name"
  
  for example, if you want to get compiler type from a node, you should put into
  argument:
  
      Execution/Compiler
  
  For easier use, display names of properties are used. It mean, if you see a property 
  name in property edit window, you can put this name into this function argument.
  
  */  
  public static Object getPropertyValue(String propertyName, Node n) throws Exception {
    Node.PropertySet[] nps = n.getPropertySets();
    //main part, for example "Execution"
    String main = propertyName.substring(0, propertyName.lastIndexOf('/')); 
    //sub main part, for example "Compiler"
    String sub = propertyName.substring(propertyName.lastIndexOf('/') + 1);

    for(int i=0;i < nps.length; i++) {      
      if(nps[i].getDisplayName().equals(main)) {
        Node.Property[] np = nps[i].getProperties();
        for(int x = 0; x < np.length; x++) {
          if(np[x].getDisplayName().equals(sub)){
            return(np[x].getValue());            
          }
        }
      }
    }
    
    return null;
  }
  
  private String cutIt(String propertyName) {
    
    //sub main part, for example "Compiler"
    String sub = propertyName.substring(propertyName.lastIndexOf('/') + 1);
    
    return propertyName.substring(0, propertyName.lastIndexOf('/')); 
    
  }
  
  
  //Input to this method is path of propertiesset names and property name at the end.  
  public static Object getValue(String propertyName, Node n) throws Exception {
    Node.PropertySet[] nps = n.getPropertySets();
    //main part, for example "Execution"
    String main = propertyName.substring(0, propertyName.lastIndexOf('/')); 
    //sub main part, for example "Compiler"
    String sub = propertyName.substring(propertyName.lastIndexOf('/') + 1);

    
    //first I must find right node.    
    if (!n.isLeaf()) {
      Children children = n.getChildren();
      children.findChild(""); // NOI18N
      Enumeration en = children.nodes();
      while (en.hasMoreElements()) {
        Node node = (Node)en.nextElement();
        
        System.out.println(node.getName());        
                
      }
    }
    
/*    String name;
    
    for(int i=0;i < nps.length; i++) {      
      if(nps[i].getDisplayName().equals(main)) {
        Node.Property[] np = nps[i].getProperties();
        for(int x = 0; x < np.length; x++) {
          
          
          
          name = pp[x].getDisplayName();
          value = pp[x].getValue();          
                    
          if(oValue instanceof Map) { // printing list of pairs key: value
            out.println();
            Set keys = ((Map)oValue).keySet();
            Iterator it = keys.iterator();
            Object key;
            while(it.hasNext()) {
              key = it.next();
              out.println(buf+"\t"+key+": "+((Map)oValue).get(key));
            }
          }
          else if(oValue instanceof List) {  // printing list of items
            out.println();
            Iterator it = ((List)oValue).iterator();
            while(it.hasNext()) {
              out.println(buf+"\t"+it.next());
            }
          }
          else {  // single value
            value = ps[j].getValue().toString();
            if (value.indexOf('@') >= 0) {
              out.println(ps[j].getValueType().toString());
            }
            else {
              out.println(value);
            }
          }
          
          
          
          
          if(np[x].getDisplayName().equals(sub)){}            
          
          }
        }
      }
    }
  */  
    return null;
  }
    
  
  
  
  
  
  
  
  /**
  This method allow you set node's properties value. You must put property name into method's
  arguments in following format:
  
      "Property List"/"Property Name"
  
  for example, if you want to set compiler type from a node, you should put into
  argument:
  
      Execution/Compiler
  
  For easier use, display names of properties are used. It mean, if you see a property 
  name in property edit window, you can put this name into this function argument.
  
  */  
  public static void setPropertyValue(String propertyName, Object value, Node n) throws Exception {
    Node.PropertySet[] nps = n.getPropertySets();
    //main part, for example "Execution"
    String main = propertyName.substring(0, propertyName.lastIndexOf('/')); 
    //sub main part, for example "Compiler"
    String sub = propertyName.substring(propertyName.lastIndexOf('/') + 1);
    for(int i=0;i < nps.length; i++) {
      if(nps[i].getDisplayName().equals(main)) {
        Node.Property[] np = nps[i].getProperties();
        for(int x = 0; x < np.length; x++) {
          if(np[x].getDisplayName().equals(sub)){
            //out.println(CompilerType.find("FastJavac Compilation").getClass().getName());
            //CompilerType.find("FastJavac Compilation")
            np[x].setValue(value);
          }
        }
      }
    }
  }
  
  
  
  
  
}