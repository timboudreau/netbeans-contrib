/*
 * TargetButton.java
 *
 * Created on July 25, 2004, 10:05 PM
 */

package org.netbeans.modules.targets;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import java.beans.BeanInfo;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.nodes.*;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.filesystems.*;
import org.netbeans.api.project.*;

import org.apache.tools.ant.module.api.*;
import org.openide.util.Utilities;
/**
 *
 * @author  Tim Boudreau
 */
class ToolbarTargetButton extends JButton implements ActionListener, NodeListener {
    private Node node = null;
    
    /** Creates a new instance of TargetButton */
    public ToolbarTargetButton(Node n) {
        setNode (n);
        addActionListener(this);
        setBorderPainted(false);
        setOpaque(false);
    }
    
    public Dimension getPreferredSize() {
        Icon ic = getIcon();
        Insets ins = getInsets();
        if (ic instanceof TextIcon) {
            int w = ((TextIcon) ic).width + 4;
            return new Dimension (w + ins.left + ins.right, 24 + ins.top + ins.bottom);
        }
        return super.getPreferredSize();
    }
    
    static void save(ToolbarTargetButton b) {
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
            
            FileObject fob = getTargetsFolder().getFileObject(escape(n.getName()));
            if (fob == null) {
                fob = getTargetsFolder().createData(escape(n.getName()));
                fob.setAttribute("antScript", scriptfile.getPath());
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify (e);
        }
    }
    
    static ToolbarTargetButton[] load() {
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
                        Node target = filenode.getChildren().findChild(escape(kids[i].getNameExt()));
                        if (target != null) {
                            results.add (new ToolbarTargetButton(target));
                        } else {
                            System.err.println("Could not find child " + escape(kids[i].getNameExt()) + " on " + filenode.getName() + " (" + fob.getPath() + ")");
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
        ToolbarTargetButton[] result = new ToolbarTargetButton [results.size()];
        result = (ToolbarTargetButton[]) results.toArray(result);
        return result;
    }
    
    private static String escape (String s) {
        return Utilities.replaceString(s, "/", "$$");
    }
    
    private static String unescape (String s) {
        return Utilities.replaceString(s, "$$", "/");
    }    
    
    public String toString() {
        return "ToolbarTargetButton [" + getToolTipText() + "]";
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
        
        Project project = null;
        
        String projectName = null;
        if (ob != null) {
            try {
                FileObject fo = ob.getPrimaryFile();
                filename = fo.getPath();
                project = FileOwnerQuery.getOwner(fo);
                if (project != null) {
                    projectName = ProjectUtils.getInformation (project).getDisplayName();
                } 
            } catch (Exception e) {
                ErrorManager.getDefault().notify (e);
            }
        }
        if (projectName == null) {
            projectName = filename;
        }
        
        setIcon (new TextIcon(n.getDisplayName(), projectName,
            n.getIcon(BeanInfo.ICON_COLOR_16x16)));
        
        String tip = NbBundle.getMessage (ToolbarTargetButton.class, "FMT_Tip",
            new Object[] {n.getDisplayName(), filename});
            
        setToolTipText(tip);
    }
    
    static boolean accept (Node n) {
        return n.getCookie (AntProjectCookie.class) != null;
    }
    
    public void actionPerformed (ActionEvent ae) {
        AntProjectCookie ck = (AntProjectCookie) 
            node.getCookie (AntProjectCookie.class);
        
        if (ck != null) {
            AntTargetExecutor.Env env = new AntTargetExecutor.Env();
        
            AntTargetExecutor exe = AntTargetExecutor.createTargetExecutor (env);
            String[] target = new String[] { node.getDisplayName() };
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
    
    private static BufferedImage makeImage (int width, int height) {
        //XXX use GraphicsEnvironment
        java.awt.image.ColorModel model = 
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().
              getDefaultScreenDevice().getDefaultConfiguration().getColorModel(
              java.awt.Transparency.TRANSLUCENT);
        
        java.awt.image.BufferedImage buffImage = 
            new java.awt.image.BufferedImage(model,
            model.createCompatibleWritableRaster(width, height), 
            model.isAlphaPremultiplied(), null);
        return buffImage;
    }
    
    private static Font iconFont = null;
    static Font getIconFont() {
        if (iconFont == null) {
            iconFont = UIManager.getFont ("controlFont"); //NOI18N
            if (iconFont == null) {
                iconFont = new Font ("Dialog", Font.BOLD, 8); //NOI18N
            } else {
                iconFont = iconFont.deriveFont (Font.PLAIN, 8);
            }
        }
        return iconFont;
    }
    
    private static class TextIcon implements Icon {
        int lineheight;
        int width;
        int height;
        int ascent;
        int targWidth;
        int projWidth;
        ImageIcon icon;
        private String target;
        private String project;
        private TextIcon (String target, String project, Image img) {
            icon = new ImageIcon (img);
            Graphics g = makeImage (1, 1).getGraphics();
            FontMetrics fm = g.getFontMetrics (getIconFont());
            width = Math.max(fm.stringWidth (target), 
                Math.max(fm.stringWidth(project), icon.getIconWidth())) + 4;
            
            lineheight = fm.getHeight() + fm.getMaxDescent();
            ascent = fm.getMaxAscent();
            this.project = project;
            this.target = target;
            projWidth = fm.stringWidth(project);
            targWidth = fm.stringWidth(target);
        }
        
        public int getIconHeight() {
            return Math.max ((lineheight * 2) + 6, icon.getIconHeight());
        }

        public int getIconWidth() {
            return width;
        }

        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            Composite comp = g2d.getComposite();
            g2d.setComposite (AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.25f));
            g2d.setColor (new Color(255, 215, 0));
            g2d.fillRect (x, y, c.getWidth() - (x*2), c.getHeight() - (y*2));
            
            g2d.setComposite (AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.67f));
            
            int ix = (c.getWidth() / 2) - (icon.getIconWidth() / 2);
            int iy = (c.getHeight() / 2) - (icon.getIconHeight() / 2);
            icon.paintIcon (c, g, ix, iy);
            g2d.setComposite(comp);
            g2d.setColor (c.isEnabled() ? new Color (0, 0, 128) : 
                    UIManager.getColor("textInactiveText")); //NOI18N
                    
            paintString (g, project, ascent + 5, c.getWidth(), projWidth);
            paintString (g, target, ascent + lineheight + 5, c.getWidth(), targWidth);
        }
        
        private void paintString (Graphics g, String s, int y, int w, int stringw) {
            g.setFont (getIconFont());
//            int x = w > stringw ? (w / 2) - (stringw / 2) : 0;
            int x = (w / 2) - (stringw / 2);
            g.drawString (s, x, y);
        }
    }
    
}
