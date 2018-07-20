
package org.netbeans.modules.fort.model.lang.impl;

import org.netbeans.modules.fort.model.lang.FOffsetable;

/**
 * simple implementation of Fortran offsetable
 * @author Andrey Gubichev
 */
public class SimpleOffsetable implements FOffsetable {
        
    private int start, end;
    
    /**
     * creates a new instance of Offsetable
     */
    public SimpleOffsetable(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * creates a new instance of Offsetable
     */  
    public SimpleOffsetable(FOffsetable off) {
        this(off.getStartOffset(), off.getEndOffset());
    }
    
    /**
     * @return start offset
     */
    public int getStartOffset() {
        return start;
    }

    /**
     * @return end offset
     */
    public int getEndOffset() {
        return end;
    }
}
