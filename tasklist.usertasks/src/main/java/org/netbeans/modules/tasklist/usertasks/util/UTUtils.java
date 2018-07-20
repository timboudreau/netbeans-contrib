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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.tasklist.usertasks.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JEditorPane;
import javax.swing.JToolBar;

import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.actions.Presenter;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Utility methods for usertasks.
 *
 * @author tl
 */
public final class UTUtils {
    /** Logger for the "User Tasks" module. */
    public static final Logger LOGGER = getLogger(UTUtils.class);
    
    static {
        // set to WARNING for release
        LOGGER.setLevel(Level.WARNING);
    }
    
    // This method returns true if the specified image has transparent pixels
    // original code:
    // http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
    private static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }
    
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
    
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
    
    //  This method returns a buffered image with the contents of an image
    // original code: 
    // http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html?l=rel
    public static BufferedImage deviceCompatible(Image image) {
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);
    
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
    
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
    
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
    
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
    
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
    
        return bimage;
    }
    
    /**
     * Creates a simple logger for the specified class. 
     * Category of the logger will be equals to the class name.
     *
     * @param clazz the name of the class will be used for the logger's category
     * @return logger
     */
    private static Logger getLogger(Class clazz) {
        // eliminate duplications. There are two console handlers somehow,
        // the second one publishes also INFO messages
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        logger.addHandler(ch);
        logger.setLevel(Level.WARNING);
        return logger;
    }

    /**
     * Copies the content of one stream to another.
     *
     * @param is input stream
     * @param os output stream
     */
    public static void copyStream(InputStream is, OutputStream os) 
    throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
    }
    
    /**
     * Creates a tag in another one.
     *
     * @param el an XML node
     * @param tagName name of the new sub-tag
     * @return created element
     */
    public static Element appendElement(Element el, String tagName) {
        Element r = el.getOwnerDocument().createElement(tagName);
        el.appendChild(r);
        return r;
    }
    
    /**
     * Creates a tag in another one.
     *
     * @param el an XML node
     * @param tagName name of the new sub-tag
     * @param content content for the new tag
     * @return created element
     */
    public static Element appendElement(Element el, String tagName, 
            String content) {
        Element r = el.getOwnerDocument().createElement(tagName);
        el.appendChild(r);
        Text txt = el.getOwnerDocument().createTextNode(content);
        r.appendChild(txt);
        return r;
    }
    
    /**
     * Appends a text element.
     *
     * @param el an element
     * @param content text
     * @return el
     */
    public static Element appendText(Element el, String content) {
        Text txt = el.getOwnerDocument().createTextNode(content);
        el.appendChild(txt);
        return el;
    }
    
    /**
     * Prepares a (possibly) multi line text for showing as a tooltip
     * (converts it to html).
     *
     * @param text a text
     * @return a tooltip
     */
    public static String prepareForTooltip(String text) {
        int index = text.indexOf('\n');
        if (index == -1)
            return text;
        
        StringBuilder sb = new StringBuilder("<html>"); // NOI18N
        while (index >= 0) {
            sb.append(text.substring(0, index));
            sb.append("<br>"); // NOI18N
            text = text.substring(index + 1);
            index = text.indexOf('\n');
        };
        sb.append(text);
        sb.append("</html>"); // NOI18N
        return sb.toString();
    }
    
    /** 
     * Create the default toolbar representation of an array of actions.
     * Null items in the array will add a separator to the toolbar.
     *
     * @param actions actions to show in the generated toolbar
     * @return a toolbar instance displaying them
     */
    public static JToolBar createToolbarPresenter(Action[] actions) {
        JToolBar p = new JToolBar();
        int i;
        int k = actions.length;

        for (i = 0; i < k; i++) {
            if (actions[i] == null) {
                p.addSeparator(new Dimension(6, 6));
            } else if (actions[i] instanceof Presenter.Toolbar) {
                p.add(((Presenter.Toolbar) actions[i]).getToolbarPresenter());
            } else {
                p.add(actions[i]);
            }
        }
        
        /*
        final Dimension D = new Dimension(24, 24);
        for (int j = 0; j < p.getComponentCount(); j++) {
            Component c = p.getComponent(j);
            if (c instanceof JButton) {
                ((JButton) c).setPreferredSize(D);
                ((JButton) c).setMinimumSize(D);
                ((JButton) c).setMaximumSize(D);
            }
        }*/

        return p;
    }
    
    /**
     * Compares 2 objects using equals(Object).
     *
     * @param obj1 an object or null
     * @param obj2 an object or null
     * @return true if obj1 == null && obj2 == null or obj1.equals(obj2)
     */
    public static boolean objectsEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null)
            return true;
        if (obj1 != null && obj2 == null)
            return false;
        if (obj1 == null && obj2 != null)
            return false;
        return obj1.equals(obj2);
    }
    
    /**
     * Utility method which attempts to find the activated nodes
     *	for the currently showing topcomponent in the editor window.
     *
     * @return editor nodes or null
     */
    public static Node[] getEditorNodes() {
        // First try to get the editor window itself; if you right click
        // on a node in the Todo Window, that node becomes the activated
        // node (which is good - it makes the properties window show the
        // todo item's properties, etc.) but that means that we can't
        // find the editor position via the normal means.
        // So, we go hunting for the topmosteditor tab, and when we find it,
        // ask for its nodes.
        Node[] nodes = null;
        WindowManager wm = WindowManager.getDefault();

        // HACK ALERT !!! HACK ALERT!!! HACK ALERT!!!
        // Look for the source editor window, and then go through its
        // top components, pick the one that is showing - that's the
        // front one!
        Mode mode  = wm.findMode(CloneableEditorSupport.EDITOR_MODE);
        if (mode == null) {
            return null;
        }
        TopComponent [] tcs = mode.getTopComponents();
        for (int j = 0; j < tcs.length; j++) {
            // Found the source editor...
            if (tcs[j].isShowing()) {
                nodes = tcs[j].getActivatedNodes();
                break;
            }
        }
        return nodes;
    }

    /**
     * Finds cursor position.
     *
     * @param nodes nodes to search. May be null
     * @return found line object or null if nothing found.
     */
    public static Line findCursorPosition(Node[] nodes) {
        if (nodes == null) {
            return null;
        }

        for (int i = 0; i < nodes.length; i++) {
            EditorCookie ec = (EditorCookie) nodes[i].getCookie(EditorCookie.class);

            if (ec != null) {
                JEditorPane[] editorPanes = ec.getOpenedPanes();
                if ((editorPanes != null) && (editorPanes.length > 0)) {
                    int line = NbDocument.findLineNumber(
                        ec.getDocument(),
                        editorPanes[0].getCaret().getDot());
                    LineCookie lc = (LineCookie) nodes[i].
                        getCookie(LineCookie.class);
                    if (lc != null) {
                        Line l = lc.getLineSet().getCurrent(line);
                        if (l != null)
                            return l;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Finds a FileObject corresponding to the specified file name.
     *
     * @param filename a filename
     * @return found FileObject or null
     */
    public static FileObject getFileObjectForFile(String filename) {
        return FileUtil.toFileObject(FileUtil.normalizeFile(new File(filename)));
    }
    
    /** 
     * Return the Line object for a particular line in a file.
     *
     * @param fo a file
     * @param lineno line number: 0, 1, 2, 3, ...
     * @return Line object or null
     */
    public static Line getLineByFile(FileObject fo, int lineno) {
        DataObject dobj = null;
        try {
            dobj = DataObject.find(fo);
        } catch (DataObjectNotFoundException e) {
            LOGGER.log(Level.WARNING, 
                    "No data object could be found for file object " +  // NOI18N
                    fo, e);
        }

        if (dobj == null) 
            return null;

        // Go to the given line
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc != null) {
                Line.Set ls = lc.getLineSet();
                if (ls != null) {
                    // I'm subtracting 1 because empirically I've discovered
                    // that the editor highlights whatever line I ask for plus 1
                    Line l = ls.getCurrent(lineno);
                    return l;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "failed", e); // NOI18N
        }
        return null;
    }

    /**
     * Finds URL with the type URLMapper.EXTERNAL for the specified
     * Line object.
     *
     * @param line a line objct
     * @return found URL or null
     */
    public static URL getExternalURLForLine(Line line) {
        DataObject dobj = (DataObject) line.getLookup().
            lookup(DataObject.class);
        URL url = null;
        if (dobj != null) {
            FileObject fo = dobj.getPrimaryFile();
            url = URLMapper.findURL(fo, URLMapper.EXTERNAL);

            /*
            if (UTUtils.LOGGER.isLoggable(Level.FINE)) {
                UTUtils.LOGGER.fine("URLMapper.EXTERNAL" + 
                    URLMapper.findURL(fo, URLMapper.EXTERNAL));
                UTUtils.LOGGER.fine("URLMapper.INTERNAL" + 
                    URLMapper.findURL(fo, URLMapper.INTERNAL));
                UTUtils.LOGGER.fine("URLMapper.NETWORK" + 
                    URLMapper.findURL(fo, URLMapper.NETWORK));
            }
             */
        }
        
        return url;
    }
    
    /**
     * Searchs for nodes in a tree that pass a filter.
     *
     * @param t a tree
     * @param filter Boolean f(Object). Filter function.
     * @return all elements that pass the filter
     */
    public static <T> List<T> filter(TreeAbstraction<T> t, UnaryFunction filter) {
        List<T> r = new ArrayList<T>();
        filter(t, t.getRoot(), filter, r);
        return r;
    }
    
    /**
     * Searches for nodes in a tree that pass a filter.
     *
     * @param t a tree
     * @param node this node and all it's descendants will be filtered
     * @param result nodes that passed the filter will be stored here
     */
    private static <T> void filter(TreeAbstraction<T> t, T node, 
            UnaryFunction filter, List<T> result) {
        if (((Boolean) filter.compute(node)).booleanValue())
            result.add(node);
        for (int i = 0; i < t.getChildCount(node); i++) {
            filter(t, t.getChild(node, i), filter, result);
        }
    }
    
    /**
     * Processes all nodes in a tree in a depth-first manner.
     *
     * @param tree a tree
     * @param f a function to be applied to each node
     */
    public static <T> void processDepthFirst(TreeAbstraction<T> tree,
            UnaryFunction f) {
        processDepthFirst(tree, tree.getRoot(), f);
    }
    
    /**
     * Processes all nodes under the specified in a depth-first manner.
     *
     * @param tree a tree
     * @param f a function to be applied to each node.
     */
    private static <T> void processDepthFirst(TreeAbstraction<T> tree,
            T object, UnaryFunction f) {
        for (int i = 0; i < tree.getChildCount(object); i++) {
            processDepthFirst(tree, tree.getChild(object, i), f);
        }
        f.compute(object) ;
    }

    /**
     * Processes all nodes in a tree in a breadth-first manner.
     *
     * @param tree a tree
     * @param f a function to be applied to each node
     */
    public static <T> void processBreadthFirst(TreeAbstraction<T> tree,
            UnaryFunction f) {
        processBreadthFirst(tree, tree.getRoot(), f);
    }
    
    /**
     * Processes all nodes under the specified in a breadth-first manner.
     *
     * @param tree a tree
     * @param f a function to be applied to each node.
     */
    private static <T> void processBreadthFirst(TreeAbstraction<T> tree,
            T object, UnaryFunction f) {
        f.compute(object);
        for (int i = 0; i < tree.getChildCount(object); i++) {
            processBreadthFirst(tree, tree.getChild(object, i), f);
        }
    }

    /**
     * Computes a sum of an array.
     *
     * @param values array of values
     * @return sum of the values
     */
    public static long sum(long[] values) {
        long r = 0;
        for (int i = 0; i < values.length; i++) {
            r += values[i];
        }
        return r;
    }
    

    /**
     * Searches for an element using ==.
     *
     * @param values list of values
     * @param value a value
     * @return index of the value or -1
     */
    public static<T> int identityIndexOf(List<T> values, T value) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == value)
                return i;
        }
        return -1;
    }
    
    /**
     * Groups values.
     * 
     * @param values values that should be grouped
     * @param c comparator for values
     * @return groups 
     */
    public static<T> List<List<T>> group(Collection<T> values, Comparator<T> c) {
        List<List<T>> res = new ArrayList<List<T>>();
        if (values.size() != 0) {
            Iterator<T> it = values.iterator();
            List<T> group = new ArrayList<T>();
            group.add(it.next());
            res.add(group);
            while (it.hasNext()) {
                T v = it.next();
                if (c.compare(v, group.get(0)) == 0) {
                    group.add(v);
                } else {
                    group = new ArrayList<T>();
                    group.add(v);
                    res.add(group);
                }
            }
        }
        
        return res;
    }
    
    /*
     * DEBUG:
     * 
     * Dumps the hierarchy of class loaders.
     * 
     * @param cl a class loader or null
     * 
    public static void dumpClassLoaders(ClassLoader cl) {
        while (cl != null) {
            UTUtils.LOGGER.fine(cl.getClass().getName() + 
                    " " + cl.toString()); // NOI18N
            cl = cl.getParent();
        }
    }*/
}
