/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */
package org.netbeans.installer.wizard.components.actions.sunstudio;

import org.netbeans.modules.servicetag.RegistrationData;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.wizard.components.WizardAction;
import org.netbeans.installer.wizard.components.panels.sunstudio.PostInstallSummaryPanel;
import org.netbeans.modules.reglib.BrowserSupport;
import org.netbeans.modules.reglib.NbConnectionSupport;
import org.netbeans.modules.reglib.NbServiceTagSupport;
import static org.netbeans.installer.utils.helper.DetailedStatus.INSTALLED_SUCCESSFULLY;
import static org.netbeans.installer.utils.helper.DetailedStatus.INSTALLED_WITH_WARNINGS;

public class RegistrationAction extends WizardAction {

    public RegistrationAction() {
    }

    public void execute() {
        // open web page...
        try {
            LogManager.logEntry("... execute netbeans registration action");
            final List<Product> products = new LinkedList<Product>();
            final Registry registry = Registry.getInstance();
            products.addAll(registry.getProducts(INSTALLED_SUCCESSFULLY));
            products.addAll(registry.getProducts(INSTALLED_WITH_WARNINGS));
            String productId = "nb";
            Product nbProduct = null;
            List <Product> productsToRegister = new ArrayList<Product>();
            if (!products.isEmpty()) {
                for (Product product : products) {
                    final String uid = product.getUid();
                    if (uid.equals("nb-base")) {
                        productId = "nb" + productId;
                        nbProduct = product;
                        productsToRegister.add(nbProduct);
                    }
                }
            }
            LogManager.log("... product ID: " + productId);
            if (productId.startsWith("nb")) {
                if (nbProduct != null) {
                    System.setProperty("netbeans.home", nbProduct.getInstallationLocation().getPath());
                }             

                boolean result = showRegistrationPage(productId, productsToRegister);
                if (result) {
                    ServiceTagCreateAction.setNetBeansStatus(true);
                   
                }
            }
        } catch (Exception ex) {
            LogManager.log(ex);
        } finally {
            LogManager.logExit("... finished netbeans registration action");
        }
    }

    private boolean showRegistrationPage(String productId, List <Product> productsToRegister) throws IOException {
        LogManager.logEntry("... show registration page for:" + productId);
        RegistrationData regData = NbServiceTagSupport.getRegistrationData();
        URL url = NbConnectionSupport.getRegistrationURL(regData.getRegistrationURN(), productId);
        boolean succeed = NbConnectionSupport.postRegistrationData(url, regData);
        boolean result = false;
        if (succeed) {
            LogManager.log("... POST request succeded, opening browser : " + url);
            try {
                result = openBrowser(url.toURI());
            } catch (URISyntaxException e) {
                LogManager.log(e);
            }
        } else {
            LogManager.log("... POST request failed, opening browser with local page");
            String [] productNames = new String [productsToRegister.size()];
            for(int i=0;i<productsToRegister.size();i++) {          
                try { 
                     productNames[i] = productsToRegister.get(i).getLogic().getSystemDisplayName();
                } catch (InitializationException e) {
                    LogManager.log(e);
                    productNames[i] = productsToRegister.get(i).getDisplayName();
                }
            }
            File registerPage = NbServiceTagSupport.getRegistrationHtmlPage(productId,productNames);
            URI registerPageUri = registerPage.toURI();
            result = openBrowser(registerPageUri);
        }
        LogManager.logExit("... registration page shown");
        return result;
    }
  
    /**
     * Opens a browser for JDK product registration.
     * @param url Registration Webapp URL
     */
    private boolean openBrowser(URI uri) throws IOException {
        LogManager.log("... opening in the browser: " + uri);
        boolean result = false;
        try {
            if (BrowserSupport.isSupported()) {
                LogManager.log("... browse (bs): " + uri);
                BrowserSupport.browse(uri);
                result = true;
            } else {
                LogManager.log("... browse (fb): " + uri);
                result = browseUnix(uri);
            }
        } catch (IllegalArgumentException ex) {
            LogManager.log("Cannot open browser", ex);
        } catch (UnsupportedOperationException ex) {
            // ignore if not supported
            LogManager.log("Cannot open browser:", ex);
        }
        return result;
    }

    public static void main(String[] args) {
        new RegistrationAction();
    }
  
    @Override
    public boolean canExecuteForward() {
        return Boolean.getBoolean(PostInstallSummaryPanel.ALLOW_SERVICETAG_REGISTRATION_PROPERTY);
    }

    @Override
    public WizardActionUi getWizardUi() {
        return null;
    }

    public static File getUnixBrowser() {
        final String[] possibleBrowsers = (SystemUtils.isLinux() ? POSSIBLE_BROWSER_LOCATIONS_LINUX : (SystemUtils.isSolaris() ? POSSIBLE_BROWSER_LOCATIONS_SOLARIS : new String[]{}));
        for (String s : possibleBrowsers) {
            File f = new File(s);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }

    private static boolean browseUnix(URI uri) throws IOException {
        File browser = getUnixBrowser();
        if (browser != null) {
            LogManager.log("... using browser: " + browser);
            Runtime.getRuntime().exec(new String[]{browser.getAbsolutePath(), uri.toString()});
            return true;
        }
        return false;
    }
    private static final String[] POSSIBLE_BROWSER_LOCATIONS_LINUX = new String[]{
        "/usr/bin/firefox",
        "/usr/bin/mozilla-firefox",
        "/usr/local/firefox/firefox",
        "/opt/bin/firefox",
        "/usr/bin/mozilla",
        "/usr/local/mozilla/mozilla",
        "/opt/bin/mozilla"
    };
    private static final String[] POSSIBLE_BROWSER_LOCATIONS_SOLARIS = new String[]{
        "/usr/sfw/lib/firefox/firefox",
        "/opt/csw/bin/firefox",
        "/usr/sfw/lib/mozilla/mozilla",
        "/opt/csw/bin/mozilla",
        "/usr/dt/bin/sun_netscape",
        "/usr/bin/firefox",
        "/usr/bin/mozilla-firefox",
        "/usr/local/firefox/firefox",
        "/opt/bin/firefox",
        "/usr/bin/mozilla",
        "/usr/local/mozilla/mozilla",
        "/opt/bin/mozilla"
    };
}
