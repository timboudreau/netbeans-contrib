
package org.netbeans.modules.fort.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.fort.model.lang.FOffsetable;
import org.netbeans.modules.fort.model.lang.impl.SimpleOffsetable;

/**
 * utility class for set of intervals
 * @author Andrey Gubichev
 */
public class IntervalSet<E extends FOffsetable> implements Iterable<E> {
       
    private List<E> intervals;
    /**
     * create a new instance of IntervalSet
     */
    public IntervalSet() {
        intervals = new ArrayList<E>();        
    }
    /**
     * create a new instance of IntervalSet
     */    
    public IntervalSet(int cap) {
        intervals = new ArrayList<E>(cap);        
    }
    /**
     * @return true if set is empty
     */    
    public boolean isEmpty() {
        return intervals.isEmpty();
    }
    /**
     * @return lower bound of intervals
     */       
    public int getLowerBound() {
        if (isEmpty())
            throw new IndexOutOfBoundsException("Empty"); // NOI18N
        
        return intervals.get(0).getStartOffset();
    }
    /**
     * @return upper bound of intervals
     */    
    public int getUpperBound() {
        if (isEmpty())
            throw new IndexOutOfBoundsException("Empty"); // NOI18N
        
        return intervals.get(intervals.size() - 1).getEndOffset();
    }
    /**
     * @return "from" bounds of intervals
     */     
    public IntervalSet<E> getFromBounds(int start, int end) {
        IntervalSet<E> result = new IntervalSet<E>();
        FOffsetable off = new SimpleOffsetable(start, end);
        
        for (E cur : this) {
            int res = 
                 IntersectionComparator.getInstance().compare(off, cur);
            
            if (res == 0) {
                result.add(cur);
            } else if (res < 0 ) {
                break;
            }
        }
        
        return result;
    }        
    /**
     * add to set
     */     
    public void add(E interval) {
                   
        int res = Collections.binarySearch(intervals, interval, 
                                           IntersectionComparator.getInstance());
        
        if (res >= 0)
            throw new IllegalArgumentException("Intersection"); // NOI18N
               
        intervals.add(-res - 1, interval);
    }
    
    /**
     * @return element at pos
     */             
    public E getElementAtPosition(int pos) {   
        int res = Collections.binarySearch(intervals, new DummyOffsetable(pos), 
                                  IntersectionComparator.getInstance());
        
        return (res < 0) ? null : intervals.get(res);
    }               
   /**
     * @return set-to-list
     */     
    public List<E> getList() {
        return Collections.<E>unmodifiableList(intervals);
    }
   /**
     * @return list iterator
     */     
    public Iterator<E> iterator() {
        return getList().iterator();
    }
    
    private static class IntersectionComparator implements Comparator<FOffsetable> {

        private final static Comparator<FOffsetable>  instance = new IntersectionComparator();

        public static Comparator<FOffsetable>  getInstance() {
            return instance;
        }

        public int compare(FOffsetable o1, FOffsetable o2) {
            if (o1.getEndOffset() < o2.getStartOffset())
                return -1;

            if (o2.getEndOffset() < o1.getStartOffset()) {
                return 1;
            }

            return 0;
        }  

        private IntersectionComparator() { }
     } 
    
    private static class DummyOffsetable implements FOffsetable {
        private int pos;
        
        public DummyOffsetable(int pos) {
            this.pos = pos;
        }
        
        public int getStartOffset() {
            return pos;
        }

        public int getEndOffset() {
            return pos;
        }
        
    }  
}
