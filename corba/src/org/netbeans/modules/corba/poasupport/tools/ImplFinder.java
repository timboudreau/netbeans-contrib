/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.poasupport.tools;

import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.TopManager;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.src.Identifier;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.utils.Pair;
import org.netbeans.modules.corba.settings.ORBSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.netbeans.modules.corba.poasupport.POASupport;
import org.openide.util.WeakListener;

/*
 * @author Dusan Balek
 */

public class ImplFinder implements PropertyChangeListener {
    
    public static final byte SEARCH_PACKAGE = 0;
    public static final byte SEARCH_PACKAGE_DEEP = 1;
    public static final byte SEARCH_FILE_SYSTEM = 2;
    public static final byte SEARCH_ALL = 3;
    
    public static final byte SERVANT = 0;
    public static final byte SERVANT_MANAGER = 1;
    public static final byte ADAPTER_ACTIVATOR = 2;
    
    private static final String IDL_EXTENSION = "idl"; // NOI18N
    private static final String JAVA_EXTENSION = "java"; // NOI18N
    
    private static ImplFinder defaultImplFinder;
    private ORBSettings os;
    private PropertyChangeListener weak1 = WeakListener.propertyChange (this, POASupport.getCORBASettings());
    private PropertyChangeListener weak2 = WeakListener.propertyChange (this, POASupport.getCORBASettings().getActiveSetting());

    public static ImplFinder getDefault () {
        if (defaultImplFinder == null) {
            defaultImplFinder = new ImplFinder();
        }
        return defaultImplFinder;
    }
    
    private byte searchMode;
    
    public ImplFinder() {
        POASupport.getCORBASettings().addPropertyChangeListener(weak1);
        os = POASupport.getCORBASettings().getActiveSetting();
        os.addPropertyChangeListener(weak2);
        resetSearchMode();
    }

    private void resetSearchMode() {
        String method = os.getFindMethod();
        if (method.equals(ORBSettingsBundle.PACKAGE))
            searchMode = SEARCH_PACKAGE;
        else if (method.equals(ORBSettingsBundle.FILESYSTEM))
            searchMode = SEARCH_FILE_SYSTEM;
        else if (method.equals(ORBSettingsBundle.REPOSITORY))
            searchMode = SEARCH_ALL;
        else
            searchMode = SEARCH_PACKAGE_DEEP;
    }
    
    public Vector getAvailableImpls(FileObject _package, byte _type) {
        switch (searchMode) {
            case SEARCH_ALL:
                Vector ret = new Vector();
                for (Enumeration e = TopManager.getDefault().getRepository().getFileSystems(); e.hasMoreElements(); )
                    ret.addAll(findImplFiles(((FileSystem)e.nextElement()).getRoot(), _type));
                return ret;
            case SEARCH_FILE_SYSTEM:
                try {
                    _package = _package.getFileSystem().getRoot();
                }
                catch(Exception e) {
                }
            case SEARCH_PACKAGE_DEEP:
            case SEARCH_PACKAGE:
                return findImplFiles(_package, _type);
        }
        return new Vector();
    }
    
    private Vector findImplFiles(FileObject _root, byte _type) {
        Vector ret = new Vector();
        String basePrefix = null;
        String basePostfix = null;
        String delPrefix = null;
        String delPostfix = null;
        String tiePrefix = null;
        String tiePostfix = null;
        String implPrefix = null;
        String implPostfix = null;
        if (_type == SERVANT) {
            basePrefix = os.getExtClassPrefix();
            basePostfix = os.getExtClassPostfix();
            delPrefix = os.getImplIntPrefix();
            delPostfix = os.getImplIntPostfix();
            tiePrefix = os.getTieClassPrefix();
            tiePostfix = os.getTieClassPostfix();
            implPrefix = os.getImplBaseImplPrefix();
            implPostfix = os.getImplBaseImplPostfix();
        }
        try {
            for (Enumeration e = _root.getChildren(searchMode != SEARCH_PACKAGE); e.hasMoreElements();) {
                FileObject fo = (FileObject)e.nextElement();
                String ext = fo.getExt();
                DataObject dobj = null;
                switch (_type) {
                    case SERVANT:
                        if (ext.equals(IDL_EXTENSION) && ((dobj = DataObject.find(fo)) instanceof IDLDataObject)) {
                            Iterator implNames = ((IDLDataObject)dobj).getImplementationNames().iterator();
                            while (implNames.hasNext()) {
                                Pair pair  = (Pair)implNames.next();
                                String implName = pair.first.toString() + pair.second.toString();
                                String pkgName = fo.getParent().getPackageName('.');
                                if (pkgName != null && pkgName.length() > 0)
                                    implName = pkgName + "." + implName;
                                ClassElement ce = ClassElement.forName( implName );
                                if (ce != null) {
                                    Identifier sid = ce.getSuperclass();
                                    if (sid != null) {
                                        String baseName = sid.getName();
                                        if (baseName != null && baseName.startsWith(basePrefix) && baseName.endsWith(basePostfix)) {
                                            ret.add(implName);
                                            continue;
                                        }
                                    }
                                    Identifier[] iids = ce.getInterfaces();
                                    for (int j = 0; j < iids.length; j++) {
                                        String baseName = iids[j].getName();
                                        if (baseName != null && baseName.startsWith(delPrefix) && baseName.endsWith(delPostfix)) {
                                            implName = baseName.substring(delPrefix.length(), baseName.length() - delPostfix.length());
                                            ret.add(iids[j].getQualifier() + "." + tiePrefix + implName + tiePostfix);
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case SERVANT_MANAGER:
                        if ((ext.equals(JAVA_EXTENSION)) && ((dobj = DataObject.find(fo)) instanceof JavaDataObject)) {
                            SourceElement source = ((JavaDataObject)dobj).getSource();
                            if (source != null) {
                                ClassElement ce = source.getClass(Identifier.create(fo.getName()));
                                if (ce.isInterface())
                                    break;
                                if (inheritsFrom(ce, "org.omg.PortableServer.ServantActivatorPOA") ||
                                inheritsFrom(ce, "org.omg.PortableServer.ServantLocatorPOA"))
                                    if (source.getPackage() != null)
                                        ret.add(source.getPackage() + "." + fo.getName());
                                    else
                                        ret.add(fo.getName());
                            }
                        }
                        break;
                    case ADAPTER_ACTIVATOR:
                        if ((ext.equals(JAVA_EXTENSION)) && ((dobj = DataObject.find(fo)) instanceof JavaDataObject)) {
                            SourceElement source = ((JavaDataObject)dobj).getSource();
                            if (source != null) {
                                ClassElement ce = source.getClass(Identifier.create(fo.getName()));
                                if (ce.isInterface())
                                    break;
                                if (inheritsFrom(ce, "org.omg.PortableServer.AdapterActivatorPOA"))
                                    if (source.getPackage() != null)
                                        ret.add(source.getPackage() + "." + fo.getName());
                                    else
                                        ret.add(fo.getName());
                            }
                        }
                }
            }
        }
        catch(Exception ex) {
        }
        /*
        if (_type == SERVANT_MANAGER) {
            ret.add("org.omg.PortableServer.ServantActivatorPOATie");
            ret.add("org.omg.PortableServer.ServantLocatorPOATie");
        }
        else if (_type == ADAPTER_ACTIVATOR) {
            ret.add("org.omg.PortableServer.AdapterActivatorPOATie");
        }
         */
        return ret;
    }
    
    private boolean inheritsFrom(ClassElement ce, String baseClass) {
        if (ce == null)
            return false;
        Identifier sid = ce.getSuperclass();
        if (sid == null)
            return false;
        String fullName = sid.getFullName();
        if (fullName != null && fullName.equals(baseClass))
            return true;
        return inheritsFrom(ClassElement.forName(sid.getFullName()), baseClass);
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        if ("_M_orb_tag".equals(e.getPropertyName())) {
            os.removePropertyChangeListener(weak2);
            os = POASupport.getCORBASettings().getActiveSetting();
            os.addPropertyChangeListener(weak2);
            resetSearchMode();
            return;
        }
        if ("_M_find_method".equals(e.getPropertyName())) {
            resetSearchMode();
        }
    }
}
