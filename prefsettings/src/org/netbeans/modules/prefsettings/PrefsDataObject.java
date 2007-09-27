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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.prefsettings;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import org.netbeans.modules.prefsettings.api.PrefsNode;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
/**
 *
 * @author Timothy Boudreau
 */
public class PrefsDataObject extends DataObject {
    PrefsDataObject(FileObject fo, PrefsDataLoader ldr) throws DataObjectExistsException {
        super (fo, ldr);
    }
    
    public String getName() {
        return getPrimaryFile().getName();
    }

    public boolean isRenameAllowed() {
        return false;
    }

    public boolean isMoveAllowed() {
        return false;
    }

    public boolean isModified() {
        return false;
    }

    public boolean isDeleteAllowed() {
        return false;
    }

    public boolean isCopyAllowed() {
        return false;
    }

    public HelpCtx getHelpCtx() {
        String ctx = attr (KEY_HELPCTX);
        if (ctx == null) {
            return HelpCtx.DEFAULT_HELP;
        } else {
            return new HelpCtx (ctx);
        }
    }

    protected Node createNodeDelegate() {
        String nodeClass = attr (KEY_NODE_CLASS);
        Node result;
        try {
            if (nodeClass != null) {
                Class clazz = ((ClassLoader) Lookup.getDefault().lookup (ClassLoader.class)).loadClass(nodeClass);
                
                if (PrefsNode.class.isAssignableFrom(clazz)) {
                    result = (Node) clazz.getConstructor(new Class[] { DataObject.class }).newInstance(new Object[] { this });
                } else {
                    result = (Node) clazz.newInstance();
                }
            } else {
                result = new DefaultPrefsNode (this);
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
            try {
                result = new BeanNode (new Failure (e.getMessage(), getPrimaryFile().getPath()));
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
                //Should never happen;  deep doo doo.
                return null;
            }
            result.setDisplayName("Broken Settings");
        }
        return result;
    }
    
    public String displayName() {
        String s = getLocalizedString(getName());
        if (s == null) {
            s = getName();
        }
        return s;
    }
    
    public String attr (String key) {
        return attr(getPrimaryFile(), key);
    }
    
    private String attr (FileObject fo, String key) {
        return (String) fo.getAttribute(key);
    }
    
    public Preferences getPreferences() {
        Preferences parentNode = getPreferencesParent();
        String prefName = stripNonDirectoryChars(getPrimaryFile().getName());
        return parentNode.node(prefName);
    }
    
    private Preferences getPreferencesParent() {
        String path = attr(KEY_PREFS_PATH);
        if (path == null) {
            FileObject parent = getPrimaryFile().getParent();
            path = attr(parent, KEY_PREFS_PATH);
            if (path == null)
                path = stripNonDirectoryChars(parent.getPath());
        }
        assert path != null;
        return NbPreferences.root().node(path);
    }
    
    private String stripNonDirectoryChars(String path) {
        StringBuffer sb = new StringBuffer(path);
        for (int i = sb.length() - 1; i >= 0; i--) {
            char ch = sb.charAt(i);
            if (ch == '.' || ch == '_' || ch < 0x20 || ch > 0x7E)
                sb.setCharAt(i, '-');
        }
        return sb.toString();
    }

    public String getLocalizedString (String key) {
        String bundle = attr (KEY_BUNDLE);
        if (bundle == null) {
            bundle = (String) getPrimaryFile().getParent().getAttribute(KEY_BUNDLE);
        }
        if (bundle != null) {
            try {
                ResourceBundle bdl = NbBundle.getBundle(bundle, Locale.getDefault(), (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class));
                if (bdl != null) {
                    return bdl.getString(key);
                }
            } catch (MissingResourceException mre) {
                mre.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    public static final String KEY_BUNDLE = "bundle"; //NOI18N
    public static final String KEY_HELPCTX = "helpContext"; //NOI18N
    public static final String KEY_NODE_CLASS = "nodeClass"; //NOI18N
    public static final String KEY_ICON = "icon";  //NOI18N
    public static final String KEY_PREFS_PATH = "prefsPath"; //NOI18N

    protected DataObject handleCopy(DataFolder f) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    protected void handleDelete() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    protected FileObject handleRename(String name) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    protected FileObject handleMove(DataFolder df) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    private static final class Failure {
        private final String problem;
        private final String settingPath;
        Failure (String problem, String settingPath) {
            this.problem = problem;
            this.settingPath = settingPath;
        }
        
        public String getProblem() {
            return problem;
        }
        
        public String getSettingPath() {
            return settingPath;
        }
    }
}
