/*
 * JavaSourceUtil.java
 *
 * Created on May 13, 2007, 4:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openide.filesystems.FileObject;
/*import org.openide.src.ClassElement;
import org.openide.src.Identifier;
import org.openide.src.Import;
import org.openide.src.MethodElement;
import org.openide.src.MethodParameter;
import org.openide.src.SourceElement;
import org.openide.src.SourceException;
import org.openide.src.Type;*/

/**
 *
 * @author Satyaranjan
 */
public class JavaSourceUtil {
    
    public final static String IPC_CONSUME_EVENT_TEMPLATE = "ipcconsumeevent.template";
    public final static String IPC_GENERATE_EVENT_TEMPLATE = "ipcgenerateevent.template";
    public final static String IPC_CONSUME_EVENT_METHOD_TEMPLATE = "ipcconsumeeventmethod.template";
    public static String[] getMethodNames(String className,FileObject fob)
    {
      /*  ClassElement classElement = ClassElement.forName(className,fob);
        if(classElement == null)
            return null;
        MethodElement[] methods = classElement.getMethods();
        
        String[] methodNames = new String[methods.length];
        for(int i =0;i<methods.length;i++)
        {
            methodNames[i] = methods[i].getName().getName();
        }

        return methodNames;    */
        return new String[0];
      
    }

    public static /*MethodElement[]*/String[] getMethods(String className,FileObject fob)
    {
       /* ClassElement classElement = ClassElement.forName(className,fob);
        if(classElement == null)
            return null;
        MethodElement[] methods = classElement.getMethods();
        return methods;    */
        return new String[0];
      
    }

    public static void addToMethodBody(/*MethodElement method,String text*/)
    {
      /*  String body = method.getBody();
        body += "\n" + text;
        try {
            method.setBody(body);
        } catch (SourceException ex) {
            ex.printStackTrace();
        }*/

    }

    public static boolean addInterface(String fullInterfaceName,String interfaceName,String className,FileObject fob)// throws SourceException 
    {
        /*
         ClassElement classElement = ClassElement.forName(className,fob);
         if(classElement == null)
           return false;
         
         Identifier[] interfaces = classElement.getInterfaces();
         for(int i = 0;i<interfaces.length;i++)
         {
            Identifier intf = interfaces[i];
            if(intf.getName().equals(interfaceName) || intf.getName().equals(fullInterfaceName))
                return false;
            if(intf.getFullName().equals(interfaceName) || intf.getFullName().equals(fullInterfaceName))
                return false;

         }
         Identifier newInterface = Identifier.create(interfaceName);       
         
         classElement.addInterface(newInterface);*/
         return true;
        
    }

    public static boolean addImports(String[] importStatement,String className,FileObject fob)
    {
        /*
         ClassElement classElement = ClassElement.forName(className,fob);
         if(classElement == null)
           return false;
         SourceElement source = classElement.getSource();
         Import[] imports = source.getImports();
         for(int i=0;i<importStatement.length;i++)
         {
            boolean found = false;
            for(int k=0;k<imports.length;k++)
            {
                
                Import imp = imports[k];
                if(!imp.isPackage())
                {
                    if(imp.getIdentifier().getFullName().equals(importStatement[i]))
                    {
                        found = true;
                        break;
                    }
                }else{
                    String originalImpPkg = importStatement[i].substring(0,importStatement[i].lastIndexOf("."))+".*";
                    System.out.println("My Original Import is :::: "+originalImpPkg);
                    System.out.println("Import found::: "+imp.getIdentifier().getFullName());
                    if(imp.getIdentifier().equals(originalImpPkg))
                    {
                        found = true;
                        break;
                    }
                }
            }

            if(!found)
            {
                Import imp = new Import(Identifier.create(importStatement[i]),false);
                try {
                    source.addImport(imp);
                } catch (SourceException ex) {
                    ex.printStackTrace();
                }
            }
         }*/
         return true;
    }

    public static boolean addNewMethod(String className,String methodName,int modifier,/*Type returnType,*/ArrayList parameters,String body,FileObject fob) //throws SourceException
    {
        /*
        ClassElement classElement = ClassElement.forName(className,fob);
         if(classElement == null)
           return false;
        MethodElement method = new MethodElement();
        Identifier methodIdentifier = Identifier.create(methodName);
        
        method.setName(methodIdentifier);
        method.setReturn(returnType);
        MethodParameter[] methodParameters = new MethodParameter[parameters.size()];
        for(int i=0;i<parameters.size();i++)
        {
            methodParameters[i] = MethodParameter.parse(parameters.get(i).toString());
        }
        method.setParameters(methodParameters);
        method.setBody(body);
        method.setModifiers(modifier);
        classElement.addMethod(method);*/
        
        return true;
    }

    public static /*MethodElement*/ void getMethod(String className,String methodName,/*Type returnType,*/ArrayList parametersType,FileObject fob)
    {
        /*
        ClassElement classElement = ClassElement.forName(className,fob);
         if(classElement == null)
           return null;
        
        MethodElement[] methods = classElement.getMethods();
        
        String[] methodNames = new String[methods.length];
        for(int i =0;i<methods.length;i++)
        {
            String mymethodName = methods[i].getName().getName();
            if(mymethodName.equals(methodName))
            {
                MethodParameter[] methodParameters = methods[i].getParameters();
                if(methodParameters.length != parametersType.size())
                    continue;
                boolean isSameMethod = false;
                for(int k=0;k<methodParameters.length;k++)
                {
                    if(!methodParameters[k].getType().getClassName().getName().equals(parametersType.get(k)) 
                           && !methodParameters[k].getType().getClassName().getFullName().equals(parametersType.get(k)))
                    {
                        isSameMethod = false;
                        break;
                    }
                    else
                        isSameMethod = true;
                }
                if(isSameMethod) return methods[i];
            }
        }   

        return null;*/
        
    }

    public static List getParameterName(/*MethodElement method,*/String parameterTypeClass,String parameterTypeFullClass)
    {
        /*
        List parameterNames = new ArrayList();
        MethodParameter[] methodParameter = method.getParameters();
        for(int i=0;i<methodParameter.length;i++)
        {
            String className = methodParameter[i].getType().getClassName().getFullName();
            if(className.equals(parameterTypeClass) || className.equals(parameterTypeFullClass))
                parameterNames.add(methodParameter[i].getName());
        }
        return parameterNames;*/
        return new ArrayList();
    }
   /* public static void mergeTemplate(String templateFile,Writer writer, HashMap values) throws Exception {

        VelocityContext context = VTResourceLoader.getContext(values);
             
            Template template = Velocity.getTemplate(templateFile,"UTF-8");
            if (template == null) {
                throw new IllegalStateException(" no template defined ");
            }
        
            template.merge(context, writer);            
            writer.close();
 
    }*/

   

}
