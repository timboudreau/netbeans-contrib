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

package currencyservice;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class simulates a client stub for an external currency service on the network.
 * The service exposes a very simple public API that provides the currency pair names
 * and prices.</p>
 * @author mbohm
 */
public class CurrencyService {
    
    /**
     * <p>A <code>java.util.Map</code> whose keys are currency pair names and whose values
     * are <code>AuthoritativeCurrencyPair</code> instances.</p>
     */
    private Map pairMap;
    
    /**
     * <p>An array of the currency pair names.</p>
     */
    private String[] pairNames;
    
    /**
     * <p>A timestamp (in milliseconds) corresponding to the most recent invocation 
     * of the <code>getPrices</code> method.</p>
     */
    private long lastAccess;
    
    /**
     * <p>A <code>CurrencyService.CurrencyModulator</code> instance we use
     * to modulate the prices.</p>
     */
    private CurrencyModulator modulator;
    
    /**
     * <p>A separate thread we use to modulate the prices.</p>
     */
    private Thread modulationThread;
    
    /**
     * <p>Construct a <code>CurrencyService</code> instance. Here we generate the
     * <code>AuthoratativeCurrencyPair</code> instances and use them to populate
     * <code>this.pairMap</code> and <code>this.pairNames</code>. We also 
     * construct and maintain a new instance of 
     * <code>CurrencyService.CurrencyModulator</code>, which implements
     * <code>Runnable</code>, but we do not yet run it.</p>
     * @param modulationInterval The interval in milliseconds to let elapse between 
     * price modulations.
     * @param stopModulationWithoutAccess If this interval in milliseconds 
     * elapses without an invocation of <code>getPrices</code>, then stop modulating the prices.
     */
    public CurrencyService(long modulationInterval, long stopModulationWithoutAccess) {
        AuthoratativeCurrencyPair[] pairs = generateAuthoratativeCurrencyPairs();
        this.pairMap = new HashMap();
        this.pairNames = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            this.pairNames[i] = pairs[i].getName();
            this.pairMap.put(this.pairNames[i], pairs[i]);
        }
        this.modulator = new CurrencyModulator(pairs, modulationInterval, stopModulationWithoutAccess);
    }
    
    /**
     * <p>Get an array of length <code>2</code> containing the sell and buy 
     * prices, respectively.</p>
     * @return The sell and buy prices, respectively.
     */
    public double[] getPrices(String pairName) {
        registerAccess();
        return ((AuthoratativeCurrencyPair)pairMap.get(pairName)).getPrices();
    }
    
    /**
     * <p>Get an array of all the currency pair names, such as "EUR/USD".</p>
     * @return An array of all the currency pair names.
     */
    public String[] getPairNames() {
        return this.pairNames;
    }
    
    /**
     * <p>Set <code>this.lastAccess</code> to the current time in 
     * milliseconds. Also, if <code>this.modulationThread</code> is not started, 
     * start it; this will cause the prices to modulate continuously.</p>
     */
    private void registerAccess() {
        this.lastAccess = System.currentTimeMillis();
        if (this.modulationThread == null) {
            startModulation();
        }
    }
    
    /**
     * <p>Start <code>this.modulationThread</code>; this will cause the prices 
     * to modulate continuously. This method contains logic to synchronize on
     * <code>this.modulator</code>, to ensure that only one thread for 
     * price modulation purposes runs at a time.</p>
     */
    private void startModulation() {
        synchronized (this.modulator) {
            if (this.modulationThread != null) {
                return;
            }
            this.modulationThread = new Thread(this.modulator);
            this.modulationThread.start();
        }
    }
    
    /**
     * <p>Generate a new array of <code>AuthoratativeCurrencyPair</code> 
     * instances.</p>
     * @return A new array of <code>AuthoratativeCurrencyPair</code> 
     * instances.
     */
    private AuthoratativeCurrencyPair[] generateAuthoratativeCurrencyPairs() {
        AuthoratativeCurrencyPair[] pairs = new AuthoratativeCurrencyPair[] {
            new AuthoratativeCurrencyPair("EUR/USD", 1.2752, 1.2755),
            new AuthoratativeCurrencyPair("USD/JPY", 112.31, 112.35, 1.0, 0.1),
            new AuthoratativeCurrencyPair("GBP/USD", 1.8601, 1.8606),
            new AuthoratativeCurrencyPair("USD/CHF", 1.2242, 1.2247),
            new AuthoratativeCurrencyPair("AUD/USD", 0.7583, 0.7587),
            new AuthoratativeCurrencyPair("USD/CAD", 1.1054, 1.1059),
            new AuthoratativeCurrencyPair("NZD/USD", 0.6340, 0.6344),
        };
        return pairs;
    }
    
    /**
     * <p>Private class used to modulate the prices of the 
     * <code>AuthoratativeCurrencyPair</code> instances. Contains the 
     * <code>Runnable</code> logic that runs in 
     * <code>this.modulationThread</code>.</p>
     */
    private class CurrencyModulator implements Runnable {
        
        /**
         * <p>An array of the <code>AuthoratativeCurrencyPair</code> instances.</p>
         */
        private AuthoratativeCurrencyPair[] pairs;
        
        /**
         * <p>The interval in milliseconds to let elapse between 
         * price modulations.</p>
         */
        private long modulationInterval;
     
        /**
         * <p>If this interval in milliseconds elapses without an invocation 
         * of <code>CurrencyService.this.getPrices</code>, then stop modulating the prices.</p>
         */
        private long stopModulationWithoutAccess;
        
        /**
         * <p>Construct a <code>CurrencyModulator</code> instance.</p>
         * @param pairs An array of the <code>AuthoratativeCurrencyPair</code> instances.
         * @param modulationInterval The interval in milliseconds to let elapse between 
         * price modulations.
         * @param stopModulationWithoutAccess If this interval in milliseconds 
         * elapses without an invocation of <code>CurrencyService.this.getPrices</code>, then stop modulating the prices.
         */
        CurrencyModulator(AuthoratativeCurrencyPair[] pairs, long modulationInterval, long stopModulationWithoutAccess) {
            this.pairs = pairs;
            this.modulationInterval = modulationInterval;
            this.stopModulationWithoutAccess = stopModulationWithoutAccess;
        }
        
        /**
         * <p>The <code>Runnable</code> logic that runs in 
         * <code>CurrencyService.this.modulationThread</code>. This method contains logic to synchronize on
         * the <code>this</code> instance, so that only one thread for 
         * modulation purposes runs at a time. First we check if the
         * <code>this.stopModulationWithoutAccess</code> interval has elapsed since the
         * last time the <code>CurrencyService.this.getPrices</code> method was invoked, and, if so, we set 
         * <code>CurrencyService.this.modulationThread</code> to <code>null</code> and exit the 
         * method. Otherwise, we invoke the <code>modulate</code> method on each 
         * <code>AuthoratativeCurrencyPair</code> instance and then sleep for
         * the duration determined by <code>this.modulationInterval</code>. This sequence
         * of operations repeats inside a <code>while</code> loop so that the
         * prices modulate continuously.</p>
         */
        public void run() {
            synchronized (this) {
                while (true) {
                    long millisSinceLastLookup = System.currentTimeMillis() - CurrencyService.this.lastAccess;
                    if (millisSinceLastLookup > this.stopModulationWithoutAccess) {
                        break;
                    }
                    for (int i = 0; i < this.pairs.length; i++) {
                        this.pairs[i].modulate();
                    }
                    try {
                        Thread.sleep(this.modulationInterval);
                    }
                    catch (InterruptedException ie) {
                        ie.printStackTrace();
                        break;
                    }
                }
                CurrencyService.this.modulationThread = null;
            }
        }
    }
}
