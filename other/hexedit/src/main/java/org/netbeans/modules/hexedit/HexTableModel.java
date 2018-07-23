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
/*
 * HexTableModel.java
 *
 * Created on April 27, 2004, 12:02 AM
 */

package org.netbeans.modules.hexedit;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author  tim
 */
class HexTableModel implements TableModel {
   private int colCount;
    private int mode = MODE_BYTE;
    private FileChannel channel;
    
    //Note these modes also correspond to byte counts
    public static final int MODE_BYTE = 1;
    public static final int MODE_SHORT = 2;
    public static final int MODE_INT = 4;
    public static final int MODE_LONG = 8;
    public static final int MODE_CHAR = 256;

    //Value if in a mode where file size % bytes per element != 0, used in the
    //final cell; this string will show in the statusbar
    public static final Object PARTIAL_VALUE = Util.getMessage("PARTIAL_VALUE"); //NOI18N

    private long length;
    private boolean readOnly;

    /** Creates a new instance of HexTableModel */
    public HexTableModel(int colCount, FileChannel channel, long length, boolean readOnly) {
        this.colCount = colCount;
        this.channel = channel;
        this.length = length;
        this.readOnly = readOnly;
    }
    
    private ByteBuffer contents = null;
    private long mappedRange = 0;
    private static int MIN_MAP_SIZE = 1024;
    private ByteBuffer readBuffer(int start, int byteCount) throws IOException {
        if (contents == null || start + byteCount > mappedRange) {
            long prevMappedRange = mappedRange;
            
            mappedRange = Math.min (length, start + Math.max(byteCount, MIN_MAP_SIZE));
            if (prevMappedRange == mappedRange) {
                //We're at the end of the file, more data is being requested 
                //than there is
                return contents;
            }
            try {
                contents = channel.position(0).map(readOnly ? FileChannel.MapMode.READ_ONLY :
                        FileChannel.MapMode.READ_WRITE, 0, mappedRange);
            } catch (IOException ioe) {
                throw ioe;
            }
        }
        contents.position (0);
        return contents;
    }  

    public Class getColumnClass(int param) {
        switch (mode) {
            case MODE_BYTE :
                return Byte.class;
            case MODE_CHAR :
                return Character.class;
            case MODE_INT :
                return Integer.class;
            case MODE_LONG :
                return Long.class;
            case MODE_SHORT :
                return Short.class;
            default :
                throw new IllegalStateException();
        }
    }
    
    public int getColumnCount() {
        return colCount;
    }
    
    public void setColumnCount(int value) {
        if (colCount != value) {
            colCount = value;
            fire();
        }
    }
    
    public String getColumnName(int param) {
        return Integer.toString(param);
    }

    public int getRowCount() {
        int result =  ((int) length / bytesPerElement()) / getColumnCount();
        if (((int) length / mode) % getColumnCount() != 0) {
            result++;
        }
        return result;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setMode (int mode) {
        switch (mode) {
            case MODE_BYTE :
            case MODE_CHAR :
            case MODE_INT :
            case MODE_LONG :
            case MODE_SHORT :
                break;
            default :
                throw new IllegalArgumentException ("Unknown mode: " + mode);  //NOI18N
        }
        if (this.mode != mode) {
            this.mode = mode;
            fire();
        }
    }

    public int getMode() {
        return mode;
    }

    public Object getValueAt(int row, int column) {
        
        int seekTo = bytesPerElement() * ((row * getColumnCount()) + column);
        ByteBuffer buf = null;
        try {
            buf = readBuffer (seekTo, bytesPerElement());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        
        if (seekTo >= buf.limit()) {
            return null;
        }
        buf.position (seekTo);
        if (buf.limit() - seekTo < bytesPerElement()) {
            //use special object for partial value when in, say, Integer
            //view but with a file whose size % 4 is not 0
            return PARTIAL_VALUE;
        }

        switch (mode) {
            case MODE_BYTE : return new Byte(buf.get());
            
            case MODE_INT : return new Integer(buf.getInt());
                
            case MODE_LONG : return new Long(buf.getLong());
            
            case MODE_CHAR : return new Character ((char)buf.get());
            
            case MODE_SHORT : return new Short (buf.getShort());
                
            default :
                throw new IllegalStateException();
        }
    }

    public byte[] getBytesForRow(int row) {
        int seekTo = bytesPerElement() * (row * getColumnCount());
        ByteBuffer buf = null;
        try {
            buf = readBuffer (seekTo, bytesPerElement());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new byte[0];
        }
        
        int size = Math.min (buf.limit() - seekTo, getColumnCount() * bytesPerElement());
        if (size < 0) {
            return new byte[0];
        }
        byte[] result = new byte[size];
        buf.position (seekTo);
        buf.get(result);
        return result;
    }

    public int bytesPerElement() {
        int result = mode;
        if (result == MODE_CHAR) {
            result = 1;
        }
        return result;
    }
    
    public boolean isCellEditable(int row, int col) {
        if (!readOnly) {
            if (bytesPerElement() > 1) {
                Object o = getValueAt (row, col);
                return o != PARTIAL_VALUE;
            }
        }
        return !readOnly;
    }
    
    public synchronized void removeTableModelListener(TableModelListener l) {
        listeners.remove (l);
    }
    
    public synchronized void addTableModelListener(TableModelListener l) {
        listeners.add (l);
    }
    
    private synchronized void fire() {
        TableModelEvent tme = new TableModelEvent (this);
        java.util.List l = Collections.unmodifiableList(listeners);
        for (Iterator i=l.iterator(); i.hasNext();) {
            TableModelListener tlm = (TableModelListener) i.next();
            tlm.tableChanged(tme);
        }
    }
    
    private synchronized void fire(TableModelEvent tme) {
        java.util.List l = Collections.unmodifiableList(listeners);
        for (Iterator i=l.iterator(); i.hasNext();) {
            TableModelListener tlm = (TableModelListener) i.next();
            tlm.tableChanged(tme);
        }
    }    
    
    public void setValueAt(Object obj, int row, int col) {
        if (isReadOnly()) {
            return;
        }
        Object old = getValueAt (row, col);
        if (obj == null || ((old != null && obj != null) && obj.equals(old))) {
            return;
        }
        
//        System.err.println("SetValueAt " + row + "," + col + " to " + obj + "(" + obj.getClass() + ") from " + old + "(" + old.getClass() + ")");
        
        int seekTo = bytesPerElement() * ((row * getColumnCount()) + col);
        try {
            ByteBuffer buf = readBuffer (seekTo, Util.byteCountFor(obj.getClass()));
            buf.position (seekTo);

            if (obj instanceof Long) {
                buf.putLong(((Long) obj).longValue());
            } else if (obj instanceof Integer) {
                buf.putInt(((Integer) obj).intValue());
            } else if (obj instanceof Short) {
                buf.putShort(((Short) obj).shortValue());
            } else if (obj instanceof Byte) {
                buf.put (((Byte) obj).byteValue());
            } else if (obj instanceof Character) {
                buf.put ((byte) ((Character) obj).charValue());
            }
        
            FileLock lock = channel.tryLock(seekTo, bytesPerElement(), false);
            ((MappedByteBuffer) buf).force();
            lock.release();
            
            TableModelEvent tme = new TableModelEvent (this, row, row, col, TableModelEvent.UPDATE);
            fire(tme);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private java.util.List listeners = new ArrayList();
}
