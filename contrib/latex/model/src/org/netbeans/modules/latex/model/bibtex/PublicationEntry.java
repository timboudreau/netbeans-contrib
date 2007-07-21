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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.bibtex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PublicationEntry extends Entry {

    private static final String AUTHOR = "author";
    private static final String TITLE  = "title";
    
    private String type;
    private String tag;
    private Map<String, String> content;
    
    public PublicationEntry() {
        content = new HashMap<String, String>();
    }
    
    /** Getter for property type.
     * @return Value of property type.
     *
     */
    public java.lang.String getType() {
        return type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     *
     */
    public void setType(java.lang.String type) {
        this.type = type;
        firePropertyChange("type", null, null);
    }
    
    /** Getter for property tag.
     * @return Value of property tag.
     *
     */
    public java.lang.String getTag() {
        return tag;
    }
    
    /** Setter for property tag.
     * @param tag New value of property tag.
     *
     */
    public void setTag(java.lang.String tag) {
//        System.err.println("settings tag " + this.tag + ":" + tag);
        this.tag = tag;
        firePropertyChange("tag", null, null);
    }
    
    public String getAuthor() {
        return getValue(AUTHOR);
    }
    
    public void setAuthor(String author) {
        setValue(AUTHOR, author);
    }
    
    public String getTitle() {
        return getValue(TITLE);
    }
    
    public void setTitle(String author) {
        setValue(TITLE, author);
    }

    protected String getValue(String key) {
        String value = getContent().get(key);
        
        if (value == null)
            return "";
        else
            return value;
    }
    
    protected void setValue(String key, String value) {
        getContent().put(key, value);
        firePropertyChange(key, null, null);
    }
    
    /** Getter for property content.
     * @return Value of property content.
     *
     */
    public Map<String, String> getContent() {
        return content;
    }
    
    /** Setter for property content.
     * @param content New value of property content.
     *
     */
    public void setContent(Map<String, String> content) {
        //well, not sure how much effective is this..
        Collection<String> properties = new ArrayList<String>();
        
        properties.addAll(content.keySet());
        this.content = content;
        
        for (String key : properties) {
            firePropertyChange(key, null, null);
        }
        
        firePropertyChange("content", null, null);
    }
    
    private void escape(StringBuffer buffer, String value) {
        int index = 0;
        int currentStart = (-1);
        
        buffer.append("\"");
        
        while ((currentStart = value.indexOf('#', index)) != (-1)) {
            int currentEnd = value.indexOf('#', currentStart + 1);
            
            if (currentEnd == (-1))
                break;
            
            buffer.append(value.substring(index, currentStart));
            buffer.append("\" # ");
            buffer.append(value.substring(currentStart + 1, currentEnd));
            buffer.append(" # \"");
            
            index = currentEnd + 1;
        }
        
        buffer.append(value.substring(index));
        buffer.append("\"");
    }
    
    public String writeOut() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("@");
        sb.append(getType());
        sb.append("{");
        sb.append(getTag());
        sb.append(",\n"); //newlines are CORRECT, the result will be put into a NB document!
        
        for (String key : getContent().keySet()) {
            String value = getContent().get(key);
            
            sb.append("    "); //indentation :-)
            sb.append(key);
            sb.append(" = ");
            escape(sb, value); 
            sb.append(",\n");
        }
        
        sb.append("}\n");
        
        return sb.toString();
    }
    
    public void update(Entry entry) {
        assert getClass().equals(entry.getClass());
        
        PublicationEntry pEntry = (PublicationEntry) entry;
        
        setType(pEntry.getType());
        setTag(pEntry.getTag());
        setContent(pEntry.getContent());
    }
    
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass()))
            return false;
        
        PublicationEntry pEntry = (PublicationEntry) o;
        
        return type.equals(pEntry.type) && tag.equals(pEntry.tag) && content.equals(pEntry.content);
    }
    
    public String toString() {
        return "[PublicationEntry, content=" + writeOut() + "; start=" + getStartPosition() + "\n; end=" + getEndPosition() + "\n]";
    }
}
