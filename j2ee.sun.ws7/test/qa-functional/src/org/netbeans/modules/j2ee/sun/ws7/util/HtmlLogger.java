/*
 * HtmlLogger.java
 *
 * Created on May 7, 2007, 7:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ws7.util;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Prabushankar.Chinnasamy
 */
public class HtmlLogger extends Formatter {

    /** Creates a new instance of HtmlLogger */
    public HtmlLogger() {}

    // This method is called for every log records
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        // Bold any levels >= WARNING
        if(rec.getMessage().startsWith("#")) {

        }
        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("\n");
            buf.append("</b>");
        } else {
            buf.append(rec.getLevel());
            buf.append("\n");
        }
        buf.append(' ');
        //buf.append(new Date());
        buf.append(' ');
        buf.append(formatMessage(rec));
        buf.append("<br>");
        return buf.toString();
    }

    // This method is called just after the handler using this
    // formatter is created
    public String getHead(Handler h) {
        return "<HTML><HEAD>"+(new Date())+"</HEAD><BODY>\n";
    }

    // This method is called just after the handler using this
    // formatter is closed
    public String getTail(Handler h) {
        return "</BODY></HTML>\n";
    }
}


