/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.enode.test;

import org.openide.nodes.Node;

/**
 * Simply implementation of MONodeEnhancer that returns
 * hardcoded strings from getURL and toString().
 * @author David Strupl
 */
public class MONodeEnhancerImpl implements MONodeEnhancer, Node.Cookie {

    /** Creates a new instance of MONodeEnhancerImpl */
    public MONodeEnhancerImpl(Node n) {
    }

    /**
     * This method is implementation for a method from
     * interface MONodeEnhancer.
     */
    public String getURL() {
        return "http://www.netbeans.org/";
    }
    public void save() {
        
    }
    /**
     * Return something user can read.
     */
    public String toString() {
        return "MONodeEnhancerImpl found!";
    }
}
