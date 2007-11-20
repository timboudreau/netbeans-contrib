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

import com.sun.data.provider.RowKey;
import com.sun.faces.extensions.avatar.components.AjaxTransaction;
import com.sun.faces.extensions.avatar.components.AjaxZone;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Button;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.HiddenField;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.ImageComponent;
import com.sun.rave.web.ui.component.ImageHyperlink;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import com.sun.rave.web.ui.component.Script;
import com.sun.rave.web.ui.component.StaticText;
import com.sun.rave.web.ui.component.Table;
import com.sun.rave.web.ui.component.TableColumn;
import com.sun.rave.web.ui.component.TableRowGroup;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.convert.IntegerConverter;
import threadmanagement.ThreadPriority;
import threadmanagement.ThreadSynchronizer;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 * @author mbohm
 */
public class Page1 extends AbstractPageBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    
    private Page page1 = new Page();
    
    public Page getPage1() {
        return page1;
    }
    
    public void setPage1(Page p) {
        this.page1 = p;
    }
    
    private Html html1 = new Html();
    
    public Html getHtml1() {
        return html1;
    }
    
    public void setHtml1(Html h) {
        this.html1 = h;
    }
    
    private Head head1 = new Head();
    
    public Head getHead1() {
        return head1;
    }
    
    public void setHead1(Head h) {
        this.head1 = h;
    }
    
    private Link link1 = new Link();
    
    public Link getLink1() {
        return link1;
    }
    
    public void setLink1(Link l) {
        this.link1 = l;
    }
    
    private Body body1 = new Body();
    
    public Body getBody1() {
        return body1;
    }
    
    public void setBody1(Body b) {
        this.body1 = b;
    }
    
    private Form form1 = new Form();
    
    public Form getForm1() {
        return form1;
    }
    
    public void setForm1(Form f) {
        this.form1 = f;
    }

    private Table exchangeRateTable = new Table();

    public Table getExchangeRateTable() {
        return exchangeRateTable;
    }

    public void setExchangeRateTable(Table t) {
        this.exchangeRateTable = t;
    }

    private TableRowGroup exchangeRateTableRowGroup = new TableRowGroup();

    public TableRowGroup getExchangeRateTableRowGroup() {
        return exchangeRateTableRowGroup;
    }

    public void setExchangeRateTableRowGroup(TableRowGroup trg) {
        this.exchangeRateTableRowGroup = trg;
    }

    private TableColumn exchangeRateCurrencyColumn = new TableColumn();

    public TableColumn getExchangeRateCurrencyColumn() {
        return exchangeRateCurrencyColumn;
    }

    public void setExchangeRateCurrencyColumn(TableColumn tc) {
        this.exchangeRateCurrencyColumn = tc;
    }

    private StaticText exchangeRateCurrencyText = new StaticText();

    public StaticText getExchangeRateCurrencyText() {
        return exchangeRateCurrencyText;
    }

    public void setExchangeRateCurrencyText(StaticText st) {
        this.exchangeRateCurrencyText = st;
    }

    private TableColumn exchangeRateSellColumn = new TableColumn();

    public TableColumn getExchangeRateSellColumn() {
        return exchangeRateSellColumn;
    }

    public void setExchangeRateSellColumn(TableColumn tc) {
        this.exchangeRateSellColumn = tc;
    }

    private StaticText exchangeRateSellText = new StaticText();

    public StaticText getExchangeRateSellText() {
        return exchangeRateSellText;
    }

    public void setExchangeRateSellText(StaticText st) {
        this.exchangeRateSellText = st;
    }

    private TableColumn exchangeRateBuyColumn = new TableColumn();

    public TableColumn getExchangeRateBuyColumn() {
        return exchangeRateBuyColumn;
    }

    public void setExchangeRateBuyColumn(TableColumn tc) {
        this.exchangeRateBuyColumn = tc;
    }

    private StaticText exchangeRateBuyText = new StaticText();

    public StaticText getExchangeRateBuyText() {
        return exchangeRateBuyText;
    }

    public void setExchangeRateBuyText(StaticText st) {
        this.exchangeRateBuyText = st;
    }

    private Script script1 = new Script();

    public Script getScript1() {
        return script1;
    }

    public void setScript1(Script s) {
        this.script1 = s;
    }

    private AjaxZone openAPositionPanelZone = new AjaxZone();

    public AjaxZone getOpenAPositionPanelZone() {
        return openAPositionPanelZone;
    }

    public void setOpenAPositionPanelZone(AjaxZone az) {
        this.openAPositionPanelZone = az;
    }

    private StaticText orderPairNameText = new StaticText();

    public StaticText getOrderPairNameText() {
        return orderPairNameText;
    }

    public void setOrderPairNameText(StaticText st) {
        this.orderPairNameText = st;
    }

    private DropDown orderPairNameDropDown = new DropDown();

    public DropDown getOrderPairNameDropDown() {
        return orderPairNameDropDown;
    }

    public void setOrderPairNameDropDown(DropDown dd) {
        this.orderPairNameDropDown = dd;
    }

    private StaticText orderTypeText = new StaticText();

    public StaticText getOrderTypeText() {
        return orderTypeText;
    }

    public void setOrderTypeText(StaticText st) {
        this.orderTypeText = st;
    }

    private DropDown orderTypeDropDown = new DropDown();

    public DropDown getOrderTypeDropDown() {
        return orderTypeDropDown;
    }

    public void setOrderTypeDropDown(DropDown dd) {
        this.orderTypeDropDown = dd;
    }

    private StaticText orderAmountText = new StaticText();

    public StaticText getOrderAmountText() {
        return orderAmountText;
    }

    public void setOrderAmountText(StaticText st) {
        this.orderAmountText = st;
    }

    private DropDown orderAmountDropDown = new DropDown();

    public DropDown getOrderAmountDropDown() {
        return orderAmountDropDown;
    }

    public void setOrderAmountDropDown(DropDown dd) {
        this.orderAmountDropDown = dd;
    }

    private Button openButton = new Button();

    public Button getOpenButton() {
        return openButton;
    }

    public void setOpenButton(Button b) {
        this.openButton = b;
    }

    private AjaxZone openPositionZone = new AjaxZone();

    public AjaxZone getOpenPositionZone() {
        return openPositionZone;
    }

    public void setOpenPositionZone(AjaxZone az) {
        this.openPositionZone = az;
    }

    private Table openPositionTable = new Table();

    public Table getOpenPositionTable() {
        return openPositionTable;
    }

    public void setOpenPositionTable(Table t) {
        this.openPositionTable = t;
    }

    private TableRowGroup openPositionTableRowGroup = new TableRowGroup();

    public TableRowGroup getOpenPositionTableRowGroup() {
        return openPositionTableRowGroup;
    }

    public void setOpenPositionTableRowGroup(TableRowGroup trg) {
        this.openPositionTableRowGroup = trg;
    }

    private TableColumn openPositionCurrencyColumn = new TableColumn();

    public TableColumn getOpenPositionCurrencyColumn() {
        return openPositionCurrencyColumn;
    }

    public void setOpenPositionCurrencyColumn(TableColumn tc) {
        this.openPositionCurrencyColumn = tc;
    }

    private StaticText openPositionCurrencyText = new StaticText();

    public StaticText getOpenPositionCurrencyText() {
        return openPositionCurrencyText;
    }

    public void setOpenPositionCurrencyText(StaticText st) {
        this.openPositionCurrencyText = st;
    }

    private TableColumn openPositionAmountColumn = new TableColumn();

    public TableColumn getOpenPositionAmountColumn() {
        return openPositionAmountColumn;
    }

    public void setOpenPositionAmountColumn(TableColumn tc) {
        this.openPositionAmountColumn = tc;
    }

    private StaticText openPositionAmountText = new StaticText();

    public StaticText getOpenPositionAmountText() {
        return openPositionAmountText;
    }

    public void setOpenPositionAmountText(StaticText st) {
        this.openPositionAmountText = st;
    }

    private TableColumn openPositionTypeColumn = new TableColumn();

    public TableColumn getOpenPositionTypeColumn() {
        return openPositionTypeColumn;
    }

    public void setOpenPositionTypeColumn(TableColumn tc) {
        this.openPositionTypeColumn = tc;
    }

    private StaticText openPositionTypeText = new StaticText();

    public StaticText getOpenPositionTypeText() {
        return openPositionTypeText;
    }

    public void setOpenPositionTypeText(StaticText st) {
        this.openPositionTypeText = st;
    }

    private TableColumn openPositionOpenPriceColumn = new TableColumn();

    public TableColumn getOpenPositionOpenPriceColumn() {
        return openPositionOpenPriceColumn;
    }

    public void setOpenPositionOpenPriceColumn(TableColumn tc) {
        this.openPositionOpenPriceColumn = tc;
    }

    private StaticText openPositionOpenPriceText = new StaticText();

    public StaticText getOpenPositionOpenPriceText() {
        return openPositionOpenPriceText;
    }

    public void setOpenPositionOpenPriceText(StaticText st) {
        this.openPositionOpenPriceText = st;
    }

    private TableColumn currentPriceColumn = new TableColumn();

    public TableColumn getCurrentPriceColumn() {
        return currentPriceColumn;
    }

    public void setCurrentPriceColumn(TableColumn tc) {
        this.currentPriceColumn = tc;
    }

    private StaticText currentPriceText = new StaticText();

    public StaticText getCurrentPriceText() {
        return currentPriceText;
    }

    public void setCurrentPriceText(StaticText st) {
        this.currentPriceText = st;
    }

    private TableColumn floatingProfitColumn = new TableColumn();

    public TableColumn getFloatingProfitColumn() {
        return floatingProfitColumn;
    }

    public void setFloatingProfitColumn(TableColumn tc) {
        this.floatingProfitColumn = tc;
    }

    private StaticText floatingProfitText = new StaticText();

    public StaticText getFloatingProfitText() {
        return floatingProfitText;
    }

    public void setFloatingProfitText(StaticText st) {
        this.floatingProfitText = st;
    }

    private TableColumn floatingCloseAmountColumn = new TableColumn();

    public TableColumn getFloatingCloseAmountColumn() {
        return floatingCloseAmountColumn;
    }

    public void setFloatingCloseAmountColumn(TableColumn tc) {
        this.floatingCloseAmountColumn = tc;
    }

    private DropDown floatingCloseAmountDropDown = new DropDown();

    public DropDown getFloatingCloseAmountDropDown() {
        return floatingCloseAmountDropDown;
    }

    public void setFloatingCloseAmountDropDown(DropDown dd) {
        this.floatingCloseAmountDropDown = dd;
    }

    private TableColumn closeColumn = new TableColumn();

    public TableColumn getCloseColumn() {
        return closeColumn;
    }

    public void setCloseColumn(TableColumn tc) {
        this.closeColumn = tc;
    }

    private Button closeButton = new Button();

    public Button getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(Button b) {
        this.closeButton = b;
    }

    private IntegerConverter integerConverter1 = new IntegerConverter();

    public IntegerConverter getIntegerConverter1() {
        return integerConverter1;
    }

    public void setIntegerConverter1(IntegerConverter ic) {
        this.integerConverter1 = ic;
    }

    private AjaxZone closedPositionZone = new AjaxZone();

    public AjaxZone getClosedPositionZone() {
        return closedPositionZone;
    }

    public void setClosedPositionZone(AjaxZone az) {
        this.closedPositionZone = az;
    }

    private Table closedPositionTable = new Table();

    public Table getClosedPositionTable() {
        return closedPositionTable;
    }

    public void setClosedPositionTable(Table t) {
        this.closedPositionTable = t;
    }

    private TableRowGroup closedPositionTableRowGroup = new TableRowGroup();

    public TableRowGroup getClosedPositionTableRowGroup() {
        return closedPositionTableRowGroup;
    }

    public void setClosedPositionTableRowGroup(TableRowGroup trg) {
        this.closedPositionTableRowGroup = trg;
    }

    private TableColumn closedPositionTypeColumn = new TableColumn();

    public TableColumn getClosedPositionTypeColumn() {
        return closedPositionTypeColumn;
    }

    public void setClosedPositionTypeColumn(TableColumn tc) {
        this.closedPositionTypeColumn = tc;
    }

    private StaticText closedPositionTypeText = new StaticText();

    public StaticText getClosedPositionTypeText() {
        return closedPositionTypeText;
    }

    public void setClosedPositionTypeText(StaticText st) {
        this.closedPositionTypeText = st;
    }

    private TableColumn closedPositionOpenPriceColumn = new TableColumn();

    public TableColumn getClosedPositionOpenPriceColumn() {
        return closedPositionOpenPriceColumn;
    }

    public void setClosedPositionOpenPriceColumn(TableColumn tc) {
        this.closedPositionOpenPriceColumn = tc;
    }

    private StaticText closedPositionOpenPriceText = new StaticText();

    public StaticText getClosedPositionOpenPriceText() {
        return closedPositionOpenPriceText;
    }

    public void setClosedPositionOpenPriceText(StaticText st) {
        this.closedPositionOpenPriceText = st;
    }

    private TableColumn closedPositionAmountColumn = new TableColumn();

    public TableColumn getClosedPositionAmountColumn() {
        return closedPositionAmountColumn;
    }

    public void setClosedPositionAmountColumn(TableColumn tc) {
        this.closedPositionAmountColumn = tc;
    }

    private StaticText closedPositionAmountText = new StaticText();

    public StaticText getClosedPositionAmountText() {
        return closedPositionAmountText;
    }

    public void setClosedPositionAmountText(StaticText st) {
        this.closedPositionAmountText = st;
    }

    private TableColumn closedPositionClosePriceColumn = new TableColumn();

    public TableColumn getClosedPositionClosePriceColumn() {
        return closedPositionClosePriceColumn;
    }

    public void setClosedPositionClosePriceColumn(TableColumn tc) {
        this.closedPositionClosePriceColumn = tc;
    }

    private StaticText closedPositionClosePriceText = new StaticText();

    public StaticText getClosedPositionClosePriceText() {
        return closedPositionClosePriceText;
    }

    public void setClosedPositionClosePriceText(StaticText st) {
        this.closedPositionClosePriceText = st;
    }

    private TableColumn closedPositionCurrencyColumn = new TableColumn();

    public TableColumn getClosedPositionCurrencyColumn() {
        return closedPositionCurrencyColumn;
    }

    public void setClosedPositionCurrencyColumn(TableColumn tc) {
        this.closedPositionCurrencyColumn = tc;
    }

    private StaticText closedPositionCurrencyText = new StaticText();

    public StaticText getClosedPositionCurrencyText() {
        return closedPositionCurrencyText;
    }

    public void setClosedPositionCurrencyText(StaticText st) {
        this.closedPositionCurrencyText = st;
    }

    private TableColumn profitColumn = new TableColumn();

    public TableColumn getProfitColumn() {
        return profitColumn;
    }

    public void setProfitColumn(TableColumn tc) {
        this.profitColumn = tc;
    }

    private StaticText profitText = new StaticText();

    public StaticText getProfitText() {
        return profitText;
    }

    public void setProfitText(StaticText st) {
        this.profitText = st;
    }

    private StaticText totalProfitLabelText = new StaticText();

    public StaticText getTotalProfitLabelText() {
        return totalProfitLabelText;
    }

    public void setTotalProfitLabelText(StaticText st) {
        this.totalProfitLabelText = st;
    }

    private StaticText totalProfitDisplayText = new StaticText();

    public StaticText getTotalProfitDisplayText() {
        return totalProfitDisplayText;
    }

    public void setTotalProfitDisplayText(StaticText st) {
        this.totalProfitDisplayText = st;
    }

    private AjaxZone totalProfitZone = new AjaxZone();

    public AjaxZone getTotalProfitZone() {
        return totalProfitZone;
    }

    public void setTotalProfitZone(AjaxZone az) {
        this.totalProfitZone = az;
    }

    private StaticText openAPositionPanelText = new StaticText();

    public StaticText getOpenAPositionPanelText() {
        return openAPositionPanelText;
    }

    public void setOpenAPositionPanelText(StaticText st) {
        this.openAPositionPanelText = st;
    }

    private HiddenField hiddenPollIndicator = new HiddenField();

    public HiddenField getHiddenPollIndicator() {
        return hiddenPollIndicator;
    }

    public void setHiddenPollIndicator(HiddenField hf) {
        this.hiddenPollIndicator = hf;
    }

    private ImageComponent exchangeRateSellImage = new ImageComponent();

    public ImageComponent getExchangeRateSellImage() {
        return exchangeRateSellImage;
    }

    public void setExchangeRateSellImage(ImageComponent ic) {
        this.exchangeRateSellImage = ic;
    }

    private ImageComponent exchangeRateBuyImage = new ImageComponent();

    public ImageComponent getExchangeRateBuyImage() {
        return exchangeRateBuyImage;
    }

    public void setExchangeRateBuyImage(ImageComponent ic) {
        this.exchangeRateBuyImage = ic;
    }

    private ImageComponent currentPriceImage = new ImageComponent();

    public ImageComponent getCurrentPriceImage() {
        return currentPriceImage;
    }

    public void setCurrentPriceImage(ImageComponent ic) {
        this.currentPriceImage = ic;
    }

    private ImageComponent floatingProfitImage = new ImageComponent();

    public ImageComponent getFloatingProfitImage() {
        return floatingProfitImage;
    }

    public void setFloatingProfitImage(ImageComponent ic) {
        this.floatingProfitImage = ic;
    }

    private HiddenField hiddenRenderId = new HiddenField();

    public HiddenField getHiddenRenderId() {
        return hiddenRenderId;
    }

    public void setHiddenRenderId(HiddenField hf) {
        this.hiddenRenderId = hf;
    }

    private HtmlPanelGrid headerPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getHeaderPanel1() {
        return headerPanel1;
    }

    public void setHeaderPanel1(HtmlPanelGrid hpg) {
        this.headerPanel1 = hpg;
    }

    private HtmlPanelGrid titlePanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getTitlePanel1() {
        return titlePanel1;
    }

    public void setTitlePanel1(HtmlPanelGrid hpg) {
        this.titlePanel1 = hpg;
    }

    private HtmlPanelGrid subPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getSubPanel1() {
        return subPanel1;
    }

    public void setSubPanel1(HtmlPanelGrid hpg) {
        this.subPanel1 = hpg;
    }

    private HtmlPanelGrid spacingPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getSpacingPanel1() {
        return spacingPanel1;
    }

    public void setSpacingPanel1(HtmlPanelGrid hpg) {
        this.spacingPanel1 = hpg;
    }

    private ImageHyperlink imageHyperlink1 = new ImageHyperlink();

    public ImageHyperlink getImageHyperlink1() {
        return imageHyperlink1;
    }

    public void setImageHyperlink1(ImageHyperlink ih) {
        this.imageHyperlink1 = ih;
    }

    private ImageHyperlink imageHyperlink2 = new ImageHyperlink();

    public ImageHyperlink getImageHyperlink2() {
        return imageHyperlink2;
    }

    public void setImageHyperlink2(ImageHyperlink ih) {
        this.imageHyperlink2 = ih;
    }

    private StaticText appNameText = new StaticText();

    public StaticText getAppNameText() {
        return appNameText;
    }

    public void setAppNameText(StaticText st) {
        this.appNameText = st;
    }

    private StaticText tagLineText = new StaticText();

    public StaticText getTagLineText() {
        return tagLineText;
    }

    public void setTagLineText(StaticText st) {
        this.tagLineText = st;
    }
    private AjaxTransaction pollTx = new AjaxTransaction();

    public AjaxTransaction getPollTx() {
        return pollTx;
    }

    public void setPollTx(AjaxTransaction at) {
        this.pollTx = at;
    }
    
    // </editor-fold>
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public Page1() {
    }
    
    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
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
            log("Page1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here
        
        boolean poll = isPollRequest();
        ThreadSynchronizer synchronizer = getSessionBean1().getSynchronizer();
        //"gesture" requests (sent via the openButton, closeButton, or floatingCloseAmountDropDown)
        //have a higher priority than "poll" requests
        ThreadPriority currentThreadPriority = poll ? synchronizer.getPriorities()[1] : synchronizer.getPriorities()[0];
        //let multiple requests from the same session through one at a time
        this.currentThreadSynchronized = synchronizer.synchronizeCurrentThread(currentThreadPriority);
    }
    
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }
    
    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
        boolean poll = isPollRequest();
        Map requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String renderId = (String)requestParameterMap.get("form1:hiddenRenderId");
        if (renderId == null) {
            //we are doing an initial render, not a postback
            renderId = getInitialRenderId();
        }
        
        CurrencyPairCache cache = getSessionBean1().getCache();
        if (poll) {
            //this is a poll request
            
            //update the cache
            cache.update(renderId);
            
            //update all the open positions
            for (Iterator iter = getSessionBean1().getOpenPositionsIterator(); iter.hasNext(); ) {
                Position openPosition = (Position)iter.next();
                String positionId = openPosition.getId();
                String pairName = openPosition.getName();
                
                //pull the currency pair from cache for this page by pairName
                //and use it to update the position
                CurrencyPair currentPair = getSessionBean1().getCache().getCurrencyPair(renderId, pairName);
                openPosition.update(currentPair);
            }
        }
        else {
            //this request was sent via the openButton, closeButton, or floatingCloseAmountDropDown
            //since we may be processing input from the floatingCloseAmountDropDown,
            //we need to commit changes on the openPositionProvider.
            getSessionBean1().getOpenPositionProvider().commitChanges();
        }
        
        //the quote table is bound to #{Page1.currencyPairProvider}
        //grab the list of currency pairs for this page from cache
        //and set it as the currencyPairProvider's list
        List currencyPairList = cache.getCurrencyPairList(renderId);
        this.currencyPairProvider = new CurrencyPairListDataProvider(currencyPairList);
    }
    
    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
        //if the current thread has the ball,
        //give it up
        if (this.currentThreadSynchronized) {
            ThreadSynchronizer synchronizer = getSessionBean1().getSynchronizer();
            synchronizer.releaseCurrentThread();
        }
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected SessionBean1 getSessionBean1() {
        return (SessionBean1)getBean("SessionBean1");
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected ApplicationBean1 getApplicationBean1() {
        return (ApplicationBean1)getBean("ApplicationBean1");
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected RequestBean1 getRequestBean1() {
        return (RequestBean1)getBean("RequestBean1");
    }

    /**
     * <p>Create a position and add it to the open positions in session.</p>
     * @return The <code>null</code> outcome.
     */
    public String openButton_action() {
        String name = (String)orderPairNameDropDown.getValue();
        Integer amount = (Integer)orderAmountDropDown.getValue();
        String type = (String)orderTypeDropDown.getValue();
        
        //grab the real-time prices for this pair name as a CurrencyPair instance
        CurrencyPairCache cache = getSessionBean1().getCache();
        CurrencyPair openPair = cache.getRealTimeCurrencyPair(name);
        
        //use the openPair to open a new position
        Position openPosition = Position.createOpenPosition(openPair, amount.intValue(), type);
        
        //add the new position to the openPositions in session
        getSessionBean1().addOpenPosition(openPosition);
        
        return null;
    }

    /**
     * <p>Close the position in question, updating the open and closed positions
     * in session.</p>
     * @return The <code>null</code> outcome.
     */
    public String closeButton_action() {
        //get the position id for the current row
        RowKey currentRow = openPositionTableRowGroup.getRowKey();
        String positionId = (String)getSessionBean1().getOpenPositionProvider().getValue("id", currentRow);
        
        //use the positionId to find the position in session
        Position position = getSessionBean1().getOpenPosition(positionId);
        String name = position.getName();
        
        //grab the real-time prices for this pair name as a CurrencyPair instance
        CurrencyPairCache cache = getSessionBean1().getCache();
        CurrencyPair closePair = cache.getRealTimeCurrencyPair(name);
        
        //determine if we are closing the whole position or just a portion of it
        int amount = position.getAmount();
        int floatingCloseAmount = position.getFloatingCloseAmount();
        int diff = amount - floatingCloseAmount;
        
        if (diff == 0) {
            //we are closing the whole position
            position.close(closePair);
            getSessionBean1().removeOpenPosition(position);
            getSessionBean1().addClosedPosition(position);
        }
        else {
            //we are closing just a portion of the position
            //adjust amount
            position.setAmount(diff);
            
            //create a new closed position and add it to the closed positions in session
            Position closedPosition = Position.createClosedPosition(closePair, floatingCloseAmount, position.getType(), position.getOpenPrice());
            getSessionBean1().addClosedPosition(closedPosition);
        }
        
        return null;
    }
    
    /**
     * <p>A <code>boolean</code> indicating whether the current thread has been successfully 
     * synchronized by the <code>ThreadSynchronizer</code>.</p>
     */
    private boolean currentThreadSynchronized;
    
    /**
     * <p>The <code>TableDataProvider</code> implementation that holds
     * a list of <code>CurrencyPair</code> instances to be rendered.</p>
     */
    private CurrencyPairListDataProvider currencyPairProvider = new CurrencyPairListDataProvider();
    
    /**
     * <p>The id we will associate with this rendered page.</p>
     */
    private String initialRenderId;
    
    /**
     * <p>Get the <code>TableDataProvider</code> implementation that holds
     * a list of <code>CurrencyPair</code> instances to be rendered.</p>
     */
    public CurrencyPairListDataProvider getCurrencyPairProvider() {
        return this.currencyPairProvider;
    }
    
    /**
     * <p>Get the id we will associate with this rendered page.
     * This property is bound to a hidden field.</p>
     */
    public String getInitialRenderId() {
        if (this.initialRenderId == null) {
            this.initialRenderId = getSessionBean1().getInitialRenderId();
        }
        return this.initialRenderId;
    }
    
    /**
     * <p>Determine whether the current request is a poll request
     * by examining the <code>form1:hiddenPollIndicator</code> and 
     * <code>form1:hiddenRenderId</code> request parameters.</p>
     */
    private boolean isPollRequest() {
        Map requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (requestParameterMap.get("form1:hiddenRenderId") == null) {
            //on the actual initial render, form1:hiddenRenderId will be null
            //consider this like a poll request            
            return true;
        }
        else {
            String pollIndicatorText = (String)requestParameterMap.get("form1:hiddenPollIndicator");
            return "pollRequest".equals(pollIndicatorText);
        }
    }
}

