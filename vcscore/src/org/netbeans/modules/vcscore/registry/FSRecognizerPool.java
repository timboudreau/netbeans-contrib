/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.io.File;

import org.openide.filesystems.FileSystem;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.WeakListener;

/**
 * The pool of filesystem recognizers.
 *
 * @author  Martin Entlicher
 */
class FSRecognizerPool extends Object implements LookupListener {
    
    private static FSRecognizerPool recognizerPool;
    
    private LookupListener recognizerLookupListener;
    private FSRecognizer[] recognizers;
    
    /** Creates a new instance of FSRecognizerPool */
    private FSRecognizerPool() {
        Lookup.Result recognizersRes = Lookup.getDefault().lookup(new Lookup.Template(FSRecognizer.class));
        recognizerLookupListener = (LookupListener) WeakListener.create(LookupListener.class, this, recognizersRes);
        recognizersRes.addLookupListener(recognizerLookupListener);
        synchronized (this) {
            recognizers = (FSRecognizer[]) recognizersRes.allInstances().toArray(new FSRecognizer[0]);
        }
        //System.out.println("FSRecognizerPool(): HAVE "+recognizers.length+" recognizers");
    }
    
    /**
     * Get the default pool of filesystem recognizers.
     */
    public static synchronized FSRecognizerPool getDefault() {
        if (recognizerPool == null) {
            recognizerPool = new FSRecognizerPool();
        }
        return recognizerPool;
    }
    
    /**
     * The result of found filesystem recognizers has changed.
     */
    public void resultChanged(LookupEvent lookupEvent) {
        Lookup.Result recognizersRes = (Lookup.Result) lookupEvent.getSource();
        synchronized (this) {
            recognizers = (FSRecognizer[]) recognizersRes.allInstances().toArray(new FSRecognizer[0]);
        }
    }
    
    /**
     * Find a filesystem, that is recognized at the given folder.
     */
    public FileSystem findFilesystem(File folder) {
        FSInfo fsInfo = findFilesystemInfo(folder);
        if (fsInfo != null) {
            return fsInfo.getFileSystem();
        } else {
            return null;
        }
    }
    
    /**
     * Find a filesystem, that is recognized at the given folder.
     */
    public FSInfo findFilesystemInfo(File folder) {
        FSInfo recognizedInfo = null;
        FSInfo[] fsInfos = FSRegistry.getDefault().getRegistered();
        //System.out.println("Registered FS Infos = "+fsInfos.length);
        for (int i = 0; i < fsInfos.length; i++) {
            File infoRoot = fsInfos[i].getFSRoot();
            if (folder.equals(infoRoot)) {
                recognizedInfo = fsInfos[i];
                break;
            /*} else if (isParentOf(infoRoot, folder)) {
                // Performance boost - if some parent of the folder is already
                // recognized as some kind of VCS filesystem, there is no attempt
                // to recognize it once more.
                return null;
             **/
            }
        }
        if (recognizedInfo == null) {
            synchronized (this) {
                for (int i = 0; i < recognizers.length; i++) {
                    FSInfo testInfo = recognizers[i].findFSInfo(folder);
                    //System.out.println("  recognizer.findFSInfo("+folder+") = "+testInfo);
                    if (testInfo != null) {
                        recognizedInfo = testInfo;
                        break;
                    }
                }
            }
            if (recognizedInfo != null) {
                File recognizerdFolder = recognizedInfo.getFSRoot();
                if (!folder.equals(recognizerdFolder)) {
                    for (int i = 0; i < fsInfos.length; i++) {
                        File infoRoot = fsInfos[i].getFSRoot();
                        if (recognizerdFolder.equals(infoRoot)) {
                            return null; // Already mounted
                        }
                    }
                }
                //FSRegistry.getDefault().register(recognizedInfo);
            }
        }
        //System.out.println("findFilesystemInfo("+((recognizedInfo == null) ? folder : recognizedInfo.getFSRoot())+") = "+recognizedInfo);
        return recognizedInfo;
    }
    
    /*
    private static boolean isParentOf(File parent, File file) {
        String parentStr = parent.getAbsolutePath();
        //file = file.getParentFile();
        String fileStr = file.getAbsolutePath();
        while (/*file != null && *//*parentStr.length() < fileStr.length()) {
            file = file.getParentFile();
            if (file == null) break;
            if (parent.equals(file)) {
                return true;
            }
            fileStr = file.getAbsolutePath();
        }
        return false;
    }
                                    */
    
}
