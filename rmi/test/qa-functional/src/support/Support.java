/*
 *
 * 
 * Support.java -- synopsis.
 * 
 * 
 *  August 31, 2000
 *  <<Revision>>
 * 
 *  SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 * 
 *  Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 *  Use is subject to license terms.
 */
package support;

import java.awt.*;
import java.io.*;
import javax.swing.table.*;
import support.PropertySupport; 
import org.openide.*;
import org.openide.filesystems.*;
import org.openide.cookies.*;
import org.openide.compiler.Compiler;
import org.openide.compiler.*;
import org.openide.execution.*;
import org.openide.loaders.*;
import org.openide.windows.*;
import org.openide.util.*;
import org.openide.nodes.*;
import org.netbeans.modules.java.JavaDataObject; 

/** Support routines for RMI Tests
 *
 * @author  Adam Sotona
 * @version 1.0
 */


public class Support {
    public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");
/** name of the RMI Executor
 */    
    public static final String RMI_EXECUTOR = bundle.getString("RMI_Executor");
/** name of the RMI Client Executor
 */    
    public static final String RMI_CLIENT_EXECUTOR = bundle.getString("RMI_Client_Executor");
/** name of the RMI Unicast Export Executor
 */    
    public static final String RMI_UNICAST_EXPORT = bundle.getString("RMI_Unicast_Export");
    
/** normal log writer
 */    
    public PrintWriter outwrite = null;
/** error log writer
 */    
    public PrintWriter errwrite = null;
/** Support constructor with setting normal and error log
 * @param aoutwrite print writer for normal log
 * @param aerrwrite print writer for error log
 */    
    public Support (PrintWriter aoutwrite, PrintWriter aerrwrite) {
        outwrite=aoutwrite;
        errwrite=aerrwrite;
        System.out.println("userdir="+System.getProperty("user.dir"));
    }
/** boolean indicating any error
 */    
    public boolean anyerror = false;
    
/** writes string to error log
 * @param s string
 */    
    public void errorlog(String s)    {
        anyerror = true;
        System.err.println("!!! "+s); // NOI18N
        if(errwrite != null) {
            errwrite.println(s);
            errwrite.flush();
        }
        if(outwrite != null) {
            outwrite.println(s);
            outwrite.flush();
        }
    }
    
/** writes string to normal log
 * @param s string
 */    
    public void log(String s)  {
        System.out.println(s);
        if(outwrite != null) {
            outwrite.println(s);
            outwrite.flush();
        }
    }
    
/** writes string and exception to error
 * @param mess string
 * @param e exception
 */    
    public void exceptionlog(String mess, Exception e)   {
        anyerror = true;
        errorlog("!!! "+mess + ":"); // NOI18N
        e.printStackTrace();
        if(errwrite != null) {
            e.printStackTrace(errwrite);
            errwrite.flush();
        }
        if(outwrite != null) {
            e.printStackTrace(outwrite);
            outwrite.flush();
        }
    }
    
    public void markRMI(org.netbeans.modules.java.JavaDataObject jdo, boolean set) throws Exception {
        Class c[]={org.netbeans.modules.java.JavaDataObject.class, Boolean.TYPE};
        java.lang.reflect.Method m=Class.forName("org.netbeans.modules.rmi.RemoteDetectionSupport").getDeclaredMethod("markRMI",c);
        m.setAccessible(true);
        Object o[]={jdo,set ? Boolean.TRUE : Boolean.FALSE};
        m.invoke(null,o);
    }
    

    public Node findChild(Node n, String s) {
        Children c=n.getChildren();
        log("looking for node: "+s);
        return c.findChild(s);
    }
    
    
    
 
    
/** builds dataobject
 * @param dataobject DataObject
 */    
    public void build(DataObject dataobject) {
        compile(dataobject,CompilerCookie.Build.class,bundle.getString("Building"),org.openide.compiler.Compiler.DEPTH_ZERO);
    } 

/** compiles dataobject
 * @param dataobject DataObject
 */    
    public void compile(DataObject dataobject) {
        compile(dataobject,CompilerCookie.Compile.class,bundle.getString("Compiling"),org.openide.compiler.Compiler.DEPTH_ZERO);
    }
    
/** cleans dataobject
 * @param dataobject DataObject
 */    
    public void clean(DataObject dataobject) {
        compile(dataobject,CompilerCookie.Clean.class,bundle.getString("Cleaning"),org.openide.compiler.Compiler.DEPTH_ZERO);
    }
    
    
/** makes any compile action (compile, make, clean)
 * @param dataobject DataObject
 * @param cookie Class
 * @param action String
 * @param depth Compiler.Depth
 * @return boolean 
 */     
    public boolean compile(DataObject dataobject,Class cookie,String action,org.openide.compiler.Compiler.Depth depth) {
        log(action+" "+dataobject.getName());// NOI18N
        try { 
            CompilerJob job = new CompilerJob(depth);
            ((CompilerCookie) dataobject.getCookie(cookie)).addToJob(job,depth);
            CompilerTask ct = job.start();
            boolean res = ct.isSuccessful();
            if (!res) errorlog(action+" "+dataobject.getName()+bundle.getString("_was_unsuccessful!")); // NOI18N
            return res; 
        } 
        catch(Exception e) {
            exceptionlog(bundle.getString("Exception_during_") + action.toLowerCase() + " " + dataobject.getName(), e); // NOI18N
        }
        return false;
    }
    
/** Sets compilerf for the dataobject
 * @param dataobject DataObject
 * @param compiler String
 */    
    public void setCompiler(DataObject dataobject,String compiler) {
        log(bundle.getString("Setting_compiler_")+compiler+bundle.getString("_for_")+dataobject.getName());
        try {
            CompilerType comptype = CompilerType.find(compiler);
            if (comptype == null)
                errorlog(bundle.getString("Comiler_Type_")+compiler+bundle.getString("_not_found!"));
            else
                PropertySupport.setPropertyValue(bundle.getString("Execution/Compiler"),comptype,dataobject.getNodeDelegate());
        }
        catch (Exception e) { exceptionlog(bundle.getString("Exception_during_setting_compiler_") + compiler + bundle.getString("_for_") + dataobject.getName(), e);  }
    }
    
/** executes dataobject
 * @param dataobject DataObject
 * @param parametrs String parametrs fo execution
 * @throws Exception exception during execution
 */    
    public void execute (DataObject dataobject, String parametrs) throws Exception {
        PropertySupport.setPropertyValue(bundle.getString("Execution/Arguments"),parametrs,dataobject.getNodeDelegate());
        log(bundle.getString("Executing_")+dataobject.getName()+bundle.getString("_with_arguments_")+parametrs);
        ((ExecCookie)dataobject.getCookie(ExecCookie.class)).start();
    }
    
/** executes object by name with executor
 * @param executor Executor
 * @param name String
 * @return ExecutorTask
 */    
    public ExecutorTask execute(Executor executor, String name) {
        ExecutorTask exectask=null;
        log(bundle.getString("Executing_")+name+bundle.getString("_by_")+executor.getName());
        try { exectask = executor.execute(new ExecInfo(name));
        }
        catch (java.io.IOException e) {
            exceptionlog(bundle.getString("Exception_during_executing_") + name + bundle.getString("_with_executor_") + executor.getName(), e);
        }
        return exectask;
    }
    
/** returns fileobject for the filename
 * @param filename String
 * @return FileObject
 */    
    public FileObject getFileObject(String filename) {
        Repository repo = TopManager.getDefault().getRepository();
        FileObject fo = repo.findResource(filename);
        return fo;
    }
    
/** returns dataobject for the filename
 * @param filename String
 * @return DataObject
 */    
    public DataObject getDataObject(String filename) {
        DataObject dataobject;
        FileObject fo = getFileObject(filename);
        if (fo==null) {
            errorlog(bundle.getString("File_")+filename+bundle.getString("_not_found\n"));
            return null;
        }
        try {
            dataobject = DataObject.find(fo);
        } catch (Exception e) {
            exceptionlog(bundle.getString("Exception_during_creating_DataObject_of_") + fo.getName(), e);
            return null;
        }
        return dataobject;
    }
    
/** returns executor matching execstring
 * @param exestring String
 * @return Executor
 */    
    public Executor getExecutor(String exestring) {
        log(bundle.getString("Finding_executor_")+exestring);
        Executor executor = Executor.find(exestring);
        if (executor==null) {
            errorlog(bundle.getString("Executor_not_found__")+exestring+"\n"); // NOI18N
            return null;
        }
        return executor;
    }
    
/** sets executor for dataobject
 * @param dataobject DataObject
 * @param executor String executor name
 * @return boolean true if succesfull
 */    
    public boolean setExecutor(DataObject dataobject,String executor) {
        Executor e = getExecutor(executor);
        if (e == null) return false;
        return setExecutor(dataobject,e);
    }
    
/** sets executor for dataobject
 * @param dataobject DataObject
 * @param executor Executor
 * @return boolean
 */    
    public boolean setExecutor(DataObject dataobject,Executor executor) {
        log(bundle.getString("Setting_executor..."));
        try { ExecSupport.setExecutor(((MultiDataObject)dataobject).getPrimaryEntry(),executor);
        }
        catch (Exception e) {
            exceptionlog(bundle.getString("Exception_during_setting_executor_") + executor.getName(), e);
            return false;
        }
        return true;
    }
    
    
    //GUI
    
    /** repository of IDE*/
    static Repository repo = org.openide.TopManager.getDefault().getRepository();

    public static void mountDir(String dirName) throws IOException{
       org.openide.filesystems.FileSystem eXception  = repo.findFileSystem(dirName);
       System.out.println("Mounting : " + dirName);
       if(eXception == null) {
           try {
 
               LocalFileSystem lfs = new LocalFileSystem();
 
               lfs.setRootDirectory(new java.io.File(dirName));
 
               repo.addFileSystem(lfs);
           }catch(Exception e){
                //TopManager.getDefault().getErrorManager().notify(ErrorManager.INFORMATIONAL, e);
               throw new IOException("Exception in Mount module : " + e);
           }
       }
       else {
           System.out.println("Directory " + dirName + " already mounted");
       }
    }
  
    public static void mountArchive(String archiveName) throws IOException {
       //FileObject eXception  = repo.findResource(archiveName);
       org.openide.filesystems.FileSystem eXception  = repo.findFileSystem(archiveName);
       if (eXception == null) {
           try {
               JarFileSystem jfs = new JarFileSystem();
               jfs.setJarFile(new java.io.File(archiveName));
               repo.addFileSystem(jfs);
           } catch (Exception e) {
                //TopManager.getDefault().getErrorManager().notify(ErrorManager.INFORMATIONAL, ex);
                throw new IOException("Exception in Mount module : " + e);
           }
       }
       else {
           System.out.println("Archive " + archiveName + " already mounted");
       }
    }
    
    public static void sleep(int milliseconds) {
        try {
            System.out.println("... waiting for "+milliseconds+".");
            Thread.currentThread().sleep(milliseconds);
        } catch(Exception ex) {
            ex.printStackTrace();
        }    
    }    
    
}