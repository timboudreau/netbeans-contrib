/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package Templates.CORBA;

__ORB_IMPORT__
/**
 *
 * @author  __USER__
 * @version 
 */
public class ServerMain {

    /** Creates new __NAME__ */
    public ServerMain () {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {

        __SETTINGS_ORB_PROPERTIES__
        __ORB_SERVER_INIT__

        __ORB_OBJECT_ACTIVATION__
        __ORB_SERVER_BINDING__
        __ORB_SERVER_RUN__
    }

}
