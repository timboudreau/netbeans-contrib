/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
