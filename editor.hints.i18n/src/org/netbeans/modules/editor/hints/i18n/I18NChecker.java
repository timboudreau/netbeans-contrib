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
package org.netbeans.modules.editor.hints.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.i18n.HardCodedString;
import org.netbeans.modules.i18n.I18nString;
import org.netbeans.modules.i18n.java.JavaI18nSupport;
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.PropertiesStructure;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.ProvidersList;
import org.netbeans.spi.editor.hints.support.ErrorParserSupport;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public class I18NChecker extends ErrorParserSupport {
    
    /** Creates a new instance of I18NChecker */
    public I18NChecker() {
    }

    public List parseForErrors(final Document doc) {
        if (!ProvidersList.isProviderEnabled(I18NProviderDescription.I18N_ERROR_PROVIDER))
            return Collections.EMPTY_LIST;
        
        //TODO: generate unique 
        try {
            final DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
            
            if (od == null)
                return Collections.EMPTY_LIST;
            
            final JavaI18nSupport support = new JavaI18nSupport(od);
            final HardCodedString[] hcs = support.getFinder().findAllHardCodedStrings();
            final List result = new ArrayList();
            final FileObject bundleFO = od.getPrimaryFile().getParent().getFileObject("Bundle.properties");
            final PropertiesDataObject bundle = (PropertiesDataObject) (bundleFO != null ? DataObject.find(bundleFO) : null); //TODO: cast
            
            if (hcs != null) {
                for (int cntr = 0; cntr < hcs.length; cntr++) {
                    final HardCodedString hardCoded = hcs[cntr];

                    if (hardCoded.getLength() == 2)
                        continue; //ignore empty strings
                    
                    Fix addToBundle = new Fix() {
                        public String getText() {
                            return (bundle == null ? "Create new bundle and r" : "R") + "eplace with localized string";
                        }
                        public ChangeInfo implement() {
                            PropertiesDataObject bundleInt = bundle;
                            
                            if (bundleInt == null) {
                                try {
                                    FileObject bundleFO = od.getPrimaryFile().getParent().createData("Bundle.properties");
                                    assert bundleFO != null;
                                    bundleInt = (PropertiesDataObject) DataObject.find(bundleFO); //TODO: cast
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            
                            if (bundleInt == null) {
                                //something was wrong, cannot continue:
                                return null;
                            }
                            
                            final I18nString string = support.getDefaultI18nString(hardCoded);
                            
                            string.setReplaceFormat(null);
                            string.getSupport().getResourceHolder().setResource(bundleInt);
                            
                            support.getResourceHolder().addProperty(string.getKey(), string.getValue(), string.getComment());
                            support.getReplacer().replace(hardCoded, string);
                            
                            SaveCookie sc = (SaveCookie) bundleInt.getCookie(SaveCookie.class);
                            
                            if (sc != null) {
                                try {
                                    sc.save();
                                } catch (IOException e) {
                                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                                }
                            }
                            
                            return null;
                        }
                    };
                    
                    Fix addNOI18N = new Fix() {
                        public String getText() {
                            return "Add // NOI18N";
                        }
                        
                        public ChangeInfo implement() {
                            try {
                                int line = NbDocument.findLineNumber((StyledDocument) doc, hardCoded.getStartPosition().getOffset());
                                int writeOffset = NbDocument.findLineOffset((StyledDocument) doc, line + 1) - 1; //TODO: last line in the document not handled correctly
                                
                                doc.insertString(writeOffset, " // NOI18N", null);
                            } catch (BadLocationException ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                            return null;
                        }
                    };
                    
                    int severity = ProvidersList.getErrorSeverity(I18NProviderDescription.I18N_ERROR_PROVIDER, I18NProviderDescription.HARDCODED_STRINGS);

                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, "Hardcoded String", Arrays.asList(new Object[] {addToBundle, addNOI18N}), getLinePart(doc, od, hcs[cntr])));
                }
            }
            
            return result;
        } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return Collections.EMPTY_LIST;
    }
    
    private Line.Part getLinePart(Document doc, DataObject od, HardCodedString hcs) {
        LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
        int startOffset = hcs.getStartPosition().getOffset();
        
        return lc.getLineSet().getOriginal(NbDocument.findLineNumber((StyledDocument) doc, startOffset)).createPart(NbDocument.findLineColumn((StyledDocument) doc, startOffset), hcs.getLength());
    }
    
    /** Adds new property (key-valkue pair) to resource object. 
     * @param key key value, if it is <code>null</code> nothing is done
     * @param value 'value' value, can be <code>null</code>
     * @param comment comment, can be <code>null</code>
     * @param forceNewValue if there already exists a key forces to reset its value
     */
    public ItemElem addProperty(PropertiesDataObject resource, Object key, Object value, String comment, boolean forceNewValue) {//TODO: force value, always create unique lables (see above)
        if(resource == null || key == null) return null;

        String keyValue     = key.toString();
        String valueValue   = value == null ? "" : value.toString();
        String commentValue = comment;
        
        // write to bundle primary file
        BundleStructure bundleStructure = ((PropertiesDataObject)resource).getBundleStructure();
        PropertiesStructure propStructure = bundleStructure.getNthEntry(0).getHandler().getStructure();
        ItemElem item = propStructure.getItem(keyValue);

        if(item == null) {
            // Item doesn't exist in this entry -> create it.
            propStructure.addItem(keyValue, valueValue, commentValue);
            item = propStructure.getItem(keyValue);
        } else if(!item.getValue().equals(valueValue) && forceNewValue) {
            item.setValue(valueValue);
            item.setComment(commentValue);
        }
        
        return item;
    }
    
}
