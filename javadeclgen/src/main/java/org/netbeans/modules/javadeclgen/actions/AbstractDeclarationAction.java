/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.javadeclgen.actions;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.SyntaxSupport;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtSyntaxSupport;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public abstract class AbstractDeclarationAction extends CookieAction {
    private static final String PARAMETER_PREFIX = "${";
    private static final String PARAMETER_SUFFIX = "}";
    protected static final String BLANK = wrapAsCodeTemplateParameter("blank default=\"\"");
    protected static final String CURSOR = wrapAsCodeTemplateParameter("cursor");
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        if (ec != null) {
            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes != null) {
                TopComponent activetc = TopComponent.getRegistry().getActivated();
                for (int i = 0; i < panes.length; i++) {
                    if (activetc.isAncestorOf(panes[i])) {                        
                        handleDeclarion(panes[i]);
                        break;
                    }
                }
            }
        }
    }
    
    protected void handleDeclarion(JTextComponent textComponent) {
        if (!textComponent.isEditable()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        SyntaxSupport syntaxSupport = Utilities.getSyntaxSupport(textComponent);
        if (syntaxSupport == null) {
            // no syntax support available :(
            Toolkit.getDefaultToolkit().beep();
            return ;
        }
        
        // get current caret position
        int offset = textComponent.getCaretPosition();
        try {
            // get token chain at the offset
            TokenItem tokenItem = ((ExtSyntaxSupport) syntaxSupport).getTokenChain(offset - 1, offset);
            
            
            final String type = tokenItem.getTokenID().getName();            
            // is this an identifier
            if (tokenItem != null) { 
                String image = tokenItem.getImage();
                if ("identifier".equals(type)) { // NOI18N
                    if (image != null && image.length() > 0) {
                        replaceText(textComponent, tokenItem.getOffset(), image);
                    }
                } else if (image != null) {
                    if ("boolean".equals(image)
                            || "byte".equals(image)
                            || "char".equals(image)
                            || "double".equals(image)
                            || "float".equals(image)
                            || "int".equals(image)
                            || "long".equals(image)
                            || "short".equals(image)
                            ) {
                        replaceText(textComponent, tokenItem.getOffset(), image);                        
                    }
                }
            }
        } catch (BadLocationException ble) {
            ErrorManager.getDefault().notify(ble);
        }
        Toolkit.getDefaultToolkit().beep();
    }
    
    protected static String wrapAsParam(String paramName) {
        return wrapAsParam(paramName, false);
    }
    
    protected static String wrapAsParam(String paramName, boolean plural) {
        return wrapAsCodeTemplateParameter(getSmartParamName(paramName, plural));
    }
    
    protected static String wrapAsCodeTemplateParameter(String text) {
        return PARAMETER_PREFIX + text + PARAMETER_SUFFIX;
    }
    
    private static final Map<String, String> singularMap = new HashMap<String, String>();
    private static final Map<String, String> pluralMap = new HashMap<String, String>();
    static {
        singularMap.put("boolean", "condition");
        singularMap.put("byte", "b");
        singularMap.put("char", "c");
        singularMap.put("double", "d");
        singularMap.put("float", "f");
        singularMap.put("int", "i");
        singularMap.put("long", "l");
        singularMap.put("short", "s");
        singularMap.put("Iterator", "it");
        singularMap.put("StringBuilder", "sb");
        singularMap.put("StringBuffer", "sb");
        
        pluralMap.put("boolean", "conditions");
        pluralMap.put("Iterator", "its");
        pluralMap.put("StringBuilder", "sbs");
        pluralMap.put("StringBuffer", "sbs");     
    }
    
    private static String getSmartParamName(String paramName, boolean plural) {
        String smartParamName = null;
        if (plural) {
            smartParamName = pluralMap.get(paramName);
        } else {
            smartParamName = singularMap.get(paramName);            
        }
        if (smartParamName != null) {
            return smartParamName;
        }
        return String.valueOf(Character.toLowerCase(paramName.charAt(0))) +
                paramName.substring(1) + 
                (plural ? "s" : "");
    }
    
    protected abstract void replaceText(JTextComponent textComponent, int offset, String text);
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}
