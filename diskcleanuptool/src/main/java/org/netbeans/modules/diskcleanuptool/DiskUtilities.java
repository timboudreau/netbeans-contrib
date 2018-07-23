/*
 * DiskUtilities.java
 *
 * Created on February 21, 2007, 2:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.diskcleanuptool;

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
    
    public static class LongWrapper {
        private volatile long value = 0L;
        
        public LongWrapper(long initialValue){
            super();
            setValue(initialValue);
        }
        
        public LongWrapper(){
            this(0L);
        }
        
        public long getValue() {
            return value;
        }
        
        public void setValue(long value) {
            this.value = value;
        }
        
        public void inc(){
            ++this.value;
        }
        
        public void dec(){
            --this.value;
        }
        
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
    
    public interface IndeterminateProgressHandle {
        void updateStatus(String s);
        void start();
        void finish();
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
     *Used to recursively delete directories in a disk and memory efficiently manner.
     *This can help with deleting extremely large directories. It is not necessarily as
     *fast as other methods, and probably shouldn't be.  It is designed to allow
     *work with a computer to progress while being able to delete large amounts of data.
     *@param dir the directory we want the listing for
     *@param check a boolean wrapper used to check whether we need to kick out or not
     *@param threadRest how many iterations after to go into rest
     *@param restMillis how many milli-seconds to rest
     */
    public static void deleteFiles(File dir, IndeterminateProgressHandle ph, LongWrapper delCounter, BooleanWrapper check, int threadRest, long restMillis) throws InterruptedException, IOException {
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
                deleteFiles(f2, ph, delCounter, check, threadRest, restMillis);
                if(check.getValue()){
                    return;
                }
                files = dir.listFiles();
                i=-1;
            }else{
                String progress = "Deleting \""+f2.getAbsolutePath()+"\" (count "+delCounter.getValue()+")";
                ph.updateStatus(progress);
                f2.delete();
                delCounter.inc();
            }
        }
        String progress = "Deleting \""+dir.getAbsolutePath()+"\" (count "+delCounter.getValue()+")";
        ph.updateStatus(progress);
        dir.delete();
        delCounter.inc();
    }
    
    /**
     *Used to recursively count directories and files in a disk and memory efficiently manner.
     *This can help with deleting extremely large directories. It is not necessarily as
     *fast as other methods, and probably shouldn't be.  It is designed to allow
     *work with a computer to progress while being able to delete large amounts of data.
     *@param dir the directory we want the listing for
     *@param check a boolean wrapper used to check whether we need to kick out or not
     *@param counter a long wrapper used to keep the number of files
     *@param threadRest how many iterations after to go into rest
     *@param restMillis how many milli-seconds to rest
     */
    public static void countFiles(File dir, BooleanWrapper check, LongWrapper counter, int threadRest, long restMillis) throws InterruptedException, IOException {
        dir = dir.getCanonicalFile().getAbsoluteFile();
        if(!dir.isDirectory()){
            throw new IllegalArgumentException("The given directory "+dir+" is not a valid directory.");
        }
        java.io.File[] files = dir.listFiles();
        counter.inc();
        for(int i = 0; !check.getValue()&&files!=null&&i<files.length;i++){
            if(threadRest!=0&&i%threadRest==0){
                Thread.sleep(restMillis);
            }
            File f2 = files[i];
            if(f2.isDirectory()){
                files=null;
                countFiles(f2, check, counter, threadRest, restMillis);
                if(check.getValue()){
                    return;
                }
                files = dir.listFiles();
                i=-1;
            }else{
                counter.inc();
            }
        }
    }
    
    public static int percent(long whole, long value){
        int ret = 0;
        if(whole==0){
            //not going to error out on 0 divide just return 0.
            ret = 0;
        }else{
            ret = (int)((100/whole)*value);
        }
        return ret;
    }
    
}