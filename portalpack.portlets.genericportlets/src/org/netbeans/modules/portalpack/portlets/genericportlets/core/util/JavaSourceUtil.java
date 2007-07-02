/*
 * JavaSourceUtil.java
 *
 * Created on May 13, 2007, 4:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import org.openide.filesystems.FileObject;

/**
 *
 * @author Satyaranjan
 */
public class JavaSourceUtil {
    
    public static String[] getMethodNames(String className,FileObject fob)
    {
        /*ClassElement classElement = ClassElement.forName(className,fob);
        if(classElement == null)
            return null;
        MethodElement[] methods = classElement.getMethods();
        
        String[] methodNames = new String[methods.length];
        for(int i =0;i<methods.length;i++)
        {
            methodNames[i] = methods[i].getName().getName();
        
        }

        return methodNames;    */
        return new String[]{};
    }

    public static boolean addInterface(String interfaceName,String className,FileObject fob) 
    {
        /* ClassElement classElement = ClassElement.forName(className,fob);
         if(classElement == null)
           return false;
         Identifier actionListener = Identifier.create(interfaceName);       
         Identifier[] interfaces = classElement.getInterfaces();
         for(int i = 0;i<interfaces.length;i++)
         {
            Identifier intf = interfaces[i];
            System.out.println("Full Name:: "+intf.getFullName());
            System.out.println("source Name::" + intf.getSourceName());
            System.out.println("Name:::"+intf.getName());
            
            if(intf.compareTo(actionListener,true))
                return false;

         }
         
         classElement.addInterface(actionListener);
         return true;*/
        return false;
    }

    public static boolean addImport(String importStatement,String className,FileObject fob)
    {
        /*
         ClassElement classElement = ClassElement.forName(className,fob);
         if(classElement == null)
           return false;*/
         return true;
    }

    public static boolean addNewMethod(String methodName,FileObject fob)
    {
        return true;
    }
}
