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

package org.netbeans.modules.portalpack.portlets.genericportlets.core;

import java.util.HashMap;


/**
 * @author Satya
 */
public class ResultContext{

    public static final String CLASS_NAME = "classname";
    public static final String PORTLET_NAME = "portletname";
    public static final String FILE_PATH = "PATH_TO_CLASS_FILE";


    private HashMap map = null;

    
    public ResultContext()
    {
        map = new HashMap();
    }

    public void setAttribute(String key,Object value)
    {
        map.put(key,value);
    }

    public Object getAttribute(String key)
    {
        return map.get(key);
    }

}
