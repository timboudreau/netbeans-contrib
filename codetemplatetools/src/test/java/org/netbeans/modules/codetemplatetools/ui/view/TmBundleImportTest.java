package org.netbeans.modules.codetemplatetools.ui.view;

import junit.framework.TestCase;


/**
 *
 * @author Tor Norbye
 */
public class TmBundleImportTest extends TestCase {
    public TmBundleImportTest(String testName) {
        super(testName);
    }

    public void testSimple() {
        assertEquals("foo", TmBundleImport.testConversion("foo"));
    }

    public void testVars() {
        assertEquals("foo ${0 default=\"hello\"}", TmBundleImport.testConversion("foo ${0:hello}"));
        assertEquals("foo ${0 default=\"hello bar\"}", TmBundleImport.testConversion("foo ${0:hello bar}"));
        assertEquals("foo ", TmBundleImport.testConversion("foo ${0:hello {bar}}"));
        // Make sure we strip out empty stuff that was dropped from nested braces
        assertEquals("foo ${cursor}", TmBundleImport.testConversion("foo $0${1:hello {bar}}"));
        assertEquals("foo { ${cursor} } { ${tabStop1 default=\"\"} }",
            TmBundleImport.testConversion("foo { $0 } { $1 }"));

        // TM doesn't seem to use this, at least not in the Ruby snippets
        //assertEquals("foo ${hello} ${hello}${hello}", TmBundleImport.testConversion("foo ${0:hello} ${0}${0}"));
    }

    public void testRegexps() {
        // Regexps are disabled for now
        //assertEquals("foo ${cursor}", TmBundleImport.testConversion("foo ${0/foo/bar/}"));
        //assertEquals("foo ${tabStop1 default=\"\"}", TmBundleImport.testConversion("foo ${1/foo/bar/}"));
        assertEquals(null, TmBundleImport.testConversion("foo ${0/foo/bar/}"));
        assertEquals(null, TmBundleImport.testConversion("foo ${1/foo/bar/}"));
    }

    public void testPipes() {
        assertEquals("||foo||", TmBundleImport.testConversion("|foo|"));
    }

    public void testEscapes() {
        assertEquals("$0 ${tabStop1 default=\"\"} $2", TmBundleImport.testConversion("\\$0 $1 \\$2"));
    }

    public void testCursor() {
        assertEquals("foo { ${cursor} }", TmBundleImport.testConversion("foo { $0 }"));
        assertEquals("${cursor} ${tabStop1 default=\"\"}", TmBundleImport.testConversion("$0 $1"));
    }

    public void testSelection() {
        assertEquals("foo { ${selection line allowSurround} }",
            TmBundleImport.testConversion("foo { $TM_SELECTED_TEXT }"));
    }

    public void testParenCommand() {
        assertEquals("(", TmBundleImport.testConversion("`snippet_paren.rb`"));
        assertEquals(")", TmBundleImport.testConversion("`snippet_paren.rb end`"));
        assertEquals("(he(l)lo)",
            TmBundleImport.testConversion(
                "`snippet_paren.rb`he`snippet_paren.rb`l`snippet_paren.rb end`lo`snippet_paren.rb end`"));
    }

    public void testComplex() {
        assertEquals("assert_in_delta(${1 default=\"expected_float\"}, ${2 default=\"actual_float\"}, ${0 default=\"2 ** -20\"})",
            TmBundleImport.testConversion(
                "assert_in_delta`snippet_paren.rb`${1:expected_float}, ${2:actual_float}, ${0:2 ** -20}`snippet_paren.rb end`"));
    }

    // Fails
    public void testNesting() {
        // Ugh!!!
        assertEquals("(void)add${Thing}:(${id})a${Thing}",
            TmBundleImport.testConversion("(void)add${1:Thing}:(${2:id})${3:a$1}"));

        // NSEnumerator *${2:${1:string}Enum} = [${3:$1Array} objectEnumerator];
        //   $1Array should be ${1}Array ?
        // \$this-&gt;${1:ModelName}-&gt;field(${2:\$name}${3:, array(${4:'$1.created'=&gt;'&lt;= now()'}})}${5:, ${6:'created DESC'}});
    }

    public void testDuplicates() {
        assertEquals("foo ${0 default=\"hello\"} ${1} ${0 editable=\"false\"} ${1 editable=\"false\"}", TmBundleImport.testConversion("foo ${0:hello} ${1} ${0} ${1}"));
    }
}
