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

package org.netbeans.modules.scala.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.UIManager;
import javax.swing.text.Caret;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.extexecution.api.ExecutionDescriptor;
import org.netbeans.modules.extexecution.api.ExecutionDescriptorBuilder;
import org.netbeans.modules.extexecution.api.ExecutionService;
import org.netbeans.modules.extexecution.api.ExternalProcessBuilder;
import org.netbeans.modules.extexecution.api.input.InputProcessor;
import org.netbeans.modules.languages.execution.console.TextAreaReadline;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;
import org.openide.windows.InputOutput;

/**
 *
 * @author Tor Norbye, Caoyuan Deng
 */
final class ScalaConsoleTopComponent extends TopComponent {
    private boolean finished = true;
    private JTextPane textPane;
    private String mimeType = "text/x-console";

    private static ScalaConsoleTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/scala/console/resources/scala16x16.png"; // NOI18N

    private static final String PREFERRED_ID = "ScalaConsoleTopComponent"; // NOI18N

    private ScalaConsoleTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ScalaConsoleTopComponent.class, "CTL_ScalaConsoleTopComponent"));
        setToolTipText(NbBundle.getMessage(ScalaConsoleTopComponent.class, "HINT_ScalaConsoleTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ScalaConsoleTopComponent getDefault() {
        if (instance == null) {
            instance = new ScalaConsoleTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the IrbTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ScalaConsoleTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING,
                    "Cannot find MyWindow component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ScalaConsoleTopComponent) {
            return (ScalaConsoleTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING,
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        if (finished) {
            // Start a new one
            finished = false;
            removeAll();
            createTerminal();
        }
    }

    @Override
    public void componentClosed() {
        // Leave the terminal session running
    }

    @Override
    public void componentActivated() {
        // Make the caret visible. See comment under componentDeactivated.
        if (textPane != null) {
            Caret caret = textPane.getCaret();
            if (caret != null) {
                caret.setVisible(true);
            }
        }
    }

    @Override
    public void componentDeactivated() {
        // I have to turn off the caret when the window loses focus. Text components
        // normally do this by themselves, but the TextAreaReadline component seems
        // to mess around with the editable property of the text pane, and
        // the caret will not turn itself on/off for noneditable text areas.
        if (textPane != null) {
            Caret caret = textPane.getCaret();
            if (caret != null) {
                caret.setVisible(false);
            }
        }
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return ScalaConsoleTopComponent.getDefault();
        }
    }

    public void createTerminal() {
        final PipedInputStream pipeIn = new PipedInputStream();

        textPane = new JTextPane();
        textPane.getDocument().putProperty("mimeType", mimeType);

        textPane.setMargin(new Insets(8,8,8,8));
        textPane.setCaretColor(new Color(0xa4, 0x00, 0x00));
        textPane.setBackground(new Color(0xf2, 0xf2, 0xf2));
        textPane.setForeground(new Color(0xa4, 0x00, 0x00));

        // From core/output2/**/AbstractOutputPane
        Integer i = (Integer) UIManager.get("customFontSize"); //NOI18N
        int size;
        if (i != null) {
            size = i.intValue();
        } else {
            Font f = (Font) UIManager.get("controlFont"); // NOI18N
            size = f != null ? f.getSize() : 11;
        }

        Font font = new Font("Monospaced", Font.PLAIN, size); //NOI18N
        if (font == null) {
            font = new Font("Lucida Sans Typewriter", Font.PLAIN, size);
        }
        textPane.setFont(font);

        setBorder(BorderFactory.createEmptyBorder());

        // Try to initialize colors from NetBeans properties, see core/output2
        Color c = UIManager.getColor("nb.output.selectionBackground"); // NOI18N
        if (c != null) {
            textPane.setSelectionColor(c);
        }


        //Object value = Settings.getValue(BaseKit.class, SettingsNames.CARET_COLOR_INSERT_MODE);
        //Color caretColor;
        //if (value instanceof Color) {
        //    caretColor = (Color)value;
        //} else {
        //    caretColor = SettingsDefaults.defaultCaretColorInsertMode;
        //}
        //text.setCaretColor(caretColor);
        //text.setBackground(UIManager.getColor("text")); //NOI18N
        //Color selectedFg = UIManager.getColor ("nb.output.foreground.selected"); //NOI18N
        //if (selectedFg == null) {
        //    selectedFg = UIManager.getColor("textText") == null ? Color.BLACK : //NOI18N
        //       UIManager.getColor("textText"); //NOI18N
        //}
        //
        //Color unselectedFg = UIManager.getColor ("nb.output.foreground"); //NOI18N
        //if (unselectedFg == null) {
        //    unselectedFg = selectedFg;
        //}
        //text.setForeground(unselectedFg);
        //text.setSelectedTextColor(selectedFg);
        //
        //Color selectedErr = UIManager.getColor ("nb.output.err.foreground.selected"); //NOI18N
        //if (selectedErr == null) {
        //    selectedErr = new Color (164, 0, 0);
        //}
        //Color unselectedErr = UIManager.getColor ("nb.output.err.foreground"); //NOI18N
        //if (unselectedErr == null) {
        //    unselectedErr = selectedErr;
        //}


        JScrollPane pane = new JScrollPane();
        pane.setViewportView(textPane);
        pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        add(pane);
        validate();

        final TextAreaReadline taReadline = new TextAreaReadline(textPane, " " +  // NOI18N
                NbBundle.getMessage(ScalaConsoleTopComponent.class, "ScalaConsoleWelcome") + " \n\n",
                pipeIn); // NOI18N
        File pwd = getMainProjectWorkPath();
	String workPath = pwd.getPath();
        final Reader in = new InputStreamReader(pipeIn);
        final PrintWriter out = new PrintWriter(new PrintStream(taReadline));
        final PrintWriter err = new PrintWriter(new PrintStream(taReadline));
//        ExecutionDescriptor descriptor = new ExecutionDescriptor("Scala Shell", pwd);
//        descriptor.interactive(true).showProgress(false).showSuspended(false).rebuildCmd(true);


        String scalaHome = ScalaExecution.getScalaHome();
        String cmdName = ScalaExecution.getScala().getName();
        List<String> scalaArgs = ScalaExecution.getScalaArgs(scalaHome, cmdName);
        final ExternalProcessBuilder builder = new ExternalProcessBuilder(scalaArgs.get(0));


        for (int index = 1; index < scalaArgs.size(); index++) {
            builder.addArgument(scalaArgs.get(index));
        }
        builder.addEnvironmentVariable("JAVA_HOME", ScalaExecution.getJavaHome());
        builder.addEnvironmentVariable("SCALA_HOME", ScalaExecution.getScalaHome());
        builder.pwd(pwd);

//        ExecutionService executionService = new ScalaExecution(descriptor);
//        Task task = executionService.run(in, out, err);
        ExecutionDescriptorBuilder execBuilder = new ExecutionDescriptorBuilder();
        execBuilder.frontWindow(true).inputVisible(true);
        execBuilder.inputOutput(new CustomInputOutput(in, out, err));
        
        ExecutionService executionService = new ExecutionService(new Callable<Process>() {

            public Process call() throws Exception {
                return builder.create();
            }

        }, "Scala Shell", execBuilder.create());

        Task task = executionService.run();

        task.addTaskListener(new TaskListener() {
            public void taskFinished(Task task) {
                finished = true;
                textPane.setEditable(false);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ScalaConsoleTopComponent.this.close();
                        ScalaConsoleTopComponent.this.removeAll();
                        textPane = null;
                    }
                });
            }
        });

        // [Issue 91208]  avoid of putting cursor in IRB console on line where is not a prompt
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                final int mouseX = ev.getX();
                final int mouseY = ev.getY();
                // Ensure that this is done after the textpane's own mouse listener
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // Attempt to force the mouse click to appear on the last line of the text input
                        int pos = textPane.getDocument().getEndPosition().getOffset()-1;
                        if (pos == -1) {
                            return;
                        }

                        try {
                            Rectangle r = textPane.modelToView(pos);

                            if (mouseY >= r.y) {
                                // The click was on the last line; try to set the X to the position where
                                // the user clicked since perhaps it was an attempt to edit the existing
                                // input string. Later I could perhaps cast the text document to a StyledDocument,
                                // then iterate through the document positions and locate the end of the
                                // input prompt (by comparing to the promptStyle in TextAreaReadline).
                                r.x = mouseX;
                                pos = textPane.viewToModel(r.getLocation());
                            }

                            textPane.getCaret().setDot(pos);
                        } catch (BadLocationException ble) {
                            Exceptions.printStackTrace(ble);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void requestFocus() {
        if (textPane != null) {
            textPane.requestFocus();
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        if (textPane != null) {
            return textPane.requestFocusInWindow();
        }

        return false;
    }

    private File getMainProjectWorkPath() {
        File pwd = null;
        Project mainProject = OpenProjects.getDefault().getMainProject();
        if (mainProject != null) {
            FileObject fo = mainProject.getProjectDirectory();
            if (! fo.isFolder()) {
                fo = fo.getParent();
            }
            pwd = FileUtil.toFile(fo);
        }
        if (pwd == null) {
            String userHome = System.getProperty("user.home");
            pwd = new File(userHome);
        }
        return pwd;
    }

    private static class CustomInputOutput implements InputOutput {

        private final Reader input;

        private final PrintWriter out;

        private final PrintWriter err;

        private boolean closed;

        public CustomInputOutput(Reader input, PrintWriter out, PrintWriter err) {
            this.input = input;
            this.out = out;
            this.err = err;
        }
                
        public void closeInputOutput() {
            closed = true;
        }

        public Reader flushReader() {
            return input;
        }

        public OutputWriter getErr() {
            return new CustomOutputWriter(err);
        }

        public Reader getIn() {
            return input;
        }

        public OutputWriter getOut() {
            return new CustomOutputWriter(out);
        }

        public boolean isClosed() {
            return closed;
        }

        public boolean isErrSeparated() {
            return false;
        }

        public boolean isFocusTaken() {
            return false;
        }

        public void select() {
            
        }

        public void setErrSeparated(boolean value) {
            
        }

        public void setErrVisible(boolean value) {
            
        }

        public void setFocusTaken(boolean value) {
            
        }

        public void setInputVisible(boolean value) {
            
        }

        public void setOutputVisible(boolean value) {
            
        }
    }

    private static class CustomOutputWriter extends OutputWriter {

        public CustomOutputWriter(PrintWriter pw) {
            super(pw);
        }

        @Override
        public void println(String s, OutputListener l) throws IOException {
            println(s);
        }

        @Override
        public void reset() throws IOException {

        }
    }
}
