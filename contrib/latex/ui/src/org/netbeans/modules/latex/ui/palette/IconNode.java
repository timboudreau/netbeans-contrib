/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui.palette;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.latex.model.IconsStorage;
import org.netbeans.modules.latex.model.IconsStorage.ChangeableIcon;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Jan Lahoda
 */
public class IconNode extends AbstractNode implements ChangeListener {
    
    private String command;
    private ChangeableIcon icon;
    
    /** Creates a new instance of IconNode */
    public IconNode(String command) {
        super(Children.LEAF);
        this.command = command;
        this.icon = IconsStorage.getDefault().getIcon(command);
        
        this.icon.addChangeListener(this);
    }
    
    public String getShortDescription() {
        return command;
    }
    
    public String getDisplayName() {
        return command;
    }
    
    public Image getIcon(int type) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        icon.paintIcon(null, g, 0, 0);
        
        return image;
    }
    
    public void stateChanged(ChangeEvent e) {
        fireIconChange();
        fireOpenedIconChange();
    }

    public String getCommand() {
        return command;
    }
    
}
