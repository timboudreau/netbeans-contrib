

package org.netbeans.modules.fort.model.lang;

import java.util.List;

/**
 *
 * @author Andrey Gubichev
 */
public interface FExpression {
    public class Kind  {
      	protected Kind(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return value;
	}
	
	private final String value;
        
        public static final Kind EQUALS                = new Kind( "" );
        public static final Kind NOTEQUALS             = new Kind( "" );
        public static final Kind LOGICAL_AND           = new Kind( "" );
        public static final Kind LOGICAL_OR            = new Kind( "" );
        public static final Kind ASSIGNMENT_NORMAL    = new Kind( "" );
        public static final Kind LESSTHAN            = new Kind( "" );
        public static final Kind GREATERTHAN         = new Kind( "" );
        public static final Kind LESSTHANEQUALTO     = new Kind( "" );
        public static final Kind GREATERTHANEQUALTO  = new Kind( "" );
        
        //Primary expressions
        public static final Kind INTEGER_LITERAL                = new Kind( "INTEGER_LITERAL" ); 
        public static final Kind CHAR_LITERAL                   = new Kind( "CHAR_LITERAL" ); 
        public static final Kind REAL_LITERAL                   = new Kind( "REAL_LITERAL" ); 
        public static final Kind COMPLEX_LITERAL                = new Kind("COMPLEX_LITERAL");
        public static final Kind LOGICAL_LITERAL                = new Kind( "LOGICAL_LITERAL" ); 
        public static final Kind PRIMARY_BRACKETED              = new Kind( "PRIMARY_BRACKETED" ); // NOI18N
 

        public static final Kind MULTIPLICATIVE_MULTIPLY        = new Kind( "" );
        public static final Kind MULTIPLICATIVE_DIVIDE          = new Kind( "" );
        public static final Kind MULTIPLICATIVE_MODULUS         = new Kind( "" );
        public static final Kind ADDITIVE_PLUS                  = new Kind( "" );
        public static final Kind ADDITIVE_MINUS                 = new Kind( "" );

        //Postfix expression
        public static final Kind FUNCTIONCALL           = new Kind( "FUNCTIONCALL" ); // NOI18N

        
        public static final Kind SIMPLETYPE_INT		= new Kind( "" );
        public static final Kind SIMPLETYPE_REAL	= new Kind( "" );
        public static final Kind SIMPLETYPE_COMPLEX       = new Kind( "" );
        public static final Kind SIMPLETYPE_CHARACTER        = new Kind( "" );
        
    }
    /**
     * Gets this expression kind
     */
    Kind getKind();
    
    
    /**
     * Gets parent expression or null if this is no parent expression
     */
    FExpression getParent();
    
    
    /**
     * Gets this expression operands
     */
    List<FExpression> getOperands();
}
