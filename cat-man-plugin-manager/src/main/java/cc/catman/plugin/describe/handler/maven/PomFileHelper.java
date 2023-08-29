package cc.catman.plugin.describe.handler.maven;

import cc.catman.plugin.common.GAV;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class PomFileHelper {
    public static  final String POM_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
                                             "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"" + "\n" +
                                             "\t" + "\t" + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "\n" +
                                             "\t" + "\t" + " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" + "\n" +
                                             "\t" + "<modelVersion>4.0.0</modelVersion>" + "\n" +
                                             "\t" + "<groupId>cc.catman.plugin</groupId>" + "\n" +
                                             "\t" + "<artifactId>cat-man-plugin</artifactId>" + "\n" +
                                             "\t" + "<version>" +
                                             "\t" + UUID.randomUUID() +
                                             "\t" + "</version>" + "\n" +
                                             "\t" + "<dependencies>" + "\n";

    public static  final String POM_SUFFIX = "\t" + "</dependencies>" + "\n" +
                                             "</project>";

    public static File build(String name,Path depsFile) throws IOException {
    StringBuilder deps=new StringBuilder();

        Files.readAllLines(depsFile)
                .stream()
                .map(line -> line.trim().split(":"))
                .filter(line -> line.length == 4).forEach(info -> {
                    deps.append("\t").append("<dependency>").append("\n")
                            .append("\t") .append("\t").append("\t").append("<groupId>").append(info[0]).append("</groupId>").append("\n")
                            .append("\t") .append("\t").append("\t") .append("<artifactId>").append(info[1]).append("<artifactId>").append("\n")
                            .append("\t") .append("\t").append("\t").append("<version>").append(info[3]).append("</version>").append("\n")
                            .append("\t").append("\t").append("</dependency>").append("\n");
                });
        File pom = File.createTempFile(name, ".xml");
        return Files.write(pom.toPath(),(POM_PREFIX + deps + POM_SUFFIX).getBytes()).toFile();
    }
    public static File build(String name,File pom, List<GAV> gavs) throws IOException {
        StringBuilder deps=new StringBuilder();

        gavs.forEach(gav -> {
                    deps.append("\t").append("<dependency>").append("\n")
                            .append("\t") .append("\t").append("\t").append("<groupId>").append(gav.getGroup()).append("</groupId>").append("\n")
                            .append("\t") .append("\t").append("\t") .append("<artifactId>").append(gav.getName()).append("<artifactId>").append("\n")
                            .append("\t") .append("\t").append("\t").append("<version>").append(gav.getVersion()).append("</version>").append("\n")
                            .append("\t").append("\t").append("</dependency>").append("\n");
                });

        return Files.write(pom.toPath(),(POM_PREFIX + deps + POM_SUFFIX).getBytes()).toFile();
    }
}
