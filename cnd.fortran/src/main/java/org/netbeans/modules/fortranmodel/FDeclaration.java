

package org.netbeans.modules.fortranmodel;

/**
 *
 * @author Andrey Gubichev
 */
public interface FDeclaration extends FQualifiedNamedElement, FScopeElement{
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
    }
        public static final Kind VARIABLE = new Kind("VARIABLE"); // NOI18N
     //   public static final Kind VARIABLE_DEFINITION = new Kind("VARIABLE_DEFINITION"); // NOI18N
        public static final Kind TYPE = new Kind("TYPE");
        public static final Kind FUNCTION = new Kind("FUNCTION"); // NOI18N
        public static final Kind FUNCTION_DEFINITION = new Kind("FUNCTION_DEFINITION"); // NOI18N
        public static final Kind SUBROUTINE = new Kind("SUBROUTINE"); // NOI18N
        public static final Kind SUBROUTINE_DEFINITION = new Kind("SUBROUTINE_DEFINITION"); // NOI18N

    
    Kind getKind();
    String getName();

}
