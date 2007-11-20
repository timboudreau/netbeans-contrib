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

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Random;

/**
 * <p>This class is used by the web application to represent a pair of 
 * currencies, such as "EUR/USD". This class should not be confused with the 
 * <code>AuthoratativeCurrencyPair</code> class in the 
 * <code>currencyservice</code> package. Two separate classes are used because 
 * this one is used by the web application itself for its purposes--namely, 
 * to hold pricing and visible change information for a currency pair 
 * displayed in a particular rendered page--while the
 * <code>AuthoratativeCurrencyPair</code> class internally helps 
 * <code>CurrencyService</code> implement its public API.</p>
 * @author mbohm
 */
public class CurrencyPair implements Serializable {
    
    /**
     * <p>Denotes a price has not changed since the previous poll request.</p>
     */
    public static final String VISIBLY_UNCHANGED = "VISIBLY_UNCHANGED";
    
    /**
     * <p>Denotes a price has visibly increased since the previous poll
     * request.</p>
     */
    public static final String VISIBLE_INCREASE = "VISIBLE_INCREASE";
    
    /**
     * <p>Denotes a price has visibly decreased since the previous poll
     * request.</p>
     */
    public static final String VISIBLE_DECREASE = "VISIBLE_DECREASE";
    
    /**
     * <p>Formats prices to have no more than four fractional digits.</p>
     */
    private static NumberFormat currencyPairFormat = NumberFormat.getInstance();
    
    /**
     * <p>The name of the pair, such as "EUR/USD".</p>
     */
    private String name;
    
    /**
     * <p>The current sell price.</p>
     */
    private double sellPrice;
    
    /**
     * <p>The current buy price.</p>
     */
    private double buyPrice;
    
    /**
     * <p>Denotes whether the sell price has visibly increased, decreased,
     * or remained unchanged since the previous poll request.</p>
     */
    private String sellPriceVisibleChange = VISIBLY_UNCHANGED;
    
    /**
     * <p>Denotes whether the buy price has visibly increased, decreased,
     * or remained unchanged since the previous poll request.</p>
     */
    private String buyPriceVisibleChange = VISIBLY_UNCHANGED;
    
    static {
        currencyPairFormat.setMaximumFractionDigits(4);
    }
    
    /**
     * <p>Get formatted text representing the supplied price.</p>
     * @param price The price for which we want formatted text.
     * @return Formatted text representing the supplied price.
     */
    public static String getTextForPrice(double price) {
        return currencyPairFormat.format(price);
    }
    
    /**
     * <p>Construct a <code>CurrencyPair</code> instance.</p>
     * @param name The name of the pair, such as "EUR/USD".
     * @param sellPrice The sell price.
     * @param buyPrice The buy price. 
     */
    public CurrencyPair(String name, double sellPrice, double buyPrice) {
        this.name = name;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
    }
    
    /**
     * The name of the currency pair. For example, "EUR/USD."
     * @return The name of the currency pair.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * <p>Get the current sell price.</p>
     * @return The current sell price.
     */
    public double getSellPrice() {
        return this.sellPrice;
    }

    /**
     * <p>Get the current buy price.</p>
     * @return The current buy price.
     */
    public double getBuyPrice() {
        return this.buyPrice;
    }
    
    /**
     * <p>Get formatted text representing the current sell price.</p>
     * @return Formatted text representing the current sell price.
     */
    public String getSellPriceText() {
        return getTextForPrice(this.sellPrice);
    }

    /**
     * <p>Get formatted text representing the current buy price.</p>
     * @return Formatted text representing the current buy price.
     */
    public String getBuyPriceText() {
        return getTextForPrice(this.buyPrice);
    }
    
    /**
     * <p>Get whether the sell price has visibly increased, decreased,
     * or remained unchanged since the previous poll request.</p>
     * @return Whether the sell price has visibly increased, decreased,
     * or remained unchanged.
     */
    public String getSellPriceVisibleChange() {
        return this.sellPriceVisibleChange;
    }
    
    /**
     * <p>Get whether the buy price has visibly increased, decreased,
     * or remained unchanged since the previous poll request.</p>
     * @return Whether the buy price has visibly increased, decreased,
     * or remained unchanged.
     */
    public String getBuyPriceVisibleChange() {
        return this.buyPriceVisibleChange;
    }
    
    /**
     * <p>Set the current sell and buy prices, respectively.</p>
     * @param newSellPrice The new sell price.
     * @param newBuyPrice The new buy price.
     */
    public void setPrices(double newSellPrice, double newBuyPrice) {
        double sellPriceDiff = newSellPrice - this.sellPrice;
        if (sellPriceDiff == 0.0) {
            this.sellPriceVisibleChange = VISIBLY_UNCHANGED;
            this.buyPriceVisibleChange = VISIBLY_UNCHANGED;
        }
        else {
            String sellPriceText = this.getSellPriceText();
            String buyPriceText = this.getBuyPriceText();
            String possibleVisibleChange = sellPriceDiff > 0.0 ? VISIBLE_INCREASE : VISIBLE_DECREASE;
            this.sellPriceVisibleChange = sellPriceText.equals(getTextForPrice(newSellPrice)) ? VISIBLY_UNCHANGED : possibleVisibleChange;
            this.buyPriceVisibleChange = buyPriceText.equals(getTextForPrice(newBuyPrice)) ? VISIBLY_UNCHANGED : possibleVisibleChange;
        }
        this.sellPrice = newSellPrice;
        this.buyPrice = newBuyPrice;
    }
}
