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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;
import javax.swing.SwingUtilities;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.spi.diff.MergeVisualizer;
import org.netbeans.modules.diff.EncodedReaderFactory;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;

/**
 * This class is used to resolve merge conflicts in a graphical way using a merge visualizer.
 * We parse the file with merge conflicts marked, let the conflicts resolve by the
 * visual merging tool and after successfull conflicts resolution save it back
 * to the original file.
 *
 * @author  Martin Entlicher
 */
public class PvcsResolveConflicts implements VcsAdditionalCommand {
    
    private static final String TMP_PREFIX = "merge"; // NOI18N
    
    private static final String CHANGE_LEFT = "<<<<<<<<<< "; // NOI18N
    private static final String CHANGE_RIGHT = ">>>>>>>>>> "; // NOI18N
    private static final String CHANGE_END = "=========="; // NOI18N

    private VcsFileSystem fileSystem = null;
    private String leftFileRevision = null;
    private String rightFileRevision = null;

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        /*
        if (fileSystem instanceof CvsFileSystem) {
            CvsFileSystem cvsFileSystem = (CvsFileSystem) fileSystem;
         */
        Collection files = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        MergeVisualizer merge = null;
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String fileName = (String) it.next();
            File file = fileSystem.getFile(fileName);
            if (file != null) {
                FileObject fo = fileSystem.findResource(fileName);
                if (merge == null) {
                    merge = (MergeVisualizer) Lookup.getDefault().lookup(MergeVisualizer.class);
                    if (merge == null) {
                        DialogDisplayer.getDefault().notify(
                            new org.openide.NotifyDescriptor.Message(
                                org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, 
                                                                     "Merge.noMergeAvailable")));
                        return false;
                    }
                }
                FileLock lock;
                try {
                    if (fo != null) {
                        lock = fo.lock();
                    } else {
                        lock = null;
                    }
                } catch (IOException ioex) {
                    if (ioex instanceof FileAlreadyLockedException) {
                        ErrorManager.getDefault().notify(ErrorManager.USER,
                                ErrorManager.getDefault().annotate(ioex,
                                NbBundle.getMessage(PvcsResolveConflicts.class, "MergedFileLocked", fo.getNameExt())));
                    }
                    continue;
                }
                try {
                    handleMergeFor(file, fo, lock, merge);
                } catch (IOException ioex) {
                    org.openide.ErrorManager.getDefault().notify(ioex);
                }
            }
        }
        return true;
    }
    
    private void handleMergeFor(final File file, FileObject fo, FileLock lock,
                                final MergeVisualizer merge) throws IOException {
        if (!file.exists()) {
            DialogDisplayer.getDefault ().notify (new org.openide.NotifyDescriptor.Message(
                org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "NoConflictsInFile", file)));
            return ;
        }
        
        String mimeType = (fo == null) ? "text/plain" : fo.getMIMEType();
        String ext = "."+fo.getExt();
        File f1 = File.createTempFile(TMP_PREFIX, ext);
        File f2 = File.createTempFile(TMP_PREFIX, ext);
        File f3 = File.createTempFile(TMP_PREFIX, ext);
        f1.deleteOnExit();
        f2.deleteOnExit();
        f3.deleteOnExit();
        
        final Difference[] diffs = copyParts(true, file, f1, true);
        if (diffs.length == 0) {
            DialogDisplayer.getDefault ().notify (new org.openide.NotifyDescriptor.Message(
                org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "NoConflictsInFile", file)));
            return ;
        }
        copyParts(false, file, f2, false);
        //GraphicalMergeVisualizer merge = new GraphicalMergeVisualizer();
        String originalLeftFileRevision = leftFileRevision;
        String originalRightFileRevision = rightFileRevision;
        if (leftFileRevision != null) leftFileRevision.trim();
        if (rightFileRevision != null) rightFileRevision.trim();
        if (leftFileRevision == null || leftFileRevision.equals(file.getName())) {
            leftFileRevision = org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "Merge.titleWorkingFile");
        } else {
            leftFileRevision = org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "Merge.titleRevision", leftFileRevision);
        }
        if (rightFileRevision == null || rightFileRevision.equals(file.getName())) {
            rightFileRevision = org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "Merge.titleWorkingFile");
        } else {
            rightFileRevision = org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "Merge.titleRevision", rightFileRevision);
        }
        String resultTitle = org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "Merge.titleResult");
        
        final StreamSource s1;
        final StreamSource s2;
        String encoding = EncodedReaderFactory.getDefault().getEncoding(fo);
        if (encoding != null) {
            s1 = StreamSource.createSource(file.getName(), leftFileRevision, mimeType, new InputStreamReader(new FileInputStream(f1), encoding));
            s2 = StreamSource.createSource(file.getName(), rightFileRevision, mimeType, new InputStreamReader(new FileInputStream(f2), encoding));
        } else {
            s1 = StreamSource.createSource(file.getName(), leftFileRevision, mimeType, f1);
            s2 = StreamSource.createSource(file.getName(), rightFileRevision, mimeType, f2);
        }
        final StreamSource result = new MergeResultWriterInfo(f1, f2, f3, file, mimeType,
                                                              originalLeftFileRevision,
                                                              originalRightFileRevision,
                                                              fo, lock, encoding);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    merge.createView(diffs, s1, s2, result);
                } catch (IOException ioex) {
                    org.openide.ErrorManager.getDefault().notify(ioex);
                }
            }
        });
    }
    
    /**
     * Copy the file and conflict parts into another file.
     */
    private Difference[] copyParts(boolean generateDiffs, File source,
                                   File dest, boolean leftPart) throws IOException {
        //System.out.println("copyParts("+generateDiffs+", "+source+", "+dest+", "+leftPart+")");
        BufferedReader r = new BufferedReader(new FileReader(source));
        BufferedWriter w = new BufferedWriter(new FileWriter(dest));
        ArrayList diffList = null;
        if (generateDiffs) {
            diffList = new ArrayList();
        }
        try {
            String line;
            boolean isChangeLeft = false;
            boolean isChangeRight = false;
            int f1l1 = 0, f1l2 = 0, f2l1 = 0, f2l2 = 0;
            StringBuffer text1 = new StringBuffer();
            StringBuffer text2 = new StringBuffer();
            int i = 1, j = 1;
            while ((line = r.readLine()) != null) {
                if (line.startsWith(CHANGE_LEFT)) {
                    if (generateDiffs) {
                        if (leftFileRevision == null) {
                            leftFileRevision = line.substring(CHANGE_LEFT.length());
                        }
                        if (isChangeRight) {
                            f2l2 = j - 1;
                        }
                        f1l1 = i;
                    }
                    isChangeLeft = true;
                    isChangeRight = false;
                    continue;
                } else if (line.startsWith(CHANGE_RIGHT)) {
                    if (generateDiffs) {
                        if (rightFileRevision == null) {
                            rightFileRevision = line.substring(CHANGE_RIGHT.length());
                        }
                        if (isChangeLeft) {
                            f1l2 = i - 1;
                        }
                        f2l1 = j;
                    }
                    isChangeRight = true;
                    isChangeLeft = false;
                    continue;
                } else if (line.equals(CHANGE_END)) {
                    if (generateDiffs) {
                        if (isChangeLeft) {
                            f1l2 = i - 1;
                        } else {
                            f2l2 = j - 1;
                        }
                        diffList.add((f1l1 > f1l2) ? new Difference(Difference.ADD,
                                                                    f1l1 - 1, 0, f2l1, f2l2,
                                                                    text1.toString(),
                                                                    text2.toString()) :
                                     (f2l1 > f2l2) ? new Difference(Difference.DELETE,
                                                                    f1l1, f1l2, f2l1 - 1, 0,
                                                                    text1.toString(),
                                                                    text2.toString())
                                                   : new Difference(Difference.CHANGE,
                                                                    f1l1, f1l2, f2l1, f2l2,
                                                                    text1.toString(),
                                                                    text2.toString()));
                        f1l1 = f1l2 = f2l1 = f2l2 = 0;
                        text1.delete(0, text1.length());
                        text2.delete(0, text2.length());
                    }
                    isChangeLeft = isChangeRight = false;
                    continue;
                }
                if (!isChangeLeft && !isChangeRight || leftPart == isChangeLeft) {
                    w.write(line);
                    w.newLine();
                }
                if (isChangeLeft) text1.append(line + "\n");
                if (isChangeRight) text2.append(line + "\n");
                if (generateDiffs) {
                    if (isChangeLeft) i++;
                    else if (isChangeRight) j++;
                    else {
                        i++;
                        j++;
                    }
                }
            }
        } finally {
            try {
                r.close();
            } finally {
                w.close();
            }
        }
        if (generateDiffs) {
            return (Difference[]) diffList.toArray(new Difference[diffList.size()]);
        } else {
            return null;
        }
    }
    
    private static class MergeResultWriterInfo extends StreamSource {
        
        private File tempf1, tempf2, tempf3, outputFile;
        private String mimeType;
        private String leftFileRevision;
        private String rightFileRevision;
        private FileObject fo;
        private FileLock lock;
        private String encoding;
        
        public MergeResultWriterInfo(File tempf1, File tempf2, File tempf3,
                                     File outputFile, String mimeType,
                                     String leftFileRevision, String rightFileRevision,
                                     FileObject fo, FileLock lock, String encoding) {
            this.tempf1 = tempf1;
            this.tempf2 = tempf2;
            this.tempf3 = tempf3;
            this.outputFile = outputFile;
            this.mimeType = mimeType;
            this.leftFileRevision = leftFileRevision;
            this.rightFileRevision = rightFileRevision;
            this.fo = fo;
            this.lock = lock;
            if (encoding == null) {
                encoding = EncodedReaderFactory.getDefault().getEncoding(tempf1);
            }
            this.encoding = encoding;
        }
        
        public String getName() {
            return outputFile.getName();
        }
        
        public String getTitle() {
            return org.openide.util.NbBundle.getMessage(PvcsResolveConflicts.class, "Merge.titleResult");
        }
        
        public String getMIMEType() {
            return mimeType;
        }
        
        public Reader createReader() throws IOException {
            throw new IOException("No reader of merge result"); // NOI18N
        }
        
        /**
         * Create a writer, that writes to the source.
         * @param conflicts The list of conflicts remaining in the source.
         *                  Can be <code>null</code> if there are no conflicts.
         * @return The writer or <code>null</code>, when no writer can be created.
         */
        public Writer createWriter(Difference[] conflicts) throws IOException {
            Writer w;
            if (fo != null) {
                w = EncodedReaderFactory.getDefault().getWriter(fo, lock, encoding);
            } else {
                w = EncodedReaderFactory.getDefault().getWriter(outputFile, mimeType, encoding);
            }
            if (conflicts == null || conflicts.length == 0) {
                return w;
            } else {
                return new MergeConflictFileWriter(w, fo, conflicts,
                                                   leftFileRevision, rightFileRevision);
            }
        }
        
        /**
         * This method is called when the visual merging process is finished.
         * All possible writting processes are finished before this method is called.
         */
        public void close() {
            tempf1.delete();
            tempf2.delete();
            tempf3.delete();
            if (lock != null) {
                lock.releaseLock();
                lock = null;
            }
            fo = null;
        }
        
    }
    
    private static class MergeConflictFileWriter extends FilterWriter {
        
        private Difference[] conflicts;
        private int lineNumber;
        private int currentConflict;
        private String leftName;
        private String rightName;
        private FileObject fo;
        
        public MergeConflictFileWriter(Writer delegate, FileObject fo,
                                       Difference[] conflicts, String leftName,
                                       String rightName) throws IOException {
            super(delegate);
            this.conflicts = conflicts;
            this.leftName = leftName;
            this.rightName = rightName;
            this.lineNumber = 1;
            this.currentConflict = 0;
            if (lineNumber == conflicts[currentConflict].getFirstStart()) {
                writeConflict(conflicts[currentConflict]);
                currentConflict++;
            }
            this.fo = fo;
        }
        
        public void write(String str) throws IOException {
            //System.out.println("MergeConflictFileWriter.write("+str+")");
            super.write(str);
            lineNumber += numChars('\n', str);
            //System.out.println("  lineNumber = "+lineNumber+", current conflict start = "+conflicts[currentConflict].getFirstStart());
            if (currentConflict < conflicts.length && lineNumber >= conflicts[currentConflict].getFirstStart()) {
                writeConflict(conflicts[currentConflict]);
                currentConflict++;
            }
        }
        
        private void writeConflict(Difference conflict) throws IOException {
            //System.out.println("MergeConflictFileWriter.writeConflict('"+conflict.getFirstText()+"', '"+conflict.getSecondText()+"')");
            super.write(CHANGE_LEFT + leftName + "\n");
            super.write(conflict.getFirstText());
            super.write(CHANGE_RIGHT + rightName + "\n");
            super.write(conflict.getSecondText());
            super.write(CHANGE_END + "\n");
        }
        
        private static int numChars(char c, String str) {
            int n = 0;
            for (int pos = str.indexOf(c); pos >= 0 && pos < str.length(); pos = str.indexOf(c, pos + 1)) {
                n++;
            }
            return n;
        }
        
        public void close() throws IOException {
            super.close();
            if (fo != null) fo.refresh(true);
        }
    }
}

