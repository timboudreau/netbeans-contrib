/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.portlets.util.ui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

public class DirectoryTree extends JTree
			      implements TreeSelectionListener, MouseListener{

      private static FileSystemView fsv = FileSystemView.getFileSystemView();

    public String selectedDir = "";

    /*--- Begin Public API -----*/

    public DirectoryTree() {
	this(null);
    }

    public DirectoryTree(File dir) {
	super(new DirNode(dir));
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
	final DirectoryTree dc  = new DirectoryTree(new File(rootDir));
	final JButton okButton     = new JButton(org.openide.util.NbBundle.getMessage(DirectoryTree.class, "LBL_OK"));
	final JButton cancelButton = new JButton(org.openide.util.NbBundle.getMessage(DirectoryTree.class, "LBL_Cancel"));

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
        DirectoryTree dir = new DirectoryTree(new File("/space/docs"));

//        dir.open();
        System.out.println("hahahah ............... "+dir.getSelectedDirectory());
    }
}
