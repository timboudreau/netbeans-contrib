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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.tasklist.usertasks.model;

import java.net.URL;
import org.netbeans.modules.tasklist.usertasks.annotations.UTAnnotation;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

/**
 * org.openide.text.Line.
 *
 * @author tl
 */
public class LineResource implements UserTaskResource {
    /** could be null */
    private URL url;
    
    /** 0-based line number */
    private int lineno;
    
    /** maybe null */
    private Line line;

    /**
     * Constructor
     * 
     * @param url URL for the file
     * @param lineno 0-based line number 
     */
    public LineResource(URL url, int lineno) {
        this.url = url;
        this.lineno = lineno;
        
        FileObject fo = URLMapper.findFileObject(url);
        if (fo != null)
            line = UTUtils.getLineByFile(fo, lineno);
    }        
    
    /**
     * Constructor.
     * 
     * @param line associated line 
     */
    public LineResource(Line line) {
        this.line = line;
    }

    @Override
    public LineResource clone() {
        try {
            return (LineResource) super.clone();
        } catch (CloneNotSupportedException ex) {
            UTUtils.LOGGER.warning(ex.getMessage());
            return null;
        }
    }

    public String getDisplayName() {
        if (line != null)
            return line.getDisplayName();
        else
            return url.toExternalForm() + ":" + lineno; // NOI18N
    }

    public void open() {
        if (line != null)
            line.show(Line.SHOW_GOTO);
        else {
            try {
                FileObject fo = URLMapper.findFileObject(url);
                if (fo != null) {
                    DataObject do_ = DataObject.find(fo);
                    OpenCookie oc = do_.getCookie(OpenCookie.class);
                    if (oc != null)
                        oc.open();
                } else {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                }
            } catch (DataObjectNotFoundException ex) {
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            }
        }
    }

    /**
     * Returns associated line.
     * 
     * @return associated line or null if there is no such DataObject 
     */
    public Line getLine() {
        return line;
    }

    /**
     * Returns URL associated with a task.
     * 
     * @return URL 
     */
    public URL getURL() {
        if (line != null) {
            DataObject do_;
            try {
                do_ = line.getLookup().lookup(DataObject.class);
                FileObject fo = do_.getPrimaryFile();
                return fo.getURL();
            } catch (FileStateInvalidException e) {
                UTUtils.LOGGER.warning(e.getMessage());
                return null;
            }
        } else {
            return url;
        }
    }

    /**
     * Returns line number.
     * 
     * @return 0-based line number 
     */
    public int getLineNumber() {
        if (line != null)
            return line.getLineNumber();
        else
            return lineno;
    }
}
