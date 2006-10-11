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
