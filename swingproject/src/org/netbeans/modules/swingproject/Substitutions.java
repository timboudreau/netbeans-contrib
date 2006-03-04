/*
 * Substitutions.java
 *
 * Created on March 3, 2006, 4:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.swingproject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.util.Utilities;

/**
 * Replaces strings in content being unzipped to customize the project name to
 * match what the user entered in the wizard.
 *
 * @author Tim Boudreau
 */
final class Substitutions {
    final Properties props;
    final String basePackage;
    final String userEnteredProjectName;
    final String basePath;

    /** Creates a new instance of Substitutions */
    Substitutions(String templateName, String userEnteredProjectName, String basePackage) throws IOException {
        this (new BufferedInputStream (
                Substitutions.class.getResourceAsStream(
                templateName + ".properties")), userEnteredProjectName, basePackage);
    }

    Substitutions (InputStream stream, String userEnteredProjectName, String basePackage) throws IOException {
        if (stream == null) {
            throw new IOException ("Could not find properties file");
        }
        props = new Properties();
        props.load(stream);
        this.userEnteredProjectName = userEnteredProjectName;
        this.basePackage = basePackage;
        this.basePath = Utilities.replaceString(basePackage, ".", "/");
    }


    String substitutePath (String filepath) {
        String result = props.getProperty(filepath);
        if (result != null) {
            result = result.replaceAll("%basepath%", basePath);
        }
        return result == null ? filepath : result;
    }

    InputStream substituteContent (long originalSize, InputStream input, String filename) throws IOException {
        if (filename.endsWith (".gif") || filename.endsWith (".png") || filename.endsWith(".jar")) {
            return input;
        }
        if (originalSize > Integer.MAX_VALUE || originalSize < 0) {
            throw new IllegalArgumentException ("File too large: " +
                    originalSize);
        }
        ByteArrayOutputStream temp = new ByteArrayOutputStream ((int) originalSize);
        copy (input, temp);
        byte[] b = temp.toByteArray();

        //XXX do we want default charset, or UTF-8 - UTF-8 I think...
        CharBuffer cb = Charset.defaultCharset().decode(ByteBuffer.wrap(b));
        String data = cb.toString();

        String user = System.getProperty ("user.name");
        for (Iterator i = props.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String val = props.getProperty(key);
            val = val.replaceAll("%basepackage%", basePackage);
            val = val.replaceAll("%basepath%", basePath);
            val = val.replaceAll("%applicationName%", userEnteredProjectName);
            if (user != null) {
                val = val.replaceAll ("%userName%", user);
            } else {
                val = val.replaceAll("%userName%", "Somebody"); //uh, well...
            }
            Matcher m = Pattern.compile(key).matcher(data);
            data = m.replaceAll(val);
        }

        return new ByteArrayInputStream (data.getBytes());
    }

    public static void copy(InputStream is, OutputStream os)
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
    
}

