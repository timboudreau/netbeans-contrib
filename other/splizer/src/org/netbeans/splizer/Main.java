/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Main.java
 *
 * Created on May 1, 2004, 12:53 AM
 */

package org.netbeans.splizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 * A terrifying little tool that will scan directories for files and add/update
 * the Sun Public License in them.  Probably could have been written in five
 * lines of perl, definitely could have been written much more efficiently
 * by a regexp expert.  Oh well.
 *
 * @author  Tim Boudreau
 */
public class Main implements Runnable {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            new Gui().setVisible(true);
        } else {
            try {
                int flags = processArgs (args);
                if ((flags & ARG_HELP) != 0) {
                    printHelp();
                    System.exit(0);
                }
                
                flags |= ARG_VERBOSE;
                
                if (dir == null) {
                    printHelp();
                } else {
                    new Main ().splIze(new File(dir), null, flags);
                }
            } catch (IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit (2);
            }
        }
    }
    
    static final int ARG_HELP = 1;
    static final int ARG_DRY_RUN = 2;
    static final int ARG_VERBOSE = 4;
    
    private static List argsLong = Arrays.asList(new String[] {
        "help", "dry-run", "xxx", "verbose"
    });
    
    private static List argsShort = Arrays.asList(new String[] {
         "h", "n", "xxx", "v" 
    });
    
    private static String dir = null;
    private static int processArgs (String[] args) {
        int result = 0;
        for (int i=0; i < args.length; i++) {
            String s = args[i];
            if (i != args.length-1) {
                while (s.startsWith("-") && s.length() > 1) {
                    s = s.substring (1, s.length());
                }
                if ("xxx".equals(s)) {
                    return ARG_HELP;
                }
                int idx = argsLong.indexOf(s);
                if (idx == -1) {
                    idx = argsShort.indexOf(s);
                }
                if (idx == -1) {
                    return ARG_HELP;
                }
                result |= idx;
            } else {
                dir = args[i];
            }
        }
        return result;
    }
    
    private void handleException (Exception e) {
        if (e instanceof IllegalArgumentException) {
            if (output != null) {
                JOptionPane.showMessageDialog(output, e.getMessage());
            } else {
                System.err.println(e.getMessage());
            }
        } else {
            e.printStackTrace();
            System.exit(1);
        }
        aborted = true;
    }
    
    private boolean aborted = false;
    private static void printHelp() {
        System.out.println("SPL-ize 0.1 by Tim Boudreau");
        System.out.println("This program modifies files, inserting the Sun Public License into them.");
        System.out.println("The author makes no guarantees that it will not cause damage.");
        System.out.println("Make a backup.  You have been warned.");
        System.out.println("\nUSAGE:");
        System.out.println("  splize [--verbose --dry-run --help] directory");
        System.out.println("  splize (with no arguments, for GUI mode");
        System.out.println("\nARGUMENTS:");
        System.out.println("    -v / --verbose : Verbose output");
        System.out.println("    -n / --dry-run : Don't actually modify files");
        System.out.println("    -h / --help : Print this charming message");
    }
    
    private JTextArea output = null;
    
    public void splIze (File f, JTextArea jtc, int flags) {
        try {
        if (!f.exists()) {
            throw new IllegalArgumentException (f.getName() + " doesn't exist");
        }
        if (!f.isDirectory()) {
            throw new IllegalArgumentException (f.getName() + " is not a directory");
        }
        if (!f.canRead()) {
            throw new IllegalArgumentException ("No read permission for " + f.getName());
        }
        if (!f.canWrite()) {
            throw new IllegalArgumentException ("No write permission for " + f.getName());
        }
        this.root = f;
        this.flags = flags;
        if (output != null) {
            //we're in gui mode, don't block
            Thread thread = new Thread(this);
            thread.start();
        } else {
            run();
        }
        } catch (Exception e) {
            handleException (e);
        }
    }
    
    private int flags;
    private File root = null;
    
    
    private void log (String s, boolean verbose) {
        if (!verbose || ((flags & ARG_VERBOSE) != 0)) {
            if (output != null) {
                output.append(s + "\n");
                output.invalidate();
            } else {
                System.out.println(s);
            }
        }
    }
    
    public void run() {
        try {
        log ("Prescanning to be sure files can be written...", true);
        ArrayList al = new ArrayList();
        try {
            checkFiles (root, al);
        } catch (Exception e) {
            handleException (e);
        }
        if (aborted) {
            log ("Aborted.", false);
        }
        log ("Checking " + al.size() + " files for licenses", false);
        
        if (al.isEmpty()) {
            log ("No files to process, giving up.", false);
            System.exit (0);
        }
        
        List updateOnly = new ArrayList();
        for (Iterator i = al.iterator(); i.hasNext();) {
            File f = (File) i.next();
            String license = checkForLicense(f);
            if (license != null) {
                i.remove();
                if (!isUpToDate(license, f)) {
                    log ("License out of date in " + f.getName(), true);
                    updateOnly.add (f);
                }
            }
        }
        
        if ((flags & ARG_DRY_RUN) != 0) {
            log ("Dry run complete.", false);
            System.exit(0);
        }
        
        if (!al.isEmpty()) {
            log ("Adding licenses to " + al.size() + " files", false);
            for (Iterator i = al.iterator(); i.hasNext();) {
                File f = (File) i.next();
                log ("Adding license to " + f.getName(), true);
                addLicenseToFile (f);
            }
        }
        
        if (!updateOnly.isEmpty()) {
            log ("Updating copyright dates in " + updateOnly.size() + " files", false);
            for (Iterator i = updateOnly.iterator(); i.hasNext();) {
                File f = (File) i.next();
                log ("Updating license for " + f.getName(), true);
                updateLicenseIn (f);
            }
        }
        
        log ("Done.", false);
        
        
        } catch (Exception e) {
            handleException (e);
        }
    }
    
    private void updateLicenseIn (File f) throws IOException {
        FileInputStream fis = new FileInputStream (f);
        long len = f.length();
        if (len > Integer.MAX_VALUE) {
            //and this will happen when?
            log ("Skipping " + f.getName() + ", it is too big", false);
            return;
        }
        MappedByteBuffer bb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, (int) f.length());

        byte[] b = new byte[(int) f.length()];
        bb.get (b);
        StringBuffer s = new StringBuffer (new String(b));
        int idx = s.indexOf ("1997");
        if (idx == -1) {
            throw new IOException ("Can't find date in license in " + f.getName());
        }
        idx+=4;
        byte[] yearbytes = Integer.toString (1900 + new Date().getYear()).getBytes();
        while (!Character.isDigit((char) b[idx])) {
            idx++;
        }
        for (int i=0; i < 4; i++) {
            b[idx+i] = yearbytes[i];
        }
        
        FileOutputStream fos = new FileOutputStream (f);
        try {
            fos.write(b);
        } finally {
            fos.close();
        }
        log ("Updated license in " + f.getName(), true);
        
    }
    
    private void addLicenseToFile (File f) throws IOException {
        FileInputStream fis = new FileInputStream (f);
        long len = f.length();
        if (len > Integer.MAX_VALUE) {
            //and this will happen when?
            log ("Skipping " + f.getName() + ", it is too big", false);
            return;
        }
        MappedByteBuffer bb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, (int) f.length());

        byte[] b = new byte[(int) f.length()];
        bb.get (b);
        
        String lic = getLicenseFor(f);
        String s = new String(b);
        if (skipFirstLine(f)) {
            int idx = s.indexOf("\n");
            StringBuffer sb = new StringBuffer();
            sb.append (s.substring (0, idx));
            sb.append ("\n");
            sb.append (lic);
            sb.append (s.substring (idx+1)); //XXX skip \n?
            s = sb.toString();
        } else {
            s = lic + s;
        }
        b = s.getBytes();
        
        FileOutputStream fos = new FileOutputStream (f);
        try {
            fos.write(b);
        } finally {
            fos.close();
        }
        log ("License added to " + f.getName() + " " + len + " -> " + f.length() + " bytes", true);
        
    }
    
    boolean isUpToDate (String license, File f) {
        int idx = license.indexOf ("1997");
        if (idx == -1) {
            throw new IllegalArgumentException (f.getName() + ": License does not contain date: " + license);
        }
        String year = Integer.toString (1900 + new Date().getYear());
        int ydx = license.indexOf (year);
        System.err.println("Index of year is " + ydx);
        return ydx > idx;// && (ydx - idx < 4);
    }
    
    private String checkForLicense (File f) throws IOException {
        if (f.length() == 0) {
            throw new IOException (f.getName() + " is 0 bytes long.  Something is wrong.");
        }
        
        int size = Math.min (500, (int) f.length());
        FileInputStream fis = new FileInputStream (f);
        ByteBuffer bb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, size);

        byte[] b = new byte[(int) Math.min (500, size)];
        bb.get (b);
        String s = new String (b);
        
        String lic = getLicenseFor (f);
        
        StringTokenizer tok = new StringTokenizer (s, "\n");
        if (skipFirstLine(f)) {
            tok.nextElement();
        }
        
        assert tok.hasMoreElements() : "Empty file";
        
        StringTokenizer ltok = new StringTokenizer (lic, "\n");
        
        StringBuffer fileToMunge = new StringBuffer();
        int x=0;
        while (tok.hasMoreElements() && x < 3) {
            x++;
            fileToMunge.append (tok.nextToken());
        }
        
        
        String mungedFromFile = munge(fileToMunge.toString());
        String mungedLicense = munge (ltok.nextToken() + ltok.nextToken() + ltok.nextToken());
        
        boolean hasLicense = liberalEquals (mungedFromFile, mungedLicense);
        
        log ("File munge: " + mungedFromFile, false);
        log ("Lic munge : " + mungedLicense, false);
            
        if (hasLicense) {
            log ("License found in " + f.getName(), true);
            return s;
        } else {
            return null;
        }
    }
    
    /**
     * A fault tolerant equality test.  We've first stripped out all whitespace
     * and common comment characters in the strings to be tested and converted them
     * to upper case.  This test will 
     * first find the offsets of the first matching characters.  Then starting from
     * that point it will compare them.  If there is a non match, first the next
     * character is checked - if it matches, an extraneous character is assumed to
     * have been inserted and a match is assumed on the next character and the matching
     * continues.  If 50% of the characters match, in this way, it will return true.
     */
    private boolean liberalEquals (String a, String b) {
        char[] cA = a.toCharArray();
        char[] cB = b.toCharArray();
        
        int aStart = 0;
        int bStart = 0;
        boolean found = true;
        
        //Find the first indices where the chars match
        
        for (int i=0; i < cA.length; i++) {
            if (cA[i] == cB[0]) {
                aStart = i;
                found = true;
                break;
            }
        }
        
        if (!found) {
            for (int i=0; i < cB.length; i++) {
                if (cB[i] == cA[0]) {
                    bStart = i;
                    found = true;
                    break;
                }
            }
        }
        
        if (!found) {
            return false;
        }
        
        int stop = Math.min (cB.length - bStart, cA.length - aStart);
        
        boolean r1 = true;
        boolean r2 = true;
        
        int aExtraneous = 0;
        int bExtraneous = 0;
        
        for (int i=0; i < stop; i++) {
            boolean match = cA[aStart + i] == cB[bStart + i];
            if (!match && i != stop -1) {
                if (cA[aStart + i + 1 - bExtraneous] == cB[bStart + i - aExtraneous]) {
                    aExtraneous++;
                    match = true;
                } else if (!match && cA[aStart + i - bExtraneous] == cB[bStart + i + 1 - aExtraneous]) {
                    bExtraneous++;
                    match = true;
                }
            } else if (i == stop -1) {
                //we'll grant the last character
                match = true;
            }
            if (i % 2 == 0) {
                r1 &= match;
            } else {
                r2 &= match;
            }
            if (!r1 && !r2) {
                break;
            }
        }
        return r1 || r2;
    }

    private String munge (String s) {
        StringTokenizer tok = new StringTokenizer (s);
        StringBuffer result = new StringBuffer ();
        while (tok.hasMoreElements()) {
            String tk = tok.nextToken().toUpperCase();
            StringBuffer buf = new StringBuffer();
            char[] c = tk.toCharArray();
            for (int i=0; i < c.length; i++) {
                if (Character.isLetter(c[i])) {
                    buf.append (c[i]);
                }
            }
            result.append (buf.toString());
        }
        return result.toString();
    }
        
    
    private boolean skipFirstLine (File f) throws IOException {
        String s = getExtension (f);
        int idx = knownTypes.indexOf(s);
        if (idx == -1) {
            throw new IOException ("Can't find file type " + s + " in known extensions " + knownTypes + " but it was added to the list of files to process");
        }
        return ((Boolean) keepFirstLine.get(idx)).booleanValue();
    }
    
    private String getLicenseFor (File f) throws IOException {
        String ext = getExtension(f);
        int idx = knownTypes.indexOf(ext);
        if (idx == -1) {
            throw new IOException ("Can't find file type " + ext + " in known extensions " + knownTypes + " but it was added to the list of files to process");
        }
        StringBuffer sb = new StringBuffer ((String) licenses.get(idx));
        int i = sb.indexOf ("@DATE@");
        String year = Integer.toString (1900 + new Date().getYear());
        sb.replace(i, i+6, year);
        return sb.toString();
    }
    
    
    
    private void checkFiles (File f, List l) {
        if (aborted) {
            return;
        }
        if (f.isDirectory()) {
            log ("Scanning " + f.getPath(), true);
            File[] files = f.listFiles();
            for (int i=0; i < files.length; i++) {
                checkFiles (files[i], l);
            }
        } else {
            if (isKnownType(f)) {
                if (!f.canRead()) {
                    throw new IllegalArgumentException ("Cannot read file " + f.getPath());
                }
                if (f.canWrite()) {
                    log("Queueing " + f.getPath(), true);
                    l.add (f);
                } else {
                    throw new IllegalArgumentException ("File " + f.getName() + " is locked or missing write permission");
                }
            } else {
                log ("Skipping " + f.getName() + ", I don't know that type", true);
            }
        }
    }
    
    private List knownTypes = Arrays.asList(new String[] {
        ".java", ".xml", ".properties", ".html"
    });
    
    private List licenses = Arrays.asList (new String[] {
        JAVA_LICENSE, XML_LICENSE, PROPERTIES_LICENSE, XML_LICENSE 
    });
    
    private List keepFirstLine = Arrays.asList (new Boolean[] {
        Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE
    });
    
    
    private String getExtension (File f) {
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i == -1) {
            return "";
        } else {
            return s.substring (i);
        }
    }
    
    private boolean isKnownType (File f) {
        return knownTypes.indexOf(getExtension(f)) != -1;
    }
    
    private static final String PROPERTIES_LICENSE = 
    "#                 Sun Public License Notice\n" +
    "#\n" + 
    "# The contents of this file are subject to the Sun Public License\n" +
    "# Version 1.0 (the \"License\"). You may not use this file except in\n" +
    "# compliance with the License. A copy of the License is available at\n"+
    "# http://www.sun.com/\n"+
    "# \n"+
    "# The Original Code is NetBeans. The Initial Developer of the Original\n"+
    "# Code is Sun Microsystems, Inc. Portions Copyright 1997-@DATE@ Sun\n"+
    "# Microsystems, Inc. All Rights Reserved.\n\n";
        
    
    private static final String JAVA_LICENSE = 
    "/*\n" +
     "*                 Sun Public License Notice\n" +
     "*\n" +
     "* The contents of this file are subject to the Sun Public License\n" +
     "* Version 1.0 (the \"License\"). You may not use this file except in\n" +
     "* compliance with the License. A copy of the License is available at\n" +
     "* http://www.sun.com/\n" +
     "*\n" +
     "* The Original Code is NetBeans. The Initial Developer of the Original\n" +
     "* Code is Sun Microsystems, Inc. Portions Copyright 1997-@DATE@ Sun\n" +
     "* Microsystems, Inc. All Rights Reserved.\n" +
     "*/\n";
    
    private static final String XML_LICENSE = 
    "<!--\n" +
    "                Sun Public License Notice\n" +
    "\n" +
    "The contents of this file are subject to the Sun Public License\n" +
    "Version 1.0 (the \"License\"). You may not use this file except in\n" +
    "compliance with the License. A copy of the License is available at\n" +
    "http://www.sun.com/\n" +
    "\n" +
    "The Original Code is NetBeans. The Initial Developer of the Original\n" +
    "Code is Sun Microsystems, Inc. Portions Copyright 1997-@DATE@ Sun\n" +
    "Microsystems, Inc. All Rights Reserved.\n" +
    "-->\n";
    
}
