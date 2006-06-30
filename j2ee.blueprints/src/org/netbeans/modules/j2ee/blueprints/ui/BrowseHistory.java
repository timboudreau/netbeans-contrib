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

package org.netbeans.modules.j2ee.blueprints.ui;

import java.util.Stack;

/**
 * Keeps track of which pages the user was browsing
 *
 * @author Mark Roth
 */
public class BrowseHistory {

    private Stack backStack = new Stack();
    private Stack forwardStack = new Stack();

    public BrowseHistory() {
    }

    public void clear() {
        backStack.clear();
        forwardStack.clear();
    }

    /**
     * Pushes the back stack and erases all forward history.
     */
    public void pushBackStack(BrowseHistoryToken token) {
        backStack.push(token);
        forwardStack.clear();
    }
    
    public boolean isBackStackEmpty() {
        return backStack.isEmpty();
    }
    
    public boolean isForwardStackEmpty() {
        return forwardStack.isEmpty();
    }
    
    /**
     * Go back one page, saving the current token in the forward stack
     */
    public BrowseHistoryToken back(BrowseHistoryToken currentToken) {
        BrowseHistoryToken result = null;
        if(!backStack.isEmpty()) {
            result = (BrowseHistoryToken)backStack.pop();
            forwardStack.push(currentToken);
        }
        return result;
    }

    /**
     * Go forward one page, saving the current token in the back stack
     */
    public BrowseHistoryToken forward(BrowseHistoryToken currentToken) {
        BrowseHistoryToken result = null;
        if(!forwardStack.isEmpty()) {
            result = (BrowseHistoryToken)forwardStack.pop();
            backStack.push(currentToken);
        }
        return result;
    }
}
