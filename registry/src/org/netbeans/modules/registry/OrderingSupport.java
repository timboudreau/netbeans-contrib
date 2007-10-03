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

package org.netbeans.modules.registry;

import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextException;
import org.netbeans.spi.registry.BasicContext;
import org.openide.util.TopologicalSortException;
import org.openide.util.Utilities;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class orders context items according to positional attributes
 * and for backward compatibility it also accepts relative ordering
 * attributes. After the relative ordering attrs are removed the
 * ordering functionality in this class can be simplified.
 * 
 * There are also several checks for "ordered" attribute
 * which are commented out till this mechanism gets common.
 *
 * @author copy & pasted from old Datasystems
 */
public class OrderingSupport {
    
    private static final Logger log = Logger.getLogger(OrderingSupport.class.getName());
    
    private static final char SEP = '/';

    public static final OrderingSupport DEFAULT = new OrderingSupport();
    
    private OrderingSupport() {
    }

    /**
     * @return ordered list of binding names and subcontext names.
     *
     */
    public List getOrderedNames(Context ctx) {
        
        // check that context is ordered
        String ordered = ctx.getAttribute(null, "ordered", null);
        if (ordered == null || (!ordered.equals("true"))) {
            // TODO: add this diagnostic later
            // log.log(Level.INFO, "Context "+ctx+" is not marked as ordered.");
        }
        
        List l = getItems(ctx);
        Map constraints = getOrderingConstraints(ctx);
        if (constraints.keySet().size() != 0) {
            try {
                l = Utilities.topologicalSort(l, constraints);
            } catch (TopologicalSortException ex) {
                List corrected = ex.partialSort();
                log.log(Level.INFO, "Note: context [" + ctx + "] cannot be consistently sorted due to ordering conflicts."); // NOI18N
                log.log(Level.FINE, "", ex);
                log.log(Level.INFO, "Using partial sort: " + corrected); // NOI18N
                l = corrected;
            }
        }
        
        // check for fullorder attribute
        String fullorder = ctx.getAttribute(null, "fullorder", null);
        if (fullorder != null) {
            List list = new ArrayList();
            // ensure that items are returned in specified full order
            // and additional items are left as they are at the end
            StringTokenizer tok = new StringTokenizer(fullorder, ",");
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                if (l.remove(token)) {
                    list.add(token);
                }
            }
            list.addAll(l);
            l = list;
        }
        return l;
    }

    // list all items of the context
    private List getItems(Context ctx) {
        List l = new ArrayList();
        Iterator i = ctx.getBindingNames().iterator();
        while (i.hasNext()) {
            l.add((String)i.next());
        }
        i = ctx.getSubcontextNames().iterator();
        while (i.hasNext()) {
            l.add(((String)i.next())+"/");
        }
        return l;
    }
    
    private Map getOrderingConstraints(Context ctx) {
        Map map = new HashMap();
        try {
            addPositions(map, ctx);
        } catch (ContextException ex) {
            log.log(Level.FINE, ex.toString(), ex);
        }
        addPartials(map, ctx);
        return map;
    }

    
    /** 
     * Read old partial ordering attributes. Method updates the passed map.
     *
     * @param m map<String, List<String>> where key is 
     * binding name or subcontext name and value is list of
     * binding names or subcontext names which must go after the key.
     */
    private void addPartials(Map m, Context ctx) {
        Iterator i = ctx.getAttributeNames(null).iterator();
        while (i.hasNext()) {
            String attrName = (String)i.next();
            if (attrName.indexOf (SEP) != -1) {
                // TODO: convert bool attr to String in ContextImpl
                String value = ctx.getAttribute(null, attrName, "");
                if (value.equals("true")) {
                    int idx = attrName.indexOf(SEP);
                    String a = attrName.substring(0, idx);
                    String b = attrName.substring(idx + 1);
                    
                    // this might not work in some cases, but this is just backward solution
                    // so I guess it is OK: I expect that file with extension is a binding and
                    // file without extension is subcontext.
                    idx = a.lastIndexOf('.');
                    if (idx != -1) {
                        // probably binding. cut off extension
                        a = a.substring(0, idx);
                    } else {
                        // probably subcontext. append "/"
                        a = a + "/";
                    }
                    idx = b.lastIndexOf('.');
                    if (idx != -1) {
                        // probably binding. cut off extension
                        b = b.substring(0, idx);
                    } else {
                        // probably subcontext. append "/"
                        b = b + "/";
                    }
                    
                    List l = (List)m.get(a);
                    if (l == null) {
                        m.put(a, l = new LinkedList());
                    }
                    l.add(b);
                    
                }
            }
        }
    }

    /** 
     * Read positional ordering attributes. Method updates the passed map.
     *
     * @param m map<String, List<String>> where key is 
     * binding name or subcontext name and value is list of
     * binding names or subcontext names which must go after the key.
     */
    private void addPositions(Map m, Context ctx) throws ContextException {
        Map map = new HashMap();
        BasicContext basicCtx = ApiContextFactory.DEFAULT.getBasicContext(ctx);
        Iterator i = ctx.getBindingNames().iterator();
        while (i.hasNext()) {
            String bindingName = (String)i.next();
            String position = ctx.getAttribute(bindingName, "position", null);
            if (position != null) {
                try {
                    Float f = new Float(position);
                    String bnd = (String)map.put(f, bindingName);
                    if (bnd != null) {
                        log.log(Level.INFO, "Ordering Error: Two items in context ["+ctx+
                            "] has the same position ["+position+"]: "+bnd+" "+bindingName);
                    }
                } catch (NumberFormatException ex) {
                    // ok to ignore.
                }
            } else {
                // TODO: add this diagnostic later
                // log.log(Level.INFO, "Binding "+bindingName+" in context "+ctx+" does not have position attribute.");
            }
        }
        i = ctx.getSubcontextNames().iterator();
        while (i.hasNext()) {
            String subcontextName = (String)i.next();
            Context subCtx = ctx.getSubcontext(subcontextName);
            if (subCtx == null) {
                continue;
            }
            String position = subCtx.getAttribute(null, "position", null);
            if (position != null) {
                try {
                    Float f = new Float(position);
                    String sub = (String)map.put(f, subcontextName+"/");
                    if (sub != null) {
                        log.log(Level.INFO, "Ordering Error: Two items in context ["+ctx+
                            "] has the same position ["+position+"]: "+sub+" "+subcontextName);
                    }
                } catch (NumberFormatException ex) {
                    // ok to ignore.
                }
            } else {
                // TODO: add this diagnostic later
                // log.log(Level.INFO, "Subcontext "+subcontextName+" in context "+ctx+" does not have position attribute.");
            }
        }
        List l = new ArrayList(map.keySet());
        Collections.sort(l);
        for (int k=0; k<l.size(); k++) {
            String itemA = (String)map.get(l.get(k));
            List list = (List)m.get(itemA);
            if (list == null) {
                m.put(itemA, list = new LinkedList());
            }
            for (int j=k+1; j<l.size(); j++) {
                String itemB = (String)map.get(l.get(j));
                list.add(itemB);
            }
        }
    }
    
    
}
