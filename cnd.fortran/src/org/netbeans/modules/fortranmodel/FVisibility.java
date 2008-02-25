

package org.netbeans.modules.fortranmodel;

/**
 * Represents member / inheritance visibility
 * @author Andrey Gubichev
 */
public class FVisibility {
    protected FVisibility(String value) {
       this.value=value;
    }

    public String getValue() {
	return value;
    }
	
    public String toString() {
	return value;
    }	
    private final String value;
    
    public static final FVisibility PUBLIC    = new FVisibility("public"); // NOI18N
   
    public static final FVisibility PRIVATE   = new FVisibility("private"); // NOI18N

    public static final FVisibility NONE      = new FVisibility("none"); // NOI18N

}
