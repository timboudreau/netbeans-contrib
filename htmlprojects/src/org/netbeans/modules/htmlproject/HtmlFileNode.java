/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */
package org.netbeans.modules.htmlproject;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * Node for a single HTML file.  Does not proxy the datanode for the 
 * associated file node.  On first display name request, will enqueue itself
 * with the parent Kids object's HtmlNameFinder to go out and fetch the
 * HTML title from within the file.
 * 
 * @author Tim Boudreau
 */ 
class HtmlFileNode extends AbstractNode implements OpenCookie {
    private static Image htmlIcon = Utilities.loadImage (
            "org/netbeans/modules/htmlproject/htmlObject.png"); //NOI18N
    private final boolean index;
    private final FileObject projDir;
    public HtmlFileNode(File f, String projPath, FileObject projDir, Kids kids) {
        super(Children.LEAF, Lookups.fixed(new Object[]{ f, kids }));
        String relPath = Kids.snipPath (f, projPath);
        this.projDir = projDir;
        setName (relPath);
        setDisplayName (f.getName());
        setShortDescription(relPath);
        String fileNameCaps = FileProcessor.getName(f).toUpperCase(
                Locale.ENGLISH);
        
        index = "INDEX".equals(fileNameCaps) // NOI18N
                     && projDir.equals(FileUtil.toFileObject(f).getParent());
    }
    
    private Kids getParentKidsObject() {
        return ((Kids) getLookup().lookup(Kids.class));
    }

    private boolean checked = false;
    private boolean posted = false;
    public String getDisplayName() {
        if (!checked && !posted) {
            posted = true;
            getParentKidsObject().enqueue (this, getFile());
        }
        return super.getDisplayName();
    }
    
    public String getHtmlDisplayName() {
        if (index) {
            return "<b>" + getDisplayName();
        } else {
            return getDisplayName() +
                    "<font color='!controlShadow'> (" + getFile().getName()  //NOI18N
                    + ')'; //NOI18N
        }
    }

    private File getFile() {
        return (File) getLookup().lookup (File.class);
    }
    
    private Kids getKids() {
        return (Kids) getLookup().lookup(Kids.class);
    }

    public Action[] getActions(boolean popup) {
        File f = getFile();
        if (f == null) {
            try {
                destroy();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
            }
            return new Action[0];
        }
        String trimmedPath = 
                getShortDescription().substring(0, f.getName().length());
        
        return new Action[] {
            new OpenAction(), 
            new ViewAction(f), 
            null, 
            new DelAction(), 
            null, 
            new ColocatedHtmlFileAction(f, trimmedPath, projDir, getKids())};
    }

    public Action getPreferredAction() {
        return getActions (true) [0];
    }

    class OpenAction extends AbstractAction {
        public OpenAction() {
            putValue (NAME, "Open");
        }

        public void actionPerformed(ActionEvent ae) {
            OpenCookie ie = realCookie();
            if (ie != null) {
                ie.open();
            }
        }

        private OpenCookie realCookie() {
            File f = getFile();
            FileObject fob = FileUtil.toFileObject (f);
            DataObject dob;
            try {
                dob = DataObject.find(fob);
                OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
                if (oc == null) {
                    final EditCookie ec = (EditCookie) dob.getCookie(EditCookie.class);
                    if (ec != null) {
                        oc = new OpenCookie() {
                            public void open() {
                                ec.edit();
                            }
                            };
                    }
                }
                return oc;
            }  catch (DataObjectNotFoundException ex) {
                throw new IllegalStateException(ex);
            }
        }

        public boolean isEnabled() {
            return realCookie() != null;
        }
    }

    class DelAction extends AbstractAction {
        public DelAction() {
            putValue (NAME, "Delete");
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                FileObject fob = FileUtil.toFileObject (getFile());
                DataObject dob = DataObject.find (fob);
                dob.delete();
                HtmlFileNode.this.fireNodeDestroyed();
                Kids kids = (Kids) getLookup().lookup (Kids.class);
                kids.upd(false);
            }  catch (DataObjectNotFoundException donfe) {
                ErrorManager.getDefault().notify (donfe);
            }  catch (IOException ioe) {
                ErrorManager.getDefault().notify (ioe);
            }
        }

        public boolean isEnabled() {
            return getFile().exists();
        }
    }


    static final class ViewAction extends AbstractAction {
        private final File f;
        public ViewAction(File f) {
            putValue (NAME, "View");
            this.f = f;
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                URLDisplayer.getDefault().showURL(f.toURI().toURL());
            }  catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify (ex);
            }
        }
    }

    public void reset() {
        if (!posted) {
            checked = false;
            getParentKidsObject().enqueue(this, getFile());
        }
    }

    void checked() {
        checked = true;
        posted = false;
    }
    
    public void open() {
        OpenAction oe = new OpenAction();
        if (oe.isEnabled()) {
            oe.actionPerformed(null);
        }
    }

    public Image getOpenedIcon(int i) {
        return htmlIcon;
    }

    public Image getIcon(int i) {
        return htmlIcon;
    }
}