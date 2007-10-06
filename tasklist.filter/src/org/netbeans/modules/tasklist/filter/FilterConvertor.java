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
import java.util.LinkedList;
import org.netbeans.spi.settings.Convertor;
import org.netbeans.spi.settings.DOMConvertor;
import org.netbeans.spi.settings.Saver;
import org.openide.ErrorManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;
import org.netbeans.modules.tasklist.filter.SuggestionProperties;


/**
 * A common base for all Filter Convertors. Filters are converted using delegation to 
 * their convertors.
 * Public ID: "-//NetBeans org.netbeans.modules.tasklist//DTD Filters 1.0//EN"
 *
 * @author  or141057
 */
public abstract class FilterConvertor extends DOMConvertor implements PropertyChangeListener {

    private static final String ATTR_ALLTRUE = "allTrue";
    private static final String ELEM_FILTER = "Filter";
    private static final String ELEM_CONDITION = "Condition";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PROPID = "propertyId";
    
  
    private Saver saver;
    
    /** Creates a new instance of FilerRepositoryConvertor */
    protected FilterConvertor(String rootElement) {
      this("-//NetBeans org.netbeans.modules.tasklist//DTD " + rootElement + " 1.0//EN", // NOI18N
            "http://tasklist.netbeans.org/dtd/filter-1_0.dtd", rootElement); 
    }

    protected FilterConvertor(String publicID, String systemID, String root) {
      super(publicID, systemID, root);
    }
    
    protected abstract Filter createFilter();

    protected SuggestionProperty getProperty(String propid) {
      return SuggestionProperties.getProperty(propid);
    }
    
    protected Object readElement(org.w3c.dom.Element element) throws java.io.IOException, java.lang.ClassNotFoundException {
        Filter f = createFilter();
        readFilter(element, f);
        return f;
    }
    
    protected void readFilter(org.w3c.dom.Element element, Filter filter) throws java.io.IOException, java.lang.ClassNotFoundException {
        filter.setMatchAll(Boolean.valueOf(element.getAttribute(ATTR_ALLTRUE)).booleanValue());
        filter.setName(element.getAttribute(ATTR_NAME));
        
        LinkedList conditions = new LinkedList();
        Node child = element.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                FilterCondition fc = (FilterCondition)this.delegateRead((Element)child);
		String propid = ((Element)child).getAttribute(ATTR_PROPID);
		AppliedFilterCondition afc = new AppliedFilterCondition(getProperty(propid), fc);
		conditions.add(afc);
            }
            child = child.getNextSibling();
        }

	filter.setConditions(conditions);
    }
        
    protected void writeElement(org.w3c.dom.Document document, org.w3c.dom.Element element, Object obj) throws java.io.IOException, org.w3c.dom.DOMException {
        Filter filter = (Filter)obj;
        writeFilter(document, element, filter);
    }
     
    protected void writeFilter(org.w3c.dom.Document document, org.w3c.dom.Element element, Filter filter) throws java.io.IOException, org.w3c.dom.DOMException {
        element.setAttribute(ATTR_ALLTRUE, Boolean.toString(filter.matchAll()));
        element.setAttribute(ATTR_NAME, filter.getName());
        
        Iterator it = filter.getConditions().iterator();
        while (it.hasNext()) { 
	  AppliedFilterCondition afc = (AppliedFilterCondition)it.next();
	  Element e = this.delegateWrite(document, afc.getCondition());
	  e.setAttribute(ATTR_PROPID, afc.getProperty().getID());
	  element.appendChild(e);
        }
    }
    
    public void registerSaver(Object obj, org.netbeans.spi.settings.Saver saver) {
        this.saver = saver;
        ((Filter)obj).addPropertyChangeListener(this);
    }
    
    public void unregisterSaver(Object obj, org.netbeans.spi.settings.Saver saver) {
       if (saver == null || saver != this.saver) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, 
                new IllegalArgumentException(
                    "Wrong argument for unregisterSaver(Object=" + obj + // NOI18N
                        ", Saver=" + saver + ")")); // NOI18N
        }        
        ((Filter)obj).removePropertyChangeListener(this);
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
