/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.group;


import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAcceptor;
import org.openide.TopManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;
import org.openide.util.actions.CookieAction;


/**
 * Creates group of data objects, i.e group data object, known
 * as {@link GroupShadow}.
 *
 * @author Martin Ryzl
 * @see GroupShadow
 */
public class CreateGroupAction extends CookieAction {

    /** Generated serail veriosn UID. */
    static final long serialVersionUID =-280394671195477993L;
    
    
    /** Performs action based on activated nodes. Implements superclass abstract method. */
    protected void performAction(final Node[] nodes) {
        try {
            FileObject fo = selectFile();
            ArrayList list;

            if (fo != null) {
                list = new ArrayList();
                for(int i = 0; i < nodes.length; i++) {
                    Object obj = nodes[i].getCookie(DataObject.class);
                    if (obj != null) {
                        list.add(GroupShadow.getLinkName(((DataObject)obj).getPrimaryFile()));
                    }
                }
                GroupShadow.writeLinks(list, fo);
            }
        } catch(IOException ex) {
            TopManager.getDefault().notifyException(ex);
        }
    }

    /** Gets icon resource. Overrides suprclass method. */
    protected String iconResource () {
        return "org/netbeans/modules/group/resources/groupShadow.gif"; // NOI18N
    }

    /** Gets cookies which should contain nodes to enable the action. Implements superclass abstract method. 
     * @return <code>DataObject</code>.class as cookie */
    protected Class[] cookieClasses() {
        return new Class[] { DataObject.class };
    }

    /** Call super.enable and when it returns true,
     * test if activated nodes are not on default file system. If yes return false.
     * @return true when activated nodes are not on default filesystem and super class class is also true.
     */
    protected boolean enable (Node[] activatedNodes) {
        boolean enable = super.enable (activatedNodes);

        if ( enable ) { // if super call enable this action, test it more to default file system ...
            // Fix #14740 disable action on SystemFileSystem.
            for (int i = 0; i < activatedNodes.length; i++) {
                DataObject dataObject = (DataObject)activatedNodes[i].getCookie (DataObject.class);
                FileObject fileObject = dataObject.getPrimaryFile();
                try {
                    if ( fileObject.isRoot() || fileObject.getFileSystem().isDefault() ) {
                        enable = false;
                        break;
                    }
                } catch (FileStateInvalidException fsie) {
                }
            } // for
        }
        
        return enable;
    }

    /** Get mode telling which cookies have to be present the action to enable. Implements superclass abstract method.
     * @return MODE_ALL */
    protected int mode() {
        return MODE_ALL;
    }

    /** Gets name of action. Implements superclass abstract method. */
    public String getName() {
        return NbBundle.getBundle(CreateGroupAction.class).getString("CreateGroup");
    }

    /** Gets help context for this action. Implements superclass abstract method. */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CreateGroupAction.class);
    }

    /** Lets the user to select a group shadow. Utilitiy method.
     * @return <code>FileObject</code> for the filesystem. 
     * @exception IOException when error occures */
    protected static FileObject selectFile() throws IOException {

        InputPanel jp = new InputPanel();

        try {
            // repository
            Node an = TopManager.getDefault().getPlaces().nodes().repository();

            NodeAcceptor na = new NodeAcceptor() {
                                  public boolean acceptNodes(Node[] nodes) {
                                      if (nodes == null || nodes.length != 1) {
                                          return false;
                                      }
                                      DataFolder cookie = (DataFolder)nodes[0].getCookie (DataFolder.class);
                                      return cookie != null && !cookie.getPrimaryFile ().isReadOnly ();
                                  }
                              };

            // select file system
            Node[] nodes = TopManager.getDefault().getNodeOperation().select(
                               NbBundle.getBundle (CreateGroupAction.class).getString ("PROP_Select_File"),
                               NbBundle.getBundle (CreateGroupAction.class).getString ("PROP_Look_In"),
                               an, na, jp
                           );

            FileObject folder = ((DataFolder)nodes[0].getCookie(DataFolder.class)).getPrimaryFile();
            return folder.createData(jp.getText(), GroupShadow.GS_EXTENSION );
        } catch (UserCancelException ex) {
            return null;
        } catch (NullPointerException ex) {
            // could occur if nodes[0].getCookie returns null, but
            // it should not happen because of the filter
            return null;
        }
    } // select file

    
    /** Input panel. */
    private static class InputPanel extends JPanel {

        /** Texfield. */
        JTextField text;

        /**  Generated serial verison UID. */
        static final long serialVersionUID =2856913107896554654L;
        
        
        /** Constructor. */
        public InputPanel () {
            BorderLayout lay = new BorderLayout ();
            lay.setVgap(5);
            lay.setHgap(5);
            setLayout (lay);

            // label and text field with mnemonic
            JLabel label = new JLabel (NbBundle.getBundle (CreateGroupAction.class).getString ("CTL_Group_Name"));
            label.setDisplayedMnemonic (NbBundle.getBundle (CreateGroupAction.class).getString ("CTL_Group_Name_MNEM").charAt (0));

            text = new JTextField ();
            text.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle (CreateGroupAction.class).getString ("ACS_TextField"));

            label.setLabelFor(text);
            add (BorderLayout.WEST, label);
            add (BorderLayout.CENTER, text);
        }

        
        /** Request focus delegates to texfield. */
        public void requestFocus () {
            text.requestFocus ();
        }

        /** Gets text from textfield. */
        public String getText () {
            return text.getText ();
        }

        /** Set text to textfield. */
        public void setText (String s) {
            setText (s);
        }
    } // End of InputPanel class.
}
