/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.generate.project.index;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tim Boudreau
 */
public class ProjectInfo implements Comparable<ProjectInfo> {

    public final String name;
    public final String version;
    public final String description;
    public final String artifactId;
    public final Path projectPath;
    public final String category;
    public final String packaging;
    public final Set<String> developers;

    public ProjectInfo(String name, String version, String description, String artifactId, Path projectPath, String category, String packaging, Set<String> developers) {
        this.name = name;
        this.description = description;
        this.artifactId = artifactId;
        this.projectPath = projectPath;
        this.category = category;
        this.packaging = packaging;
        this.developers = developers;
        this.version = version;
    }

    @Override
    public int compareTo(ProjectInfo o) {
        if (!packaging.equals(o.packaging)) {
            if ("jar".equals(packaging) && "nbm".equals(o.packaging)) {
                return 1;
            } else {
                return -1;
            }
        }
        return title().compareToIgnoreCase(o.title());
    }

    public StringBuilder ap(StringBuilder sb, String s) {
        if (s != null && !s.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(s);
        }
        return sb;
    }

    public String toString(Path root) {
        StringBuilder sb = new StringBuilder("###").append(title());
        if (version != null && !version.isEmpty() && !"RELEASE90".equals(version)) {
            sb.append("\n*Version*: ").append(version).append("\n");
        }
        sb.append("\n*Relative Path*: ").append(root.relativize(projectPath).toString()).append("\n\n");
        if (!developers.isEmpty()) {
            sb.append("*Author(s)*: ").append(authors()).append('\n');
        }
        if (description != null && !description.isEmpty()) {
            sb.append("> ").append(description());
        }
        sb.append("\n\n");

        return sb.toString();
    }

    public String toString() {
        return toString(new File(".").getAbsoluteFile().toPath());
    }

    public String authors() {
        StringBuilder sb = new StringBuilder();
        for (String dev : developers) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(dev);
        }
        return sb.toString();
    }

    public Path path() {
        return projectPath;
    }

    public String description() {
        return description == null ? "(no description)" : description.replaceAll("\\s+", " ");
    }

    static final Pattern STRIP_AID = Pattern.compile("org[-\\.\\_]netbeans[-\\.\\_].*?[-\\.\\_](.*)");

    private String capitalize(String aid) {
        StringBuilder sb = new StringBuilder();
        boolean lastWasSpace = true;
        for (char c : aid.toCharArray()) {
            if (lastWasSpace) {
                lastWasSpace = false;
                c = Character.toUpperCase(c);
            }
            switch (c) {
                case '-':
                case '_':
                    c = ' ';
                    lastWasSpace = true;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public String title() {
        String name = this.name;
        if (name != null && !name.isEmpty()) {
            if (name.charAt(0) == '.') {
                name = name.substring(1);
            }
            return capitalize(name);
        }
        String aid = artifactId;
        Matcher m = STRIP_AID.matcher(aid);
        if (m.find()) {
            aid = m.group(1);
        }
        return capitalize(aid);
    }

}
