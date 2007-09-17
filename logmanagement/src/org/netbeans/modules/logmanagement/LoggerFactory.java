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
 *//*
 * LoggerFactory.java
 *
 * Created on Sep 11, 2007, 2:05:27 PM
 */

package org.netbeans.modules.logmanagement;

import java.util.Collections;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Anurdha
 */
 class LoggerFactory extends ChildFactory<Logger> {

    private Logger logger;

     LoggerFactory(Logger logger) {
        this.logger = logger;
    }

    protected boolean createKeys(List<Logger> arg0) {

        arg0.addAll(logger.getChilderns());
        Collections.sort(arg0);
        return true;
    }

    @Override
    protected Node createNodeForKey(Logger key) {
        Node node = null;

        node = new LoggerNode(key);


        return node;
    }
}