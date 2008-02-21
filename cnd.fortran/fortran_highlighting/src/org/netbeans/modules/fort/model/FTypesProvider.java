
package org.netbeans.modules.fort.model;

import java.io.Reader;
import java.util.List;

/**
 * interface for type resolvers
 * @author Andrey Gubichev
 */
public interface FTypesProvider {
    /**
     * resolves types in given source
     */
    ResolverResult resolve(Reader source);
    /**
     * @return list of resolved types
     */
    List<FTypesEntry> getFTypes();
    
    /**
     * accuracy of resolvers
     */
    public enum ResolverMetric implements Comparable<ResolverMetric> {
        MAY_BE, PROBABLY, EXACTLY;                      
    }
    
    /**
     * results for resolver
     */
    public interface ResolverResult {
        FModelProvider getModelProvider();  
        FSyntaxProvider getSyntaxProvider();  
        
        ResolverMetric getAccuracy();
    }

    public interface FTypesEntry {
        FModelProvider getModelProvider();
        List<FSyntaxProvider> getSyntaxProviders();
    }
     
}
