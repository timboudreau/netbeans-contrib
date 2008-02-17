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
package org.netbeans.modules.erlang.platform.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.api.queries.FileEncodingQuery;

import org.netbeans.modules.languages.execution.ExecutionDescriptor;
import org.netbeans.modules.languages.execution.ExecutionService;
import org.netbeans.modules.languages.execution.RegexpOutputRecognizer;
import org.openide.filesystems.FileObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;


/**
 * Execution service for Ruby. Performs some Ruby specific setup like
 * setting environment required for JRuby, or enabling I/O syncing for
 * native Ruby.
 *
 * @author Tor Norbye
 */
public class RubyExecution extends ExecutionService {
    // JRuby 0.9.1:
    // main.rb:6 warning: parenthesize argument(s) for future version
    // :[-1,-1]:[0,0]: main.rb:7: unterminated string meets end of file (SyntaxError)
    // Ruby 1.8.2:
    // main.rb:5: unterminated string meets end of file
    // Ruby stack trace lines, 3 variants:
    ///Users/tor/codehaus/jruby/trunk/trunk/jruby/src/builtin/socket.rb:240:in `initialize': Errno::EADDRINUSE (Errno::EADDRINUSE)
    //        from /Users/tor/semplice/modules/scripting/build/cluster/jruby-0.9.1/lib/ruby/1.8/webrick/utils.rb:73:in `new'
    //        from /Users/tor/semplice/modules/scripting/build/cluster/jruby-0.9.1/lib/ruby/gems/1.8/gems/rails-1.1.6/lib/commands/servers/webrick.rb:59
//    public static final RegexpOutputRecognizer RUBY_COMPILER =
//        new RegexpOutputRecognizer("^((\\[|\\]|\\-|\\:|[0-9]|\\s|\\,)*)(\\s*from )?(\\S.*\\.(rb|rake))\\:([0-9]+).*" // NOI18N
//                , 4, 6, -1);
    public static final RegexpOutputRecognizer RUBY_COMPILER =
        new RegexpOutputRecognizer("^((\\[|\\]|\\-|\\:|[0-9]|\\s|\\,)*)(\\S.*\\.(erl|hrl))\\:([0-9]+).*(\\s*(Warning )|(syntax error ))?.*" // NOI18N
                , 3, 5, -1);

    public static final RegexpOutputRecognizer RUBY_TEST_OUTPUT =
        new RegexpOutputRecognizer("\\s*test.*\\[(\\S:?.*\\.(rb|rake|mab|rjs|rxml|builder))\\:([0-9]+).*\\s?" // NOI18N
                , 1, 3, -1);
    // TODO - add some more recognizers here which recognize the prefix path to Ruby (gems, GEM_HOME, etc.) such that I
    // can hyperlink to errors in the "rake", "rails" etc. load scripts

    /** When not set (the default) do stdio syncing for native Ruby binaries */
    private static final boolean SYNC_RUBY_STDIO = System.getProperty("ruby.no.sync-stdio") == null; // NOI18N

    /** Set to suppress using the -Kkcode flag in case you're using a weird interpreter which doesn't support it */
    //private static final boolean SKIP_KCODE = System.getProperty("ruby.no.kcode") == null; // NOI18N
    private static final boolean SKIP_KCODE = true;
    
    /** When not set (the default) bypass the JRuby launcher unix/ba-file scripts and launch VM directly */
    public static final boolean LAUNCH_JRUBY_SCRIPT =
        System.getProperty("ruby.use.jruby.script") != null; // NOI18N

    private String charsetName;
    
    public RubyExecution(ExecutionDescriptor descriptor) {
        super(descriptor);

        if (descriptor != null) {
        if (descriptor.getCmd() == null) {
            descriptor.cmd(new File(RubyInstallation.getInstance().getRuby()));
        }

        descriptor.addBinPath(true);
    }
    }

    /** Create a Ruby execution service with the given source-encoding charset */
    public RubyExecution(ExecutionDescriptor descriptor, String charsetName) {
        this(descriptor);
        this.charsetName = charsetName;
    }

    /**
     * Returns the basic Ruby interpreter command and associated flags (not
     * application arguments)
     */
    public static List<? extends String> getRubyArgs(String rubyHome, String cmdName) {
        return new RubyExecution(null).getRubyArgs(rubyHome, cmdName, null);
    }

    private List<? extends String> getRubyArgs(String rubyHome, String cmdName, ExecutionDescriptor descriptor) {
        List<String> argvList = new ArrayList<String>();
        // Decide whether I'm launching JRuby, and if so, take a shortcut and launch
        // the VM directly. This is important because killing JRuby via the launcher script
        // is not working right; now that JRuby on Unix exec's the VM that part is okay but
        // on Windows there are still problems.        
        if (!LAUNCH_JRUBY_SCRIPT && cmdName.startsWith("jruby")) {
            String javaHome = getJavaHome();
            
            argvList.add(javaHome + File.separator + "bin" + File.separator +
                "java"); // NOI18N   
            // XXX Do I need java.exe on Windows?

            // Additional execution flags specified in the JRuby startup script:
            argvList.add("-Xverify:none"); // NOI18N
            argvList.add("-da"); // NOI18N
            
            String extraArgs = System.getenv("JRUBY_EXTRA_VM_ARGS"); // NOI18N

            String javaMemory = "-Xmx256m"; // NOI18N
            String javaStack = "-Xss1024k"; // NOI18N
            
            if (extraArgs != null) {
                if (extraArgs.indexOf("-Xmx") != -1) { // NOI18N
                    javaMemory = null;
                }
                if (extraArgs.indexOf("-Xss") != -1) { // NOI18N
                    javaStack = null;
                }
                String[] jrubyArgs = Utilities.parseParameters(extraArgs);
                for (String arg : jrubyArgs) {
                    argvList.add(arg);
                }
            }
            
            if (javaMemory != null) {
                argvList.add(javaMemory);
            }
            if (javaStack != null) {
                argvList.add(javaStack);
            }

            // Classpath
            argvList.add("-classpath"); // NOI18N

            File rubyHomeDir = null;

            try {
                rubyHomeDir = new File(rubyHome);
                rubyHomeDir = rubyHomeDir.getCanonicalFile();
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }

            File jrubyLib = new File(rubyHomeDir, "lib"); // NOI18N
            assert jrubyLib.exists() : '"' + jrubyLib.getAbsolutePath() + "\" exists (\"" + descriptor.getCmd() + "\" is not valid JRuby executable?)";

            StringBuilder cp = new StringBuilder();
            File[] libs = jrubyLib.listFiles();

            boolean isWindows = Utilities.isWindows();

            for (File lib : libs) {
                if (lib.getName().endsWith(".jar")) { // NOI18N

                    if (cp.length() > 0) {
                        cp.append(File.pathSeparatorChar);
                    }

                    String p = lib.getAbsolutePath();

                    if (isWindows && (p.indexOf(' ') != -1) && (p.indexOf('"') == -1)) {
                        p = '"' + p + '"';
                    }

                    cp.append(p);
                }
            }

            // Add in user-specified jars passed via JRUBY_EXTRA_CLASSPATH
            
            String extraCp = null;
            
            if (descriptor != null) {
                extraCp = descriptor.getClassPath();
                if (extraCp != null && File.pathSeparatorChar != ':') {
                    boolean insertQuotes = false;
                    if (extraCp.indexOf(' ') != -1) {
                        insertQuotes = true;
                    }
                    // Ugly hack - getClassPath has mixed together path separator chars
                    // (:) and filesystem separators, e.g. I might have C:\foo:D:\bar but
                    // obviously only the path separator after "foo" should be changed to ;
                    StringBuilder p = new StringBuilder();
                    int pathOffset = 0;
                    for (int i = 0; i < extraCp.length(); i++) {
                        char c = extraCp.charAt(i);
                        if (insertQuotes && pathOffset == 0) {
                            p.append('"');
                        }
                        if (c == ':' && pathOffset != 1) {
                            if (insertQuotes) {
                                 p.append('"');
                            }
                            p.append(File.pathSeparatorChar);
                            pathOffset = 0;
                            continue;
                        } else {
                            pathOffset++;
                        }
                        p.append(c);
                    }
                    if (insertQuotes && p.length() > 0) {
                        p.append('"');
                    }
                    extraCp = p.toString();
                }
            }
            
            if (extraCp == null) {
                extraCp = System.getenv("JRUBY_EXTRA_CLASSPATH"); // NOI18N
            }

            if (extraCp != null) {
                    if (cp.length() > 0) {
                        cp.append(File.pathSeparatorChar);
                    }
                //if (File.pathSeparatorChar != ':' && extraCp.indexOf(File.pathSeparatorChar) == -1 &&
                //        extraCp.indexOf(':') != -1) {
                //    extraCp = extraCp.replace(':', File.pathSeparatorChar);
                //}
                cp.append(extraCp);
            }

            argvList.add(cp.toString());

            argvList.add("-Djruby.base=" + rubyHomeDir); // NOI18N
            argvList.add("-Djruby.home=" + rubyHomeDir); // NOI18N
            argvList.add("-Djruby.lib=" + jrubyLib); // NOI18N

            // TODO - turn off verifier?

            if (Utilities.isWindows()) {
                argvList.add("-Djruby.shell=\"cmd.exe\""); // NOI18N
                argvList.add("-Djruby.script=jruby.bat"); // NOI18N
            } else {
                argvList.add("-Djruby.shell=/bin/sh"); // NOI18N
                argvList.add("-Djruby.script=jruby"); // NOI18N
            }

            // Main class
            argvList.add("org.jruby.Main"); // NOI18N

            // TODO: JRUBYOPTS

            // Application arguments follow
        }
        
        if (!SKIP_KCODE && cmdName.startsWith("ruby")) { // NOI18N
            String cs = charsetName;
            if (cs == null) {
            // Add project encoding flags
                FileObject fo = descriptor.getFileObject();
                if (fo != null) {
                    Charset charset = FileEncodingQuery.getEncoding(fo);
                    if (charset != null) {
                        cs = charset.name();
                    }
                }
            }

            if (cs != null) {
                if (cs.equals("UTF-8")) { // NOI18N
                    argvList.add("-Ku"); // NOI18N
                //} else if (cs.equals("")) {
                // What else???
                }
            }
        }

        // Is this a native Ruby process? If so, do sync-io workaround.
        if (SYNC_RUBY_STDIO && cmdName.startsWith("ruby")) { // NOI18N

            int dot = cmdName.indexOf('.');

            if ((dot == -1) || (dot == 4) || (dot == 5)) { // 5: rubyw

                InstalledFileLocator locator = InstalledFileLocator.getDefault();
                File f =
                    locator.locate("modules/org-netbeans-modules-ruby-project.jar", // NOI18N
                        null, false); // NOI18N

                if (f == null) {
                    throw new RuntimeException("Can't find cluster");
                }

                f = new File(f.getParentFile().getParentFile().getAbsolutePath() + File.separator +
                        "sync-stdio.rb"); // NOI18N

                try {
                    f = f.getCanonicalFile();
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }

                argvList.add("-r" + f.getAbsolutePath()); // NOI18N
            }
        }
        return argvList;
    }

    @Override
    protected List<? extends String> buildArgs() {
        List<String> argvList = new ArrayList<String>();
        String rubyHome = descriptor.getCmd().getParentFile().getParent();
        String cmdName = descriptor.getCmd().getName();
        argvList.addAll(getRubyArgs(rubyHome, cmdName, descriptor));
        argvList.addAll(super.buildArgs());
        return argvList;
    }

    private static String getJavaHome() {
        String javaHome = System.getProperty("jruby.java.home"); // NOI18N

        if (javaHome == null) {
            javaHome = System.getProperty("java.home"); // NOI18N
        }
        
        return javaHome;
    }

    /**
     * Add settings in the environment appropriate for running JRuby:
     * add the given directory into the path, and set up JRUBY_HOME
     */
    public void setupProcessEnvironment(Map<String, String> env) {
        super.setupProcessEnvironment(env);

        // In case we're launching JRuby:
        String jrubyHome = RubyInstallation.getInstance().getJRubyHome();
        env.put("JRUBY_HOME", jrubyHome); // NOI18N
        env.put("JRUBY_BASE", jrubyHome); // NOI18N
        env.put("JAVA_HOME", getJavaHome());
    }
}
