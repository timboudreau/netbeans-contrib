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

import currencyservice.CurrencyService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Instances of this class are held in session scope to cache currency pair
 * pricing information.
 * Many of the methods of this class accept a <code>renderId</code>
 * parameter. This is because prices are cached separately for each rendered page
 * associated with the session (for instance, if a user opens multiple 
 * browser tabs). Caching prices on a per-page basis is necessary because 
 * information regarding whether a price has visibly changed since the previous 
 * poll request is applicable to a particular rendered page rather than the entire 
 * session. This class also includes a convenience method to bypass the cached
 * prices and retrieve a currency pair's real-time prices.</p>
 * @author mbohm
 */
public class CurrencyPairCache {
    
    /**
     * <p>The simulated client stub for an external currency service on the 
     * network.</p>
     */
    private CurrencyService service;
    
    /**
     * <p>An array of the currency pair names, such as "EUR/USD".</p>
     */
    private String[] pairNames;
    
    /**
     * <p>A <code>java.util.Map</code> with a <code>renderId</code> as the key and 
     * a <code>CurrencyPairCache.Cache</code> instance as the value.</p>
     */
    private Map cacheMap;
    
    /**
     * <p>Construct a <code>CurrencyPairCache</code> instance.</p>
     * @param service The simulated client stub for an external currency service on the 
     * network.
     */
    public CurrencyPairCache(CurrencyService service) {
        this.service = service;
        this.pairNames = service.getPairNames();
        this.cacheMap = new HashMap();
    }
    
    /**
     * <p>Bypass cached prices and get the real-time information for a currency
     * pair directly from the currency service.</p>
     * @param pairName The currency pair name, such as "EUR/USD".
     * @return A new <code>CurrencyPair</code> instance containing real-time 
     * information obtained directly from the currency service.
     */
    public CurrencyPair getRealTimeCurrencyPair(String pairName) {
        double[] prices = service.getPrices(pairName);
        return new CurrencyPair(pairName, prices[0], prices[1]);
    }
    
    /**
     * <p>Obtain fresh pricing data from the currency service for all currency
     * pairs and update the cached data for the rendered page represented 
     * by <code>renderId</code>.</p>
     * @param renderId A <code>renderId</code> representing a rendered page 
     * associated with the current session.
     */
    public void update(String renderId) {
        Cache cache = (Cache)cacheMap.get(renderId);
        if (cache == null) {
            initCache(renderId);
            return;
        }
        cache.update();
    }
    
    /**
     * <p>Get a <code>java.util.List</code> containing the 
     * <code>CurrencyPair</code> instances cached for the rendered page represented
     * by <code>renderId</code>.</p>
     * @param renderId A <code>renderId</code> representing a rendered page 
     * associated with the current session.
     * @return A <code>java.util.List</code> containing the 
     * <code>CurrencyPair</code> instances cached for <code>renderId</code>.
     */
    public List getCurrencyPairList(String renderId) {
        Cache cache = getCache(renderId);
        return cache.getPairList();
    }
    
    /**
     * <p>Get the <code>CurrencyPair</code> instance with the supplied
     * name that is cached for the rendered page represented
     * by <code>renderId</code>.</p>
     * @param renderId A <code>renderId</code> representing a rendered page 
     * associated with the current session.
     * @param pairName The currency pair name, such as "EUR/USD".
     * @return The <code>CurrencyPair</code> instance with the supplied
     * name that is cached for <code>renderId</code>.
     */
    public CurrencyPair getCurrencyPair(String renderId, String pairName) {
        Cache cache = getCache(renderId);
        return cache.getPair(pairName);
    }
    
    /**
     * <p>Get the <code>CurrencyPairCache.Cache</code> instance for the 
     * rendered page represented by <code>renderId</code>, creating one if
     * necessary.</p>
     * @param renderId A <code>renderId</code> representing a rendered page 
     * associated with the current session.
     * @return The <code>CurrencyPairCache.Cache</code> instance for 
     * <code>renderId</code>.
     */
    private Cache getCache(String renderId) {
        Cache cache = (Cache)cacheMap.get(renderId);
        if (cache == null) {
            cache = initCache(renderId);
        }
        return cache;
    }
    
    /**
     * <p>Create and return a <code>CurrencyPairCache.Cache</code> instance for the 
     * rendered page represented by <code>renderId</code> and store that instance
     * in <code>this.cacheMap</code>.</p>
     * @param renderId A <code>renderId</code> representing a rendered page 
     * associated with the current session.
     * @return A new <code>CurrencyPairCache.Cache</code> instance for 
     * <code>renderId</code>.
     */
    private Cache initCache(String renderId) {
        Cache cache = new Cache();
        this.cacheMap.put(renderId, cache);
        return cache;
    }
    
    /**
     * <p>An internal cache object containing <code>CurrencyPair</code> 
     * instances for a particular rendered page associated with the current 
     * session.</p>
     */
    private class Cache {
        
        /**
         * <p>A <code>java.util.Map</code> whose keys are currency pair names and whose values are 
         * <code>CurrencyPair</code> instances.</p>
         */
        private Map pairMap;
        
        /**
         * <p>A <code>java.util.List</code> of <code>CurrencyPair</code> instances.</p>
         */
        private List pairList;
        
        /**
         * <p>Construct a <code>CurrencyPairCache.Cache</code> instance. We contact
         * the currency service and obtain prices for all currency pairs and 
         * populate <code>this.pairMap</code> and <code>this.pairList</code> with 
         * new <code>CurrencyPair</code> instances.</p>
         */
        public Cache() {
            this.pairMap = new HashMap();
            this.pairList = new ArrayList();
            String[] pairNames = CurrencyPairCache.this.pairNames;
            for (int i = 0; i < pairNames.length; i++) {
                double[] prices = CurrencyPairCache.this.service.getPrices(pairNames[i]);
                CurrencyPair pair = new CurrencyPair(pairNames[i], prices[0], prices[1]);
                this.pairMap.put(pairNames[i], pair);
                this.pairList.add(pair);
            }
        }
        
        /**
         * <p>Get a <code>java.util.List</code> containing 
         * <code>CurrencyPair</code> instances.</p>
         * @return A <code>java.util.List</code> containing 
         * <code>CurrencyPair</code> instances.
         */
        public List getPairList() {
            return this.pairList;
        }
        
        /**
         * <p>Get the <code>CurrencyPair</code> instance with the supplied
         * name.</p>
         * @param pairName The currency pair name, such as "EUR/USD".
         * @return The <code>CurrencyPair</code> instance with the supplied
         * name.
         */
        public CurrencyPair getPair(String pairName) {
            return (CurrencyPair)this.pairMap.get(pairName);
        }
        
        /**
         * <p>Obtain current pricing information for all currency pairs
         * from the currency service and update the 
         * <code>CurrencyPair</code> instances contained herein.</p>
         */
        public void update() {
            String[] pairNames = CurrencyPairCache.this.pairNames;
            for (int i = 0; i < pairNames.length; i++) {
                double[] prices = CurrencyPairCache.this.service.getPrices(pairNames[i]);
                CurrencyPair pair = getPair(pairNames[i]);
                pair.setPrices(prices[0], prices[1]);
            }
        }
    }
}
