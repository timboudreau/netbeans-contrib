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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;

/**
 * Helper class for all Node related operations.
 * @author Satyaranjan
 */
public class NodeHelper {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

    public static Property[] getProperties(Map<String, String> propertyMap, final BaseBean bean) throws Exception {

        List propertyList = new ArrayList();
        Set set = propertyMap.keySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            try {
                final String key = it.next().toString();
                String disValue = (String) propertyMap.get(key);
                Class type = null;

                Property property = null;
                if (bean.getProperty(key).isIndexed()) {

                    property = new PropertySupport.ReadOnly(key, String[].class, disValue, disValue) {

                        public Object getValue() throws IllegalAccessException, InvocationTargetException {
                            return bean.getValues(key);
                        }
                    };
                } else {
                    type = String.class;
                    property = new PropertySupport.ReadOnly(key, String.class, disValue, disValue) {

                        public Object getValue() throws IllegalAccessException, InvocationTargetException {
                            return bean.getValue(key);
                        }
                    };
                }
                if(property.getValue() != null)
                    propertyList.add(property);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error getting property !!!", e);
            }
        }

        return (Property[]) propertyList.toArray(new Property[0]);
    }
}
