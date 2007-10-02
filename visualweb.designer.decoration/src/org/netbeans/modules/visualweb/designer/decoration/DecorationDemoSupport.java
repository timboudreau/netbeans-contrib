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

package org.netbeans.modules.visualweb.designer.decoration;

import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.DesignContext;
import com.sun.rave.designtime.DesignProject;
import com.sun.rave.designtime.DesignProperty;
import com.sun.rave.designtime.faces.FacesDesignContext;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XXX Decoration demo support only.
 * It contains non-standard, hardcoded solutions to determine the back end
 * design bean for corresponding front end bean.
 * <p>
 * This solution shouldn't be used in production code. For that purpose
 * there should be designed appropriate api, which woudl provide the needed
 * link (i.e. to be able to retrieve back end bean(s) for specific front end bean).
 * </p>
 *
 * @author Peter Zavadsky
 */
final class DecorationDemoSupport {

    /** Logger used for this class. */
    private static final Logger logger = Logger.getLogger(DecorationDemoSupport.class.getName());
    
    static {
        initLogging();
    }
    
    /** XXX Some global support method would be needed. */
    private static void initLogging() {
        String prop = System.getProperty(DecorationDemoSupport.class.getName());
        if (prop == null) {
            return;
        }
        
        Level level = Level.parse(prop);
        logger.setLevel(level);
        
        // XXX Also the root handlers are needed to set (the console handlers).
        // TODO Probably better is to create and use local hanlder.
        Handler[] handlers = Logger.getLogger("").getHandlers(); // NOI18N
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            if (handler instanceof ConsoleHandler) {
                // Be careful not to clash with others, this is a root handler.
                Level oldLevel = handler.getLevel();
                if (level.intValue() < oldLevel.intValue()) {
                    handler.setLevel(level);
                }
            }
        }
    }
    
    
    /** Creates a new instance of DecorationDemoSupport */
    private DecorationDemoSupport() {
    }

    
    static DesignBean getDecorationBean(DesignBean designBean) {
        logger.fine("designBean=" + designBean); // NOI18N
        return findBackEndDesignBean(designBean);
    }
    
    private static DesignBean findBackEndDesignBean(DesignBean designBean) {
        while (designBean != null) {
            if (isBackEndDesignBean(designBean)) {
                break;
            } else {
                designBean = getBackingDesignBean(designBean);
            }
        }
        return designBean;
    }
    
    /** There has to be an API method for this. */
    private static boolean isBackEndDesignBean(DesignBean designBean) {
        logger.finer("is back end designBean=" + designBean); // NOI18N
        if (isRowSetDesignBean(designBean)) {
            logger.finer("true"); // NOI18N
            return true;
        }
        logger.finer("false"); // NOI18N
        return false;
    }
    
    private static DesignBean getBackingDesignBean(DesignBean designBean) {
        if (designBean == null) {
            return null;
        }
        
        DesignProperty backingDesignProperty = getBackingDesignProperty(designBean);
        if (backingDesignProperty != null) {
            return getBackingDesignBean(backingDesignProperty);
        }

        DesignBean backingChildDesignBean = getBackingOwnerChildDesignBean(designBean);
        if (backingChildDesignBean == null) {
            return null;
        }
        return getBackingDesignBean(backingChildDesignBean);
    }
    
    /** There should be an API method for this, probably returning an array of backing properties. */
    private static DesignProperty getBackingDesignProperty(DesignBean designBean) {
        if (isTableRowGroupDesignBean(designBean)) {
            return designBean.getProperty("sourceData"); // NOI18N
        } else if (isListDesignBean(designBean)) {
            return designBean.getProperty("items"); // NOI18N
        } else if (isDropDownListDesignBean(designBean)) {
            return designBean.getProperty("items"); // NOI18N
        } else if (isDataProviderDesignBean(designBean)) {
            return designBean.getProperty("cachedRowSet"); // NOI18N
        }
        return null;
    }
    
    /** There should be an API method for this, returning an array or backing beans. */
    private static DesignBean getBackingDesignBean(DesignProperty designProperty) {
        String valueSource = designProperty.getValueSource();
        if (valueSource == null) {
            return null;
        }
        return getDesignBeanForValueSource(valueSource, designProperty.getDesignBean().getDesignContext().getProject());
    }
    
    /** There should be an API method for this. */
    private static DesignBean getBackingOwnerChildDesignBean(DesignBean designBean) {
        if (isTableDesignBean(designBean)) {
            DesignBean[] children = designBean.getChildBeans();
            for (int i = 0; i < children.length; i++) {
                DesignBean child = children[i];
                logger.fine("child[" + i + "]=" + child); // NOI18N
                if (isTableRowGroupDesignBean(child)) {
                    return child;
                }
            }
        }
        return null;
    }
    
    
    private static boolean isTableDesignBean(DesignBean designBean) {
        return isDesignBeanOfClassName(designBean, "com.sun.rave.web.ui.component.Table"); // NOI18N
    }
    
    private static boolean isTableRowGroupDesignBean(DesignBean designBean) {
        return isDesignBeanOfClassName(designBean, "com.sun.rave.web.ui.component.TableRowGroup"); // NOI18N
    }

    private static boolean isListDesignBean(DesignBean designBean) {
        return isDesignBeanOfClassName(designBean, "com.sun.rave.web.ui.component.Listbox"); // NOI18N
    }
    
    private static boolean isDropDownListDesignBean(DesignBean designBean) {
        return isDesignBeanOfClassName(designBean, "com.sun.rave.web.ui.component.DropDown"); // NOI18N
    }
    
    private static boolean isDataProviderDesignBean(DesignBean designBean) {
        return isDesignBeanOfClassName(designBean, "com.sun.data.provider.impl.CachedRowSetDataProvider"); // NOI18N
    }
    
    private static boolean isRowSetDesignBean(DesignBean designBean) {
        return isDesignBeanOfClassName(designBean, "com.sun.sql.rowset.CachedRowSetXImpl"); // NOI18N
    }
    
    /** XXX Ugly method (string comparisons instead of class). */
    private static boolean isDesignBeanOfClassName(DesignBean designBean, String className) {
        if (designBean == null || className == null) {
            return false;
        }
        
        Object instance = designBean.getInstance();
        if (instance == null) {
            return false;
        }
        
        if (className.equals(instance.getClass().getName())) {
            return true;
        }
        return false;
    }
    
    private static DesignBean getDesignBeanForValueSource(String valueSource, DesignProject designProject) {
        if (valueSource == null || designProject == null) {
            return null;
        }
        
        String designContextName = getDesignContextNameFromValueSource(valueSource);
        logger.fine("\ndesignContextName=" + designContextName); // NOI18N
        if (designContextName == null) {
            return null;
        }
        
        DesignContext designContext = findDesignContext(designContextName, designProject);
        logger.fine("designContext=" + designContext); // NOI18N
        if (designContext == null) {
            return null;
        }
        
        String designBeanName = getDesignBeanNameFromValueSource(valueSource);
        logger.fine("designBeanName=" + designBeanName); // NOI18N
        if (designBeanName == null) {
            return null;
        }
        
        return findDesignBean(designBeanName, designContext);
    }

    private static String getDesignContextNameFromValueSource(String valueSource) {
        if (valueSource == null) {
            return null;
        }

        int hashBracket = valueSource.indexOf("#{"); // NOI18N
        if (hashBracket == -1) {
            return null;
        }
        
        // To get after the #{.
        hashBracket += 2;
        if (valueSource.length() < hashBracket) {
            return null;
        }

        int dot = valueSource.indexOf('.', hashBracket);
        if (dot == -1) {
            return null;
        }
         
        return valueSource.substring(hashBracket, dot);
    }

    private static String getDesignBeanNameFromValueSource(String valueSource) {
        if (valueSource == null) {
            return null;
        }
        
        int hashBracket = valueSource.indexOf("#{"); // NOI18N
        if (hashBracket == -1) {
            return null;
        }
        
        // To get after the #{.
        hashBracket += 2;
        if (valueSource.length() < hashBracket) {
            return null;
        }

        int dot = valueSource.indexOf('.', hashBracket);
        if (dot == -1) {
            return null;
        }
        
        // To get after the dot.
        dot += 1;
        if (valueSource.length() < dot) {
            return null;
        }
        
        int bracket = valueSource.indexOf('}', dot);
        int nextDot = valueSource.indexOf('.', dot);
        if (bracket == -1 && nextDot == -1) {
            return null;
        }
        
        int nextChar;
        if (bracket == -1) {
            nextChar = nextDot;
        } else if (nextDot == -1) {
            nextChar = bracket;
        } else {
            nextChar = (bracket < nextDot ? bracket : nextDot);
        }
        
        return valueSource.substring(dot, nextChar);
    }

    private static DesignContext findDesignContext(String designContextName, DesignProject designProject) {
        if (designContextName == null || designProject == null) {
            return null;
        }
        
        DesignContext[] designContexts = designProject.getDesignContexts();
        for (int i = 0; i < designContexts.length; i++) {
            DesignContext designContext = designContexts[i];
            // XXX getDisplayName? That usually implies localized name, but we need programatic name.
            if (designContextName.equals(designContext.getDisplayName())) {
                return designContext;
            }
        }
        return null;
    }

    private static DesignBean findDesignBean(String designBeanName, DesignContext designContext) {
        if (designBeanName == null || designContext == null) {
            return null;
        }
        
        return designContext.getBeanByName(designBeanName);
    }
}
