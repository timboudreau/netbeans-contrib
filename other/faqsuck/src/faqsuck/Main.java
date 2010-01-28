/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package faqsuck;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Assembles a single giant HTML file out of the online developer FAQ.
 *
 * @author Tim Boudreau
 */
public class Main {
    private static final Pattern TOC_CONTENT = Pattern.compile (
        "^<hr\\s?/>(.*?)^NewPP limit report", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private static final Pattern CATEGORY_CONTENT = Pattern.compile (
        "<a name=\\\"(.*?)\".*?>.*?<span class=\"mw-headline\">(.*?)</span></h3>(.*?)(?:<a name)",
        Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNIX_LINES);

    private static final Pattern ELEMENT_HEADING = Pattern.compile (
        "<li>\\s*?<a href=\"(\\S*?)\".*?>(.*?)</a>",Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Max number of Categories and URLs to process - set low for testing changes
     */
    static final int LIMIT = Integer.MAX_VALUE; //set to 3 during development
    private static final String HEAD = "<html><head><title>NetBeans Developer FAQ</title>" +
            "<style type=\"text/css\">body {\n" +
            "   margin-left: 1.25em;  margin-right: 1.25em;\n" +
            "   font-family:\"Verdana\",\"sans-serif\";}\n" +
            "   li {margin-top:0.45em;}" +
            "   .catTitle {background-color:#AA3322; color:#FFFFFF; border-color:#AA3322; border-style:solid; border-width:0.37em; margin-top:1em; font-size:1.7em;}\n" +
            "   .itemTitle {color:#AA3322; border-bottom-style:solid; border-bottom-width:2px; border-bottom-color:#DDAA88; margin-top:0.75em; font-size:1.45em;}\n" +
            "   .itemBody {margin-left:1.5em;  margin-right:1.5em;}\n" +
            "   .tocCategory {margin-top: 1.1em; margin-bottom:0.4em;}\n" +
            "   pre { background-color:#FFFFD9; overflow:auto; border: 1px solid #CCCCCC;\n}"  +
            "   a:link {text-decoration:none}\n" +
            "   a:visited {text-decoration:none}\n" +
            "</style>" +
            "</head>\n<body>\n<h1>NetBeans Developer Faq</h1>\n";
    private static final String FOOT = "</body></html>";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java -jar faqsuck.jar url baseurl [outputfile]");
            System.exit(1);
        }
        String faqUrl = args[0];
        String baseUrl;
        if (args.length == 1) {
            int ix = faqUrl.lastIndexOf ("/");
            baseUrl = faqUrl.substring (0, ix);
        } else {
            baseUrl = args[1];
            new URL (baseUrl);
        }
        PrintStream out;

        if (args.length == 3) {
            File f = new File (args[2]);
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    throw new IllegalArgumentException ("Could not create file "
                            + f.getPath());
                }
            }
            out = new PrintStream (new BufferedOutputStream(new FileOutputStream(f)));
        } else {
            out = System.out;
        }
        try {
            System.err.println("Base url is " + baseUrl);
            System.err.println("loading " + faqUrl);
            URL url = new URL (faqUrl);
            InputStream in = url.openConnection().getInputStream();
            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            try {
                copy (in, outs);
            } finally {
                in.close();
                outs.close();
            }
            String tocContent = new String (outs.toByteArray());
            List<Category> categories = new ArrayList<Category>();
            System.err.println("Read categories");
            readCategories (baseUrl, tocContent, categories);
            out.println (HEAD);
            out.println ("<font size=\"-2\"><i>Generated on " + new Date() + "</i>" +
                    " from <a href=\"" + faqUrl + "\">" + faqUrl + "</a>" +
                    "</font><p/>");
            out.println("<ul>");
            int ix = 0;
            for (Category c : categories) {
                if (!c.allExternal()) {
                    out.println(c.toTocHtml());
                }
                ix++;
                if (ix > Main.LIMIT) { //for debugging w/o reading all entries
                    break;
                }
            }
            out.println("</ul>\n");
            out.println ("\n<hr/><p>&nbsp;</p>\n");
            ix = 0;
            for (Category c : categories) {
                if (!c.allExternal()) {
                    out.println(c.toBodyHtml());
                }
                ix++;
                if (ix > Main.LIMIT) { //for debugging w/o reading all entries
                    break;
                }
            }

            List<Entry> list = new ArrayList<Entry>();
            for (Category c : categories) {
                for (Entry e : c.entries()) {
                    if (e.isExternal()) {
                        list.add(e);
                    }
                }
            }
            if (!list.isEmpty()) {
                out.println ("<p/>");
                out.println ("<h1>External FAQ Entries</h1>");
                out.println ("The following FAQ entries are not hosted in the NetBeans wiki and could not be included:<ul>");
                for (Entry e : list) {
                    out.println ("<li><a target=\"extfaq\" href=\"" + e.url + "\">" + e.title + "</a></li>");
                }
            }
            out.println(FOOT);
        } finally {
            out.close();
        }
    }

    static void copy(InputStream is, OutputStream os)
    throws IOException {
        final byte[] BUFFER = new byte[4096];
        int len;

        for (;;) {
            len = is.read(BUFFER);

            if (len == -1) {
                return;
            }

            os.write(BUFFER, 0, len);
        }
    }

    private static void readCategories(String baseUrl, String tocContent, List<Category> categories) {
        Matcher m = TOC_CONTENT.matcher (tocContent);
        if (!m.find()) {
            throw new IllegalArgumentException ("Could not find main body content using " + TOC_CONTENT + " in\n" + tocContent);
        }
        if (m.groupCount() == 0) {
            throw new IllegalArgumentException ("Matched main body content using " + TOC_CONTENT + " in but no group");
        }
        String body = m.group(1);
        Matcher catHeaderMatcher = CATEGORY_CONTENT.matcher(body);
        while (catHeaderMatcher.find()) {
            String id = catHeaderMatcher.group(1);
            String title = catHeaderMatcher.group(2);
            String catBody = catHeaderMatcher.group(3);
            Category category = new Category (title, id);
            categories.add (category);
            Matcher elMatcher = ELEMENT_HEADING.matcher(catBody);
            while (elMatcher.find()) {
                String url = elMatcher.group(1);
                String elTitle = elMatcher.group(2);
                category.addEntry(new Entry (url, elTitle, baseUrl));
            }

        }
    }

}
