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
        FSInfo recognizedInfo = null;
        FSInfo[] fsInfos = FSRegistry.getDefault().getRegistered();
        for (int i = 0; i < fsInfos.length; i++) {
            File infoRoot = fsInfos[i].getFSRoot();
            if (folder.equals(infoRoot) ||
                folder.getAbsolutePath().startsWith(infoRoot.getAbsolutePath())) {
                
                recognizedInfo = fsInfos[i];
                break;
            }
        }
        if (recognizedInfo == null) {
            synchronized (this) {
                for (int i = 0; i < recognizers.length; i++) {
                    FSInfo testInfo = recognizers[i].getFSInfo(folder);
                    if (testInfo != null) {
                        recognizedInfo = testInfo;
                        break;
                    }
                }
            }
        }
        if (recognizedInfo == null) return null;
        FSRegistry.getDefault().register(recognizedInfo);
        return recognizedInfo.getFileSystem();
    }
    
}
