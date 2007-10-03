/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.registry;

import java.io.File;

import org.openide.filesystems.FileSystem;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.WeakListeners;

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
        recognizerLookupListener = (LookupListener) WeakListeners.create(LookupListener.class, this, recognizersRes);
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
