/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Used for creating a proper clipboard representation.
 * @author David Strupl
 */
public class ETableTransferHandler extends TransferHandler {
    
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof ETable) {
            ETable table = (ETable) c;
            int[] rows;
            int[] cols;
            
            if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
                return null;
            }
            
            if (!table.getRowSelectionAllowed()) {
                int rowCount = table.getRowCount();
                
                rows = new int[rowCount];
                for (int counter = 0; counter < rowCount; counter++) {
                    rows[counter] = counter;
                }
            } else {
                rows = table.getSelectedRows();
            }
            
            if (!table.getColumnSelectionAllowed()) {
                int colCount = table.getColumnCount();
                
                cols = new int[colCount];
                for (int counter = 0; counter < colCount; counter++) {
                    cols[counter] = counter;
                }
            } else {
                cols = table.getSelectedColumns();
            }
            
            if (rows == null || cols == null || rows.length == 0 || cols.length == 0) {
                return null;
            }
            
            StringBuffer plainBuf = new StringBuffer();
            String itemDelim = table.getTransferDelimiter(false);
            String lineDelim = table.getTransferDelimiter(true);
            
            for (int row = 0; row < rows.length; row++) {
                for (int col = 0; col < cols.length; col++) {
                    Object obj = table.getValueAt(rows[row], cols[col]);
                    String val = table.convertValueToString(obj);
                    plainBuf.append(val + itemDelim);
                }
                // we want a newline at the end of each line and not a tab
                plainBuf.delete(plainBuf.length() - itemDelim.length(), plainBuf.length()-1);
                plainBuf.append(lineDelim);
            }
            // remove the last newline
            plainBuf.deleteCharAt(plainBuf.length() - 1);
            return new ETableTransferable(plainBuf.toString());
        }
        
        return null;
    }
    
    public int getSourceActions(JComponent c) {
        return COPY;
    }
}
