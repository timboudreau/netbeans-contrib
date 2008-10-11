package org.netbeans.modules.gsf.browser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFileChooser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.netbeans.modules.gsf.Language;
import org.netbeans.modules.gsf.LanguageRegistry;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedClass;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedElement;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedField;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedMethod;
import org.netbeans.modules.gsfret.source.usages.ClassIndexImpl;
import org.netbeans.modules.gsfret.source.usages.ClassIndexManager;
import org.netbeans.modules.gsfret.source.usages.PersistentClassIndex;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;


public final class DumpIndex extends CallableSystemAction {
    public void performAction() {
        JFileChooser fc = new JFileChooser();

        // Show save dialog; this method does not return until the dialog is closed
        if (fc.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();
                dump(selFile);
        }
    }

    private void dump(File outputFile) {
        PrintWriter writer = null;
        //Map<URL, ClassIndexImpl> map = ClassIndexManager.getDefault().getAllIndices();
        //for (URL url : map.keySet()) {
        //    ClassIndexImpl index = map.get(url);
        Map<String,Language> map = new HashMap<String,Language>();
        for (Language language : LanguageRegistry.getInstance()) {
            if (language.getIndexer() != null) {
                map.put(language.getDisplayName(), language);
            }
        }
        List<String> names = new ArrayList<String>(map.keySet());
        Collections.sort(names);
        for (String name : names) {
            Language language = map.get(name);
            assert language != null;

            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile + "." + name)));
                //writer.write("Language: " + name + " of mimetype = " + language.getMimeType() + "\n");
                //Set<ClassIndexImpl> set = ClassIndexManager.get(language).getBootIndices();
                Collection<ClassIndexImpl> set = ClassIndexManager.get(language).getAllIndices().values();
                for (ClassIndexImpl index : set) {
                    if (index instanceof PersistentClassIndex) {
                        try {
                            IndexReader reader = ((PersistentClassIndex)index).getDumpIndexReader();
                            if (reader != null) {
                                    writeDocument(writer, reader);
                            }
                        } catch (FileNotFoundException fnfe) {
                            //writer.write("\nEMPTY\n");
                        }
                    }
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                ioe.printStackTrace(writer);
            } finally {
                if (writer != null) {
                    //try {
                        writer.close();
                    //} catch (IOException ioe) {
                    //    Exceptions.printStackTrace(ioe);
                    //}
                }
            }
        }
    }
    
    private String sortCommaList(String s) {
        String[] items = s.split(",");
        Arrays.sort(items);
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item);
        }

        return sb.toString();
    }

    protected String prettyPrintValue(String key, String value) {
        if (value == null) {
            return value;
        }
        if (key.equals("timeStamp")) {
            return "-----------------";
        }
//        int timeStamp = value.indexOf("timeStamp=");
//        if (timeStamp != -1) {
//            // Strip it out, replace with ----'s to make diffs more manageable
//            // timeStamp=20071019181657325
//            int start = timeStamp+"timeStamp".length();
//            assert value.substring(start, start+17).matches("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
//            value = value.substring(0, start) + "-----------------" + value.substring(start+17);
//        }
        if ("method".equals(key)) {
            // Decode the attributes
            int attributeIndex = value.indexOf(';');
            if (attributeIndex != -1) {
                int flags = IndexedElement.stringToFlag(value, attributeIndex+1);
                if (flags != 0) {
                    String desc = IndexedMethod.decodeFlags(flags);
                    value = value.substring(0, attributeIndex) + ";" + desc + value.substring(attributeIndex+3);
                }
            }
        } else if ("attrs".equals(key)) {
            // Decode the attributes
            int flags = IndexedElement.stringToFlag(value, 0);
            if (flags != 0) {
                String desc = IndexedClass.decodeFlags(flags);
                value = desc + value.substring(2);
            } else {
                value = "|CLASS|";
            }
        } else if ("field".equals(key)) {
            // Decode the attributes
            int attributeIndex = value.indexOf(';');
            if (attributeIndex != -1) {
                int flags = IndexedElement.stringToFlag(value, attributeIndex+1);
                if (flags != 0) {
                    String desc = IndexedField.decodeFlags(flags);
                    value = value.substring(0, attributeIndex) + ";" + desc + value.substring(attributeIndex+3);
                }
            }
        }

        return value;
    }


    
    private void writeDocument(Writer writer, IndexReader reader) {
        for (int i = 0; i < reader.maxDoc(); i++) {
            try {
                Document luceneDoc = reader.document(i);

                if (luceneDoc == null) {
                    continue;
                }

                List<Match> data;
                data = new ArrayList<Match>();

                @SuppressWarnings("unchecked")
                Enumeration<Field> en = luceneDoc.fields();

                while (en.hasMoreElements()) {
                    Field f = en.nextElement();
                    String key = f.name();
                    // Skip timestamps?
                    //if (key.equals("timestamp")) {
                    //    continue;
                    //}
                    String value = f.stringValue();
                    data.add(new Match(key, value));
                }

                // Sort the data to be helpful
                Collections.sort(data,
                    new Comparator<Match>() {
                        public int compare(Match m1, Match m2) {
                            // Sort by key, then by value - except the "method" and "attribute" keys should go to the end
                            if (m1.key.equals(m2.key)) {
                                return m1.value.compareTo(m2.value);
                            }

                            if (m1.key.equals("method")) {
                                return 1;
                            }

                            if (m2.key.equals("method")) {
                                return -1;
                            }

                            if (m1.key.equals("attribute")) {
                                return 1;
                            }

                            if (m2.key.equals("attribute")) {
                                return -1;
                            }

                            return m1.key.compareTo(m2.key);
                        }
                    });

                //String label = luceneDoc.get(DocumentUtil.FIELD_FILENAME);
                String label = luceneDoc.get("filename");

                if (label == null) {
                    label = "?";
                }

                for (int j = 0; j < data.size(); j++) {
                    Match m = data.get(j);

                    if (m.key.equals("timeStamp") || m.key.equals("resName")) {
                        continue;
                    }

                    //writer.write(label + ":" /*+ j + ":"*/);
                    writer.write(m.key);
                    writer.write('=');
                    writer.write(prettyPrintValue(m.key, m.value));
                    writer.write('\n');
                }

                // Separate documents
                writer.write("\n\n");
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(DumpIndex.class, "CTL_DumpIndex");
    }

    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    private class Match {
        private String key;
        private String value;

        Match(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
