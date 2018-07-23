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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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
 *
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import org.netbeans.api.diff.Difference;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.spi.diff.DiffProvider;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

public class TeamwareCreatePatchCommand implements VcsAdditionalCommand {
    
    private VcsFileSystem fileSystem;
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public boolean exec(Hashtable vars, String[] args,
    CommandOutputListener stdout,
    CommandOutputListener stderr,
    CommandDataOutputListener stdoutDataListener, String dataRegex,
    CommandDataOutputListener stderrDataListener, String errorRegex) {
        
        String rootDir = (String) vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        String dirName = (String) vars.get("DIR");
        File root = new File(rootDir);
        File dir = (module != null) ? new File(root, module) : root;
        if (dirName != null) {
            dir = new File(dir, dirName);
        }
        String path = "";
        if (module != null) {
            path = module.replace(File.separatorChar, '/');
            if (!path.endsWith("/")) {
                path += "/";
            }
        }
        if (dirName != null) {
            path += dirName.replace(File.separatorChar, '/');
            if (!path.endsWith("/")) {
                path += "/";
            }
        }
        String fileName = (String) vars.get("FILE");
        if (fileName != null) {
            path += fileName;
        }
        File file;
        if (fileName == null) {
            file = dir;
        } else {
            file = new File(dir, fileName);
        }
        if (file.isDirectory() && !path.endsWith("/")) {
            path += "/";
        }
        File toFile = new File((String) vars.get("TOFILE"));
        String editedOnly = (String) vars.get("EDITED_FILES_ONLY");
        String date = (String) vars.get("DATE");
        if (date == null) {
            date = "700101";
        }
        try {
            PrintStream out = new PrintStream(new FileOutputStream(toFile));
            diff(path, out, file, new Boolean(editedOnly).booleanValue(), date);
            out.close();
            FileObject[] fos = FileUtil.fromFile(toFile);
            for (int i = 0; i < fos.length; i++) {
                try {
                    DataObject dobj = DataObject.find(fos[i]);
                    ((OpenCookie) dobj.getCookie(OpenCookie.class)).open();
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    private void diff(String path, PrintStream out, File file, boolean editedOnly, String sinceDate) {
        CommandOutputListener devnull = new CommandOutputListener() {
            public void outputLine(String _) { }
        };
        if (file.isDirectory()) {
            diffDir(path, out, file, editedOnly, sinceDate);
        } else {
            File sccsDir = new File(file.getParentFile(), "SCCS");
            String[] data = TeamwareRefreshSupport.listFile(file,
                sccsDir, devnull);
            if (data[0] != null) {
                if (editedOnly) {
                    if (data[0].equals("Editing")) {
                        diffFile(path, out, file);
                    }
                } else {
                    if (data[0].equals("Editing") || data[0].equals("Checked in")) {
                        diffFile(path, out, file, sinceDate, data[0]);
                    }
                }
                
            }
        }
    }
    
    private void diffDir(String path, PrintStream out, File dir, boolean editedOnly, String sinceDate) {
	if (TeamwareRefreshSupport.ignoreFile(dir)) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0 ; i < files.length; i++) {
            String fileName = files[i].getName();
            if (files[i].isDirectory()) {
                fileName += "/";
            }
            diff(path + fileName, out, files[i], editedOnly, sinceDate);
        }
    }
    
    private void diffFile(String path, final PrintStream out, File file) {
        File rFile = null;
        try {
            rFile = File.createTempFile("sccs", "txt");
            boolean exists = retrieveOldVersion(file, rFile, "");
            out.print(getDiffs(path, rFile, file, exists));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        if (rFile != null) {
            rFile.delete();
        }
    }
    
    private void diffFile(String path,
        PrintStream out, File file, String sinceDate, String status) {
            
        File rFile = null;
        boolean deleteFile = false;
        try {
            rFile = File.createTempFile("sccs", "txt");
            boolean exists = retrieveOldVersion(file, rFile, "-c" + sinceDate);
            if (status.equals("Checked in")) {
                File kFile = File.createTempFile("sccs", "txt");
                if (retrieveOldVersion(file, kFile, "")) {
                    file = kFile;
                    deleteFile = true;
                }
            }
            out.print(getDiffs(path, rFile, file, exists));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        if (rFile != null) {
            rFile.delete();
        }
        if (deleteFile) {
            file.delete();
        }
    }
    
    /** Returns true iff there was a valid version available of the file
     * requested. */
    private boolean retrieveOldVersion(File file, File rFile, String revArgs) throws Exception {
        rFile.setReadOnly();
        Hashtable vars = new Hashtable();
        vars.put("WORKDIR", file.getParent());
        vars.put("FILE", file.getName());
        vars.put("TMPFILE", rFile.toString());
        vars.put("REVARGS", "-k" + (revArgs.length() == 0 ? "" : " ") + revArgs);
        VcsCommand cmd = fileSystem.getCommand("GET_REVISION");
                    VcsCommandExecutor ec =
            fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        fileSystem.getCommandsPool().waitToFinish(ec);
        return ec.getExitStatus() == 0;
    }
    
    /* The rest of the code here is lifted from
     * org.netbeans.modules.diff.builtin.visualizer.TextDiffVisualizer
     */

    private static final String CONTEXT_MARK1B = "*** ";
    private static final String CONTEXT_MARK1E = " ****\n";
    private static final String CONTEXT_MARK2B = "--- ";
    private static final String CONTEXT_MARK2E = " ----\n";
    private static final String CONTEXT_MARK_DELIMETER = ",";
    private static final String DIFFERENCE_DELIMETER = "***************\n";
    private static final String LINE_PREP = "  ";
    private static final String LINE_PREP_ADD = "+ ";
    private static final String LINE_PREP_REMOVE = "- ";
    private static final String LINE_PREP_CHANGE = "! ";

    private String getDiffs(String path,
        File oldFile, File currentFile,
        boolean exists) throws IOException {
            
        DiffProvider dp =
            (DiffProvider) Lookup.getDefault().lookup(DiffProvider.class);
        BufferedReader br1 = new BufferedReader(new FileReader(oldFile));
        BufferedReader br2 = new BufferedReader(new FileReader(currentFile));
        Difference[] diffs = dp.computeDiff(br1, br2);
        if (diffs.length == 0) {
            return "";
        }
        br1.close();
        br2.close();
        br1 = new BufferedReader(new FileReader(oldFile));
        br2 = new BufferedReader(new FileReader(currentFile));
        StringBuffer content = new StringBuffer();
        content.append("\n------- ");
        content.append(path);
        content.append(" -------\n");
        if (exists) {
            content.append(CONTEXT_MARK1B);
            content.append(path);
            content.append("\n");
        }
        content.append(CONTEXT_MARK2B);
        content.append(path);
        content.append("\n");
        int contextNumLines = 3;
        int line1 = 1; // Current line read from 1st file
        int line2 = 1; // Current line read from 2nd file
        for (int i = 0; i < diffs.length; i++) {

            content.append(DIFFERENCE_DELIMETER);

            int[] cr = getContextRange(diffs, i, contextNumLines);

            int begin = diffs[i].getFirstStart() - contextNumLines;
            if (diffs[i].getType() == Difference.ADD) {
                begin++;
            }
            if (begin < 1) {
                begin = 1;
            }
            StringBuffer context = new StringBuffer();
            line1 = dumpContext(0, diffs, i, cr[0], context, contextNumLines, br1, line1);
            if (line1 <= cr[1]) {
                cr[1] = line1 - 1;
            }
            content.append(CONTEXT_MARK1B);
            if (exists) {
                content.append(begin);
                content.append(CONTEXT_MARK_DELIMETER);
                content.append(cr[1]);
            } else {
                content.append("0");
            }
            content.append(CONTEXT_MARK1E);
            content.append(context);

            begin = diffs[i].getSecondStart() - contextNumLines;
            if (diffs[i].getType() == Difference.DELETE) {
                begin++;
            }
            if (begin < 1) {
                begin = 1;
            }
            context = new StringBuffer();
            line2 = dumpContext(1, diffs, i, cr[0], context, contextNumLines, br2, line2);
            if (line2 <= cr[2]) {
                cr[2] = line2 - 1;
            }
            content.append(CONTEXT_MARK2B);
            content.append(begin);
            content.append(CONTEXT_MARK_DELIMETER);
            content.append(cr[2]);
            content.append(CONTEXT_MARK2E);
            content.append(context);

            i = cr[0];
            //i = dumpContext(diffs, 
            //Difference diff = diffs[i];
            //Difference nextDiff = ((i + 1) < diffs.length) ? diffs[i + 1] : null;
            //if (isNew) {
            //    content.append(DIFFERENCE_DELIMETER);
            //}
        }
        return content.toString();
    }
    
    private static int[] getContextRange(Difference[] diffs, int i,
                                       int contextNumLines) {
        int line1 = diffs[i].getFirstStart();
        int line2 = diffs[i].getSecondStart();
        for ( ; i < diffs.length; i++) {
            Difference diff = diffs[i];
            if (line1 + 2*contextNumLines < diff.getFirstStart() &&
                line2 + 2*contextNumLines < diff.getSecondStart()) {
                break;
            }
            line1 = diff.getFirstStart();
            line2 = diff.getSecondStart();
            int l1 = Math.max(0, diff.getFirstEnd() - diff.getFirstStart());
            int l2 = Math.max(0, diff.getSecondEnd() - diff.getSecondStart());
            line1 += l1;
            line2 += l2;
        }
        return new int[] { i - 1, line1 + contextNumLines, line2 + contextNumLines };
    }
    
    private static int dumpContext(int which, Difference[] diffs, int i, int j,
        StringBuffer content, int contextNumLines, BufferedReader br, int line)
        throws IOException {

        int startLine;
        if (which == 0) {
            startLine = diffs[i].getFirstStart() - contextNumLines;
            if (diffs[i].getType() == Difference.ADD) {
                startLine++;
            }
        } else {
            startLine = diffs[i].getSecondStart() - contextNumLines;
            if (diffs[i].getType() == Difference.DELETE) {
                startLine++;
            }
        }
        for ( ; line < startLine; line++) {
            br.readLine();
        }
        int position = content.length();
        boolean isChange = false;
        for ( ; i <= j; i++) {
            Difference diff = diffs[i];
            if (which == 0) {
                startLine = diff.getFirstStart();
            }
            else startLine = diff.getSecondStart();
            for ( ; line < startLine; line++) {
                content.append(LINE_PREP);
                content.append(br.readLine());
                content.append("\n");
            }
            int length = 0;
            String prep = null;
            switch (diffs[i].getType()) {
                case Difference.ADD:
                    if (which == 1) {
                        prep = LINE_PREP_ADD;
                        length = diff.getSecondEnd() - diff.getSecondStart() + 1;
                    }
                    break;
                case Difference.DELETE:
                    if (which == 0) {
                        prep = LINE_PREP_REMOVE;
                        length = diff.getFirstEnd() - diff.getFirstStart() + 1;
                    }
                    break;
                case Difference.CHANGE:
                    prep = LINE_PREP_CHANGE;
                    if (which == 0) {
                        length = diff.getFirstEnd() - diff.getFirstStart() + 1;
                    } else {
                        length = diff.getSecondEnd() - diff.getSecondStart() + 1;
                    }
                    break;
            }
            if (prep != null) {
                isChange = true;
                for (int k = 0; k < length; k++, line++) {
                    content.append(prep);
                    content.append(br.readLine());
                    content.append("\n");
                }
            }
        }
        if (!isChange) {
            content.delete(position, content.length());
        } else {
            for (int k = 0; k < contextNumLines; k++, line++) {
                String lineStr = br.readLine();
                if (lineStr == null) {
                    break;
                }
                content.append(LINE_PREP);
                content.append(lineStr);
                content.append("\n");
            }
        }
        return line;
    }


}
