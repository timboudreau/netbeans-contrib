/*
 * ComponentGeneratorTest.java
 *
 * Created on July 11, 2002, 2:27 PM
 */

package org.netbeans.modules.jemmysupport.bundlelookup;

import java.io.*;

import junit.framework.*;
import org.netbeans.junit.*;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.actions.PasteAction;
import org.netbeans.jellytools.modules.jemmysupport.*;

/** JUnit test suite with Jemmy/Jelly2 support
 *
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public class ResourceBundleLookupTest extends JellyTestCase {
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public ResourceBundleLookupTest(String testName) {
        super(testName);
    }
    
    /** method used for explicit testsuite definition
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new ResourceBundleLookupTest("testSearch1"));
        suite.addTest(new ResourceBundleLookupTest("testSearch2"));
        suite.addTest(new ResourceBundleLookupTest("testSearch3"));
        suite.addTest(new ResourceBundleLookupTest("testSearch4"));
        suite.addTest(new ResourceBundleLookupTest("testSearch5"));
        suite.addTest(new ResourceBundleLookupTest("testSearch6"));
        suite.addTest(new ResourceBundleLookupTest("testResultsPopup"));
        return suite;
    }
    
    /** method called before each testcase
     */
    protected void setUp() throws IOException {
    }
    
    /** method called after each testcase
     */
    protected void tearDown() {
    }
    
    /** Use for internal test execution inside IDE
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
                
    /** simple test case
     */
    public void testSearch1() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(false);
        op.checkRegularExpressionText(false);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch2() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(true);
        op.checkSubstringText(true);
        op.checkRegularExpressionText(false);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch3() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(true);
        op.checkRegularExpressionText(false);
        op.search();
        op.verifyStatus("Found 4 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch4() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("te[s]t.alue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(false);
        op.checkRegularExpressionText(true);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch5() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("te[s]t.alue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(true);
        op.checkSubstringText(true);
        op.checkRegularExpressionText(true);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch6() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("te[s]t.alue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(true);
        op.checkRegularExpressionText(true);
        op.search();
        op.verifyStatus("Found 4 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testResultsPopup() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkCaseSensitiveText(true);
        op.checkSubstringText(false);
        op.checkRegularExpressionText(false);
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        op.search();
        op.verifyStatus("Found 1 keys.");
        int i=op.tabSearchResults().findCellRow("testkey");
        op.tabSearchResults().selectCell(i, 0);
        op.tabSearchResults().clickForPopup();
        JPopupMenuOperator popup=new JPopupMenuOperator();
        new JMenuItemOperator(popup, "Copy: java.util.ResourceBundle.getBundle(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\").getString(\"testkey\")");
        new JMenuItemOperator(popup, "Copy: org.openide.util.NbBundle.getBundle(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\").getString(\"testkey\")");
        new JMenuItemOperator(popup, "Copy: org.netbeans.jellytools.Bundle.getString(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\", \"testkey\")");
        new JMenuItemOperator(popup, "Copy: org.netbeans.jellytools.Bundle.getStringTrimmed(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\", \"testkey\")").push();
        op.setSearchedText("");
        op.txtSearchedText().clickMouse();
        new PasteAction().performShortcut(op.txtSearchedText());
        assertEquals("org.netbeans.jellytools.Bundle.getStringTrimmed(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\", \"testkey\")", op.getSearchedText());
        op.setSearchedText("");
        op.verify();
        op.close();
    }
}
