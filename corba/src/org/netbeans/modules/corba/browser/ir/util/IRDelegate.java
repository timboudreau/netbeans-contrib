/*
 * IRDelegate.java
 *
 * Created on October 17, 2001, 11:04 AM
 */

package org.netbeans.modules.corba.browser.ir.util;

/**
 *
 * @author  tzezula
 */
public interface IRDelegate extends org.openide.nodes.Node.Cookie {
    public org.omg.CORBA.IRObject getIRObject ();
}

