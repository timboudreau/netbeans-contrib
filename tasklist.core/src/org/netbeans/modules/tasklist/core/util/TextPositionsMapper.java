package org.netbeans.modules.tasklist.core.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Maps offset in a string to line/column
 */
public class TextPositionsMapper {
    private String text;
    private int[] offsets;
    
    /**
     * Constructs a mapper
     *
     * @param text a text
     */
    public TextPositionsMapper(String text) {
        this.text = text;
        
        BufferedReader br = new BufferedReader(new StringReader(text));
        List offsets = new ArrayList();
        offsets.add(new Integer(0));

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\r') {
                if (i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                    offsets.add(new Integer(i + 1));
                } else {
                    offsets.add(new Integer(i + 1));
                }
            } else if (c == '\n') {
                offsets.add(new Integer(i + 1));
            }
        }
        
        this.offsets = new int[offsets.size()];
        for (int i = 0; i < this.offsets.length; i++) {
            this.offsets[i] = ((Integer) offsets.get(i)).intValue();
        }
    }
    
    /**
     * Returns line/column in the text for the specified offset.
     *
     * @param offset an offset. 0 based
     * @param position int[] {line, column}. Line and column are 0 based.
     */
    public void findPosition(int offset, int[] position) {
        assert offset >= 0 : "offset couldn't be negative"; // NOI18N
        
        int index = Arrays.binarySearch(offsets, offset);
        if (index >= 0) {
            position[0] = index;
            position[1] = 0;
        } else {
            index = -(index + 1);
            assert index != 0 : "offset couldn't be negative"; // NOI18N
            position[0] = index - 1;
            position[1] = offset - offsets[index - 1];
        }
    }

    /**
     * Returns the text
     *
     * @return text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Returns the text for the specified line. Line feed characters at the end
     * of a line will also be returned.
     *
     * @param line line number (0, 1, 2, ..)
     * @return text of a line
     */
    public String getLine(int line) {
        int offset = offsets[line];
        int offset2;
        if (offsets.length > line + 1) {
            offset2 = offsets[line + 1];
        } else {
            offset2 = text.length();
        }
        return text.substring(offset, offset2);
    }
}
