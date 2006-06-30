/* The contents of this file are subject to the terms of the Common Development
/* and Distribution License (the License). You may not use this file except in
/* compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
/* or http://www.netbeans.org/cddl.txt.
/*
/* When distributing Covered Code, include this CDDL Header Notice in each file
/* and include the License file at http://www.netbeans.org/cddl.txt.
/* If applicable, add the following below the CDDL Header, with the fields
/* enclosed by brackets [] replaced by your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
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
