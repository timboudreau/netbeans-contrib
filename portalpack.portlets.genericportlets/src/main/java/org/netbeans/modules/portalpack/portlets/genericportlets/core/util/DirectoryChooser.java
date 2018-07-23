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
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

public class DirectoryChooser extends JTree
			      implements TreeSelectionListener, MouseListener{

      private static FileSystemView fsv = FileSystemView.getFileSystemView();

    public String selectedDir = "";

    /*--- Begin Public API -----*/

    public DirectoryChooser() {
	this(null);
    }

    public DirectoryChooser(File dir) {
	super(new DirNode(fsv.getRoots()[0]));
	getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	setSelectedDirectory(dir);
	addTreeSelectionListener(this);
	addMouseListener(this);
    }

    public void setSelectedDirectory(File dir) {
	if (dir == null) {
	    dir = fsv.getDefaultDirectory();
	}
	setSelectionPath(mkPath(dir));
    }

    public File getSelectedDirectory() {
	DirNode node = (DirNode)getLastSelectedPathComponent();
	if (node != null) {
	    File dir = node.getDir();
	    if (fsv.isFileSystem(dir)) {
		return dir;
	    }
	}
	return null;
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    public ActionListener[] getActionListeners() {
        return (ActionListener[])listenerList.getListeners(ActionListener.class);
    }

    /*--- End Public API -----*/




    /*--- TreeSelectionListener Interface -----*/

    public void valueChanged(TreeSelectionEvent ev) {
	File oldDir = null;
	TreePath oldPath = ev.getOldLeadSelectionPath();
	if (oldPath != null) {
	    oldDir = ((DirNode)oldPath.getLastPathComponent()).getDir();
	    if (!fsv.isFileSystem(oldDir)) {
		oldDir = null;
	    }
	}
	File newDir = getSelectedDirectory();
	firePropertyChange("selectedDirectory", oldDir, newDir);
    }

    /*--- MouseListener Interface -----*/

    public void mousePressed(MouseEvent e) {
	if (e.getClickCount() == 2) {
	    TreePath path = getPathForLocation(e.getX(), e.getY());
	    if (path != null && path.equals(getSelectionPath()) &&
		getSelectedDirectory() != null) {

		fireActionPerformed("dirSelected", e);
	    }
	}
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


    /*--- Private Section ------*/

    private TreePath mkPath(File dir) {
	DirNode root = (DirNode)getModel().getRoot();
	if (root.getDir().equals(dir)) {
	    return new TreePath(root);
	}

	TreePath parentPath = mkPath(fsv.getParentDirectory(dir));
	DirNode parentNode = (DirNode)parentPath.getLastPathComponent();
	Enumeration enumeration = parentNode.children();
	while (enumeration.hasMoreElements()) {
	    DirNode child = (DirNode)enumeration.nextElement();
	    if (child.getDir().equals(dir)) {
		return parentPath.pathByAddingChild(child);
	    }
	}
	return null;
    }


    private void fireActionPerformed(String command, InputEvent evt) {
        ActionEvent e =
	    new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
			    command, evt.getWhen(), evt.getModifiers());
	ActionListener[] listeners = getActionListeners();
        for (int i = listeners.length - 1; i >= 0; i--) {
	    listeners[i].actionPerformed(e);
        }
    }


    private static class DirNode extends DefaultMutableTreeNode {
	DirNode(File dir) {
	    super(dir);
	}

	public File getDir() {
	    return (File)userObject;
	}

	public int getChildCount() {
	    populateChildren();
	    return super.getChildCount();
	}

	public Enumeration children() {
	    populateChildren();
	    return super.children();
	}

	public boolean isLeaf() {
	    return false;
	}

	private void populateChildren() {
	    if (children == null) {
		File[] files = fsv.getFiles(getDir(), true);
		Arrays.sort(files);
		for (int i = 0; i < files.length; i++) {
		    File f = files[i];
		    if (fsv.isTraversable(f).booleanValue()) {
			insert(new DirNode(f),
			       (children == null) ? 0 : children.size());
		    }
		}
	    }
	}

	public String toString() {
	    return fsv.getSystemDisplayName(getDir());
	}

	public boolean equals(Object o) {
	    return (o instanceof DirNode &&
		    userObject.equals(((DirNode)o).userObject));
	}
    }


    /*--- Main for testing  ---*/

    public void open(String rootDir) {
//	try {
//	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//	} catch (Exception ex) {
//	}

	final JDialog dialog = new JDialog((JFrame)null, true);
	final DirectoryChooser dc  = new DirectoryChooser(new File(rootDir));
	final JButton okButton     = new JButton(org.openide.util.NbBundle.getMessage(DirectoryChooser.class, "LBL_OK"));
	final JButton cancelButton = new JButton(org.openide.util.NbBundle.getMessage(DirectoryChooser.class, "LBL_Cancel"));

	dialog.getContentPane().add(new JScrollPane(dc), BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);
	dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	ActionListener actionListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		Object c = e.getSource();
		if (c == okButton){// || c == dc) {
            selectedDir = dc.getSelectedDirectory().getAbsolutePath();
    //	    System.out.println("You selected: "+dc.getSelectedDirectory());
		}if(c == cancelButton)
            selectedDir = "";
        dialog.dispose();
	    }
	};

//	dc.addActionListener(actionListener);
	okButton.addActionListener(actionListener);
	cancelButton.addActionListener(actionListener);

	dc.addPropertyChangeListener(new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent ev) {
		if (ev.getPropertyName().equals("selectedDirectory")) {
		    okButton.setEnabled(dc.getSelectedDirectory() != null);
		}
	    }
	});

	dialog.setBounds(200, 200, 300, 350);
	dc.scrollRowToVisible(Math.max(0, dc.getMinSelectionRow()-4));
	dialog.setVisible(true);
	//System.exit(0);
    }

    public String getSelectedDir()
    {
        return selectedDir;
    }


    public static void main(String[] args)
    {
        DirectoryChooser dir = new DirectoryChooser(new File("/space/docs"));

//        dir.open();
        System.out.println("hahahah ............... "+dir.getSelectedDirectory());
    }
}