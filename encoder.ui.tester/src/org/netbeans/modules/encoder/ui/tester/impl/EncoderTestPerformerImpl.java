/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.encoder.ui.tester.impl;

import com.sun.encoder.EncoderConfigurationException;
import com.sun.encoder.EncoderException;
import com.sun.encoder.EncoderType;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.netbeans.modules.encoder.ui.basic.EncodingConst;
import org.netbeans.modules.encoder.ui.basic.Utils;
import org.netbeans.modules.encoder.ui.tester.EncoderTestPerformer;
import org.netbeans.modules.encoder.ui.tester.EncoderTestTask;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.Utilities;
import org.xml.sax.SAXException;

/**
 * The encoder test performer
 *
 * @author Cannis Meng
 */
public class EncoderTestPerformerImpl implements EncoderTestPerformer, ActionListener {
    
    private static final ResourceBundle _bundle =
            ResourceBundle.getBundle("org/netbeans/modules/encoder/ui/tester/impl/Bundle");
    private static final String PROCESS = _bundle.getString("test_performer.lbl.process");
    private static final String CANCEL = _bundle.getString("test_performer.lbl.cancel");
    public static final String ENCODE = "Encode";  //NOI18N
    public static final String DECODE = "Decode";  //NOI18N
    
    public static final QName TOP_PROPERTY_ELEMENT = new QName(EncodingConst.URI, EncodingConst.TOP_FLAG);
    private static final XmlBoolean XML_BOOLEAN_TRUE = XmlBoolean.Factory.newValue(Boolean.TRUE);
            
    private TesterPanel testerPanel;
    private File xsdFile;
    private DialogDescriptor dialogDescriptor;
    private Dialog dialog;
    private EncoderTestTask mEncoderTestTask;
    private File metaFile;
    private EncoderType mEncoderType;
    
    public void performTest(File xsdFile, EncoderType encoderType) {
        metaFile = xsdFile;
        mEncoderType = encoderType;
        if (mEncoderTestTask == null) {
            mEncoderTestTask = new EncoderTestTaskImpl();
        }
        showDialog();                       
    }
    
    private void showDialog() {
        try {
            QName[] qnames = getTopElementDecls(metaFile);
            if (qnames.length == 0) {
                // i.e. no top element(s) are selected in the XSD
                // show dialog to ask user to fix the XSD file before testing
                JOptionPane.showMessageDialog(null,
                        _bundle.getString("test_panel.lbl.no_top_element_in_xsd"));
                return;
            }
            testerPanel = new TesterPanel(metaFile.getAbsolutePath());
            testerPanel.setTopElementDecls(getTopElementDecls(metaFile), null);
        } catch (XmlException ex) {
            ErrorManager.getDefault().notify(ex);
            return;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return;
        }
        dialogDescriptor = new DialogDescriptor(testerPanel, 
                _bundle.getString("test_performer.lbl.test_encoding"),
                true,
                new Object[] {PROCESS, CANCEL},
                PROCESS,
                DialogDescriptor.BOTTOM_ALIGN,
                HelpCtx.DEFAULT_HELP, 
                this);
        dialogDescriptor.setClosingOptions(new Object[] {CANCEL});
        dialogDescriptor.setButtonListener(this);        
        dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(PROCESS)) {
            try {
                testerPanel.savePreferences();
            } catch (BackingStoreException ex) {
                //Ignore
            }
            process();            
        }
    }
    
    /**
     * Gets the top level element declaractions (the declarations that define
     * messages) from an XSD file.  It only search within the XSD specified,
     * not any of the referenced XSDs.
     * 
     * @param xsdFile the XSD file
     */
    private QName[] getTopElementDecls(File xsdFile)
            throws XmlException, IOException {
        SchemaDocument schemaDoc = SchemaDocument.Factory.parse(xsdFile);
        if (schemaDoc.getSchema() == null) {
            return new QName[0];
        }
        String targetNS = schemaDoc.getSchema().getTargetNamespace();
        if (targetNS != null && targetNS.length() == 0) {
            targetNS = null;
        }
        TopLevelElement[] elemDecls = schemaDoc.getSchema().getElementArray();
        if (elemDecls == null || elemDecls.length == 0) {
            return new QName[0];
        }
        List<QName> topElemList = new ArrayList<QName>();
        for (int i = 0; i < elemDecls.length; i++) {
            if (!elemDecls[i].isSetAnnotation()
                    || elemDecls[i].getAnnotation().sizeOfAppinfoArray() == 0) {
                continue;
            }
            final int countAppinfos = elemDecls[i].getAnnotation().sizeOfAppinfoArray();
            for (int j = 0; j < countAppinfos; j++) {
                if (!elemDecls[i].getAnnotation().getAppinfoArray(j).isSetSource()) {
                    continue;
                }
                if (!EncodingConst.URI.equals(
                        elemDecls[i].getAnnotation().getAppinfoArray(j).getSource())) {
                    continue;
                }
                XmlObject xmlObj = elemDecls[i].getAnnotation().getAppinfoArray(j);
                XmlObject[] topProps = xmlObj.selectChildren(TOP_PROPERTY_ELEMENT);
                if (topProps == null || topProps.length == 0) {
                    continue;
                }
                if (XML_BOOLEAN_TRUE.compareValue(topProps[0]) == 0) {
                    if (targetNS == null) {
                        topElemList.add(new QName(elemDecls[i].getName()));
                    } else {
                        topElemList.add(new QName(targetNS, elemDecls[i].getName()));
                    }
                }
            }
        }
        return topElemList.toArray(new QName[0]);
    }
    
    private void process() {               
        //verify the input first
        QName rootElement = testerPanel.getSelectedTopElementDecl();
        if (rootElement == null) {
            JOptionPane.showMessageDialog(testerPanel,
                    _bundle.getString("test_performer.lbl.select_top_element"));
            return;
        }
        
        String type = testerPanel.getActionType();
        File processFile = new File(testerPanel.getProcessFile());
        
        if (!processFile.exists()) {
            JOptionPane.showMessageDialog(testerPanel,
                    _bundle.getString("test_performer.lbl.enter_process_file"));
            return;
        }        
        
        String oFile = testerPanel.getOutputFile();
        File outputFile = new File(oFile);       
        if (testerPanel.getOutputFileName() == null
                || testerPanel.getOutputFileName().equals("")) {  //NOI18N
            //no outputfile
            JOptionPane.showMessageDialog(testerPanel,
                    _bundle.getString("test_performer.lbl.enter_output_file."));
            return;
        }

        /**
         * if outputFile already exists and "overwrite" is unchecked, then
         * output file name is based on the given base name with "_1" suffixed
         * and with the same file extension.
         */
        if (outputFile.exists()) {
            if (!testerPanel.isOverwrite()) {
                String ext = FileUtil.getExtension(outputFile.getAbsolutePath());   
                String parent = outputFile.getParent();
                String name = outputFile.getName().replaceAll("." + ext, "");  //NOI18N
                outputFile = new File(parent + File.separatorChar + name + "_1." + ext);  //NOI18N
            }
        }
        
        boolean mResult = true;

        if (type.equals(ENCODE)) {
            try {
                testerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                mEncoderTestTask.encode(mEncoderType, metaFile, rootElement,
                        processFile, outputFile, testerPanel.getPostencodeCoding(),
                        testerPanel.isToString());
            } catch (IOException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (ParserConfigurationException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (EncoderException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (SAXException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;            
            } catch (EncoderConfigurationException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;            
            } finally {
                testerPanel.setCursor(null);
            }
        } else {            
            try {
                testerPanel.setCursor(Utilities.createProgressCursor(testerPanel));
                mEncoderTestTask.decode(mEncoderType, metaFile, rootElement,
                        processFile, outputFile, testerPanel.getPredecodeCoding(),
                        testerPanel.isFromString());
            } catch (TransformerConfigurationException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (final TransformerException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (EncoderException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (IOException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;
            } catch (EncoderConfigurationException ex) {
                Utils.notify(ex, true, dialog, JOptionPane.ERROR_MESSAGE);
                mResult = false;            
            } finally {
                testerPanel.setCursor(null);
            }            
        }
        
        if (!mResult) {
            return;
        }
        
        try {            
            DataObject dObj = DataLoaderPool.getDefault().
                            findDataObject(FileUtil.toFileObject(outputFile));
            if (dObj != null) {
                EditCookie edit = (EditCookie) dObj.getCookie(EditCookie.class);
                if (edit != null) {
                    dialog.setVisible(false);
                    edit.edit();
                }
            }
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
}
