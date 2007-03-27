/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.rules.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.netbeans.api.jackpot.Query;
import org.netbeans.spi.jackpot.EmptyScriptException;
import org.netbeans.spi.jackpot.ScriptParsingException;
import org.openide.filesystems.FileObject;

/**
 * Creates a Query class from a rule file, using TransformParser.
 * 
 * @author Tom Ball
 */
public class RuleTransformerFactory {
   
    /**
     * Returns a query from a rule file object.
     *
     * @param fobj the rule file to convert
     * @param classpath the classpath for the parser
     * @return the Query instance
     * @throws Exception if there are any exceptions during conversion
     */
    public static Query getQuery(FileObject fobj, String classpath) throws Exception {
        String path = fobj.getPath();
        final InputStream is = fobj.getInputStream();
        final long lastmod = fobj.lastModified().getTime();
        Reader in = new InputStreamReader(is);
        Class c = createScriptClass(path, classpath, in, lastmod);
        Object obj = c.newInstance();
        if (!(obj instanceof Query))
            throw new ScriptParsingException("I don't know how to handle " + c);
        return (Query)obj;
    }

    private static Class createScriptClass(String scriptPath, String classpath, Reader in, long lastmod) throws Exception {
        TransformParser tp = new TransformParser(scriptPath, in, lastmod, classpath);
        tp.parseRules();
        if (!tp.hasRules())
            throw new EmptyScriptException(scriptPath);
        if (tp.hasErrors())
            throw new ScriptParsingException(tp.getErrors());
        Class ret;
        Throwable t0 = null;
        try {
            ret = tp.codeRules();
        } catch(Throwable t) {
            t0 = t;
            ret = null;
        }
        if (tp.hasErrors())
            throw new ScriptParsingException("script errors:\n" + tp.getErrors());
        else if(t0 != null)
            throw new ScriptParsingException("script compilation failed", t0);
        return ret;
    }
    
}
