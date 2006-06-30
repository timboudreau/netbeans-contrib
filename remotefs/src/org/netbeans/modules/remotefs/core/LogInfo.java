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

/** Interface for storing login information. Usually username, pasword, etc.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public interface LogInfo extends java.io.Serializable{

  /** Return human redable description of this LogInfo */
  public String displayName();


}