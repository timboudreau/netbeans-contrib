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

import java.text.NumberFormat;
import java.util.Random;

/**
 * <p>This class is used internally by the <code>CurrencyService</code> class to hold the current
 * prices of a currency pair. A currency pair represents a pair of currencies, 
 * such as "EUR/USD". This class should not be confused with the 
 * <code>CurrencyPair</code> class in the <code>currencytrader</code> package.
 * Two separate classes are used because this one internally helps
 * <code>CurrencyService</code> implement its public API, while 
 * <code>CurrencyPair</code> is used by the web application itself to hold 
 * pricing information for its purposes.</p>
 * @author mbohm
 */
class AuthoratativeCurrencyPair {
    
    /**
     *  <p>Generates random numbers and booleans for modulating prices.</p>
     */
    private static Random random = new Random();
    
    /**
     * <p>Formats prices to have no more than four fractional digits.</p>
     */
    private static NumberFormat currencyPairFormat = NumberFormat.getInstance();
    
    /**
     * <p>An array of length <code>2</code> containing the sell and buy 
     * prices, respectively.</p>
     */
    private double[] prices;
    
    /**
     * <p>The name of the pair, such as "EUR/USD".</p>
     */
    private String name;
    
    /** 
     * <p>The percentage chance we will choose to change the prices, provided 
     * they did not change on the previous invocation of the 
     * <code>modulate</code> method.
     * </p>
     */
    private int percentChanceOfChange = 30;
    
    /**
     * <p>The maximum amount by which the sell price is permitted to change 
     * during a price modulation.</p>
     */
    private double maxDelta = 0.01;
    
    /**
     * <p>The maximum amount by which the sell and buy price are permitted to 
     * differ.</p>
     */
    private double maxSpread = 0.001;
    
    /**
     * <p>Whether the sell price visibly changed on the previous invocation of 
     * the <code>modulate</code> method.</p>
     */
    private boolean sellPriceVisiblyChanged;
            
    /**
     * <p>Whether the buy price visibly changed on the previous invocation of 
     * the <code>modulate</code> method.</p>
     */
    private boolean buyPriceVisiblyChanged;
    
    static {
        currencyPairFormat.setMaximumFractionDigits(4);
    }
    
    /**
     * <p>Construct an <code>AuthoratativeCurrencyPair</code> instance.</p>
     * @param name The name of the pair.
     * @param sellPrice The initial sell price.
     * @param buyPrice The initial buy price.
     */
    public AuthoratativeCurrencyPair(String name, double sellPrice, double buyPrice) {
        this.name = name;
        this.prices = new double[2];
        this.prices[0] = sellPrice;
        this.prices[1] = buyPrice;
    }
    
    /**
     * <p>Construct an <code>AuthoratativeCurrencyPair</code> instance.</p>
     * @param name The name of the pair.
     * @param sellPrice The initial sell price.
     * @param buyPrice The initial buy price.
     * @param maxDelta The maximum amount by which the sell price is permitted to change 
     * during a price modulation.
     * @param maxSpread The maximum amount by which the sell and buy price are permitted to 
     * differ.
     */
    public AuthoratativeCurrencyPair(String name, double sellPrice, double buyPrice, double maxDelta, double maxSpread) {
        this(name, sellPrice, buyPrice);
        this.maxDelta = maxDelta;
        this.maxSpread = maxSpread;
    }
    
    /**
     * <p>Construct an <code>AuthoratativeCurrencyPair</code> instance.</p>
     * @param name The name of the pair.
     * @param sellPrice The initial sell price.
     * @param buyPrice The initial buy price.
     * @param maxDelta The maximum amount by which the sell price is permitted to change 
     * during a price modulation.
     * @param maxSpread The maximum amount by which the sell and buy price are permitted to 
     * differ.
     * @param percentChanceOfChange The percentage chance we will choose to change the prices, provided 
     * they did not change on the previous invocation of the <code>modulate</code> method.
     * @throws IllegalArgumentException If percentChanceOfChange is less than <code>0</code> or more than <code>100</code>.
     */
    public AuthoratativeCurrencyPair(String name, double sellPrice, double buyPrice, double maxDelta, double maxSpread, int percentChanceOfChange) {
        this(name, sellPrice, buyPrice, maxDelta, maxSpread);
        if (percentChanceOfChange < 0 || percentChanceOfChange > 100) {
            throw new IllegalArgumentException("percentChanceOfChange must be between 0 and 100. value supplied was " + percentChanceOfChange);
        }
        this.percentChanceOfChange = percentChanceOfChange;
    }
    
    /**
     * <p>Get the name of the pair, such as "EUR/USD".</p>
     * @return The currency pair name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * <p>Get an array of length <code>2</code> containing the sell and buy 
     * prices, respectively. This method contains
     * synchronization logic to prevent a caller from getting the prices 
     * while they are changing.</p>
     * @return The sell and buy prices, respectively.
     */
    public double[] getPrices() {
        synchronized(this) {
            return this.prices;
        }
    }
    
    /**
     * <p>If the sell price or buy price did not change visibly on the previous call to this method,
     * then choose a number between <code>1</code> and <code>100</code> and compare it to 
     * <code>this.percentChanceOfChange</code> to determine whether we should change 
     * the sell and buy prices.</p>
     */
    public void modulate() {
        if (this.sellPriceVisiblyChanged || this.buyPriceVisiblyChanged || this.percentChanceOfChange == 0) {
            //just return without changing the prices.
            //mark the sell and buy prices not visibly changed,
            //as these will be examined on the next invocation of this method.
            this.sellPriceVisiblyChanged = false;
            this.buyPriceVisiblyChanged = false;
            return;
        }
        if (this.percentChanceOfChange == 100) {
            changePrices();
            return;
        }
        int between1And100 = random.nextInt(100) + 1;
        if (between1And100 <= this.percentChanceOfChange) {
            changePrices();
            return;
        }
        //we did not change the prices.
        //mark the sell and buy prices not visibly changed,
        //as these will be examined on the next invocation of this method.
        this.sellPriceVisiblyChanged = false;
        this.buyPriceVisiblyChanged = false;
    }
    
    /**
     * <p>Change the sell and buy prices. This method contains
     * synchronization logic to prevent a caller from getting the prices 
     * while they are changing.</p>
     * <p>First, randomly determine whether to increase or decrease the sell 
     * price. Then make several attempts to randomly generate visibly changed 
     * sell and buy prices, taking care not to let the prices go below zero.
     * Then set the members of <code>this.prices</code>, and also set <code>this.sellPriceVisiblyChanged</code> and 
     * <code>this.buyPriceVisiblyChanged</code> according to whether we've  
     * changed the prices visibly.</p>
     */
    private void changePrices() {
        synchronized (this) {
            boolean increase = random.nextBoolean();
            double newSellPrice = this.prices[0];
            boolean visibleSellChange = false;
            //make up to three attempts to generate a visibly changed sell price
            for (int i = 0; i < 3; i++) {
                newSellPrice = getNewSellPrice(this.prices[0], increase);
                if (!getTextForPrice(this.prices[0]).equals(getTextForPrice(newSellPrice))) {
                    visibleSellChange = true;
                    break;
                }
            }
            
            //reevaluate increase, in case we had to switch it to avoid going negative
            increase = newSellPrice > this.prices[0];
            
            double newBuyPrice = this.prices[1];
            boolean visibleBuyChange = false;
            //make up to three attempts to generate a visibly changed buy price
            for (int i = 0; i < 3; i++) {
                newBuyPrice = getNewBuyPrice(this.prices[1], newSellPrice, increase);
                if (!getTextForPrice(this.prices[1]).equals(getTextForPrice(newBuyPrice))) {
                    visibleBuyChange = true;
                    break;
                }
            }
            
            //set members accordingly
            this.prices[0] = newSellPrice;
            this.sellPriceVisiblyChanged = visibleSellChange;
            this.prices[1] = newBuyPrice;
            this.buyPriceVisiblyChanged = visibleBuyChange;
        }
    }
    
    /**
     * <p>Generate a new sell price. First, randomly generate the delta
     * based on <code>this.maxDelta</code>. If the delta is zero, reset it to a very 
     * small figure to ensure the sell price does change. If <code>increase</code>
     * is true or if decreasing by the delta would generate
     * a new sell price at or below zero, then return a new sell price by increasing
     * <code>oldSellPrice</code> by the delta. Otherwise, return a new sell price by decreasing
     * <code>oldSellPrice</code> by the delta.</p>
     * @param oldSellPrice The old sell price.
     * @param increase Whether we intend to increase the sell price.
     * @return The new sell price.
     */
    private double getNewSellPrice(double oldSellPrice, boolean increase) {
        double delta = random.nextDouble() * this.maxDelta;
        if (delta == 0.0) {
            delta = 0.00000000000001;
        }
        if (increase) {
            return oldSellPrice + delta;
        }
        else {
            double diminishedSellPrice = oldSellPrice - delta;
            //if dimishedSellPrice is at or below zero, then do an increase instead of a decrease
            if (diminishedSellPrice <= 0.0) {
                return oldSellPrice + delta;
            }
            return diminishedSellPrice;
        }
    }
    
    /**
     * <p>Generate a new buy price. The new buy price must meet the following conditions:</p>
     * <ul>
     *  <li>The new buy price must be greater than or equal to the new sell price.</li>
     *  <li>The difference between the new buy price and the new sell price must 
     *  not exceed <code>this.maxSpread</code>.</li>
     *  <li>If the sell price has increased (so that the <code>increase</code> parameter is <code>true</code>),
     *  then the buy price must not decrease; that is, the new buy price must be greater than or equal
     *  to the old buy price.</li>
     *  <li>If the sell price has decreased (so that the <code>increase</code> parameter is <code>false</code>),
     *  then the buy price must not increase; that is, the new buy price must be less than or equal
     *  to the old buy price.</li>
     * </ul>
     * @param oldBuyPrice The old buy price.
     * @param newSellPrice The new sell price.
     * @param increase <code>true</code> if the sell price has increased, <code>false</code> otherwise.
     * @return The new buy price.
     */
    private double getNewBuyPrice(double oldBuyPrice, double newSellPrice, boolean increase) {
        if (!increase) {
            //the sell price has decreased. the buy price must decrease or remain the same.
            //the buy price must not increase.
            //add the actual spread (based on the maxSpread) to the newSellPrice to calculate the newBuyPrice
            //but first, make sure the maxSpread is not so great that the newBuyPrice can end up greater than the oldBuyPrice
            
            //establish a saneMaxSpread variable
            double saneMaxSpread = this.maxSpread;
            
            //the "superSpread" is the absolute maximum saneMaxSpread we want to tolerate. 
            //if saneMaxSpread is greater than the superSpread, then the newBuyPrice can end up greater than the oldBuyPrice
            double superSpread = oldBuyPrice - newSellPrice;
            if (saneMaxSpread > superSpread) {
                saneMaxSpread = superSpread;
            }
            
            //pick a number between zero and saneMaxSpread.
            //this will be the actualSpread.
            double actualSpread = random.nextDouble() * saneMaxSpread;
            
            //add the actualSpread to the newSellPrice to calculate the newBuyPrice.
            return newSellPrice + actualSpread;
        }
        else {
            //the sell price has increased. the buy price must increase or remain the same.
            //the buy price must not decrease.
            
            if (newSellPrice < oldBuyPrice) {
                //newSellPrice is less than oldBuyPrice, 
                //so we cannot simply add a spread to newSellPrice to calculate newBuyPrice,
                //because that algorithm could yield a newBuyPrice that's less than the oldBuyPrice.
                //instead, we must calculate newBuyPrice by finding an appropriate maxBuyDelta and
                //adding it to the oldBuyPrice. the maxBuyDelta must not be so great that 
                //the difference between the newBuyPrice and newSellPrice can exceed the maxSpread.
                
                //the oldBuyPrice is greater than the newSellPrice and less than the maximum allowable newBuyPrice.
                //the maxSpread is the difference between the maximum allowable newBuyPrice and the newSellPrice.
                //the maxBuyDelta is the maxSpread minus the difference between the oldBuyPrice and the newSellPrice.
                double maxBuyDelta = this.maxSpread - oldBuyPrice + newSellPrice;   //+ newSellPrice is due to algebra. this is intentional!
                
                //pick a number between zero and maxBuyDelta.
                //this will be the actualBuyDelta.
                double actualBuyDelta = random.nextDouble() * maxBuyDelta;
                
                //add the actualBuyDelta to the oldBuyPrice to calculate the newBuyPrice.
                return oldBuyPrice + actualBuyDelta;
            }
            else {
                //newSellPrice is greater than or equal to the oldBuyPrice
                //so we can just add add a spread to the newSellPrice to calculate the newBuyPrice.
                
                //pick a number between zero and maxSpread.
                //this will be the actualSpread.
                double actualSpread = random.nextDouble() * this.maxSpread;
                
                //add the actualSpread to the newSellPrice to calculate the newBuyPrice.
                return newSellPrice + actualSpread;
            }
        }
    }    
    
    /**
     * <p>Get formatted text representing the supplied price.</p>
     * @param price The price for which we want formatted text.
     * @return Formatted text representing the supplied price.
     */
    private String getTextForPrice(double price) {
        return currencyPairFormat.format(price);
    }
}
