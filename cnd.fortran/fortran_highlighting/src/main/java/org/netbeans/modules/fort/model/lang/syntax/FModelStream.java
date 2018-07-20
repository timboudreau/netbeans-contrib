
package org.netbeans.modules.fort.model.lang.syntax;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.fort.model.lang.FOffsetableDeclaration;

/**
 * fortran model stream
 * @author Andrey Gubichev
 */
public class FModelStream implements Iterable<FOffsetableDeclaration> {
     
    private final List<FOffsetableDeclaration> elements;
    /**
     * creates a new instance of FModelStream
     */    
    public FModelStream(List<FOffsetableDeclaration> in) {
        elements = Collections.unmodifiableList(in);
    }
    /**
     * @return elements' iterator
     */       
    public Iterator<FOffsetableDeclaration> iterator() {
        return elements.iterator();
    }
    /**
     * @return elements(index)
     */    
    public FOffsetableDeclaration get(int index) {
        return elements.get(index);
    }
    
    /**
     * @return size of elements
     */
   public int size() {
        return elements.size();
    }
 
    /**
     * "equals" implementation
     */
    public boolean equals(Object obj) {
        if (obj instanceof FModelStream) {
            return elements.equals(((FModelStream) obj).elements);
        }
        return false;
    }
    
    /**
     * @return hashcode
     */
    public int hashCode() {
        return elements.hashCode();
    }
    
    /**
     * for-each implementation
     */
    public void forEach(ElementVisitor visitor, FFilter filter) {
        for (FOffsetableDeclaration el : this) {
            if (filter.accept(el)) {
                if (!visitor.visit(el)) 
                    break;                  
            }
        }
    }
    /**
     * for-each implementation
     */    
    public void forEach(ElementVisitor visitor) {
        forEach(visitor, TrueFilter.instance);                        
    }        
    
    
    // Inner classes 
       
    interface ElementVisitor {
        boolean visit(FOffsetableDeclaration el);
    }
        
     interface FFilter {
        boolean accept(FOffsetableDeclaration el);          
    }        
            
     static class ClazzFilter implements FFilter {        
        private Class<?> cl;
        
        public boolean accept(FOffsetableDeclaration el) {
            return el.getClass() == cl;
        }       
        
        public ClazzFilter(Class<?> cl) {
            this.cl = cl;
        }
    }
                 
     static final class TrueFilter implements FFilter {
        public static final FFilter instance = new TrueFilter();        
        
        public boolean accept(FOffsetableDeclaration el) {
            return true;
        }
        
        private TrueFilter() { }        
    }
}

