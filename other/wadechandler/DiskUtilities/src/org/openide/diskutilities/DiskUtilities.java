/*
 * DiskUtilities.java
 *
 */

package org.openide.diskutilities;

import java.io.File;
import java.io.IOException;

/**
 * Contains different disk utilities etc
 * @author Wade Chandler
 * @version 1.0
 */
public class DiskUtilities {
    
    /** Creates a new instance of DiskUtilities */
    public DiskUtilities() {
    }
    
    public static class BooleanWrapper {
        private volatile boolean value = false;
        
        public BooleanWrapper(boolean initialValue){
            super();
            setValue(initialValue);
        }
        
        public BooleanWrapper(){
            this(false);
        }
        
        public boolean getValue() {
            return value;
        }
        
        public void setValue(boolean value) {
            this.value = value;
        }
    }
    
    /**
     *File used to recursively run over and sort a directory hierarchy to flatten out the directory
     *structure of a file system into an array or in this case a Vector.
     *@param out the Vector the enumerated file names will be written to.  These will be sorted with each
     *       directory listing with java.util.Arrays.sort(File[]) so see that method for the format of the
     *       sort
     *@param dir the directory we want the listing for
     *@param deepCounter the counter for tracking how deep we can go.  This should be 0 in normal calls
     *@param howDeep the number of directory levels deep we want to return if the value is less than 0 then
     *       all depths will be enumerated under this path
     *@param check a boolean wrapper used to check whether we need to kick out or not
     *@param threadRest how many iterations after to go into rest
     *@param restMillis how many milli-seconds to rest
     */
    private static void enumerateFiles(java.util.Vector out, String dir, int deepCounter, int howDeep, BooleanWrapper check, int threadRest, int restMillis) throws InterruptedException, IOException {
        deepCounter++;
        if(howDeep>=0&&deepCounter>howDeep){
            return;
        }
        java.io.File f = new java.io.File(dir);
        if(!f.isDirectory()){
            throw new IllegalArgumentException("The given directory "+dir+" is not a valid directory.");
        }
        java.io.File[] files = f.listFiles();
        java.util.Arrays.sort(files);
        for(int i = 0; !check.getValue()&&files!=null&&i<files.length;i++){
            if(threadRest!=0&&i%threadRest==0){
                Thread.sleep(0, restMillis);
            }
            out.add(files[i].getAbsoluteFile().getCanonicalPath());
            if(files[i].isDirectory()){
                enumerateFiles(out, files[i].getAbsoluteFile().getCanonicalPath(), (deepCounter), howDeep, check, threadRest, restMillis);
            }
        }
    }
    
    /**
     *File used to recursively run over and sort a directory hierarchy to flatten out the directory
     *structure of a file system into an array or in this case a Vector.
     *@param out the Vector the enumerated file names will be written to.  These will be sorted with each
     *       directory listing with java.util.Arrays.sort(File[]) so see that method for the format of the
     *       sort
     *@param dir the directory we want the listing for
     *@param deepCounter the counter for tracking how deep we can go.  This should be 0 in normal calls
     *@param howDeep the number of directory levels deep we want to return if the value is less than 0 then
     *       all depths will be enumerated under this path
     *@param check a boolean wrapper used to check whether we need to kick out or not
     *@param threadRest how many iterations after to go into rest
     *@param restNanos how many nanos to rest
     */
   /* private static void enumerateFiles(java.util.Vector out, String dir, int deepCounter, int howDeep, BooleanWrapper check, int threadRest, int restNanos) throws Throwable {
        deepCounter++;
        if(howDeep>=0&&deepCounter>howDeep){
            return;
        }
        java.io.File f = new java.io.File(dir);
        if(!f.isDirectory()){
            throw new IllegalArgumentException("The given directory "+dir+" is not a valid directory.");
        }
        java.io.File[] files = f.listFiles();
        java.util.Arrays.sort(files);
        for(int i = 0; !check.getValue()&&files!=null&&i<files.length;i++){
            if(threadRest!=0&&i%threadRest==0){
                Thread.sleep(0, restNanos);
            }
            out.add(files[i].getAbsoluteFile().getCanonicalPath());
            if(files[i].isDirectory()){
                enumerateFiles(out, files[i].getAbsoluteFile().getCanonicalPath(), (deepCounter), howDeep, check, threadRest, restNanos);
            }
        }
    }*/
    
    /**
     *Used to recursively delete directories in a disk and memory efficiently manner.
     *This can help with deleting extremely large directories. It is not necessarily as
     *fast as other methods, and probably shouldn't be.  It is designed to allow
     *work with a computer to progress while being able to delete large amounts of data.
     *@param dir the directory we want the listing for
     *@param check a boolean wrapper used to check whether we need to kick out or not
     *@param threadRest how many iterations after to go into rest
     *@param restMillis how many milli-seconds to rest
     */
    private static void deleteFiles(File dir, BooleanWrapper check, int threadRest, long restMillis) throws InterruptedException, IOException {
        dir = dir.getCanonicalFile().getAbsoluteFile();
        if(!dir.isDirectory()){
            throw new IllegalArgumentException("The given directory "+dir+" is not a valid directory.");
        }
        java.io.File[] files = dir.listFiles();
        for(int i = 0; !check.getValue()&&files!=null&&i<files.length;i++){
            if(threadRest!=0&&i%threadRest==0){
                Thread.sleep(restMillis);
            }
            File f2 = files[i];
            if(f2.isDirectory()){
                files=null;
                deleteFiles(f2, check, threadRest, restMillis);
                if(check.getValue()){
                    return;
                }
                files = dir.listFiles();
                i=-1;
            }else{
                f2.delete();
            }
        }
        dir.delete();
    }
    
    public static void main(String args[]){
        if(args.length<2){
            throw new IllegalArgumentException("DiskUtilities must be given two arguments.");
        }
        if(args[0].equalsIgnoreCase("--delete")){
            try {
                DiskUtilities.deleteFiles(new File(args[1]),new BooleanWrapper(), 5, 200);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}