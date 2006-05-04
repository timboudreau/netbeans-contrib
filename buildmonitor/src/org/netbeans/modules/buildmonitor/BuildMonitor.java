/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.buildmonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.Timer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
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
 * @author Tom Ball
 */
public class BuildMonitor implements Serializable, HelpCtx.Provider {
    private URL buildStatusURL;
    private int pollMinutes;
    private transient String name;
    private transient PropertyChangeListener listener; // only one view supported
    private transient Timer timer;
    
    // from build status file
    private transient String title;
    private transient URL buildLink;
    private transient String buildDescription;
    private transient Status lastStatus;
    private transient URL statusLink;
    private transient String statusDescription;
    private transient String guid;
    private transient String pubDate;

    private static final long serialVersionUID = 5735178156173098330L;
    
    public static BuildMonitor create(FileObject fo) throws IOException {
        URL url;
	Object o = fo.getAttribute("url"); // NOI18N
	if (o instanceof String) {
	    url = new URL((String)o);
	} else {
	    url = (URL)o;
	}
	String name = (String)fo.getAttribute("name"); //NOI18N
        Integer minutesAttr = (Integer)fo.getAttribute("minutes"); //NOI18N
        int minutes = minutesAttr != null ? minutesAttr.intValue() : 30;
        BuildMonitor monitor = new BuildMonitor(name, url, minutes);
	return monitor;
    }
    
    private BuildMonitor(String name, URL url, int minutes) {
	this.name = name;
	buildStatusURL = url;
	pollMinutes = minutes;
	lastStatus = Status.NO_STATUS_AVAIL;
        updateBuildStatus();
        startTimer();
    }

    private void startTimer() {
        final String threadName = name != null ? name + " build status" : "build status";
        int pollMilliseconds = pollMinutes * 60 /*seconds*/ * 1000 /* millis */;
	timer = new Timer(pollMilliseconds, new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        updateBuildStatus();
                    }
                }, threadName).start(); //NOI18N
	    }
	});
	timer.start();
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String newName) {
        name = newName;
        firePropertyChange("name", name); //NOI18N
    }
    
    public Status getStatus() {
	return lastStatus != null ? lastStatus : Status.NO_STATUS_AVAIL;
    }
    
    private void setStatus(Status newStatus) {
	lastStatus = newStatus;
	firePropertyChange("status", newStatus); //NOI18N
    }
    
    public URL getURL() {
	return buildStatusURL;
    }
    
    public void setURL(URL url) {
        buildStatusURL = url;
        firePropertyChange("url", url); //NOI18N
    }
    
    public int getPollMinutes() {
	return pollMinutes;
    }
    
    public void setPollMinutes(int poll) {
        pollMinutes = poll;
        firePropertyChange("pollMinutes", new Integer(pollMinutes)); //NOI18N
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
        return statusDescription != null ? 
            statusDescription : getString("TOOLTIP_NO_STATUS"); //NOI18N
    }

    public String getGuid() {
        return guid;
    }

    public String getPubDate() {
        return pubDate;
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	this.listener = listener;
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	if (listener == this.listener)
	    this.listener = null;
    }
    
    private void firePropertyChange(String name, Object value) {
	if (listener != null) {    
	    PropertyChangeEvent event = new PropertyChangeEvent(this, name, null, value);
	    listener.propertyChange(event);
	}	
    }
    
    private String getString(String key) {
        return NbBundle.getBundle(BuildMonitor.class).getString(key);
    }
    
    public void updateBuildStatus() {
        if (getURL() == null)
            return;
        new StatusUpdater().start();
    }
    
    /**
     * Updates the monitor status by parsing the specified URL contents.
     * This is done on a separate thread so as to not block the event queue.
     */
    private class StatusUpdater extends Thread {
        StatusUpdater() {
            super(name != null ? name : "build status" + " updater");
        }
        
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
	public void startElement(String namespaceURI, String localName,
				 String rawName, Attributes attrs) 
	    throws SAXException {
            buffer = new StringBuffer();
            if (rawName.equalsIgnoreCase("item")) //NOI18N
                inItem = true;
	}

        public void characters(char buf[], int offset, int len) throws SAXException {
            buffer.append(buf, offset, len);
        }

        public void endElement(String namespaceURI, String localName,
			       String rawName) {
            if (skipRemainingItems)
                return;
            String text = buffer.toString();
            if (rawName.equalsIgnoreCase("item")) { //NOI18N
                inItem = false;
                // only first item is read, since it has the most recent status
                skipRemainingItems = true;
            }
            else if (rawName.equalsIgnoreCase("title")) { //NOI18N
                if (inItem)
                    lastStatus = Status.lookup(text);
                else
                    title = text;
            }
            else if (rawName.equalsIgnoreCase("link")) { //NOI18N
                URL url = null;
                try {
                    url = new URL(text);
                } catch (MalformedURLException e) {
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

    private void readObject (java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        updateBuildStatus();
        startTimer();
    }
}
