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
package org.netbeans.modules.erlang.project.gems;

/**
 * A descriptor of a Ruby Gem.
 *
 * @author Tor Norbye
 */
public final class Gem implements Comparable<Gem> {
    private String name;
    private String desc;
    private String installedVersions;
    private String availableVersions;
    
    public Gem(String name, String installedVersions, String availableVersions) {
        this.name = name;
        this.installedVersions = installedVersions;
        this.availableVersions = availableVersions;
    }

    public String getName() {
        return name;
    }

    public String getInstalledVersions() {
        return installedVersions;
    }

    public String getAvailableVersions() {
        return availableVersions;
    }

    public String getDescription() {
        return desc;
    }

    public String toString() {
        // Shown in ListCellRenderer etc.
        StringBuilder sb = new StringBuilder(100);
        sb.append("<html><b>");
        sb.append(name);
        sb.append("</b>");

        if (installedVersions != null) {
            sb.append(" (");
            sb.append(installedVersions);
            sb.append(") ");
        }

        if (desc != null) {
            sb.append(": ");
            sb.append(desc);
        }

        sb.append("</html>");

        return sb.toString();
    }

    public int compareTo(Gem other) {
        return name.compareTo(other.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvailableVersions(String versions) {
        this.availableVersions = versions;
    }

    public void setInstalledVersions(String versions) {
        this.installedVersions = versions;
    }

    public void setDescription(String description) {
        this.desc = description;
    }
}
