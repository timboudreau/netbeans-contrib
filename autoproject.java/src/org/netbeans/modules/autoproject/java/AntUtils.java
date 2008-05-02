/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.apache.tools.ant.module.api.AntProjectCookie;
import org.apache.tools.ant.module.api.support.TargetLister;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Stuff copied from ant.freeform which should probably be in o.apache.tools.ant.module.
 */
public class AntUtils {

    private AntUtils() {}

    private static final Logger LOG = Logger.getLogger(AntUtils.class.getName());

    /**
     * Returns sorted list of targets name of the Ant script represented by the
     * given file object.
     * @param fo Ant script which target names should be returned
     * @return sorted list of target names or null if fo does not represent
     * valid Ant script
     */
    public static List<String> getAntScriptTargetNames(FileObject fo) {
        if (fo == null) {
            throw new IllegalArgumentException("Cannot call Util.getAntScriptTargetNames with null"); // NOI18N
        }
        AntProjectCookie apc = getAntProjectCookie(fo);
        if (apc == null) {
            return null;
        }
        Set<TargetLister.Target> allTargets;
        try {
            allTargets = TargetLister.getTargets(apc);
        } catch (IOException e) {
            LOG.log(Level.INFO, null, e);
            return null;
        }
        SortedSet<String> targetNames = new TreeSet<String>(Collator.getInstance());
        for (TargetLister.Target target : allTargets) {
            if (target.isOverridden()) {
                // Cannot call it directly.
                continue;
            }
            if (target.isInternal()) {
                // Should not be called from outside.
                continue;
            }
            targetNames.add(target.getName());
        }
        return new ArrayList<String>(targetNames);
    }

    public static AntProjectCookie getAntProjectCookie(FileObject fo) {
        DataObject dob;
        try {
            dob = DataObject.find(fo);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        assert dob != null;
        AntProjectCookie apc = dob.getCookie(AntProjectCookie.class);
        if (apc == null && /* #88430 */fo.isData()) {
            // Some file that *could* be an Ant script and just wasn't recognized
            // as such? Cf. also TargetLister.getAntProjectCookie, which has the
            // advantage of being inside the Ant module and therefore able to
            // directly instantiate AntProjectSupport.
            try {
                apc = forceParse(fo);
            } catch (IOException e) {
                LOG.log(Level.INFO, null, e);
            } catch (SAXException e) {
                LOG.warning("Parse error in " + fo + ": " + e);
            }
        }
        return apc;
    }
    /**
     * Try to parse a (presumably XML) file even though it is not known to be an Ant script.
     */
    private static AntProjectCookie forceParse(FileObject fo) throws IOException, SAXException {
        Document doc = XMLUtil.parse(new InputSource(fo.getURL().toExternalForm()), false, true, new ErrH(), null);
        return new TrivialAntProjectCookie(fo, doc);
    }

    private static final class ErrH implements ErrorHandler {
        public ErrH() {}
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }
        public void warning(SAXParseException exception) throws SAXException {
            // ignore that
        }
    }

    private static final class TrivialAntProjectCookie implements AntProjectCookie.ParseStatus {

        private final FileObject fo;
        private final Document doc;

        public TrivialAntProjectCookie(FileObject fo, Document doc) {
            this.fo = fo;
            this.doc = doc;
        }

        public FileObject getFileObject() {
            return fo;
        }

        public File getFile() {
            return FileUtil.toFile(fo);
        }

        public Document getDocument() {
            return doc;
        }

        public Element getProjectElement() {
            return doc.getDocumentElement();
        }

        public boolean isParsed() {
            return true;
        }

        public Throwable getParseException() {
            return null;
        }

        public void addChangeListener(ChangeListener l) {}

        public void removeChangeListener(ChangeListener l) {}

    }

}
