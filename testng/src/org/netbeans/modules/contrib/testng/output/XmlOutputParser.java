/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng.output;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author lukas
 */
public class XmlOutputParser extends DefaultHandler {

    private static final Logger LOG = Logger.getLogger(XmlOutputParser.class.getName());
    private int allTestsCount;
    private int failedTestsCount;
    private int skippedTestsCount; //XXX - jUnit calls these errors; need to investigate if there's error state in TestNG
    private int suiteTime;
    /** */
    private static final int STATE_OUT_OF_SCOPE = 0;
    private static final int STATE_SUITE = 3;
    private static final int STATE_GROUPS = 4;
    private static final int STATE_GROUP = 5;
    private static final int STATE_METHOD = 6;
    private static final int STATE_TEST = 7;
    private static final int STATE_CLASS = 8;
    private static final int STATE_TEST_METHOD = 9;
    private static final int STATE_TEST_PARAMS = 10;
    private static final int STATE_TEST_PARAM = 11;
    private static final int STATE_TEST_VALUE = 12;
    private static final int STATE_EXCEPTION = 13;
    private static final int STATE_MESSAGE = 14;
    private static final int STATE_FULL_STACKTRACE = 15;
    private int state = STATE_OUT_OF_SCOPE;
    /** */
    private Report suiteResult;
    private Report.Testcase testcase;
    private Report.Trouble trouble;
    private String tcClassName;
    private StringBuffer text;
    private final XMLReader xmlReader;

    /** Creates a new instance of XMLOutputParser */
    private XmlOutputParser() throws SAXException {
        xmlReader = XMLUtil.createXMLReader();
        xmlReader.setContentHandler(this);
    }

    static Report parseXmlOutput(Reader reader) throws SAXException, IOException {
        assert reader != null;
        XmlOutputParser parser = new XmlOutputParser();
        try {
            parser.xmlReader.parse(new InputSource(reader));
        } catch (SAXException ex) {
            LOG.info("Exception while parsing XML output from TestNG: " + ex.getMessage()); //NOI18N
            throw ex;
        } catch (IOException ex) {
            assert false;            /* should never happen */
        } finally {
            reader.close();          //throws IOException
        }
        return parser.suiteResult;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (state) {
            case STATE_SUITE:
                if ("groups".equals(qName)) { //NOI18N
                    //XXX - not handled yet, shoould perhaps create ie. "group" view
                    state = STATE_GROUPS;
                } else if ("test".equals(qName)) { //NOI18N
                    //test[@name] not handled for now, only change state
                    state = STATE_TEST;
                }
                break;
            case STATE_GROUPS:
                if ("group".equals(qName)) { //NOI18N
                    state = STATE_GROUP;
                }
                break;
            case STATE_GROUP:
                if ("method".equals(qName)) { //NOI18N
                    state = STATE_METHOD;
                }
                break;
            case STATE_METHOD:
                //empty for now
                break;
            case STATE_TEST:
                if ("class".equals(qName)) { //NOI18N
                    tcClassName = attributes.getValue("name"); //NOI18N
                    state = STATE_CLASS;
                }
                break;
            case STATE_CLASS:
                if ("test-method".equals(qName)) { //NOI18N
                    int duration = Integer.valueOf(attributes.getValue("duration-ms")); //NOI18N
                    testcase = createTestcaseReport(tcClassName, attributes.getValue("name"), duration); //NOI18N
                    suiteTime += duration;
                    state = STATE_TEST_METHOD;
                }
                break;
            case STATE_TEST_METHOD:
                if ("params".equals(qName)) { //NOI18N
                    state = STATE_TEST_PARAMS;
                } else if ("exception".equals(qName)) { //NOI18N
                    assert testcase != null;
                    trouble = new Report.Trouble(false); //XXX - do we have errors in TestNG? If so how to distinguish them from failures?
                    trouble.exceptionClsName = attributes.getValue("class"); //NOI18N
                    state = STATE_EXCEPTION;
                }
                break;
            case STATE_EXCEPTION:
                //how to get text msgs here?
                //exMessage =
                if ("message".equals(qName)) { //NOI18N
                    state = STATE_MESSAGE;
                } else if ("full-stacktrace".equals(qName)) { //NOI18N
                    state = STATE_FULL_STACKTRACE;
                }
                break;
            default:
                if (qName.equals("suite")) { //NOI18N
                    String sName = attributes.getValue("name"); //NOI18N
                    suiteResult = new Report(sName != null ? sName : "Unknown"); //NOI18N
                    state = STATE_SUITE;
                }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (state) {
            case STATE_GROUPS:
                assert "groups".equals(qName); //NOI18N
                state = STATE_SUITE;
                break;
            case STATE_GROUP:
                assert "group".equals(qName); //NOI18N
                state = STATE_GROUPS;
                break;
            case STATE_METHOD:
                assert "method".equals(qName) : "was " + qName; //NOI18N
                state = STATE_GROUP;
                break;
            case STATE_SUITE:
                assert "suite".equals(qName) : "was " + qName; //NOI18N
                suiteResult.elapsedTimeMillis = suiteTime;
//            XXX: is this needed?
//            suiteResult.totalTests = count(/suite/test/class/test-method)
//            suiteResult.failures = count(/suite/test/class/test-method[@status='FAIL'])
//            suiteResult.errors = count(/suite/test/class/test-method[@status='????']) - perhaps can use this for skipped tests (@status='SKIP'])
                state = STATE_OUT_OF_SCOPE;
                break;
            case STATE_TEST:
                assert "test".equals(qName); //NOI18N
                state = STATE_SUITE;
                break;
            case STATE_CLASS:
                assert "class".equals(qName); //NOI18N
                tcClassName = null;
                state = STATE_TEST;
                break;
            case STATE_TEST_METHOD:
                assert "test-method".equals(qName) : "was " + qName; //NOI18N
                assert testcase != null;
                suiteResult.reportTest(testcase, Report.InfoSource.XML_FILE);
                testcase = null;
                state = STATE_CLASS;
                break;
            case STATE_TEST_PARAMS:
                //XXX - param and value elements are not handled yet
                if ("param".equals(qName) || "value".equals(qName)) { //NOI18N
                    break;
                }
                assert "params".equals(qName) : "was " + qName; //NOI18N
                state = STATE_TEST_METHOD;
                break;
            case STATE_EXCEPTION:
                assert "exception".equals(qName); //NOI18N
                state = STATE_TEST_METHOD;
                break;
            case STATE_MESSAGE:
                assert "message".equals(qName); //NOI18N
                assert testcase != null;
                assert trouble != null;
                if (text != null) {
                    trouble.message = text.toString();
                    text = null;
                }
                state = STATE_EXCEPTION;
                break;
            case STATE_FULL_STACKTRACE:
                assert "full-stacktrace".equals(qName); //NOI18N
                if (text != null) {
                    trouble.stackTrace = text.toString().split("[\\r\\n]+"); //NOI18N
                    text = null;
                }
                testcase.trouble = trouble;
                trouble = null;
                state = STATE_EXCEPTION;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        switch (state) {
            case STATE_MESSAGE:
            case STATE_FULL_STACKTRACE:
                if (text == null) {
                    text = new StringBuffer(512);
                }
                text.append(ch, start, length);
                break;
        }
    }

    private static Report.Testcase createTestcaseReport(String className, String name, int time) {
        Report.Testcase tc = new Report.Testcase();
        tc.className = className;
        tc.name = name;
        tc.timeMillis = time;
        return tc;
    }
}
