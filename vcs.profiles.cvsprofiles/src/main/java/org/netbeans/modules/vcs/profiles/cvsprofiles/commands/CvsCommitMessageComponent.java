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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import org.netbeans.modules.vcscore.util.NestableInputComponent;
import org.netbeans.modules.vcscore.util.VariableInputNest;
import org.netbeans.modules.vcscore.commands.PreCommandPerformer;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.openide.util.UserCancelException;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.io.*;
import java.util.Map;

/**
 * Implements VID.JCOMPONENT for commit message featuring
 * [load template] button.
 *
 * @author Petr Kuzel
 */
public class CvsCommitMessageComponent extends JPanel implements NestableInputComponent {

    /** PreCommandPerformer command. */
    private static final String COMMAND = "{INSERT_OUTPUT_OF_COMMIT_TEMPLATE_GETTER(0, true)}"; // NOI18N

    /** Name of associated variable in cvs.xml profile */
    private static final String VARIABLE = "TEMPLATE_FILE"; // NOI18N

    private VariableInputNest nest;

    private final JLabel label = new JLabel();
    private final JTextArea textArea = new JTextArea();
    private final JButton loadButton = new JButton();
    private RequestProcessor.Task fetcherTask;

    private boolean wasValid = false;
    private boolean wasHistoryChanged = false;

    /** NestableInputComponent is created by reflection. */
    public CvsCommitMessageComponent() {
    }

    public void joinNest(VariableInputNest container) {
        this.nest = container;
        setLayout(new BorderLayout());

        label.setText(getString("COMMAND_COMMIT_Reason"));
        label.setLabelFor(textArea);
        label.setDisplayedMnemonic(getString("COMMAND_COMMIT_Reason_mne").charAt(0));
        add(label, BorderLayout.NORTH);

        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setColumns(80);
        textArea.setRows(8);
        textArea.setToolTipText(getString("COMMAND_COMMIT_Reason_desc"));
        textArea.getAccessibleContext().setAccessibleDescription(getString("COMMAND_COMMIT_Reason_desc"));
        Font font = textArea.getFont();
        textArea.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));  // NOI18N
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateValidity();
            }

            public void removeUpdate(DocumentEvent e) {
                updateValidity();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        add(scrollableTextArea, BorderLayout.CENTER);

        loadButton.setText(getString("COMMAND_COMMIT_tg"));
        loadButton.setToolTipText(getString("COMMAND_COMMIT_tg_desc"));
        loadButton.setMnemonic(getString("COMMAND_COMMIT_tg_mne").charAt(0));
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setEditable(false);
                updateValidity();
                textArea.setText(getString("COMMAND_COMMIT_tl"));
                loadButton.setEnabled(false);
                //loadButton.setText("Cancel Loading");
                TemplateFetcher fetcher = new TemplateFetcher(nest.getCommandExecutionContext(), nest.getCommandHashtable());
                fetcherTask = RequestProcessor.getDefault().post(fetcher);
            }
        });
        JPanel trailingAlign = new JPanel(new FlowLayout(FlowLayout.TRAILING, 4, 0));
        trailingAlign.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
        trailingAlign.add(loadButton);
        add(trailingAlign, BorderLayout.SOUTH);
    }

    /** @return Absolute path to message file. */
    public String getValue(String variable) {
        assert VARIABLE.equals(variable);

        if (validityCheck()) {
            try {
                return saveContent();
            } catch (IOException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, getString("COMMAND_COMMIT_ex"));
                err.notify(e);
            }
        }
        return null;
    }

    // #54683 put focus directly into text area
    public void requestFocus() {
        textArea.requestFocus();
    }

    public boolean requestFocus(boolean temporary) {
        return textArea.requestFocus(temporary);
    }

    public boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();
    }


    /**
     * Save the cleaned content of the text area into a temp file.
     * @return the file full path.
     */
    private String saveContent() throws IOException {
        String content = cleanupContent(textArea.getText());
        File tmpfile = File.createTempFile("cvs-commit-", ".input");  // NOI18N
        tmpfile.deleteOnExit();
        Writer w = new FileWriter(tmpfile);
        try {
            w.write(content);
            w.flush();
            return tmpfile.getAbsolutePath();
        } finally {
            w.close();
        }
    }
    
    /**
     * Load the content of the text area from a file.
     */
    private void loadContent(String fileName) throws IOException {
        Reader r = new FileReader(fileName);
        try {
            textArea.read(r, null);
        } finally {
            r.close();
        }
    }

    public String getVerificationMessage(String variable) {
        assert VARIABLE.equals(variable) : "Unexpected var:" + variable; // NOI18N
        if (validityCheck()) {
            return null;
        } else if (loadingInProgress()) {
            return getString("COMMAND_COMMIT_val_l");
        } else {
            return getString("COMMAND_COMMIT_val_m");
        }
    }

    /** Commit message is checked and template loading must not be in progress. */
    private boolean validityCheck() {
        return  loadingInProgress() == false && commitMessage();
    }

    /** Commit message must be entered or good practises policy set to none */
    private boolean commitMessage() {
        return cleanupContent(textArea.getText()).length() > 0 || "none".equalsIgnoreCase(System.getProperty("vcs.practices.policy"));
    }

    /** Is running background template loading? */
    private boolean loadingInProgress() {
        return textArea.isEditable() == false;
    }

    /** Removes all lines begining with "CVS:" */
    private static String cleanupContent(String template) {
        BufferedReader r = new BufferedReader(new StringReader(template));
        try {
            StringWriter sw = new StringWriter(template.length());
            PrintWriter w = new PrintWriter(sw);
            while (true) {
                try {
                    String line = r.readLine();
                    if (line == null) break;
                    if (line.startsWith("CVS:")) continue;   // NOI18N
                    w.println(line);
                } catch (IOException e) {
                    ErrorManager err = ErrorManager.getDefault();
                    err.notify(e);
                }
            }
            w.flush();
            return sw.toString().trim();
        } finally {
            try {
                r.close();
            } catch (IOException e) {
                // already closed
            }
        }
    }

    /** Refires validity event. */
    private void updateValidity() {
        boolean valid = validityCheck();
        if (valid != wasValid) {
            nest.fireValueChanged(VARIABLE, null);
            wasValid = valid;
        }
    }

    public void leaveNest() {
        nest = null;
        if (fetcherTask != null) fetcherTask.cancel();
    }

    /** Seeks for loacalized string */
    private static String getString(String key) {
        return NbBundle.getMessage(CvsCommitMessageComponent.class, key);
    }

    public void updatedVars(Map variables) {
        String message = (String) variables.get("message");
        if (message != null) {
            textArea.setText(message);
        }
    }
    
    public void setHistoricalValue(String historicalValue) {
        if (historicalValue == null) {
            textArea.setText(""); // Set an empty text when it was not valid.
        } else {
            try {
                loadContent(historicalValue);
            } catch (IOException ioex) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(ioex, getString("COMMAND_COMMIT_ex"));
                err.notify(ioex);
            }
        }
    }
    
    /** Asynchronously loads template from the server. */
    private class TemplateFetcher implements Runnable {

        private final CommandExecutionContext ctx;

        private final Hashtable env;

        public TemplateFetcher(CommandExecutionContext ctx, Hashtable env) {
            this.ctx = ctx;
            this.env = env;
        }

        public void run() {
            PreCommandPerformer executor = new PreCommandPerformer(ctx, env);
            try {
                final String content = executor.process(COMMAND);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        textArea.setText(content);
                        textArea.requestFocus();
                        textArea.getCaret().setDot(0);
                        textArea.setEditable(true);
                        loadButton.setEnabled(true);
                        fetcherTask = null;
                        updateValidity();
                    }
                });
            } catch (UserCancelException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, "Unexpected dialog raised by " + COMMAND);  // NOU18N
                err.notify(e);
            }
        }
    }
}
