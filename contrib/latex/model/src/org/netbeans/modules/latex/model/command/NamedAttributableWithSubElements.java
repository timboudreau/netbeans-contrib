/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class NamedAttributableWithSubElements extends NamedAttributable {
    
    private Map/*<String, Environment>*/ environments;
    private Map/*<String, Command>*/     commands;
    
    /** Creates a new instance of NamedAttributableWithSubElements */
    /*package private*/ NamedAttributableWithSubElements() {
    }
    
    public synchronized Map getEnvironments() {
        if (environments == null)
            return environments = new HashMap();

        return environments;
    }
    
    public synchronized Map getCommands() {
        if (commands == null)
            return commands = new HashMap();

        return commands;
    }

}
