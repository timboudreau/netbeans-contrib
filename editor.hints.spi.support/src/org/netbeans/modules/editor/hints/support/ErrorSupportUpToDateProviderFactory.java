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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.editor.hints.support;

import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.Document;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProviderFactory;
/**
 *
 * @author Jan Lahoda
 */
public class ErrorSupportUpToDateProviderFactory implements UpToDateStatusProviderFactory {

    private static Map<Document, ErrorSupportUpToDateProvider> doc2Provider = new WeakHashMap<Document, ErrorSupportUpToDateProvider>();

    /** Creates a new instance of ErrorSupportUpToDateProviderFactory */
    public ErrorSupportUpToDateProviderFactory() {
    }

    public static ErrorSupportUpToDateProvider getProvider(Document document) {
        if (doc2Provider.containsKey(document))
            return doc2Provider.get(document);
        
        ErrorSupportUpToDateProvider result = null;
        
//        if (HintsOperator.isSupported(document))
            result = new ErrorSupportUpToDateProvider();
        
        doc2Provider.put(document, result);
        
        return result;
    }
    
    public UpToDateStatusProvider createUpToDateStatusProvider(Document document) {
        return getProvider(document);
    }
}
