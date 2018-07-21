/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.generate.project.index;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 *
 * @author Tim Boudreau
 */
public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        String path = args.length == 0 ? "///" : args[0];
        if ("///".equals(path)) {
            File f = new File(".").getAbsoluteFile();
            while (f != null && !f.getName().isEmpty()) {
                File gitDir = new File(f, ".git");
                if (gitDir.exists()) {
                    path = f.getAbsolutePath();
                    break;
                }
                f = f.getParentFile();
            }
        }
        if ("///".equals(path)) {
            System.err.println("No path supplied, and could not find a .git directory under " + new File(".").getAbsolutePath());
            System.exit(1);
        }
        Path base = Paths.get(path);

        Path masterPom = Paths.get(path, "pom.xml");
        if (!Files.exists(masterPom)) {
            System.err.println("Master pom.xml does not exist at " + masterPom);
            System.exit(2);
        }
        Set<Path> relative = new ProjectScanner(masterPom).getModules();
        Set<String> buildable = new HashSet<>();
        for (Path rel : relative) {
            String s = rel.toString().trim();
            if (!s.isEmpty()) {
                buildable.add(s);
            }
        }
//        System.out.println("buildable is " + buildable);

        System.err.println("Scanning for pom files in " + base);
        final Set<Path> allPaths = new HashSet<>();
        Files.walkFileTree(base, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if ("test".equals(dir.getFileName().toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Path rel = base.relativize(dir);
                if (rel.toString().contains("src/main") || rel.toString().contains("/src/test") || dir.getFileName().toString().equals(".git")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().equals("pom.xml")) {
                    allPaths.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                exc.printStackTrace();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        System.err.println("Generating index from " + allPaths.size() + " pom files...");
        Set<ProjectInfo> buildableModules = new ConcurrentSkipListSet<>();
        Set<ProjectInfo> unbuildableModules = new ConcurrentSkipListSet<>();
        Set<ProjectInfo> libs = new ConcurrentSkipListSet<>();
        Set<ProjectInfo> unbuildableLibs = new ConcurrentSkipListSet<>();
        Predicate<ProjectInfo> buildableCheck = i -> {
            String s = base.relativize(i.projectPath).toString();
            boolean result = buildable.contains(s);
            return result;
        };
        allPaths.parallelStream().forEach(pth -> {
            try {
                ProjectInfo info = new ProjectScanner(pth).toProjectInfo();
                if ("nbm".equals(info.packaging)) {
                    if (buildableCheck.test(info)) {
                        buildableModules.add(info);
                    } else {
                        unbuildableModules.add(info);
                    }
                } else if ("jar".equals(info.packaging)) {
                    if (buildableCheck.test(info)) {
                        libs.add(info);
                    } else {
                        unbuildableLibs.add(info);
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, pth.toString(), ex);
            }
        });

        String outFile = System.getProperty("index.file");
        try (Output out = outFile == null ? STDOUT : new FileOutput(outFile)) {
            System.err.println("Generating index...");
            out.println("\n## Buildable Modules\n");
            out.println("NetBeans modules that can be built now.\n");
            for (ProjectInfo proj : buildableModules) {
                out.println(proj.toString(base));
            }
            System.out.println("\n## Libraries\n");
            out.println("Libraries which can be built now.\n");
            for (ProjectInfo proj : libs) {
                out.println(proj.toString(base));
            }
            out.println("\n## Unbuildable Modules\n");
            out.println("Modules not currently included in the build, either because their dependencies have "
                    + "changed incompatibly, don't exist anymore, or in some cases something trivial needs to be done.");
            for (ProjectInfo proj : unbuildableModules) {
                out.println(proj.toString(base));
            }
            out.println("\n## Unbuildable Libraries\n");
            out.println("Modules not currently included in the build, either because their dependencies have "
                    + "changed incompatibly, don't exist anymore, or in some cases something trivial needs to be done.");
            for (ProjectInfo proj : unbuildableLibs) {
                out.println(proj.toString(base));
            }
        }

        System.err.println("Done.");
    }

    interface Output extends AutoCloseable {

        void println(String s);

        default void close() throws IOException {
            // do nothing
        }
    }

    static final Output STDOUT = System.out::println;

    static final class FileOutput implements Output {

        private final PrintStream ps;

        FileOutput(String s) throws FileNotFoundException {
            ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(s))));
        }

        @Override
        public void println(String s) {
            ps.println(s);
        }

        @Override
        public void close() throws IOException {
            ps.close();
        }
    }
}
