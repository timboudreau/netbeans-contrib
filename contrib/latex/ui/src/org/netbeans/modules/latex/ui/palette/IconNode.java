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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui.palette;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.latex.model.IconsStorage;
import org.netbeans.modules.latex.model.IconsStorage.ChangeableIcon;
import org.netbeans.modules.latex.ui.TexCloneableEditor;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class IconNode extends AbstractNode implements ChangeListener {

    private String displayCommand;
    private String iconCommand;
    private String insertCommand;
    
    /** Creates a new instance of IconNode */
    public IconNode(String command) {
        this(command, command, command);
    }

    public IconNode(String displayCommand, String iconCommand, String insertCommand) {
        super(Children.LEAF);
        this.displayCommand = displayCommand;
        this.iconCommand = iconCommand;
        this.insertCommand = insertCommand;
    }
    
    public String getShortDescription() {
        return displayCommand;
    }
    
    public String getDisplayName() {
        return displayCommand;
    }

    private ChangeableIcon iconSmall;
    private ChangeableIcon iconBig;

    public synchronized Icon getIconImpl(int type) {
        switch (type) {
            case BeanInfo.ICON_COLOR_16x16:
            case BeanInfo.ICON_MONO_16x16:
            default:
                if (iconSmall == null) {
                    iconSmall = IconsStorage.getDefault().getIcon(iconCommand, 16, 16);
                    iconSmall.addChangeListener(this);
                }

                return iconSmall;

            case BeanInfo.ICON_COLOR_32x32:
            case BeanInfo.ICON_MONO_32x32:
                if (iconBig == null) {
                    iconBig = IconsStorage.getDefault().getIcon(iconCommand, 32, 32);
                    iconBig.addChangeListener(this);
                }
                
                return iconBig;
        }
    }

    public Image getIcon(int type) {
        Icon icon = getIconImpl(type);
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        icon.paintIcon(null, g, 0, 0);
        
        return image;
    }
    
    public void stateChanged(ChangeEvent e) {
        fireIconChange();
        fireOpenedIconChange();
        //the palette does not listen on these changes, force redraw:
        TexCloneableEditor.refresh();
    }

    public String getCommand() {
        return insertCommand;
    }

    private static String getAttribute(FileObject file, String name, String defaultValue) {
        Object attribute = file.getAttribute(name);

        if (attribute instanceof String)
            return (String) attribute;

        return defaultValue;
    }

    public static Node createIconNode(FileObject file) {
        String displayCommand = getAttribute(file, "displayCommand", "");
        String iconCommand = getAttribute(file, "iconCommand", "");
        String insertCommand = getAttribute(file, "insertCommand", "");

        return new IconNode(displayCommand, iconCommand, insertCommand);
    }

}
