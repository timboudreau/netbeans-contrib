/*
 * OperationAlreadyExistsException.java
 *
 * Created on September 26, 2001, 7:58 PM
 */

package org.netbeans.modules.corba.idl.generator;

/**
 *
 * @author  Tomas Zezula
 */
public class OperationAlreadyDefinedException extends AlreadyDefinedSymbolException {

    /** Creates new OperationAlreadyExistsException */
    public OperationAlreadyDefinedException (String str) {
        super (str);
    }

}
