/*
 * TargetButton.java
 *
 * Created on July 25, 2004, 10:05 PM
 */

package org.netbeans.modules.targets;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import java.beans.BeanInfo;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.nodes.*;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.filesystems.*;

import org.apache.tools.ant.module.api.*;
/**
 *
 * @author  Tim Boudreau
 */
class TargetButton extends JButton implements ActionListener, NodeListener {
    private Node node = null;
    
    /** Creates a new instance of TargetButton */
    public TargetButton(Node n) {
        setNode (n);
        addActionListener(this);
    }
    
    static void save(TargetButton b) {
        try {
            Node n = b.node;
            AntProjectCookie ck = (AntProjectCookie) n.getCookie (AntProjectCookie.class);
            DataObject ob = (DataObject) n.getCookie(DataObject.class);
            FileObject script = ob.getPrimaryFile();
            File scriptfile = FileUtil.normalizeFile(FileUtil.toFile(script));
            String scriptName = scriptfile.getPath();
            if (!scriptName.startsWith (File.separator)) {
                scriptName = File.separator + scriptName;
            }
            System.err.println("Saving " + b + " script=" + scriptName);
            
            FileObject fob = getTargetsFolder().getFileObject(n.getName());
            if (fob == null) {
                fob = getTargetsFolder().createData(n.getName());
                fob.setAttribute("antScript", scriptfile.getPath());
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify (e);
        }
    }
    
    static TargetButton[] load() {
        FileObject fld = getTargetsFolder();
        FileObject[] kids = fld.getChildren();
        ArrayList results = new ArrayList();
        for (int i=0; i < kids.length; i++) {
            String file = (String) kids[i].getAttribute ("antScript");
            FileObject fob = FileUtil.toFileObject(FileUtil.normalizeFile(new File(file)));
            System.err.println("Load file " + kids[i].getPath() + " script= " + file);
            if (fob != null) {
                DataObject dob;
                try {
                    dob = DataObject.find (fob);
                } catch (Exception e) {
                    dob = null;
                }
                if (dob != null) {
                    Node filenode = dob.getNodeDelegate();
                    if (!filenode.getName().equals(kids[i].getName())) {
                        Node target = filenode.getChildren().findChild(kids[i].getNameExt());
                        if (target != null) {
                            results.add (new TargetButton(target));
                        } else {
                            System.err.println("Could not find child " + kids[i].getNameExt() + " on " + filenode.getName() + " (" + fob.getPath() + ")");
                            System.err.println("Children: ");
                            Node[] n = filenode.getChildren().getNodes();
                            for (int j=0; j < n.length; j++) {
                                System.err.println(" -" + n[j].getName());
                            }
                        }
                    }
                } else {
                    System.err.println("Could not find data object for " + file + "  - " + (fob != null ? fob.getPath() : " null"));
                }
            } else {
                System.err.println("No file object " + FileUtil.normalizeFile(new File(file)));
            }
        }
        TargetButton[] result = new TargetButton [results.size()];
        result = (TargetButton[]) results.toArray(result);
        return result;
    }
    
    public String toString() {
        return "TargetButton [" + getToolTipText() + "]";
    }
    
    private static FileObject getTargetsFolder() {
        FileObject result = 
            Repository.getDefault().getDefaultFileSystem().findResource("Targets"); //NOI18N
        assert result != null : "Targets folder is missing";
        return result;
    }
    
    private void setNode(Node n) {
        this.node = n;
        n.addNodeListener (this);
        n.addPropertyChangeListener(this);
        
        AntProjectCookie ck = (AntProjectCookie) n.getCookie (AntProjectCookie.class);
        DataObject ob = (DataObject) n.getCookie(DataObject.class);
        String filename = "???";
        if (ob != null) {
            try {
                FileObject fo = ob.getPrimaryFile();
                filename = fo.getPath();
            } catch (Exception e) {
                ErrorManager.getDefault().notify (e);
            }
        }
        
        setText (n.getDisplayName());
        setIcon (new ImageIcon(n.getIcon(BeanInfo.ICON_COLOR_16x16)));
        String tip = NbBundle.getMessage (TargetButton.class, "FMT_Tip",
            new Object[] {n.getDisplayName(), filename});
            
        setToolTipText(tip);
        
        
    }
    
    static boolean accept (Node n) {
        return n.getCookie (AntProjectCookie.class) != null;
    }
    
    public void actionPerformed (ActionEvent ae) {
        AntProjectCookie ck = (AntProjectCookie) node.getCookie (AntProjectCookie.class);
        if (ck != null) {
            AntTargetExecutor.Env env = new AntTargetExecutor.Env();
        
            AntTargetExecutor exe = AntTargetExecutor.createTargetExecutor (env);
            String[] target = new String[] { node.getDisplayName() };
            System.err.println("Will try to execute " + target[0] + " on " + ck + " exe " + exe + " env " + env);
            try {
                exe.execute (ck, target);
            } catch (java.io.IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
        
    }

    public void childrenAdded(org.openide.nodes.NodeMemberEvent ev) {
    }

    public void childrenRemoved(org.openide.nodes.NodeMemberEvent ev) {
    }

    public void childrenReordered(org.openide.nodes.NodeReorderEvent ev) {
    }

    public void nodeDestroyed(org.openide.nodes.NodeEvent ev) {
        node.removeNodeListener (this);
        node.removePropertyChangeListener(this);
        getParent().remove(this);
    }

    public void propertyChange(java.beans.PropertyChangeEvent e) {
        if (Node.PROP_ICON.equals(e.getPropertyName())) {
            setIcon (new ImageIcon(node.getIcon(BeanInfo.ICON_COLOR_16x16)));
        } else if (Node.PROP_DISPLAY_NAME.equals(e.getPropertyName())) {
            setText (node.getDisplayName());
        }
    }
}
