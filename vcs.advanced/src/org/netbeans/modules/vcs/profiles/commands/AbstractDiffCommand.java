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

package org.netbeans.modules.vcs.profiles.commands;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import org.netbeans.api.diff.Difference;
import org.netbeans.spi.diff.DiffVisualizer;
import org.netbeans.modules.diff.builtin.DefaultDiff;
import org.netbeans.modules.diff.builtin.DiffPresenter;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDisplayer;

/**
 * The abstract diff command, that use a VCS diff command to get the differences.
 *
 * @author  Martin Entlicher
 */
public abstract class AbstractDiffCommand extends Object implements VcsAdditionalCommand, CommandDataOutputListener {
    
    private static final String TEXT_MIMETYPE = "text/plain";

    private VcsFileSystem fileSystem = null;
    private List differences = new ArrayList();

    private File tmpDir = null;
    private File tmpDir2 = null;
    private String tmpDirName = ""; // NOI18N
    private String tmpDir2Name = ""; // NOI18N
    Hashtable vars = null;

    private String rootDir = null;
    private String file = null;
    private String dir = null;
    
    private transient CommandOutputListener stdoutNRListener = null;
    private transient CommandOutputListener stderrNRListener = null;
    private transient CommandDataOutputListener stdoutListener = null;
    protected transient CommandDataOutputListener stderrListener = null;

    protected String diffOutRev1 = null;
    protected String diffOutRev2 = null;
    protected int outputType = -1;
    
    private String checkoutCmd = null;
    private String diffCmd = null;

    /** Set the VCS file system to use to execute commands.
     */
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private boolean checkOut(Hashtable vars, String file, String revision, String tmpDir) {
        VcsCommand cmd = fileSystem.getCommand(checkoutCmd);
        String varRevision = ""; // NOI18N
        if (revision != null) varRevision = ""+revision; // NOI18N
        vars.put("REVISION", varRevision); // NOI18N
        vars.put("TEMPDIR", tmpDir); // NOI18N
        Hashtable newVars = new Hashtable(vars);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, newVars);
        fileSystem.getCommandsPool().preprocessCommand(vce, newVars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(vce);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            return false;
        }
        return vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED;
    }

    private boolean performDiff(String revision1, String revision2) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(diffCmd);
        if (revision1 != null) vars.put("REVISION1", revision1); // NOI18N
        else vars.put("REVISION1", ""); // Put an empty variable to disable the error output about undefined variables // NOI18N
        if (revision2 != null) vars.put("REVISION2", revision2); // NOI18N
        else vars.put("REVISION2", ""); // Put an empty variable to disable the error output about undefined variables // NOI18N
        //System.out.println("diff command: "+cmd); // NOI18N
        Hashtable newVars = new Hashtable(vars);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, newVars);
        vce.addDataOutputListener(this);
        fileSystem.getCommandsPool().preprocessCommand(vce, newVars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(vce);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            // be sure to propagate the interruption
            fileSystem.getCommandsPool().kill(vce);
            throw iexc;
        }
        diffFinished();
        if (vce.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //D.deb("exec failed "+ec.getExitStatus()); // NOI18N
            return false;
        }
        return true;
    }
    
    /**
     * Executes the checkout and diff commands and display differences.
     * @param vars variables needed to run cvs commands
     * @param args the arguments. At least three arguments have to be present:
     *             the diff output type, the checkout command and the diff command.
     *             Additionally, the first two arguments can be the revision tags to be compared.
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull,
     *         false if some error has occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener,
                        CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        boolean status = true;
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        //this.dataRegex = dataRegex;
        this.stderrListener = stderrListener;
        //this.errorRegex = errorRegex;
        this.vars = vars;
        int arglen = args.length;
        //System.out.println("DIFF: args = "+VcsUtilities.arrayToString(args));
        if (arglen < 3) {
            String message = "Too few arguments to Diff command !";
            String[] elements = { message };
            if (stderrListener != null) stderrListener.outputData(elements);
            if (stderrNRListener != null) stderrNRListener.outputLine(message);
            return false;
        }
        try {
            this.outputType = Integer.parseInt(args[arglen - 3]);
        } catch (NumberFormatException e) {
            String message = "Bad output type specification:"+args[arglen - 3];
            String[] elements = { message };
            if (stderrListener != null) stderrListener.outputData(elements);
            if (stderrNRListener != null) stderrNRListener.outputLine(message);
            return false;
        }
        //System.out.println("outputType = "+outputType);
        this.checkoutCmd = args[arglen - 2];
        this.diffCmd = args[arglen - 1];
        String mime = (String) vars.get("MIMETYPE"); // NOI18N
        if (mime == null || mime.indexOf("unknown") >= 0) { // NOI18N
            vars.put("MIMETYPE", TEXT_MIMETYPE);
            mime = TEXT_MIMETYPE;
        }
        final String mimeType = mime;
        this.rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        String module = (String) vars.get("MODULE"); // NOI18N
        if (module == null) module = ""; // NOI18N
        if (module.length() > 0) module += File.separator;
        //this.dir = (String) vars.get("DIR"); // NOI18N
        this.dir = module + (String) vars.get("DIR"); // NOI18N
        this.file = (String) vars.get("FILE"); // NOI18N
        VariableValueAdjustment adjust = fileSystem.getVarValueAdjustment();
        if (adjust != null) {
            this.file = adjust.revertAdjustedVarValue(this.file);
        }
        tmpDir = VcsUtilities.createTMP();
        //tmpDirName = tmpDir.getName();
        tmpDirName = tmpDir.getAbsolutePath();
        String path = rootDir+File.separator+dir+File.separator+file;
        //this.diffDataRegex = (String) vars.get("DATAREGEX"); // NOI18N
        //if (this.diffDataRegex == null) this.diffDataRegex = "(^.*$)"; // NOI18N
        String revision1 = null;
        String revision2 = null;
        if (args.length > 3) {
            revision1 = args[0];
            if (args.length > 4) {
                revision2 = args[1];
            }
        }
        boolean diffStatus;
        try {
            diffStatus = performDiff(revision1, revision2);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (differences.size() == 0) {
            if (diffStatus == true) {
                DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message(
                    NbBundle.getMessage(AbstractDiffCommand.class, "NoDifferenceInFile", file)));
                return true;
            } else {
                return false;
            }
        }
        if (diffOutRev1 != null) revision1 = diffOutRev1;
        if (diffOutRev2 != null) revision2 = diffOutRev2;
        //System.out.println("revision1 = "+revision1+", revision2 = "+revision2);
        if (revision2 != null) {
            tmpDir2 = VcsUtilities.createTMP();
            tmpDir2Name = tmpDir2.getAbsolutePath();
        }
        status = checkOut(vars, dir+File.separator+file, revision1, tmpDirName);
        if (!status) {
            closing();
            return status;
        }
        if (revision2 != null) {
            status = checkOut(vars, dir+File.separator+file, revision2, tmpDir2Name);
            if (!status) {
                closing();
                return status;
            }
        }
        final String file1Title = (revision1 == null) ? getTitleHeadRevision() : getTitleRevision(revision1);
        final String file2Title = (revision2 == null) ? getTitleWorkingRevision() : getTitleRevision(revision2);
        String file1 = tmpDir+File.separator+file;
        String file2;
        if (tmpDir2 == null) {
            file2 = rootDir+File.separator+dir+File.separator+file;
        } else {
            file2 = tmpDir2+File.separator+file;
        }
        DiffPresenter.Info diffInfo = new DiffInfo((Difference[]) differences.toArray(new Difference[differences.size()]),
                                                   NbBundle.getMessage(AbstractDiffCommand.class, "Diff.titleComponent", file), "",
                                                   file1Title, file2Title,
                                                   mimeType, false, true,
                                                   new File(file1), new File(file2),
                                                   tmpDir, tmpDir2);
        DiffPresenter presenter = new DiffPresenter(diffInfo);
        DefaultDiff.DiffTopComponent diffComponent = new DefaultDiff.DiffTopComponent(presenter);
        diffInfo.setPresentingComponent(diffComponent);
        presenter.setVisualizer((DiffVisualizer) Lookup.getDefault().lookup(DiffVisualizer.class));
        diffComponent.open();
        /*
        javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                   public void run () {
                                                       String file1 = tmpDir+File.separator+file;
                                                       String file2;
                                                       if (tmpDir2 == null) {
                                                           file2 = rootDir+File.separator+dir+File.separator+file;
                                                       } else {
                                                           file2 = tmpDir2+File.separator+file;
                                                       }
                                                       diff.open(java.text.MessageFormat.format (org.openide.util.NbBundle.getBundle(AbstractDiff.class).
                                                                                        getString("DiffComponent.titleFile"), new Object[] { file }),
                                                            mimeType, file1, file2, file1Title, file2Title);
                                                       diff.addCloseListener(new TopComponentCloseListener() {
                                                           public void closing() {
                                                               AbstractDiffCmdline.this.closing();
                                                           }
                                                       });
                                                   }
                                               });
         */
        return status;
    }

    /*
     * Called when the diff is being closed. Cleanup of temporary files.
     */
    public void closing() {
        //new File(tmpDir, file).delete();
        //System.out.println("AbstractDiffCmdline.closing(), delete "+tmpDir+" & "+tmpDir2);
        VcsUtilities.deleteRecursive(tmpDir);
        if (tmpDir2 != null) {
            VcsUtilities.deleteRecursive(tmpDir2);
        }
    }
    
    protected boolean checkEmpty(String str, String element) {
        if (str == null || str.length() == 0) {
            if (this.stderrListener != null) {
                String[] elements = { "Bad format of diff result: "+element }; // NOI18N
                stderrListener.outputData(elements);
            }
            return true;
        }
        return false;
    }

    protected abstract String getTitleHeadRevision();
    
    protected abstract String getTitleWorkingRevision();
    
    protected abstract String getTitleRevision(String revNumber);

    /** Add a difference to the list of differences. */
    protected final void addDifference(Difference diff) {
        differences.add(diff);
    }
    
    protected final void setTextOnLastDifference(String text1, String text2) {
        if (differences.size() > 0) {
            if (text1.length() == 0) text1 = null;
            if (text2.length() == 0) text2 = null;
            Difference d = (Difference) differences.remove(differences.size() - 1);
            differences.add(new Difference(d.getType(), d.getFirstStart(), d.getFirstEnd(),
            d.getSecondStart(), d.getSecondEnd(), text1, text2));
        }
    }

    /**
     * This method is called when the diff command finishes.
     * Allows subclasess to do some cleanup of diff actions
     */
    protected void diffFinished() {
    }
    
    public abstract void outputData(String[] elements);
    
    private static class DiffInfo extends DiffPresenter.Info {
        
        private Difference[] diffs;
        private File file1;
        private File file2;
        private File tmpDir;
        private File tmpDir2;
        
        public DiffInfo(Difference[] diffs, String name1, String name2, String title1, String title2,
                        String mimeType, boolean chooseProviders, boolean chooseVisualizers,
                        File file1, File file2, File tmpDir, File tmpDir2) {
            super(name1, name2, title1, title2, mimeType, chooseProviders, chooseVisualizers);
            this.file1 = file1;
            this.file2 = file2;
            this.tmpDir = tmpDir;
            this.tmpDir2 = tmpDir2;
            this.diffs = diffs;
        }
        
        public Reader createFirstReader() {
            try {
                return new FileReader(file1);
            } catch (java.io.FileNotFoundException fnfex) {
                return null;
            }
        }
        
        public Reader createSecondReader() {
            try {
                return new FileReader(file2);
            } catch (java.io.FileNotFoundException fnfex) {
                return null;
            }
        }
        
        public Difference[] getDifferences() {
            return diffs;
        }
        
        protected void finalize() throws Throwable {
            //file1.delete();
            //file2.delete();
            VcsUtilities.deleteRecursive(tmpDir);
            if (tmpDir2 != null) {
                VcsUtilities.deleteRecursive(tmpDir2);
            }
        }
        
    }
}
