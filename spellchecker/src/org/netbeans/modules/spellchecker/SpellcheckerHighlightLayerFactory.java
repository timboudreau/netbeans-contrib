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
package org.netbeans.modules.spellchecker;

import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;

/**
 *
 * @author Jan Lahoda
 */
public class SpellcheckerHighlightLayerFactory implements HighlightsLayerFactory {
    
    public SpellcheckerHighlightLayerFactory() {
    }
    
    public HighlightsLayer[] createLayers(Context ctx) {
        OffsetsBag bag = getBag(ctx.getComponent());
        
        return new HighlightsLayer[] {
            HighlightsLayer.create(SpellcheckerHighlightLayerFactory.class.getName(), ZOrder.SYNTAX_RACK, false, bag),
        };
    }
    
    public static synchronized OffsetsBag getBag(JTextComponent component) {
        OffsetsBag bag = (OffsetsBag) component.getClientProperty(SpellcheckerHighlightLayerFactory.class);
        
        if (bag == null) {
            component.putClientProperty(SpellcheckerHighlightLayerFactory.class, bag = new OffsetsBag(component.getDocument()));
        }
        
        return bag;
    }
    
}
