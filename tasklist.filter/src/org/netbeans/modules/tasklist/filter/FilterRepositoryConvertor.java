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

package org.netbeans.modules.tasklist.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.netbeans.spi.settings.Convertor;
import org.netbeans.spi.settings.DOMConvertor;
import org.netbeans.spi.settings.Saver;
import org.openide.ErrorManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Convertor for FilterRepository. Filters are converted using delegation to 
 * their convertors.
 * Public ID: "-//NetBeans org.netbeans.modules.tasklist//DTD Filters 1.0//EN"
 *
 * @author  or141057
 */
public class FilterRepositoryConvertor extends DOMConvertor implements PropertyChangeListener {

    private static final String ATTR_ACTIVE = "active";
    private static final String ELEM_FILTER = "Filter";
    private static final String ELEM_FILTERS = "Filters";
    
   /**
     * Creates a converter for the specified FO
     * @param fo an XML FO with Column Widths DTD
     */
    private static Object create(org.openide.filesystems.FileObject fo) {
        return new FilterRepositoryConvertor();
    }
    
    private Saver saver;
    
    /** Creates a new instance of FilerRepositoryConvertor */
    public FilterRepositoryConvertor() {
        super("-//NetBeans org.netbeans.modules.tasklist//DTD Filters 1.0//EN", // NOI18N
            "http://tasklist.netbeans.org/dtd/filters-1_0.dtd", ELEM_FILTERS); // NOI18N
        
    }
    
    protected Object readElement(org.w3c.dom.Element element) throws java.io.IOException, java.lang.ClassNotFoundException {
        FilterRepository rep = new FilterRepository();
        int activeIndex = Integer.parseInt(element.getAttribute(ATTR_ACTIVE));
     
        Node child = element.getFirstChild();
        while (child != null) {
            if ((child.getNodeType() == Node.ELEMENT_NODE)) {
                Filter f = (Filter)this.delegateRead((Element)child);
                rep.add(f);
            }
            child = child.getNextSibling();
        }
        
        if (activeIndex != -1) {
            rep.setActive((Filter)rep.get(activeIndex));
        }
        
        return rep;
    }

    protected void writeElement(org.w3c.dom.Document document, org.w3c.dom.Element element, Object obj) throws java.io.IOException, org.w3c.dom.DOMException {
        FilterRepository fr = (FilterRepository)obj;
        element.setAttribute(ATTR_ACTIVE, Integer.toString(fr.indexOf(fr.getActive())));

        Iterator fit = fr.iterator();
        while (fit.hasNext()) {
            Element childNode = this.delegateWrite(document, fit.next());
            element.appendChild(childNode);
        }
    } 
    
    public void registerSaver(Object obj, org.netbeans.spi.settings.Saver saver) {
        this.saver = saver;
        ((FilterRepository)obj).addPropertyChangeListener(this);
    }
    
    public void unregisterSaver(Object obj, org.netbeans.spi.settings.Saver saver) {
       if (saver == null || saver != this.saver) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, 
                new IllegalArgumentException(
                    "Wrong argument for unregisterSaver(Object=" + obj + // NOI18N
                        ", Saver=" + saver + ")")); // NOI18N
        }        
        ((FilterRepository)obj).removePropertyChangeListener(this);
        this.saver = null;
    }
    
   
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            saver.requestSave();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
}
