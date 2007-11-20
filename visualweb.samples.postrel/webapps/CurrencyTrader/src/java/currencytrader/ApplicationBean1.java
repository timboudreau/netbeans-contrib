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

import com.sun.rave.web.ui.appbase.AbstractApplicationBean;
import com.sun.rave.web.ui.model.Option;
import currencyservice.CurrencyService;
import javax.faces.FacesException;

/**
 * <p>Application scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available to all users
 *  and pages in the application.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 * @author mbohm
 */
public class ApplicationBean1 extends AbstractApplicationBean {
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
     * <p>Construct a new application data bean instance.</p>
     */
    public ApplicationBean1() {
    }
    
    /**
     * <p>This method is called when this bean is initially added to
     * application scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * application scope.</p>
     *
     * <p>You may customize this method to initialize and cache application wide
     * data values (such as the lists of valid options for dropdown list
     * components), or to allocate resources that are required for the
     * lifetime of the application.</p>
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
            log("ApplicationBean1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here
        
        //instantiate the CurrencyService and get the currency pair names
        this.service = new CurrencyService(5000L, 15000L);
        String[] pairNames = service.getPairNames();
        
        //populate pairNameOptions
        this.pairNameOptions = new Option[pairNames.length];
        for (int i = 0; i < pairNames.length; i++) {
            pairNameOptions[i] = new Option(pairNames[i], pairNames[i]);
        }
        
        //populate typeOptions
        this.typeOptions = new Option[] {
            new Option("Sell", "Sell"),
            new Option("Buy", "Buy")
        };
        
        //populate amountOptions
        this.amountOptions = new Option[10];
        for (int i = 0; i < amountOptions.length; i++) {
            int amount = 100 * (i + 1);
            amountOptions[i] = new Option(new Integer(amount), String.valueOf(amount));
        }
    }
    
    /**
     * <p>This method is called when this bean is removed from
     * application scope.  Typically, this occurs as a result of
     * the application being shut down by its owning container.</p>
     *
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    public void destroy() {
    }
    
    /**
     * <p>Return an appropriate character encoding based on the
     * <code>Locale</code> defined for the current JavaServer Faces
     * view.  If no more suitable encoding can be found, return
     * "UTF-8" as a general purpose default.</p>
     *
     * <p>The default implementation uses the implementation from
     * our superclass, <code>AbstractApplicationBean</code>.</p>
     */
    public String getLocaleCharacterEncoding() {
        return super.getLocaleCharacterEncoding();
    }
    
    /**
     * <p>The currency service, which simulates a client stub for an external 
     * currency service on the network.</p>
     */
    private CurrencyService service;
    
    /**
     * <p>An <code>Option</code> array of the currency pair names.</p>
     */
    private Option[] pairNameOptions;
    
    /**
     * <p>An <code>Option</code> array of the position types ("Sell" or 
     * "Buy").</p>
     */
    private Option[] typeOptions;
    
    /**
     * <p>An <code>Option</code> array of the amounts available when opening
     * a position.</p>
     */
    private Option[] amountOptions;
    
    /**
     * <p>A <code>java.util.Map</code> whose keys are <code>String</code> constants in the
     * <code>CurrencyPair</code> class and whose values are the
     * URLs of the up and down arrow images and the blank image. The 
     * <code>url</code> property of the image components on Page1 are bound to 
     * expressions referencing this map.</p>
     */
    private VisibleChangeMap visibleChangeImageSrcMap = new VisibleChangeMap();

    /**
     * Get the currency service, which simulates a client stub for an external 
     * currency service on the network.
     * @return The currency service.
     */
    public CurrencyService getService() {
        return this.service;
    }
    
    /**
     * <p>Get an <code>Option</code> array of the currency pair names.</p>
     * @return An <code>Option</code> array of the currency pair names.
     */
    public Option[] getPairNameOptions() {
        return this.pairNameOptions;
    }
    
    /**
     * <p>Get an <code>Option</code> array of the position types ("Sell" or 
     * "Buy").</p>
     * @return An <code>Option</code> array of the position types.
     */
    public Option[] getTypeOptions() {
        return this.typeOptions;
    }
    
    /**
     * <p>Get an <code>Option</code> array of the amounts available when opening
     * a position.</p>
     * @return An <code>Option</code> array of the amounts available.
     */
    public Option[] getAmountOptions() {
        return this.amountOptions;
    }
    
    /**
     * <p>Get a <code>java.util.Map</code> whose keys are <code>String</code> constants in the
     * <code>CurrencyPair</code> class and whose values are the
     * URLs of the up and down arrow images and the blank image. The 
     * <code>url</code> property of the image components on Page1 are bound to 
     * expressions referencing this map.</p>
     * @return A <code>java.util.Map</code> whose values are the
     * URLs of the up and down arrow images and the blank image.
     */
    public VisibleChangeMap getVisibleChangeImageSrcMap() {
        return this.visibleChangeImageSrcMap;
    }

}
