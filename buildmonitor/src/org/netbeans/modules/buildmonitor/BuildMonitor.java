/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.buildmonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Timer;
import org.openide.ErrorManager;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Monitors a remote continuous build via a generated XML status file,
 * notifying listeners of any changes.  The status file format is defined by
 * CruiseControl.
 *
 * @author Tom Ball, Jesse Glick
 */
public class BuildMonitor {
    
    private static final String KEY_URL = "url";
    private static final String KEY_NAME = "name";
    private static final String KEY_INTERVAL = "interval";
    
    private URL buildStatusURL;
    private int pollMinutes;
    private String name;
    private PropertyChangeSupport pcs;
    private Timer timer;
    
    // from build status file
    private String title;
    private URL buildLink;
    private String buildDescription;
    private Status lastStatus;
    private String lastStatusText;
    private URL statusLink;
    private String statusDescription;
    private String guid;
    private String pubDate;
    private Preferences node;
    
    static RequestProcessor WORKER = new RequestProcessor("build status updater");

    public static BuildMonitor create(Preferences node) throws MalformedURLException {
        String u = node.get(KEY_URL, null);
        URL url = u != null ? new URL(u) : null;
	String name = node.get(KEY_NAME, node.name());
        int minutes = node.getInt(KEY_INTERVAL, 30);
        return new BuildMonitor(name, url, minutes, node);
    }
    
    private BuildMonitor(String name, URL url, int minutes, Preferences node) {
        this.pcs = new PropertyChangeSupport(this);
	this.name = name;
	buildStatusURL = url;
	pollMinutes = minutes;
	lastStatus = Status.NO_STATUS_AVAIL;
        this.node = node;
        updateBuildStatus();
        startTimer();
    }
    
    public void delete() {
        try {
            node.removeNode();
        } catch (BackingStoreException x) {
            Exceptions.printStackTrace(x);
        }
    }

    private void startTimer() {
        int pollMilliseconds = pollMinutes * 60 /*seconds*/ * 1000 /* millis */;
	timer = new Timer(pollMilliseconds, new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
                updateBuildStatus();
	    }
	});
	timer.start();
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String newName) {
        name = newName;
        node.put(KEY_NAME, newName);
        firePropertyChange("name", name); //NOI18N
    }
    
    public Status getStatus() {
	return lastStatus != null ? lastStatus : Status.NO_STATUS_AVAIL;
    }

    public URL getURL() {
	return buildStatusURL;
    }
    
    public void setURL(URL url) {
        buildStatusURL = url;
        node.put(KEY_URL, url.toExternalForm());
        firePropertyChange("url", url); //NOI18N
        updateBuildStatus();
    }
    
    public int getPollMinutes() {
	return pollMinutes;
    }
    
    public void setPollMinutes(int poll) {
        pollMinutes = poll;
        node.putInt(KEY_INTERVAL, poll);
        firePropertyChange("pollMinutes", pollMinutes); //NOI18N
    }

    public String getTitle() {
        return title;
    }

    public URL getBuildLink() {
        return buildLink;
    }

    public String getBuildDescription() {
        return buildDescription;
    }

    public URL getStatusLink() {
        return statusLink;
    }

    public String getStatusDescription() {
        return statusDescription != null ? statusDescription :
            // Hudson provides no description
            (lastStatusText != null ? lastStatusText : getString("TOOLTIP_NO_STATUS")); //NOI18N
    }

    public String getGuid() {
        return guid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    private void firePropertyChange(String name, Object value) {
        pcs.firePropertyChange(name, null, value);
    }
    
    private String getString(String key) {
        return NbBundle.getBundle(BuildMonitor.class).getString(key);
    }
    
    public void updateBuildStatus() {
        if (getURL() == null)
            return;
        WORKER.post(new StatusUpdater());
    }
    
    /**
     * Updates the monitor status by parsing the specified URL contents.
     * This is done on a separate thread so as to not block the event queue.
     */
    private class StatusUpdater implements Runnable {
        
        public void run() {
            try {
                XMLReader parser = XMLUtil.createXMLReader();
                XMLHandler handler = new XMLHandler();
                parser.setContentHandler(handler);
                parser.setErrorHandler(handler);

                InputSource is = new InputSource(getInputStream(getURL()));
                if(Thread.currentThread().isInterrupted())
                    return;
                parser.parse(is);
                firePropertyChange("status", lastStatus); //NOI18N
            } catch (IOException e) {
                ErrorManager.getDefault().log(ErrorManager.EXCEPTION, "IOException reading " + getURL() + ": " + e);
            } catch (SAXException e) {
                ErrorManager.getDefault().log(ErrorManager.EXCEPTION, "SAXException reading " + getURL() + ": " + e);
                Exception e2 = e.getException();
                if (e2 != null)
                    ErrorManager.getDefault().log(ErrorManager.EXCEPTION, "   embedded exception: " + e2);
            }
        }

        public InputStream getInputStream(URL downloadURL) throws IOException {
            IOException expn = null;
            URLConnection ucn = null;

            if(Thread.currentThread().isInterrupted())
                return null;
            Proxy proxy = getProxy();
            if(proxy != null)
                ucn = downloadURL.openConnection(proxy);
            else
                ucn = downloadURL.openConnection();

            if(Thread.currentThread().isInterrupted())
                return null;
            ucn.connect();
            return ucn.getInputStream();
        }

        private Proxy getProxy() {
            String proxyHost = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");
            if((proxyHost == null) || (proxyHost.length() <= 0))
                return null;
            int port = 0;
            try{
                port = Integer.parseInt(proxyPort);
            }catch(Exception e){}
            SocketAddress proxySocket = new InetSocketAddress(proxyHost, port);
            return new Proxy(Proxy.Type.HTTP, proxySocket);
        }
    }

    private class XMLHandler extends DefaultHandler {
        private boolean inItem;
        private boolean skipRemainingItems;
        private StringBuffer buffer;
        private String href;
	public void startElement(String namespaceURI, String localName,
				 String rawName, Attributes attrs) 
	    throws SAXException {
            buffer = new StringBuffer();
            if (rawName.equalsIgnoreCase("item") || rawName.equalsIgnoreCase("entry")) //NOI18N
                inItem = true;
            if (rawName.equalsIgnoreCase("link")) {
                href = attrs.getValue("href");
            }
	}

        public void characters(char buf[], int offset, int len) throws SAXException {
            buffer.append(buf, offset, len);
        }

        public void endElement(String namespaceURI, String localName,
			       String rawName) {
            if (skipRemainingItems)
                return;
            String text = buffer.toString();
            if (rawName.equalsIgnoreCase("item") || rawName.equalsIgnoreCase("entry")) { //NOI18N
                inItem = false;
                if (lastStatus != Status.NO_STATUS_AVAIL) // latest status not available during Hudson builds
                    // only first item is read, since it has the most recent status
                    skipRemainingItems = true;
                // XXX would more simply throw a StopException
            }
            else if (rawName.equalsIgnoreCase("title")) { //NOI18N
                if (inItem)
                    lastStatus = Status.lookup(lastStatusText = text);
                else
                    title = text;
            }
            else if (rawName.equalsIgnoreCase("link")) { //NOI18N
                URL url = null;
                try {
                    if (text.length() > 0) {
                        url = new URL(text);
                    } else if (href != null && href.length() > 0) {
                        url = new URL(href);
                    }
                } catch (MalformedURLException e) {
                    ErrorManager.getDefault().annotate(e, ErrorManager.UNKNOWN, "URL: " + text, null, null, null);
                    ErrorManager.getDefault().notify(e);
                }
                if (inItem)
                    statusLink = url;
                else
                    buildLink = url;
            }
            else if (rawName.equalsIgnoreCase("description")) { //NOI18N
                if (inItem)
                    statusDescription = text;
                else
                    buildDescription = text;
            }
            else if (rawName.equalsIgnoreCase("guid")) //NOI18N
                guid = text;
            else if (rawName.equalsIgnoreCase("pubDate")) //NOI18N
                pubDate = text;
        }
        
	public void warning(SAXParseException e) throws SAXException {
	    String s = "warning reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage();
	    ErrorManager.getDefault().log(ErrorManager.WARNING, s);
	}
	public void error(SAXParseException e) throws SAXException {
	    String s = "error reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage();
	    ErrorManager.getDefault().log(ErrorManager.ERROR, s);
	}
	public void fatalError(SAXParseException e) throws SAXException {
	    String s = "fatal error reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage();
	    ErrorManager.getDefault().log(ErrorManager.ERROR, s);
	}
    }

}
