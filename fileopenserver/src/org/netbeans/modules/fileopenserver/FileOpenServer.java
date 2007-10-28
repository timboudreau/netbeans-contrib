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

package org.netbeans.modules.fileopenserver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Stack;

/**
 * The file open server implementation. Listens for file open rquests on a TCP
 * port. The file open request is a filename per line sent on a TCP socket.
 *
 * A simple code in elisp could be used to open a file in emacs buffer in
 * Eclipse.
 *
 * <code>
 *   (defun open-file-in-ide()
 *    (interactive)
 *    (let ((fileopenserver (open-network-stream "EclipseFileOpenServer" nil "127.0.0.1" 4050)))
 *      (if (eq major-mode 'dired-mode)
 *  	(process-send-string fileopenserver ((expand-file-name dired-filename-at-point)))
 *  	(process-send-string fileopenserver (expand-file-name (buffer-file-name)))
 *  	)
 *      (process-send-eof fileopenserver)
 *      (process-kill-without-query fileopenserver)
 *      )
 *    )
 * (global-set-key [(control e)]               'open-file-in-ide)
 * </code>
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class FileOpenServer implements Runnable {
    private static FileOpenServer fileOpenServer;

    public static FileOpenServer getFileOpenServer() {
        return getFileOpenServer(getFileOpenServerSettings().getPortNumber());
    }
    
    public static FileOpenServer getFileOpenServer(int portNumber) {
        if (fileOpenServer == null) {
            fileOpenServer = new FileOpenServer(portNumber);
        }
        return fileOpenServer;
    }
        
    private int _portNumber =   FileOpenServerConstants.PROPERTY_PORT_NUMBER_DEFAULT_VALUE;
    
    private ServerSocket _serverSocket;
    
    private Thread _serverThread;
    
    private boolean _listen;
    
    private Stack _fileOpenQueue;
    
    private FileOpenRequestListener _fileOpenRequestListener;
    
    private PropertyChangeSupport changeSupport;
    
    /**
     * Constructs a server with specified port number.
     *
     * @param portNumber
     *            the port number to listen on.
     */
    private FileOpenServer(int portNumber) {
        super();
        _portNumber = portNumber;
        changeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * @return Returns the portNumber.
     */
    public int getPortNumber() {
        return _portNumber;
    }
    
    /**
     * Set the port number to listen on.
     *
     * @param portNumber
     *            The portNumber to set.
     */
    public void setPortNumber(int portNumber) {
        _portNumber = portNumber;
    }
    
    /**
     * Set the file open request listener.
     *
     * @param fileOpenRequestListener
     *            The fileOpenRequestListener to set.
     */
    public void setFileOpenRequestListener(FileOpenRequestListener fileOpenRequestListener) {
        _fileOpenRequestListener = fileOpenRequestListener;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
    /**
     * Start the server thread of not already started.
     */
    public void startServer() {
        if (_serverThread == null) {
            _serverThread = new Thread(this);
            _serverThread.setDaemon(true);
            _listen = true;
            _serverThread.start();
        }
        changeSupport.firePropertyChange("STARTED", false, true);
    }
    
    /**
     * Stop the server thread of not already started.
     */
    public void stopServer() {
        if (_serverThread != null) {
            _listen = false;
            try {
                _serverThread.join();
            } catch (InterruptedException e) {
            }
            _serverThread = null;
        }
        if (_serverSocket != null) {
            try {
                _serverSocket.close();
            } catch (IOException e) {
            }
            _serverSocket = null;
        }
        changeSupport.firePropertyChange("STARTED", true, false);
    }
    
    /**
     * Restart the server.
     */
    public void restartServer() {
        restartServer(_portNumber);
    }
    
    /**
     * Restart the server with specified port.
     */
    public void restartServer(int portNumber) {
        stopServer();
        setPortNumber(portNumber);
        startServer();
    }
    
    /**
     * @return Returns the started state.
     */
    public boolean isStarted() {
        return (_serverThread != null);
    }
    
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
    public void run() {
        try {
            _serverSocket = new ServerSocket(_portNumber);
            _serverSocket.setSoTimeout(3000); // 3 seconds
            while (_listen) {
                Socket connection = null;
                try {
                    connection = _serverSocket.accept();
                } catch (IOException ioe) {
                    if (ioe instanceof SocketTimeoutException) {
                        continue;
                    } else {
                        throw ioe;
                    }
                }
                BufferedReader reader =
                new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
                String fileName = null;
                while ((fileName = reader.readLine()) != null) {
                    int lineNumber = 1;
                    int columnNumber = 0;
                    if (fileName.startsWith("+")) {
                        int spaceAt = fileName.indexOf(' ');
                        if (spaceAt != -1) {
                            String lineNumberString =
                            fileName.substring(1, spaceAt);
                            int colonAt = lineNumberString.indexOf(':');
                            if (colonAt != -1) {
                                String columnNumberString =
                                lineNumberString.substring(colonAt + 1);
                                try {
                                    columnNumber = Integer.parseInt(columnNumberString);
                                } catch (NumberFormatException e1) {
                                }
                                lineNumberString = lineNumberString.substring(0, colonAt);
                            }
                            try {
                                lineNumber = Integer.parseInt(lineNumberString);
                            } catch (NumberFormatException e1) {
                            }
                            fileName = fileName.substring(spaceAt + 1);
                        }
                    }
                    if (fileName != null) {
                        final File file = new File(fileName);
                        if (file != null && file.exists()) {
                            FileOpenRequestEvent fileOpenRequestEvent =
                            new FileOpenRequestEvent(this, fileName, lineNumber, columnNumber);
                            fireFileOpenRequestEvent(fileOpenRequestEvent);
                        }
                    }
                }
                reader.close();
                connection.close();
            }
            if (_listen == false) {
                if (_serverSocket != null) {
                    _serverSocket.close();
                }
            }
        } catch (IOException e) {
        } finally {
            _serverSocket = null;
        }
    }
    
    void fireFileOpenRequestEvent(FileOpenRequestEvent fileOpenRequestEvent) {
        if (_fileOpenRequestListener != null) {
            _fileOpenRequestListener.fileOpenRequest(fileOpenRequestEvent);
        }
    }
    
    static FileOpenServerSettings getFileOpenServerSettings() {
        return FileOpenServerSettings.getInstance();
    }    
}
