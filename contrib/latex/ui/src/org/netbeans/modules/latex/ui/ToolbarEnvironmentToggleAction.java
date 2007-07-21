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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Node;
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
public class ToolbarEnvironmentToggleAction extends ToolbarEnvironmentAction implements ToolbarStatusChangeListener, Presenter.Toolbar           {
    
//    private String        command;
    private JToggleButton button;
    
    private EnvironmentDescription description;
    
    private static final EnvironmentDescription EMPTY = new EnvironmentDescription("<empty>", "", true);
    
    private static Icon getIcon(String resource) {
        URL url = ToolbarFactory.class.getClassLoader().getResource(resource);
        
        if (url == null)
            throw new IllegalStateException("Resource " + resource + " not found.");
        
        return new ImageIcon(url);
    }
    
    public ToolbarEnvironmentToggleAction(String icon, String environment) {
        super();
        
        putValue(Action.SMALL_ICON, getIcon(icon));
        button = new JToggleButton(this);
        
//        this.command = command;
        ToolbarUpdater.addToolbarStatusChangeListener(this);
        
        description = new EnvironmentDescription("<name>", environment, false);
        
        ToolbarUpdater.addToUpdate(description);
    }
    
    public JToggleButton getToggleButton() {
        return button;
    }
    
    public String getEnvironment() {
        return description.getEnvironment();
    }
    
    public void actionPerformed(ActionEvent evt) {
        JEditorPane pane = UIUtilities.getCurrentEditorPane();
        
        EnvironmentDescription cd = getToggleButton().isSelected() ? description : EMPTY;
        
        doUpdate(cd);
    }
    
    protected EnvironmentDescription findCorrespondingEnvironmentDescription(BlockNode bn) {
        if (bn != null && getEnvironment().equals(bn.getBlockName())) {
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
