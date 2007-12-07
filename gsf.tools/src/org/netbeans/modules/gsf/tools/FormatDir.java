package org.netbeans.modules.gsf.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import org.netbeans.modules.gsf.Language;
import org.netbeans.modules.gsf.LanguageRegistry;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;


public final class FormatDir extends CallableSystemAction {
    public void performAction() {
        final File[] fileHolder = new File[1];

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        JFileChooser fc = new JFileChooser();
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        // Show save dialog; this method does not return until the dialog is closed
                        if (fc.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
                            File selDir = fc.getSelectedFile();
                            fileHolder[0] = selDir;
                        }
                    }
                });
        } catch (InterruptedException ie) {
            Exceptions.printStackTrace(ie);
        } catch (InvocationTargetException ie) {
            Exceptions.printStackTrace(ie);
        }

        File selDir = fileHolder[0];
        FileObject dir = FileUtil.toFileObject(selDir);

        if (dir != null) {
            for (FileObject fo : dir.getChildren()) {
                reformat(fo);
            }
        }

        StatusDisplayer.getDefault().setStatusText("Formatting done");
    }

    private void reformat(FileObject fo) {
        if (fo.isFolder()) {
            for (FileObject child : fo.getChildren()) {
                reformat(child);
            }

            return;
        }

        Document doc = null;
        DataObject dobj = null;

        try {
            dobj = DataObject.find(fo);
        } catch (DataObjectNotFoundException dnfe) {
            Exceptions.printStackTrace(dnfe);

            return;
        }

        LanguageRegistry registry = LanguageRegistry.getInstance();

        if (!registry.isSupported(fo.getMIMEType())) {
            return;
        }

        EditorCookie ec = dobj.getCookie(EditorCookie.class);

        if (ec == null) {
            System.err.println("WARNING - skipping " + FileUtil.getFileDisplayName(fo) +
                " - no editor cookie");
        }

        try {
            doc = ec.openDocument();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }

        if (doc == null) {
            System.err.println("WARNING - skipping " + FileUtil.getFileDisplayName(fo) +
                " - doc == null");
        }

        StatusDisplayer.getDefault().setStatusText("Formatting " + fo.getName());

        Language language = registry.getLanguageByMimeType(fo.getMIMEType());

        if (language == null) {
            System.err.println("WARNING - skipping " + FileUtil.getFileDisplayName(fo) +
                " - language == null");

            return;
        }

        if (language.getFormatter() == null) {
            System.err.println("WARNING - skipping " + FileUtil.getFileDisplayName(fo) +
                " - no formatter");
        }

        int indentSize = language.getFormatter().indentSize();
        int startOffset = 0;
        int endOffset = doc.getLength();
        language.getFormatter().reindent(doc, startOffset, endOffset, null);

        // Save
        SaveCookie sc = dobj.getCookie(SaveCookie.class);

        if (sc != null) {
            try {
                sc.save();
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(FormatDir.class, "CTL_FormatDir");
    }

    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return true;
    }
}
