/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks;

import java.util.*;
import org.netbeans.api.bookmarks.Bookmark;

import org.netbeans.api.registry.*;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * Utility class for manipulation with contexts. This should be part
 * of Registry API but unfortunatelly (at least for now) it is not.
 * @author David Strupl
 */
public class RegistryUtil {
    
    /** Creates a new instance of RegistryUtil */
    private RegistryUtil() {
    }
    
    /**
     * Copies one context with all bindings, subcontexts and
     * attributes to the target context.
     */
    public static void copy(Context what, Context where, String newName) throws ContextException {
        Context checkWhere = where;
        while (checkWhere != null) {
            if (what.equals(checkWhere)) {
                String s1 = what.getAbsoluteContextName();
                String s2 = where.getAbsoluteContextName();
                s1 = what.getAttribute(null, org.openide.nodes.Node.PROP_DISPLAY_NAME, s1);
                s2 = where.getAttribute(null, org.openide.nodes.Node.PROP_DISPLAY_NAME, s2);

                IllegalStateException ise = new IllegalStateException();
                ErrorManager.getDefault().annotate(ise, ErrorManager.USER, "Cannot copy " + what + " to " + where, 
                    NbBundle.getMessage(RegistryUtil.class, "WARN_CannotCopy", s1, s2), null, new Date() ); 
                throw ise;
            }
            checkWhere = checkWhere.getParentContext();
        }
        
        if (newName == null) {
            newName = what.getContextName();
        }
        Context target = where.createSubcontext(newName);
        
        // copy attributes of this context
        Collection ctxAttrNames = what.getAttributeNames(null);
        for (Iterator it = ctxAttrNames.iterator(); it.hasNext(); ) {
            String attrName = (String)it.next();
            target.setAttribute(null, attrName, what.getAttribute(null, attrName, null));
        }
        
        // first copy bindings
        Collection bNames = what.getBindingNames();
        for (Iterator i = bNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            Object obj = what.getObject(name, null);
            if (obj instanceof Bookmark) {
                obj = BookmarkServiceImpl.cloneBookmark((Bookmark)obj);
                BookmarkServiceImpl.saveBookmarkActionImpl(target, name);
            }
            target.putObject(name, obj);
            Collection attrNames = what.getAttributeNames(name);
            for (Iterator it = attrNames.iterator(); it.hasNext(); ) {
                String attrName = (String)it.next();
                target.setAttribute(name, attrName, what.getAttribute(name, attrName, null));
            }
        }
        
        // copy contexts
        Collection cNames = what.getSubcontextNames();
        for (Iterator i = cNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            Context sub = what.getSubcontext(name);
            copy(sub, target, sub.getContextName());
        }
        
        // set order on the newly created context
        List orderedNames = what.getOrderedNames();
        target.orderContext(orderedNames);
    }
}
