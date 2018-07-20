package org.netbeans.modules.toolsintegration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor.Message;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;


class ExternalTool {
    
    public static final int NO_ANNOTATION = 0;
    public static final int ERROR_ANNOTATION = 1;
    public static final int WARNING_ANNOTATION = 2;
    public static final int SUGGESTION_ANNOTATION = 3;
    public static final int TASK_ANNOTATION = 4;
    
    public static final ExternalTool NEW = new ExternalTool (
        "New Tool",
        "", "", Collections.EMPTY_LIST, Collections.EMPTY_LIST,
        false, // inheritIDEEnvironment
        true,  // showOutput
        true,  // showError
        false, // showInput
        false, // alwaysNewTab
        false, // append
        false, // highlightOutput
        null,  // highlightOutputExpression
        NO_ANNOTATION
    );
    
    
    private String  name;
    private String  fileName;
    private String  workingDirectory;
    private List    parameters;
    private List    variables;
    private String  parametersAsText;
    private String  variablesAsText;
    
    private boolean inheritIDEEnvironment;
    private boolean showOutput;
    private boolean showError;
    private boolean showInput;
    private boolean newTabAlways;
    private boolean append;
    private boolean highlightOutput;
    private String  highlightExpression;
    private int     annotateAs;

    
    ExternalTool (
        String name, 
        String fileName, 
        String workingDirectory, 
        List parameters, 
        List variables,
        
        boolean inheritIDEEnvironment,
        boolean showOutput,
        boolean showError,
        boolean showInput,
        boolean newTabAlways,
        boolean append,
        boolean highlightOutput,
        String  highlightExpression,
        int     annotateAs
    ) {
        this.name = name;
        this.fileName = fileName;
        this.workingDirectory = workingDirectory;
        if (this.workingDirectory != null &&
            this.workingDirectory.trim ().length () == 0
        )
            this.workingDirectory = null;
        this.parameters = Collections.unmodifiableList(parameters);
        this.variables = Collections.unmodifiableList(variables);
        
        this.inheritIDEEnvironment = inheritIDEEnvironment;
        this.showOutput = showOutput;
        this.showError = showError;
        this.showInput = showInput;
        this.newTabAlways = newTabAlways;
        this.append = append;
        this.highlightOutput = highlightOutput;
        this.highlightExpression = highlightExpression;
        this.annotateAs = annotateAs;
    }
    
    ExternalTool (
        String name, 
        String fileName, 
        String workingDirectory, 
        String parameters, 
        String variables,
        
        boolean inheritIDEEnvironment,
        boolean showOutput,
        boolean showError,
        boolean showInput,
        boolean newTabAlways,
        boolean append,
        boolean highlightOutput,
        String  highlightExpression,
        int     annotateAs
    ) {
        this.name = name;
        this.fileName = fileName;
        this.workingDirectory = workingDirectory;
        if (this.workingDirectory != null &&
            this.workingDirectory.trim ().length () == 0
        )
            this.workingDirectory = null;

        // parse parameters
        this.parameters = new ArrayList ();
        int i = 0;
        while (i < parameters.length ()) {
            String param = null;
            if (parameters.charAt (i) == '\"') {
                int j = parameters.indexOf ("\" ", i + 1);
                this.parameters.add (parameters.substring (i + 1, j));
                i = j + 2;
            }  else {
                int j = parameters.indexOf (' ', i);
                if (j < 0) j = parameters.length ();
                this.parameters.add (parameters.substring (i, j));
                i = j + 1;
            }
        };
        
        // parse variables
        this.variables = new ArrayList ();
        Properties properties = new Properties ();
        try {
            properties.load(new ByteArrayInputStream (variables.getBytes()));
            Enumeration en = properties.propertyNames ();
            while (en.hasMoreElements()) {
                String propertyName = (String) en.nextElement ();
                this.variables.add (propertyName + "=" + properties.getProperty (propertyName));
            }

            this.parameters = Collections.unmodifiableList(this.parameters);
            this.variables = Collections.unmodifiableList(this.variables);
        }  catch (IOException ex) {
        }
        
        this.inheritIDEEnvironment = inheritIDEEnvironment;
        this.showOutput = showOutput;
        this.showError = showError;
        this.showInput = showInput;
        this.newTabAlways = newTabAlways;
        this.append = append;
        this.highlightOutput = highlightOutput;
        this.highlightExpression = highlightExpression;
        this.annotateAs = annotateAs;
    }
    
    String getName () {
        return name;
    }

    String getFileName () {
        return fileName;
    }

    String getWorkingDirectory () {
        return workingDirectory;
    }

    List getParameters () {
        return parameters;
    }

    List getVariables () {
        return variables;
    }

    boolean isInheritIDEEnvironment () {
        return inheritIDEEnvironment;
    }

    boolean isShowOutput () {
        return showOutput;
    }

    boolean isShowError () {
        return showError;
    }

    boolean isShowInput () {
        return showInput;
    }

    boolean isNewTabAlways () {
        return newTabAlways;
    }

    boolean isAppend () {
        return append;
    }

    boolean isHighlightOutput () {
        return highlightOutput;
    }

    String getHighlightExpression () {
        if (!highlightOutput) return null;
        return highlightExpression;
    }
    
    int getAnnotateAs () {
        if (!isHighlightOutput ()) return NO_ANNOTATION;
        return annotateAs;
    }

    String getParametersAsText () {
        if (parametersAsText == null) {
            StringBuffer sb = new StringBuffer ();
            Iterator it = parameters.iterator ();
            while (it.hasNext()) {
                sb.append((String) it.next()).append(' ');
            }
            parametersAsText = sb.toString ().trim ();
        }
        return parametersAsText;
    }

    String getVariablesAsText () {
        if (variablesAsText == null) {
            StringBuffer sb = new StringBuffer ();
            Iterator it = variables.iterator ();
            while (it.hasNext()) {
                sb.append((String) it.next()).append('\n');
            }
            variablesAsText = sb.toString ();
        }
        return variablesAsText;
    }
    
    private Pattern pattern;
    private boolean patternInitialized = false;
    
    private Pattern getPattern () {
        if (!patternInitialized) {
            if (highlightExpression != null) {
                String expression = highlightExpression.replaceAll (
                        OutputFilterEditor.FILE_NAME1,
                        FILE_NAME
                );
                expression = expression.replaceAll (
                        OutputFilterEditor.CLASS_NAME1,
                        CLASS_NAME
                );
                expression = expression.replaceAll (
                        OutputFilterEditor.LINE_NUMBER1,
                        LINE_NUMBER
                );
                try {
                    pattern = Pattern.compile (expression);
                } catch (PatternSyntaxException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
            }
            patternInitialized = true;
        }
        return pattern;
    }
    
    void exec () {
        try {
            // remove old annotations
            ToolsAnnotation.removeAllAnnotations (getName ());
            
            // init command + params + resolve params
            String[] command = new String [parameters.size () + 1];
            command [0] = fileName;
            Iterator it = getParameters ().iterator ();
            int i = 1;
            while (it.hasNext())
                command [i ++] = Variables.resolveVariables (
                    (String) it.next ()
                );
            
            // check working dir
            File workingDir = getWorkingDirectory () == null ? 
                null : 
                new File (Variables.resolveVariables (getWorkingDirectory ()));
            if (workingDir != null && !workingDir.exists ()) {
                DialogDisplayer.getDefault ().notify (new Message (
                    workingDir + "working dir does not exist!", 
                    Message.WARNING_MESSAGE
                ));
                return;
            }
            System.out.println("workingDir " + workingDir);
            
            // start process
            Process process = Runtime.getRuntime ().exec (
                command,
                isInheritIDEEnvironment () ?
                    null :
                    (String[]) variables.toArray 
                        (new String [variables.size ()]),
                workingDir
            );
            
            // parse output to Output Window and Anotations
            boolean showOutput = isShowError () || isShowInput () || 
                isShowOutput ();
            boolean showAnnotations = getAnnotateAs () != NO_ANNOTATION;
            InputOutput inputOutput = null;
            if (showOutput) {
                inputOutput = IOProvider.getDefault ().getIO 
                    (getName (), isNewTabAlways ());
                inputOutput.select ();
                if (!isNewTabAlways () && !isAppend ())
                    inputOutput.getOut ().reset ();
                inputOutput.getOut ().println (toString (command, variables));
            }
            if (isShowInput ())
                new CopyMaker (
                    inputOutput.getIn (), 
                    new OutputStreamWriter (process.getOutputStream()), 
                    true
                ).start ();
            if (isShowOutput () || showAnnotations) {
                new OutputCopy (
                    process.getInputStream (), 
                    inputOutput == null ? null : inputOutput.getOut (),
                    isShowOutput (),
                    getPattern (),
                    getName (),
                    getAnnotateAs ()
                ).start ();
            }
            if (isShowError () || showAnnotations)
                new OutputCopy (
                    process.getErrorStream (), 
                    inputOutput == null ? null : inputOutput.getErr (),
                    isShowError (),
                    getPattern (),
                    getName (),
                    getAnnotateAs ()
                ).start ();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    private static String toString (String[] command, List parameters) {
        StringBuffer sb = new StringBuffer ();
        if (command.length > 0) {
            sb.append (command [0]);
            int i, k = command.length;
            for (i = 1; i < k; i++) {
                sb.append (' ').append (command [i]);
            }
        }
        return sb.toString ();
    }
    
    public String toString () {
        return super.toString () + name + " : " + fileName;
    }
    
    private static String     LINE_NUMBER = "[0-9]+";
    private static String     FILE_NAME = "[a-zA-Z][:]?[a-zA-Z0-9_\\\\/ \\.]+"; // means: [a-zA-Z0-9_:\/ .]+
    private static String     CLASS_NAME = "([a-zA-Z][a-zA-Z0-9_]+)+";
    private static Pattern    LINE_NUMBER_PATTERN = Pattern.compile (LINE_NUMBER);
    private static Pattern    FILE_NAME_PATTERN = Pattern.compile (FILE_NAME); 
    private static Pattern    CLASS_NAME_PATTERN = Pattern.compile (CLASS_NAME);
        
    private static Object[] parse (String message) {
        String text = message;
        System.out.println (message);
        
        Matcher m = FILE_NAME_PATTERN.matcher (message);
        if (!m.lookingAt ()) return null;
        String name = m.group ();
        System.out.println ("  :" + name);
        message = message.substring (name.length ());

        m = LINE_NUMBER_PATTERN.matcher (message);
        if (!m.find ()) return null;
        String lineNumberString = m.group ();
        System.out.println ("  :" + lineNumberString);
        int lineNumber = Integer.parseInt (lineNumberString);

        FileObject fo = FileUtil.toFileObject (new File (name));
        if (fo == null) return null;
        try {
            DataObject dob = DataObject.find (fo);
            LineCookie lineCookie = (LineCookie) dob.getCookie 
                (LineCookie.class);
            if (lineCookie == null) return null;
            Line line = lineCookie.getLineSet ().getOriginal (lineNumber - 1);
            return new Object[] {line, text};
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
    }
    
    
   /** This thread simply reads from given Reader and writes read chars to given Writer. */
    private static class OutputCopy extends Thread implements OutputListener {
        
        private OutputWriter      outputWriter;
        private BufferedReader    reader;
        private boolean           writeToOutput;
        private String            name;
        private int               annotateAs;
        private Pattern           pattern;

        OutputCopy (
            InputStream     inputStream,
            OutputWriter    outputWriter,
            boolean         writeToOutput,
            Pattern         pattern,
            String          name,
            int             annotateAs
        ) {
            this.outputWriter = outputWriter;
            this.writeToOutput = writeToOutput;
            reader = new BufferedReader (new InputStreamReader (inputStream));
            this.pattern = pattern;
            this.name = name;
            this.annotateAs = annotateAs;
        }

        /* Makes copy. */
        public void run () {
            String line;
            try {                
                while ((line = reader.readLine ()) != null) {
                    if (pattern != null) {
                        Matcher m = pattern.matcher (line);
                        if (m.find ()) {
                            if (writeToOutput)
                                outputWriter.println (line, this);
                            if (annotateAs != ExternalTool.NO_ANNOTATION) {
                                Object[] result = parse (line);
                                ToolsAnnotation.addAnnotation (
                                    (Line) result [0], 
                                    name, 
                                    annotateAs, 
                                    (String) result [1]
                                );        
                            }
                            continue;
                        }
                    }
                    if (writeToOutput)
                        outputWriter.println (line);
                }
            } catch (IOException ex) {
                ErrorManager.getDefault ().notify (ex);
            } finally {
                if (writeToOutput)
                    outputWriter.close ();
            }
        }
        
        public void outputLineAction (OutputEvent ev) {
            System.out.println("outputLineAction " + ev.getLine ());
            Object[] result = parse (ev.getLine ());
            Line line = (Line) result [0];
            line.show (line.SHOW_TOFRONT);
        }
        
        public void outputLineSelected (OutputEvent ev) {}
        public void outputLineCleared (OutputEvent ev) {}
    } // end of CopyMaker
    
   /** This thread simply reads from given Reader and writes read chars to given Writer. */
    private static class CopyMaker extends Thread {
        final Writer os;
        final Reader is;
        /** while set to false at streams that writes to the OutputWindow it must be
        * true for a stream that reads from the window.
        */
        final boolean autoflush;
        private boolean done = false;

        CopyMaker (Reader is, Writer os, boolean b) {
            this.os = os;
            this.is = is;
            autoflush = b;
        }

        /* Makes copy. */
        public void run() {
            int read;
            char[] buff = new char [256];
            try {                
                while ((read = read(is, buff, 0, 256)) > 0x0) {
                    os.write(buff,0,read);
                    if (autoflush) os.flush();
                }
               } catch (IOException ex) {
            } catch (InterruptedException e) {
            }
        }
        
        public void interrupt() {
            super.interrupt();
            done = true;
        }
        
        private int read(Reader is, char[] buff, int start, int count) throws InterruptedException, IOException {
            // XXX (anovak) IBM JDK 1.3.x on OS/2 is broken
            // is.ready()/available() returns false/0 until
            // at least one byte from the stream is read.
            // Then it works as advertised.
            // isao 2001-11-12: ditto for JDK 1.3 on OpenVMS
            // XXX is this true for any 1.4 port? -jglick
            
            if (Utilities.getOperatingSystem() != Utilities.OS_OS2) {
                while (!is.ready() && !done) sleep(100);
            }
            return is.read(buff, start, count);
        }
    } // end of CopyMaker
}

