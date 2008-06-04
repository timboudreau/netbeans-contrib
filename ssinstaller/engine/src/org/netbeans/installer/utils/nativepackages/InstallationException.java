package org.netbeans.installer.utils.nativepackages;

public class InstallationException extends Exception {

    public InstallationException(Throwable cause) {
        super(cause);
    }

    public InstallationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstallationException(String message) {
        super(message);
    }

}
