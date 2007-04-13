/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.codetemplatetools.ui.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.xml.XMLUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Import TM bundles  (.tmbundle) into NetBeans live code templates.
 * Special support for Ruby-oriented bundles (for example it knows
 * what the shell command `snippet_paren.rb end` means.)
 *
 * @todo i18n
 * @todo progress bar
 *
 * @author Tor Norbye
 */
public class TmBundleImport {
    // UUIDs for macros we know about that we have already built in in a better way and importing
    // would clobber
    private static String[] handledBetter =
        new String[] {
            "0F940CBC-2173-49FF-B6FD-98A62863F8F2", "855FC4EF-7B1E-48EE-AD4E-5ECB8ED79D1C",
            "855FC4EF-7B1E-48EE-AD4E-5ECB8ED79D1C"
        };
    public static final String RUBY_MIME_TYPE = "text/x-ruby"; // application/x-ruby is also used a fair bit.
    public static final String RHTML_MIME_TYPE = "application/x-httpd-eruby"; // NOI18N
    private int imported;
    private StringBuilder log = new StringBuilder();
    private final String TMKEY = "key"; // NOI18N
    private final String TMSTRING = "string"; // NOI18N
    private final String TMDICT = "dict"; // NOI18N
    private final String TMCONTENT = "content"; // NOI18N
    private final String TMNAME = "name"; // NOI18N
    private final String TMSCOPE = "scope"; // NOI18N
    private final String TMTAGTRIGGER = "tabTrigger"; // NOI18N
    private final String TMUUID = "uuid"; // NOI18N
    private Map /*<String,Map<String,String>>*/ propsByMime =
        new HashMap /*<String,Map<String,String>>*/();
    private String defaultMime;

    public TmBundleImport() {
    }

    public Map /*String,Map<String,String>>*/ importBundle(File file, String defaultMime) {
        this.defaultMime = defaultMime;

        File snippets = new File(file, "Snippets");

        if (!snippets.exists()) {
            log.append(file.getPath() + " does not exist - is this really a TM bundle?\n");

            return null;
        }

        log.append("IMPORT SUMMARY\n------------------------------\n\n");

        // TODO - Macros, binaries, others?
        File[] snippetFiles = snippets.listFiles();

        for (File f : snippetFiles) {
            if (f.getName().endsWith(".plist")) {
                importFile(f);
            }
        }

        log.append("\n\n");
        //log.append(NbBundle.getMessage(TmBundleImport.class, "TmImportSummary", imported));
        log.append("Imported " + imported + " snippets.");

        JTextArea text = new JTextArea();
        text.setColumns(60);
        text.setRows(15);
        text.setText(log.toString());
        text.setCaretPosition(0);

        NotifyDescriptor nd =
            new NotifyDescriptor.Message(new JScrollPane(text),
                NotifyDescriptor.Message.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);

        return propsByMime;
    }

    /** Get text within an element */
    private String getText(Element element) {
        StringBuilder sb = new StringBuilder();
        NodeList nl = element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);

            if (n.getNodeType() == Node.TEXT_NODE) {
                sb.append(n.getNodeValue());
            }
        }

        return sb.toString();
    }

    private void importFile(File file) {
        try {
            Element r = null;

            try {
                InputSource inputSource = new InputSource(new FileReader(file));
                org.w3c.dom.Document doc = XMLUtil.parse(inputSource, false, false, null, null);
                r = doc.getDocumentElement();
            } catch (SAXParseException spe) {
                log.append("Parsing error in \"" + file.getName() + "\"; skipping\n  " +
                    spe.getMessage());

                return;
            }

            NodeList dicts = r.getElementsByTagName(TMDICT); // NOI18N

            if (dicts.getLength() != 1) {
                log.append(("Unexpected number of <dict> elements in plist \"" + file.getName() +
                    "\"\n"));

                return;
            }

            Element dict = (Element)dicts.item(0);
            NodeList properties = dict.getChildNodes();
            Map<String, String> map = new HashMap<String, String>();
            String key = null;

            for (int i = 0; i < properties.getLength(); i++) {
                Node node = properties.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element e = (Element)node;

                if (e.getTagName().equals(TMKEY)) { // NOI18N
                    key = getText(e);
                } else if (e.getTagName().equals(TMSTRING)) { // NOI18N

                    String value = getText(e);

                    if (key == null) {
                        log.append("Abort: Unexpected <string> (missing key) for " + value +
                            " in \"" + file.getName() + "\"\n");

                        return;
                    }

                    map.put(key, value);
                }
            }

            if (map.containsKey(TMTAGTRIGGER) && map.containsKey(TMCONTENT)) {
                String tabTrigger = map.get(TMTAGTRIGGER);
                String content = map.get(TMCONTENT);

                String name = map.get(TMNAME);

                // Not yet used...
                String scope = map.get(TMSCOPE);
                String uuid = map.get(TMUUID);

                if (skipKnownRubyTemplates(tabTrigger, content, name, scope, uuid)) {
                    log.append("Skipping snippet " + tabTrigger + ": it is already builtin\n");

                    return;
                }

                String netbeansAbbrev = convertTmContent(name, content, log);

                if (netbeansAbbrev == null) {
                    // Couldn't convert - might contain unsuppored syntax
                    return;
                }

                String mimeType = getMimeType(scope);

                addAbbrev(mimeType, tabTrigger, name, netbeansAbbrev);
            } else {
                log.append(("The snippet \"" + file.getName() +
                    "\" is bound to another key than Tab - skipping\n"));

                return;
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
        } catch (SAXException se) {
            ErrorManager.getDefault().notify(se);
        }
    }

    /** Get a mime type to use for the given TM scope */
    private String getMimeType(String scope) {
        if (scope.startsWith("source.ruby")) { // NOI18N

            return RUBY_MIME_TYPE;
        }

        if (scope.startsWith("text.html.ruby")) { // NOI18N

            return RHTML_MIME_TYPE;
        }

        if (scope.startsWith("source.yaml")) { // NOI18N

            return "text/x-yaml";
        }

        // TODO
        // text.html, (string.quoted.double.ruby|string.interpolated.ruby) - string source.
        // TODO - TM macros completely unrelated to Ruby - e.g. python etc.
        return defaultMime;
    }

    /**
     * Avoid importing templates that are already built in and handled in a better way than the
     * importer will
     */
    private boolean skipKnownRubyTemplates(String tabTrigger, String content, String name,
        String scope, String uuid) {
        for (String id : handledBetter) {
            if (id.equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    /** For unit testing */
    public static String testConversion(String content) {
        return convertTmContent(null, content, new StringBuilder());
    }

    private static String convertTmContent(String name, String content, StringBuilder log) {
        StringBuilder sb = new StringBuilder();

        // Convert the abbreviation in content into a NetBeans-style abbreviation

        // This means changing the escaping rules (in NetBeans, | must be written as || right now),
        // changing variable name references,
        // and handling TM-specific stuff like backquotes (execute command), etc.

        // collect { |${1:e}| $0 }   =>    collect { ||${e}|| ${cursor}

        // $num => tabStop$num
        // Tricky:
        // open("path;or;url", "w") do |doc| .. end (ope).plist:   
        //   <string>open(${1:"${2:path/or/url/or/pipe}"}${3/(^[rwab+]+$)|.*/(?1:, ")/}${3:w}${3/(^[rwab+]+$)|.*/(?1:")/}) { |${4:io}| $0 }</string>

        // Not handled: nested expressions ${1::${something}} - look in opt parse
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            // Escaped chars - \$ for example should insert "$"
            if (c == '\\') {
                if (i < (content.length() - 1)) {
                    i++;
                    c = content.charAt(i);
                }

                sb.append(c);

                continue;
            } else if (c == '|') {
                // NetBeans requires || for | (because | used to mean the cursor position)
                sb.append("||");

                continue;
            } else if ((c == '$') && (i < (content.length() - 1))) {
                char peek = content.charAt(i + 1);

                if (Character.isDigit(peek)) {
                    // It's $0, $1, $2, .... these are tabstops.
                    i++; // Skip both
                    sb.append('$');
                    sb.append('{');
                    sb.append(getTabStopString(peek));
                    sb.append('}');
                } else if ((peek == '{') && (i < (content.length() - 3))) {
                    // Special variable section:
                    // ${0:foo} --> ${foo}
                    char peek2 = content.charAt(i + 2);
                    char peek3 = content.charAt(i + 3);

                    if (Character.isDigit(peek2) && (peek3 == ':')) {
                        sb.append("${");
                        i += 3;
                    } else if (Character.isDigit(peek2) && (peek3 == '/')) {
                        //// Regexp
                        //// Strip to the end
                        //sb.append("${");
                        //sb.append(getTabStopString(peek2));
                        //for (; i < content.length(); i++) {
                        //    c = content.charAt(i);
                        //    if (c == '}') {
                        //        sb.append('}');
                        //        break;
                        //    }
                        //}
                        //log.append("Not yet supported: Stripping out regular expression substitution from the snippet " + name + "\n");
                        //continue;
                        log.append(
                            "Regular expression substitution not supported; skipping snippet \"" +
                            name + "\"\n");

                        return null;
                    } else {
                        // ${ but not ${[digit]: - just put in verbatim
                        sb.append(c);
                    }
                } else if (peek == 'T') {
                    // Look for special variables - TM_SELECTED_WORD, TM_
                    String SELECTED_TEXT = "TM_SELECTED_TEXT";

                    if (content.regionMatches(false, i + 1, SELECTED_TEXT, 0, SELECTED_TEXT.length())) {
                        i += SELECTED_TEXT.length();
                        sb.append("${selection line allowSurround}");

                        continue;
                    }

                    //http://macromates.com/textmate/manual/snippets
                    // TODO:  TM_FILENAME, TM_FILEPATH, TM_CURRENT_LINE, TM_COLUMN_NUMBER,
                    // TM_CURRENT_WORD, TM_PROJECT_DIRECTORY, TM_DIRECTORY, TM_LINE_NUMBER,
                    // TM_SOFT_TABS, TM_TAB_SIZE, TM_MINIMIZE_PAREN
                    // Turns out, only TM_SELECTED_TEXT seems to be used at least in the Ruby and Rails bundles I've
                    // seen so I won't work too hard to support this
                    sb.append(c);
                } else {
                    sb.append(c);
                }
            } else if (c == '`') {
                // Special case: minimize parens. Later I can consider doing something
                // more clever here where I also do conditional parenthesis control
                String END_PAREN = "`snippet_paren.rb end`"; // NOI18N

                if (content.regionMatches(false, i, END_PAREN, 0, END_PAREN.length())) {
                    i += END_PAREN.length();
                    i--; // compensate for loop iteration increment
                    sb.append(")");

                    continue;
                }

                String BEGIN_PAREN = "`snippet_paren.rb`"; // NOI18N

                if (content.regionMatches(false, i, BEGIN_PAREN, 0, BEGIN_PAREN.length())) {
                    i += BEGIN_PAREN.length();
                    i--; // compensate for loop iteration increment
                    sb.append("(");

                    continue;
                }

                // Command execution: Not supported!  (But in the Ruby bundle I'm looking at,
                // this isn't used so shouldn't be a huge problem)
                log.append("Shell command execution is not supported; skipping snippet \"" + name +
                    "\"\n");

                return null;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static String getTabStopString(char digit) {
        assert Character.isDigit(digit);

        if (digit == '0') {
            return "cursor"; // NOI18N
        } else {
            return "tabStop" + digit + " default=\"\""; // NOI18N
        }
    }

    /**
     * @todo Do something about desc?
     * @todo Add a UID to easy in import duplicate avoidance?
     */
    private void addAbbrev(String mime, String key, String desc, String content) {
        Map /*<String,String*/ map = (Map)propsByMime.get(mime);

        if (map == null) {
            map = new HashMap /*<String,String>*/();
            propsByMime.put(mime, map);
        }

        map.put(key, content);

        imported++;
    }
}
