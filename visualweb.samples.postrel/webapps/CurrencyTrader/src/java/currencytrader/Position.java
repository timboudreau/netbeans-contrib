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

import com.sun.rave.web.ui.model.Option;
import java.io.Serializable;
import java.text.NumberFormat;

/**
 * <p>This class represents an open or closed position. In the currency trading 
 * domain, a "position" is a trading transaction. A position is said to be
 * "opened" when some amount of the primary currency (e.g., "EUR") in a 
 * currency pair (e.g., "EUR/USD") is bought or sold. A position is said to be
 * "closed" when the same or a lesser amount of the primary currency is traded
 * in the inverse manner, that is, sold if the position was opened by buying and
 * bought if the position was opened by selling.</p>
 *
 * <p>Components in the Open Positions and Closed Positions table on Page1 are
 * bound to properties in this object via interceding 
 * <code>TableDataProvider</code> instances in session.</p>
 * @author mbohm
 */
public class Position implements Serializable {
    
    /**
     * <p>Used to create position ids that are unique across all positions
     * across all users.</p>
     */
    private static int counter;
    
    /**
     * <p>Whether this position is open. The default value is <code>true</code>.</p>
     */
    private boolean open = true;
    
    /**
     * <p>The unique id for this position. The id is unique across all positions
     * across all users.</p>
     */
    private String id;
    
    /**
     * <p>The name of the currency pair for this position, such as 
     * "EUR/USD".</p>
     */
    private String name;
    
    /**
     * <p>The type of this position, namely, "Sell" or "Buy".</p>
     */ 
    private String type;
    
    /**
     * <p>The number of units (in thousands) of the primary currency that was
     * bought or sold when this position was opened (in the case of an 
     * open position) or closed (in the case of a closed position).</p>
     */
    private int amount;
    
    /**
     * <p>The number of units (in thousands) of the primary currency that would
     * be bought or sold if the position were closed now. This amount is meaningful
     * only when the position is still open.</p>
     */ 
    private int floatingCloseAmount;
    
    /**
     * <p>The sell price (in the case of a Sell position) or buy price (in the 
     * case of a Buy position) at the time the position was opened.</p>
     */
    private double openPrice;
    
    /**
     * <p>A <code>CurrencyPair</code> instance containing pricing information
     * (including whether prices have visibly changed since the previous 
     * poll request) for the currency pair applicable to this position.</p>
     * 
     */
    private CurrencyPair pair;
    
    /**
     * <p>The floating (in the case of an open position) or actual (in the case
     * of a closed postion) profit. In the currency trading domain, "floating 
     * profit" denotes the profit that would be sustained if the position were
     * closed now.</p>
     */
    private double profit;
    
    /**
     * <p>Denotes whether the current price has visibly increased, decreased,
     * or remained unchanged since the previous poll request. The "current 
     * price" is the current buy price in the case of a Sell trade and the 
     * current sell price in the case of a Buy trade.</p>
     */
    private String currentPriceVisibleChange;
    
    /**
     * <p>Denotes whether the floating profit has visibly increased, decreased,
     * or remained unchanged since the previous poll request.
     * In the currency trading domain, "floating 
     * profit" denotes the profit that would be sustained if the position were
     * closed now.</p>
     */
    private String floatingProfitVisibleChange;
    
    /**
     * <p>Get a position id that is unique across all positions across all
     * users. Synchronization on the <code>Position</code> class is used for 
     * this purpose.</p>
     * @return A position id that is unique across all positions across all
     * users.
     */
    private static String getNextId() {
        //synchronize to give all positions across all users a unique id
        synchronized (Position.class) {
            return String.valueOf(counter++);
        }
    }
    
    /**
     * <p>Create and return a <code>Position</code> instance representing
     * an open position.</p>
     * @param openPair A <code>CurrencyPair</code> instance containing pricing
     * information for the applicable currency pair.
     * @param amount The number of units (in thousands) of the primary currency
     * that are being bought or sold with the opening of the position.
     * @param type The type of the position, namely, "Sell" or "Buy".
     * @return The new <code>Position</code> instance.
     */
    public static Position createOpenPosition(CurrencyPair openPair, int amount, String type) {
        return new Position(openPair, amount, type);
    }
    
    /**
     * <p>Create and return a <code>Position</code> instance representing
     * a closed position.</p>
     * @param closePair A <code>CurrencyPair</code> instance containing pricing
     * information for the applicable currency pair.
     * @param amount The number of units (in thousands) of the primary currency
     * that are being bought or sold with the closing of the position.
     * @param type The type of the position, namely, "Sell" or "Buy".
     * @param openPrice The sell price (in the case of a Sell position) or buy price (in the 
     * case of a Buy position) at the time the position was opened.
     * @return The new <code>Position</code> instance.
     */
    public static Position createClosedPosition(CurrencyPair closePair, int amount, String type, double openPrice) {
        return new Position(closePair, amount, type, openPrice);
    }
    
    /**
     * <p>Constructs a <code>Position</code> instance representing an open
     * position.</p>
     * @param openPair A <code>CurrencyPair</code> instance containing pricing
     * information for the applicable currency pair.
     * @param amount The number of units (in thousands) of the primary currency
     * that are being bought or sold with the opening of the position.
     * @param type The type of the position, namely, "Sell" or "Buy".
     */
    private Position(CurrencyPair openPair, int amount, String type) {
        this.id = getNextId();
        this.name = openPair.getName();
        this.amount = amount;
        this.type = type;
        double openPrice;
        if ("Sell".equals(type)) {
            openPrice = openPair.getSellPrice();
        }
        else {
            openPrice = openPair.getBuyPrice();
        }
        this.openPrice = openPrice;
        this.floatingCloseAmount = amount; //default to amount
        update(openPair);
    }
    
    /**
     * <p>Constructs a <code>Position</code> instance representing a closed
     * position.</p>
     * @param closePair A <code>CurrencyPair</code> instance containing pricing
     * information for the applicable currency pair.
     * @param amount The number of units (in thousands) of the primary currency
     * that are being bought or sold with the closing of the position.
     * @param type The type of the position, namely, "Sell" or "Buy".
     * @param openPrice The sell price (in the case of a Sell position) or buy price (in the 
     * case of a Buy position) at the time the position was opened.
     */
    private Position(CurrencyPair closePair, int amount, String type, double openPrice) {
        this.id = getNextId();
        this.name = closePair.getName();
        this.amount = amount;
        this.type = type;
        this.openPrice = openPrice;
        //no need to set floatingCloseAmount
        close(closePair);
    }
    
    /**
     * <p>Get the unique id for this position. The id is unique across all positions
     * across all users.</p>
     * @return The unique id for this position.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * <p>Get the name of the currency pair for this position, such as 
     * "EUR/USD".</p>
     * @return The name of the currency pair for this position.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * <p>Get the number of units (in thousands) of the primary currency that was
     * bought or sold when this position was opened (in the case of an 
     * open position) or closed (in the case of a closed position).</p>
     * @return The number of units (in thousands) of the primary currency.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * <p>Set the number of units (in thousands) of the primary currency that 
     * remain open in this position.</p>
     * @param amount The number of units (in thousands) of the primary currency.
     */
    public void setAmount(int amount) {
        this.amount = amount;
        setFloatingCloseAmount(amount);
    }

    /**
     * <p>Get the type of this position, namely, "Sell" or "Buy".</p>
     * @return The type of this position, namely, "Sell" or "Buy".
     */ 
    public String getType() {
        return this.type;
    }

    /**
     * <p>Get the sell price (in the case of a Sell position) or buy price (in the 
     * case of a Buy position) at the time the position was opened.</p>
     * @return The relevant price at the time the position was opened.
     */
    public double getOpenPrice() {
        return this.openPrice;
    }

    /**
     * <p>Get formatted text representing the open price.</p>
     * @return Formatted text representing the open price.
     */
    public String getOpenPriceText() {
        return getTextForPrice(this.openPrice);
    }
    
    /**
     * <p>Get formatted text representing the current price (in the case of 
     * an open position) or close price (in the case of a closed position).
     * The "current price" is the current buy price in the case of a Sell 
     * trade and the current sell price in the case of a Buy trade.</p>
     * @return Formatted text representing the current or close price, 
     * depending on whether the position is open or closed, respectively.
     */
    public String getClosePriceText() {
        double closePrice = getClosePrice();
        return getTextForPrice(closePrice);
    }
    
    /**
     * <p>Get whether the current price has visibly increased, decreased,
     * or remained unchanged since the previous poll request. The "current 
     * price" is the current buy price in the case of a Sell trade and the 
     * current sell price in the case of a Buy trade.</p>
     * @return Whether the current price has visibly increased, decreased,
     * or remained unchanged since the previous poll request.
     */
    public String getCurrentPriceVisibleChange() {
        return this.currentPriceVisibleChange;
    }
    
    /**
     * <p>Get the floating (in the case of an open position) or actual (in the case
     * of a closed postion) profit. In the currency trading domain, "floating 
     * profit" denotes the profit that would be sustained if the position were
     * closed now.</p>
     * @return The floating or actual profit, depending on whether the 
     * position is open or closed, respectively.
     */
    public double getProfit() {
        return this.profit;
    }

    /**
     * <p>Get formatted text representing the floating (in the case of an open 
     * position) or actual (in the case of a closed postion) profit.
     * In the currency trading domain, "floating 
     * profit" denotes the profit that would be sustained if the position were
     * closed now.</p>
     * @return Formatted text representing the floating or actual profit, 
     * depending on whether the position is open or closed, respectively.
     */ 
    public String getProfitText() {
        return getTextForProfit(this.profit);
    }
    
    /**
     * <p>Get whether the floating profit has visibly increased, decreased,
     * or remained unchanged since the previous poll request.
     * In the currency trading domain, "floating 
     * profit" denotes the profit that would be sustained if the position were
     * closed now.</p>
     * @return Whether the floating profit has visibly increased, decreased,
     * or remained unchanged since the previous poll request.
     */
    public String getFloatingProfitVisibleChange() {
        return this.floatingProfitVisibleChange;
    }
    
    /**
     * <p>Get the number of units (in thousands) of the primary currency that would
     * be bought or sold if the position were closed now. This amount is meaningful
     * only when the position is still open.</p>
     * @return The number of units (in thousands) of the primary currency that would
     * be bought or sold if the position were closed now.
     */
    public int getFloatingCloseAmount() {
        return this.floatingCloseAmount;
    }
    
    /**
     * <p>Set the number of units (in thousands) of the primary currency that would
     * be bought or sold if the position were closed now. This amount is meaningful
     * only when the position is still open. Simply return if this position is 
     * closed or if the supplied <code>floatingCloseAmt</code> is greater than
     * <code>this.amount</code>. Otherwise, set <code>this.floatingCloseAmount</code>
     * and <code>this.profit</code> accordingly.</p>
     * @param floatingCloseAmt The number of units (in thousands) of the primary currency that would
     * be bought or sold if the position were closed now.
     */
    public void setFloatingCloseAmount(int floatingCloseAmt) {
        if (floatingCloseAmt > this.amount || !this.open) {
            return;
        }

        this.floatingCloseAmount = floatingCloseAmt;
        this.profit = calculateProfit(true);
    }
    
    /**
     * <p>Get an array of <code>Option</code> instances representing the choices
     * to present in the floating close dropdown. Specifically, we present choices by the
     * hundred from <code>100</code> to <code>this.amount</code>. The <code>Option</code>
     * instances have an <code>Integer</code> value and a <code>String</code> label.</p>
     * @return An array of <code>Option</code> instances representing the choices to present
     * in the floating close dropdown.
     */
    public Option[] getFloatingCloseAmountOptions() {
        Option[] amountOptions = new Option[this.amount/100];
        for (int i = this.amount; i >= 100; i -= 100) {
            amountOptions[(i/100) - 1] = new Option(new Integer(i), String.valueOf(i));
        }
        return amountOptions;
    }
    
    /**
     * <p>Close the position. We assign the supplied
     * <code>currentPair</code> to <code>this.pair</code>, set <code>this.profit</code>
     * appropriately, and set <code>this.open</code> to <code>false</code>.</p>
     * @param currentPair A <code>CurrencyPair</code> instance containing
     * pricing information for the applicable currency pair.
     * @throws <code>NullPointerException</code> if the supplied <code>currentPair</code>
     * is <code>null</code>.
     * @throws <code>IllegalArgumentException</code> if the name of the supplied <code>currentPair</code>
     * is not equal to <code>this.name</code>.
     */
    public void close(CurrencyPair currentPair) {
        checkPair(currentPair);
        this.pair = currentPair;
        this.profit = calculateProfit(false);
        this.open = false;
    }
    
    /**
     * <p>Update the position to use the supplied <code>currentPair</code>, which
     * contains pricing information (including whether prices have visibly 
     * changed since the previous poll request) for the applicable currency 
     * pair. We assign the supplied <code>currentPair</code> to 
     * <code>this.pair</code> and set <code>this.profit</code>,
     * <code>this.currentPriceVisibleChange</code>, and 
     * <code>this.floatingProfitVisibleChange</code> appropriately.</p>
     * @param currentPair A <code>CurrencyPair</code> instance containing
     * pricing information for the applicable currency pair.
     * @throws <code>IllegalStateException</code> if the position is closed.
     * @throws <code>NullPointerException</code> if the supplied <code>currentPair</code>
     * is <code>null</code>.
     * @throws <code>IllegalArgumentException</code> if the name of the supplied <code>currentPair</code>
     * is not equal to <code>this.name</code>.
     */
    public void update(CurrencyPair currentPair) {
        if (!open) {
            throw new IllegalStateException("Position.updateRealTimeFields: position " + this.id + " is not open");
        }
        
        checkPair(currentPair);
        
        this.pair = currentPair;
        this.profit = calculateProfit(true);
        
        if ("Sell".equals(this.type)) {
            this.currentPriceVisibleChange = currentPair.getBuyPriceVisibleChange(); //inverse on purpose

            //in a Sell position, floating profit varies inversely with buy price increase
            //due to the "buy low, sell high" principle
            if (CurrencyPair.VISIBLE_INCREASE.equals(this.currentPriceVisibleChange)) {
                this.floatingProfitVisibleChange = CurrencyPair.VISIBLE_DECREASE;
            }
            else if (CurrencyPair.VISIBLE_DECREASE.equals(this.currentPriceVisibleChange)) {
                this.floatingProfitVisibleChange = CurrencyPair.VISIBLE_INCREASE;
            }
            else {
                this.floatingProfitVisibleChange = CurrencyPair.VISIBLY_UNCHANGED;
            }
        }
        else {
            this.currentPriceVisibleChange = currentPair.getSellPriceVisibleChange();
            this.floatingProfitVisibleChange = this.currentPriceVisibleChange;
        }
    }    
    
    /**
     * <p>Calculate the floating or actual profit, depending on the supplied
     * boolean, in terms of USD. We calculate floating profit based on <code>this.floatingCloseAmount</code>
     * and actual profit based on <code>this.amount</code>. The calculation is also based
     * on <code>this.openPrice</code> as well as the current buy price (in the 
     * case of a Sell position) or sell price (in the case of a Buy position). If
     * the primary currency is "USD," the profit reaped (or loss endured) is in terms
     * of the other currency. In such cases, it is necessary to convert that profit
     * to USD by using it to buy USD.</p>
     * @param floating Whether we are calculating floating (as opposed to actual) profit.
     * @return The floating or actual profit, in terms of USD.
     */
    private double calculateProfit(boolean floating) {
        double currentSellPrice = this.pair.getSellPrice();
        double currentBuyPrice = this.pair.getBuyPrice();
        
        int floatingOrActualCloseAmount = floating ? this.floatingCloseAmount : this.amount;
        floatingOrActualCloseAmount *= 1000;
        
        double gain, cost;
        if ("Sell".equals(this.type)) {
            //your gain is when you sell, which in this case is when the position was opened.
            //so multiply the price at that time (this.openPrice) by the number of units (floatingOrActualCloseAmount).
            gain = this.openPrice * floatingOrActualCloseAmount;
            
            //your cost is when you buy. since we are calculating the profit that
            //would be sustained if we closed now, "when you buy" is effectively now.
            //so multiply the current buy price by the number of units (floatingOrActualCloseAmount).
            cost = currentBuyPrice * floatingOrActualCloseAmount;
        }
        else {
            //your gain is when you sell. since we are calculating the profit that
            //would be sustained if we closed now, "when you sell" is effectively now.
            //so multiply the current sell price by the number of units (floatingOrActualCloseAmount).
            gain = currentSellPrice * floatingOrActualCloseAmount;
            
            //your cost is when you buy, which in this case is when the position was opened.
            //so multiply the price at that time (this.openPrice) by the number of units (floatingOrActualCloseAmount).
            cost = this.openPrice * floatingOrActualCloseAmount;
        }
        
        double calculatedProfit = gain - cost;
        
        if (this.name.startsWith("USD")) { //example: USD/JPY
            //calculatedProfit is in terms of the other currency.
            //so we need to convert calculatedProfit to USD.
            //to convert it: you have a calculatedProfit in the other currency,
            //so use that money to buy USD.
            calculatedProfit = calculatedProfit / currentBuyPrice; //divide, say, 120 yen profit by 120 to calculate 1 dollar profit
        }

        return calculatedProfit;
    }
    
    /**
     * <p>Get the current price (in the case of 
     * an open position) or close price (in the case of a closed position).
     * The "current price" is the current buy price in the case of a Sell 
     * trade and the current sell price in the case of a Buy trade.</p>
     * @return The current or close price, 
     * depending on whether the position is open or closed, respectively.
     */
    private double getClosePrice() {
        if ("Sell".equals(this.type)) {
            return this.pair.getBuyPrice();
        }
        else {
            return this.pair.getSellPrice();
        }
    }
    
    /**
     * <p>Get formatted text representing the supplied price.</p>
     * @param price The price for which we want formatted text.
     * @return Formatted text representing the supplied price.
     */
    private String getTextForPrice(double price) {
        return CurrencyPair.getTextForPrice(price);
    }
    
    /**
     * <p>Get formatted text representing the supplied profit figure.</p>
     * @param floatingOrActualProfit The profit figure for which we want formatted text.
     * @return Formatted text representing the supplied profit figure.
     */
    private String getTextForProfit(double floatingOrActualProfit) {
        return NumberFormat.getCurrencyInstance().format(floatingOrActualProfit); 
    }
    
    /**
     * <p>Check the supplied <code>currentPair</code> to ensure it is not
     * <code>null</code> and that its name equals the name of this position.</p>
     * @param currentPair A <code>CurrencyPair</code> instance containing
     * pricing information for the applicable currency pair.
     * @throws <code>NullPointerException</code> if the supplied <code>currentPair</code>
     * is <code>null</code>.
     * @throws <code>IllegalArgumentException</code> if the name of the supplied <code>currentPair</code>
     * is not equal to <code>this.name</code>.
     */
    private void checkPair(CurrencyPair currentPair) {
        if (currentPair == null) {
            throw new NullPointerException();
        }
        if (!this.name.equals(currentPair.getName())) {
            throw new IllegalArgumentException("name of position " + this.id + " is " + this.name + ", but supplied name of currentPair was " + currentPair.getName());
        }
    }
}
