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

package org.netbeans.modules.jndi;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import org.netbeans.modules.jndi.spi.NbInitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

/** This class extends InitialDirContext with methods for timeout handling
 * 
 *  @author Ales Novak, Tom Zezula
 */
final class JndiDirContext extends InitialDirContext {

    // Set the NbInitialContextFactoryBuilder as default
    // factory builder.
    static {
	try {
          NamingManager.setInitialContextFactoryBuilder ( new NbInitialContextFactoryBuilder());
	}catch (NamingException namingException) {
	  // Give it up.
	}
    }


    /** Environment used for InitialContext*/
    protected Hashtable envTable;

    /**
     * Constuctor 
     * @param env  hashtable of properties for InitialDirContext
     */
    public JndiDirContext(Hashtable env) throws NamingException {
        super(env);
        this.envTable = env;
    }

    /** Returns environment for which the Context was created
     *  @return Hashtable of key type java.lang.String, value type java.lang.String
     */ 
    public final Hashtable getEnvironment() {
        // return envTable;
        try{
            return super.getEnvironment();
        }catch(Exception e){
            return null;
        }
    }

    /** This method check whether the Context is valid,
     *  if not it simply throws Exception
     *  @param javax.naming.Context context to be checked
     *  @exception NamingException
     */
    public final void checkContext () throws NamingException{
        // We simply call any context operation to see that the
        // context is correct
        this.list("");
    }

}
