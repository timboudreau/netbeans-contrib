/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */

package org.netbeans.modules.remotefs.core;

/** Object that is used in RemoteClient for identifing file name.
 *
 * @author  lmartinek
 * @version 
 */
public interface RemoteFileName {

    /** Get the name. Only last name is returned, not whole path
     * @return  name of this object */
    public abstract String getName();
    
    /** Set new name. Used for renaming. Only name is chnaged, path remains.
     * @param newname  new name */
    public abstract void setName(String newname);
    
    /** Get full name (with whole path).
     * @return  full name*/
    public abstract String getFullName();
    
    /** Create new name object under this name object.
     * @param name name of new name object
     * @return created name object */
    public abstract RemoteFileName createNew(String name);
    
}
