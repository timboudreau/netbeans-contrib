/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.ErrorManager;
import org.openide.util.actions.Presenter;

/** This action represents the buttons on the toolbar that provides Bold, Italics, Emphasine,
 *  etc. This class assures both directions of communication, that means when the caret is moved
 *  into a (for example) bold text, the button reflects this change, and when the button is
 *  pressed, the currently selected text is surrounded with (for example) \textbf tag.
 *
 *  This call cannot be used for buttons that requires environments!
 *
 * @author  Jan Lahoda
 */
public class ToolbarCommandToggleAction extends    ToolbarCommandAction
                                        implements ToolbarStatusChangeListener,
                                                   Presenter.Toolbar           {
    
//    private String        command;
    private JToggleButton button;
    
    private CommandDescription description;
    
    private static final CommandDescription EMPTY = new CommandDescription("<empty>", "", true);
    
    private static Icon getIcon(String resource) {
        URL url = ToolbarFactory.class.getClassLoader().getResource(resource);
        
        if (url == null)
            throw new IllegalStateException("Resource " + resource + " not found.");
        
        return new ImageIcon(url);
    }
    
    public ToolbarCommandToggleAction(String icon, String command) {
        super();
        
        putValue(Action.SMALL_ICON, getIcon(icon));
        button = new JToggleButton(this);
        
//        this.command = command;
        ToolbarUpdater.getDefault().addToolbarStatusChangeListener(this);
        
        description = new CommandDescription("<name>", command, false);
    }
    
    public JToggleButton getToggleButton() {
        return button;
    }
    
    public String getCommand() {
        return description.getCommand();
    }
    
    public void actionPerformed(ActionEvent evt) {
        JEditorPane pane = UIUtilities.getCurrentEditorPane();
        
        CommandDescription cd = getToggleButton().isSelected() ? description : EMPTY;
        
        doUpdate(cd);
    }
    
    protected CommandDescription findCorrespondingCommandDescription(CommandNode cn) {
        if (cn != null && getCommand().equals(cn.getCommand().getCommand())) {
            return description;
        } else {
            return EMPTY;
        }
    }
    
    public void statusChange(Node currentNode) {
        getToggleButton().setSelected(findCorrespondingNode(currentNode) != null);
    }
    
    public void enableChange(final boolean enable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getToggleButton().setEnabled(enable);
            }
        });
    }
    
    public Component getToolbarPresenter() {
        return button;
    }
    
}
