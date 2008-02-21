
package org.netbeans.modules.fort.model.lang.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The class to implement position inside file
 * @author Andrey Gubichev
 */
public class Position  {
    private final int line;
    private final int col;
    private final int offset;

    /**
     * creates a new instance of Position
     */
    public Position() {
        this(0,0,0);
    }

    /**
     * creates a new instance of Position
     */
    public Position(int x, int y, int z) {
            this.line = x;
            this.col = y;
            this.offset = z;            
  
    }

    /**
     * creates a new instance of Position
     */
    Position(Position p) {
        this.line=p.getLine();
        this.col = p.getColumn();
        this.offset = p.getOffset();
    } 
    

    /**
     * @return offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return line
     */
    public int getLine() {
        return line;
    }

    /**
     * @return column
     */
    public int getColumn() {
        return col;
    }
 
    /**
     * @return string representation
     */   
    public String toString() {
        return "" + getLine() + ':' + getColumn() + '/' + getOffset();
    }

    /**
     * write to output
     */
    void toStream(DataOutput output) throws IOException {
        output.writeInt(line);
        output.writeInt(col);
        output.writeInt(offset);
    }

    
    Position(DataInput input) throws IOException {
        line = input.readInt();
        col = input.readInt();
        offset = input.readInt();
    }
}       
