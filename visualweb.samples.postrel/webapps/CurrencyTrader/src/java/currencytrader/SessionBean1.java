/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package currencytrader;

import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import threadmanagement.ThreadPriority;
import threadmanagement.ThreadSynchronizer;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 * @author mbohm
 */
public class SessionBean1 extends AbstractSessionBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    // </editor-fold>
    
    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public SessionBean1() {
    }
    
    /**
     * <p>This method is called when this bean is initially added to
     * session scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * session scope.</p>
     *
     * <p>You may customize this method to initialize and cache data values
     * or resources that are required for the lifetime of a particular
     * user session.</p>
     */
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here
        
        // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("SessionBean1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here       

        //set up the data providers, cache, and synchronizer
        openPositionProvider = new PositionListDataProvider(openPositionList);
        openPositionProvider.setObjectType(Position.class);
        closedPositionProvider = new PositionListDataProvider(closedPositionList);
        closedPositionProvider.setObjectType(Position.class);
        initCacheAndSynchronizer();
    }
    
    /**
     * <p>This method is called when the session containing it is about to be
     * passivated.  Typically, this occurs in a distributed servlet container
     * when the session is about to be transferred to a different
     * container instance, after which the <code>activate()</code> method
     * will be called to indicate that the transfer is complete.</p>
     *
     * <p>You may customize this method to release references to session data
     * or resources that can not be serialized with the session itself.</p>
     */
    public void passivate() {
    }
    
    /**
     * <p>This method is called when the session containing it was
     * reactivated.</p>
     *
     * <p>You may customize this method to reacquire references to session
     * data or resources that could not be serialized with the
     * session itself.</p>
     */
    public void activate() {
        //re-create the cache and synchronizer
        initCacheAndSynchronizer();
    }
    
    /**
     * <p>This method is called when this bean is removed from
     * session scope.  Typically, this occurs as a result of
     * the session timing out or being terminated by the application.</p>
     *
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    public void destroy() {
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected ApplicationBean1 getApplicationBean1() {
        return (ApplicationBean1)getBean("ApplicationBean1");
    }
    
    /**
     * <p>Used to produce unique render ids within this session.</p>
     */
    private int initialRenderCounter;
    
    /**
     * <p>A <code>java.util.Map</code> whose keys are position ids and whose values are
     * <code>Position</code> instances representing open positions.</p>
     */
    private Map openPositionMap = new HashMap();
    
    /**
     * <p>A <code>java.util.List</code> containing <code>Position</code>
     * instances representing open positions.</p>
     */
    private List openPositionList = new ArrayList();
    
    /**
     * <p>A <code>java.util.Map</code> whose keys are position ids and whose values are
     * <code>Position</code> instances representing closed positions.</p>
     */
    private Map closedPositionMap = new HashMap();
    
    /**
     * <p>A <code>java.util.List</code> containing <code>Position</code>
     * instances representing closed positions.</p>
     */
    private List closedPositionList = new ArrayList();
    
    /**
     * <p>A <code>CurrencyPairCache</code> instance that caches
     * currency pair pricing information. In the event that the session is 
     * "passivated," there is no need to serialize the cache; the cache can simply be
     * re-created once the session is activated. Therefore, this field is marked 
     * <code>transient</code>.</p>
     */
    private transient CurrencyPairCache cache;
    
    /**
     * <p>A <code>ThreadSynchronizer</code> instance that handles the
     * thread synchronization for the session, permitting one thread per session
     * to execute at a time. Methods of the synchronizer are invoked from the <code>init</code>
     * and <code>destroy</code> methods of <code>Page1</code>. Because the
     * synchronizer holds references to <code>Thread</code> instances for the
     * various requests, it cannot be serialized; therefore, this field is marked
     * <code>transient</code>. In the event that the session is 
     * "passivated," the synchronizer is re-created once the session is activated.</p>
     */
    private transient ThreadSynchronizer synchronizer;
    
    /**
     * <p>A <code>TableDataProvider</code> implementation that holds a
     * list of <code>Position</code> instances representing open positions.</p>
     */
    private PositionListDataProvider openPositionProvider = new PositionListDataProvider();
    
    /**
     * <p>A <code>TableDataProvider</code> implementation that holds a
     * list of <code>Position</code> instances representing closed positions.</p>
     */
    private PositionListDataProvider closedPositionProvider = new PositionListDataProvider();
    
    /**
     * <p>Get a render id that is unique within this session. The render id
     * identifies a particular rendered page associated with the session.
     * For example, if a user opens Page1
     * in two different browser tabs, two different render ids will be generated.
     * Identifying each rendered page in this way is necessary because 
     * information regarding whether a price has 
     * visibly changed since the previous poll request is applicable to a 
     * particular rendered page rather than the entire session.</p>
     * @return A render id that identifies a particular rendered page within the session.
     */
    public String getInitialRenderId() {
        return String.valueOf(initialRenderCounter++);
    }

    /**
     * <p>Add a <code>Position</code> instance representing an open position
     * to <code>this.openPositionList</code> and <code>this.openPositionMap</code>.</p>
     * @param openPosition The open position to add.
     */
    public void addOpenPosition(Position openPosition) {
        this.openPositionList.add(openPosition);
        this.openPositionMap.put(openPosition.getId(), openPosition);
    }
    
    /**
     * <p>Remove a <code>Position</code> instance representing an open position
     * from <code>this.openPositionList</code> and <code>this.openPositionMap</code>.</p>
     * @param openPosition The open position to remove.
     */
    public void removeOpenPosition(Position openPosition) {
        this.openPositionList.remove(openPosition);
        this.openPositionMap.remove(openPosition.getId());
    }
    
    /**
     * <p>Add a <code>Position</code> instance representing a closed position
     * to <code>this.closedPositionList</code> and <code>this.closedPositionMap</code>.</p>
     * @param closedPosition The closed position to add.
     */
    public void addClosedPosition(Position closedPosition) {
        this.closedPositionList.add(closedPosition);
        this.closedPositionMap.put(closedPosition.getId(), closedPosition);
    }
    
    /**
     * <p>Get the <code>Position</code> instance representing the open position
     * with the supplied <code>id</code>.</p>
     * @param id The position id.
     * @return The position with the supplied id.
     */
    public Position getOpenPosition(String id) {
        return (Position)this.openPositionMap.get(id);
    }
    
    /**
     * <p>Get a <code>java.util.Iterator</code> to iterate over the
     * <code>Position</code> instances representing the open positions.</p>
     * @return A <code>java.util.Iterator</code> to iterate over the open positions.
     */
    public Iterator getOpenPositionsIterator() {
        return this.openPositionList.iterator();
    }
    
    /**
     * <p>Get formatted text representing the total profit figure sustained so 
     * far. The total profit figure is calculated by summing the profit figures 
     * of all closed positions.</p>
     * @return Formatted text representing the total profit figure sustained so 
     * far.
     */
    public String getTotalProfitText() {
        double totalProfit = 0;
        for (Iterator iter = this.closedPositionList.iterator(); iter.hasNext(); ) {
            Position closedPosition = (Position)iter.next();
            totalProfit += closedPosition.getProfit();
        }
        return NumberFormat.getCurrencyInstance().format(totalProfit);
    }
    
    /**
     * <p>Get a <code>CurrencyPairCache</code> instance that caches
     * currency pair pricing information for this session.</p>
     * @return A <code>CurrencyPairCache</code> instance that caches
     * currency pair pricing information for this session.
     */
    public CurrencyPairCache getCache() {
        return this.cache;
    }
    
    /**
     * <p>Get a <code>ThreadSynchronizer</code> instance that handles the
     * thread synchronization for the session, permitting one thread per session
     * to execute at a time. The synchronizer is used in the <code>init</code>
     * and <code>destroy</code> methods of <code>Page1</code>.</p>
     * @return A <code>ThreadSynchronizer</code> instance that handles the
     * thread synchronization for the session.
     */
    public ThreadSynchronizer getSynchronizer() {
        return this.synchronizer;
    }
    
    /**
     * <p>Get a <code>TableDataProvider</code> implementation that holds a
     * list of <code>Position</code> instances representing open positions.</p>
     * @return A <code>TableDataProvider</code> implementation that holds a
     * list of <code>Position</code> instances representing open positions.
     */
    public PositionListDataProvider getOpenPositionProvider() {
        return this.openPositionProvider;
    }
    
    /**
     * <p>Get a <code>TableDataProvider</code> implementation that holds a
     * list of <code>Position</code> instances representing closed positions.</p>
     * @return A <code>TableDataProvider</code> implementation that holds a
     * list of <code>Position</code> instances representing closed positions.
     */
    public PositionListDataProvider getClosedPositionProvider() {
        return this.closedPositionProvider;
    }
    
    /**
     * <p>Create instances of <code>CurrencyPairCache</code> and
     * <code>ThreadSynchronizer</code> and assign them to <code>this.cache</code>
     * and <code>this.synchronizer</code>, respectively. This method is invoked
     * from the <code>init</code> and <code>activate</code> methods of this class.</p>
     */
    private void initCacheAndSynchronizer() {
        this.cache = new CurrencyPairCache(getApplicationBean1().getService());
        //"gesture" requests (sent via the openButton, closeButton, or floatingCloseAmountDropDown)
        //have a higher priority than "poll" requests
        ThreadPriority[] priorities = {new ThreadPriority("gesture", true), new ThreadPriority("poll", false)};
        this.synchronizer = new ThreadSynchronizer(priorities, 10000L);   
    }
}
