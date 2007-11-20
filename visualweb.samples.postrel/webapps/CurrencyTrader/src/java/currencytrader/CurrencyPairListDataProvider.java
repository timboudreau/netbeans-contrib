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

import com.sun.data.provider.impl.ObjectListDataProvider;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mbohm
 */
public class CurrencyPairListDataProvider extends ObjectListDataProvider {

    public CurrencyPairListDataProvider() {
        if (Beans.isDesignTime()) {
            List pairList = new ArrayList();
            CurrencyPair[] pairs =  {
                new CurrencyPair("EUR/USD", 1.2752, 1.2755),
                new CurrencyPair("USD/JPY", 112.31, 112.35),
                new CurrencyPair("GBP/USD", 1.8601, 1.8606),
                new CurrencyPair("USD/CHF", 1.2242, 1.2247),
                new CurrencyPair("AUD/USD", 0.7583, 0.7587),
                new CurrencyPair("USD/CAD", 1.1054, 1.1059),
                new CurrencyPair("NZD/USD", 0.6340, 0.6344)
            };
            for (int i=0; i < pairs.length; i++) {
                pairList.add(pairs[i]);
            }
            this.setList(pairList);
        }
    }

    public CurrencyPairListDataProvider(List pairList) {
        super(pairList);
    }
}
