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

/** RemoteOutputStream that subclasses FileOutputStream and overwrites close() method to notify FTPFile.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class RemoteOutputStream extends java.io.FileOutputStream {
     private RemoteFile file;

     /** Creates new FTPOutputStream.
     * @param file FTPFile
     * @throws IOException 
     */
    public RemoteOutputStream(RemoteFile file) throws java.io.IOException {
      super(file.file);
      this.file = file;
    }
     
     /** Close the stream and notify FTPFile.
     * @throws IOException
     */
    public void close() throws java.io.IOException {
      super.close();
      file.save();
    }
     
} 