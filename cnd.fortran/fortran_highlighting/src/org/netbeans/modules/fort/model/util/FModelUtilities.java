
package org.netbeans.modules.fort.model.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;

/**
 * utility class
 * @author Andrey Gubichev
 */
public class FModelUtilities {

    /**
     * 
     * @return true if it is a compound statement
     */
    public static boolean checkCompound(FCompoundStatement comp) {
        int startEl, endEl;
        int prev = -1;
        
        for (FCompoundStatement el : comp.getStatements()) {
            startEl = el.getStartOffset();
            endEl = el.getEndOffset();
            
            if (startEl > endEl || prev > startEl)
                return false;
            
            prev = endEl;
        }
        
        return true;
    }   
    
    
    /**
     * finds at given position in statement
     */
    public static FCompoundStatement findAt(FCompoundStatement comp, int pos) {
        assert comp.getStatements() instanceof RandomAccess;
        assert checkCompound(comp);        
        
        int res = Collections.binarySearch(comp.getStatements(), new DummyCompound(pos), 
                                  CompoundCorparator.getInstance());
        
        return (res < 0) ? null : comp.getStatements().get(res);
    }       
    
    /**
     * finds at given position
     */
    public static FCompoundStatement findAtRecursive(FCompoundStatement comp, int pos) {
        FCompoundStatement res = findAt(comp, pos);
        
        if (res == null) {
            return comp;
        }
        
        return findAtRecursive(res, pos);       
    }
    
    /**
     * visit all statements in compound
     */
    public static void walkCompound(FCompoundStatement comp, FVisitor visitor) {
        for (FCompoundStatement el : comp.getStatements()) {
            if (visitor.visit(el))
                walkCompound(el, visitor);
            else
                return;
        }
    }        
    
    /**
     * simple visitor
     */
    public interface FVisitor {
        /**
         * visit
         */
        boolean visit(FCompoundStatement comp);
    }
    
    /**
     * simple filter
     */
    public interface FFilter<T extends FCompoundStatement> {
        boolean accept(T el);        
    }

    private static class DummyCompound implements FCompoundStatement {
        private int pos;
        
        public DummyCompound(int pos) {
            this.pos = pos;
        }
        
        public int getStartOffset() {
            return pos;
        }

        public int getEndOffset() {
            return pos;
        }

        public List<FCompoundStatement> getStatements() {
            return Collections.<FCompoundStatement>emptyList();
        }        
    }
    
    private static class CompoundCorparator implements Comparator<FCompoundStatement> {
    
        private final static Comparator<FCompoundStatement>  instance = new CompoundCorparator();
        
        public static Comparator<FCompoundStatement>  getInstance() {
            return instance;
        }
        
        public int compare(FCompoundStatement o1, FCompoundStatement o2) {
            if (o1.getEndOffset() < o2.getStartOffset())
                return -1;
            
            if (o2.getEndOffset() < o1.getStartOffset()) {
                return 1;
            }
            
            return 0;
        }  
        
        private CompoundCorparator() { }
    }   
    
}
