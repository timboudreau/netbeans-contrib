/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.installer.wizard;

import java.io.File;
import java.io.IOException;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.NbiProperties;

/**
 *  This Ulitity class check given file location for installaiton.
 *  Only some common checks are performed.
 */
public class FileLocationValidator {

    NbiProperties component = new NbiProperties();
    private String[] prohibitedInstallationPathParts;

    public String validateInput(String string) {

        if (string.equals(StringUtils.EMPTY_STRING)) {
            return StringUtils.format(
                    component.getProperty(ERROR_NULL_PROPERTY),
                    string);
        }

        File file = FileUtils.eliminateRelativity(string);

        String filePath = file.getAbsolutePath();
        if (filePath.length() > 45) {
            filePath = filePath.substring(0, 45) + "...";
        }

        if (!SystemUtils.isPathValid(file.getAbsolutePath())) {
            return StringUtils.format(
                    component.getProperty(ERROR_NOT_VALID_PROPERTY),
                    filePath);
        }

        final String[] prohibitedParts = prohibitedInstallationPathParts;
        if (prohibitedParts != null) {
            for (String s : prohibitedParts) {
                if (s != null && s.length() > 0) {
                    String prop = null;
                    if (s.length() == 1) { // character
                        if (file.getAbsolutePath().contains(s)) {
                            if (s.equals("!")) {
                                prop = ERROR_CONTAINS_EXCLAMATION_PROPERTY;
                            } else if (s.equals(";")) {
                                prop = ERROR_CONTAINS_SEMICOLON_PROPERTY;
                            } else if (s.equals(":")) {
                                prop = ERROR_CONTAINS_COLON_PROPERTY;
                            } else if (s.equals("&")) {
                                prop = ERROR_CONTAINS_AMPERSAND_PROPERTY;
                            } else {
                                // no user-friendly description for all other chars at this moment
                                // can be easily extended later
                                prop = ERROR_CONTAINS_WRONG_CHAR_PROPERTY;
                            }
                        }
                    } else {// check if path matches regexp..
                        if (file.getAbsolutePath().matches(s)) {
                            prop = ERROR_MATCHES_PROHIBITED_REGEXP;
                        }
                    }
                    if (prop != null) {
                        return StringUtils.format(
                                component.getProperty(prop),
                                filePath,
                                s);
                    }
                }
            }
        }

        if (!file.equals(file.getAbsoluteFile())) {
            return StringUtils.format(
                    component.getProperty(ERROR_NOT_ABSOLUTE_PROPERTY),
                    file.getPath());
        }

        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            return StringUtils.format(
                    component.getProperty(ERROR_CANNOT_CANONIZE_PROPERTY),
                    filePath);
        }

        filePath = file.getAbsolutePath();
        if (filePath.length() > 45) {
            filePath = filePath.substring(0, 45) + "...";
        }

        if (file.exists() && !file.isDirectory()) {
            return StringUtils.format(
                    component.getProperty(ERROR_NOT_DIRECTORY_PROPERTY),
                    filePath);
        }

        if (!FileUtils.canRead(file)) {
            return StringUtils.format(
                    component.getProperty(ERROR_NOT_READABLE_PROPERTY),
                    filePath);
        }

        if (!FileUtils.canWrite(file)) {
            return StringUtils.format(
                    component.getProperty(ERROR_NOT_WRITABLE_PROPERTY),
                    filePath);
        }

        if (!FileUtils.isEmpty(file)) {
            return StringUtils.format(
                    component.getProperty(ERROR_NOT_EMPTY_PROPERTY),
                    filePath);
        }
        /*
        if (SystemUtils.isMacOS() && (
        product.getLogic().wrapForMacOs() ||
        product.getLogic().requireDotAppForMacOs()) &&
        !file.getAbsolutePath().endsWith(APP_SUFFIX)) {
        return StringUtils.format(
        component.getProperty(ERROR_NOT_ENDS_WITH_APP_PROPERTY),
        filePath);
        }


        if(!Boolean.getBoolean(SystemUtils.NO_SPACE_CHECK_PROPERTY)) {
        final long requiredSize =
        product.getRequiredDiskSpace() + REQUIRED_SPACE_ADDITION;
        final long availableSize =
        SystemUtils.getFreeSpace(file);
        if (availableSize < requiredSize) {
        return StringUtils.format(
        component.getProperty(ERROR_NOT_ENOUGH_SPACE_PROPERTY),
        filePath,
        StringUtils.formatSize(requiredSize - availableSize));
        }
        }*/


        return null;
    }

    public void setProperty(String name, String value) {
        component.setProperty(name, value);
    }

    public FileLocationValidator(String[] prohibitedInstallationPathParts) {
        this();
        this.prohibitedInstallationPathParts = prohibitedInstallationPathParts;
    }

    public FileLocationValidator() {
        setProperty(ERROR_NULL_PROPERTY,
                DEFAULT_ERROR_NULL);
        setProperty(ERROR_NOT_VALID_PROPERTY,
                DEFAULT_ERROR_NOT_VALID);
        setProperty(ERROR_CONTAINS_EXCLAMATION_PROPERTY,
                DEFAULT_ERROR_CONTAINS_EXCLAMATION);
        setProperty(ERROR_CONTAINS_SEMICOLON_PROPERTY,
                DEFAULT_ERROR_CONTAINS_SEMICOLON);
        setProperty(ERROR_CONTAINS_COLON_PROPERTY,
                DEFAULT_ERROR_CONTAINS_COLON);
        setProperty(ERROR_CONTAINS_AMPERSAND_PROPERTY,
                DEFAULT_ERROR_CONTAINS_AMPERSAND);
        setProperty(ERROR_CONTAINS_WRONG_CHAR_PROPERTY,
                DEFAULT_ERROR_CONTAINS_WRONG_CHAR);
        setProperty(ERROR_MATCHES_PROHIBITED_REGEXP,
                DEFAULT_ERROR_MATCHES_PROHIBITIED_REGEXP);
        setProperty(ERROR_CANNOT_CANONIZE_PROPERTY,
                DEFAULT_ERROR_CANNOT_CANONIZE);
        setProperty(ERROR_NOT_ABSOLUTE_PROPERTY,
                DEFAULT_ERROR_NOT_ABSOLUTE);
        setProperty(ERROR_NOT_DIRECTORY_PROPERTY,
                DEFAULT_ERROR_NOT_DIRECTORY);
        setProperty(ERROR_NOT_READABLE_PROPERTY,
                DEFAULT_ERROR_NOT_READABLE);
        setProperty(ERROR_NOT_WRITABLE_PROPERTY,
                DEFAULT_ERROR_NOT_WRITABLE);
        setProperty(ERROR_NOT_EMPTY_PROPERTY,
                DEFAULT_ERROR_NOT_EMPTY);        
    }
    public static final String ERROR_NULL_PROPERTY =
            "error.null"; // NOI18N
    public static final String ERROR_NOT_VALID_PROPERTY =
            "error.not.valid"; // NOI18N
    public static final String ERROR_CONTAINS_EXCLAMATION_PROPERTY =
            "error.contains.exclamation"; // NOI18N
    public static final String ERROR_CONTAINS_SEMICOLON_PROPERTY =
            "error.contains.semicolon"; // NOI18N
    public static final String ERROR_CONTAINS_COLON_PROPERTY =
            "error.contains.colon"; // NOI18N
    public static final String ERROR_CONTAINS_AMPERSAND_PROPERTY =
            "error.contains.ampersand"; // NOI18N
    public static final String ERROR_CONTAINS_WRONG_CHAR_PROPERTY =
            "error.contains.wrong.char"; // NOI18N
    public static final String ERROR_MATCHES_PROHIBITED_REGEXP =
            "error.matches.prohibited.regexp";//NOI18N
    public static final String ERROR_NOT_ABSOLUTE_PROPERTY =
            "error.not.absolute"; // NOI18N
    public static final String ERROR_CANNOT_CANONIZE_PROPERTY =
            "error.cannot.canonize"; // NOI18N
    public static final String ERROR_NOT_DIRECTORY_PROPERTY =
            "error.not.directory"; // NOI18N
    public static final String ERROR_NOT_READABLE_PROPERTY =
            "error.not.readable"; // NOI18N
    public static final String ERROR_NOT_WRITABLE_PROPERTY =
            "error.not.writable"; // NOI18N
    public static final String ERROR_NOT_EMPTY_PROPERTY =
            "error.not.empty"; // NOI18N
    public static final String ERROR_NOT_ENDS_WITH_APP_PROPERTY =
            "error.not.ends.with.app"; // NOI18N
    public static final String ERROR_NOT_ENOUGH_SPACE_PROPERTY =
            "error.not.enough.space"; // NOI18N
    public static final String ERROR_CANNOT_GET_LOGIC_PROPERTY =
            "error.cannot.get.logic";//NOI18N
    public static final String ERROR_CANNOT_CHECK_SPACE_PROPERTY =
            "error.cannot.check.space"; // NOI18N

    public static final String DEFAULT_ERROR_NULL =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.null"); // NOI18N
    public static final String DEFAULT_ERROR_NOT_VALID =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.not.valid"); // NOI18N
    public static final String DEFAULT_ERROR_CONTAINS_EXCLAMATION =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.contains.exclamation"); // NOI18N
    public static final String DEFAULT_ERROR_CONTAINS_SEMICOLON =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.contains.semicolon"); // NOI18N
    public static final String DEFAULT_ERROR_CONTAINS_COLON =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.contains.colon"); // NOI18N
    public static final String DEFAULT_ERROR_CONTAINS_AMPERSAND =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.contains.ampersand"); // NOI18N
    public static final String DEFAULT_ERROR_CONTAINS_WRONG_CHAR =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.contains.wrong.char"); // NOI18N
    public static final String DEFAULT_ERROR_MATCHES_PROHIBITIED_REGEXP =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.matches.prohibited.regexp"); // NOI18N
    public static final String DEFAULT_ERROR_NOT_ABSOLUTE =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.not.absolute"); // NOI18N
    public static final String DEFAULT_ERROR_CANNOT_CANONIZE =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.cannot.canonize"); // NOI18N
    public static final String DEFAULT_ERROR_NOT_DIRECTORY =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.not.directory"); // NOI18N
    public static final String DEFAULT_ERROR_NOT_READABLE =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.not.readable"); // NOI18N
    public static final String DEFAULT_ERROR_NOT_WRITABLE =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.not.writable"); // NOI18N
    public static final String DEFAULT_ERROR_NOT_EMPTY =
            ResourceUtils.getString(FileLocationValidator.class,
            "FLV.error.not.empty"); // NOI18N
}
