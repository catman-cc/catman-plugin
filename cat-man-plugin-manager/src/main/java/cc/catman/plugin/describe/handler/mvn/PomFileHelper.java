package cc.catman.plugin.describe.handler.mvn;

import cc.catman.plugin.common.GAV;
import org.springframework.core.io.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
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

    public static String artifacts(Resource resource) throws IOException {
        StringBuilder deps=new StringBuilder();
        try(BufferedReader br=new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            String line;
            // 通过readLine()方法按行读取字符串
            while ((line = br.readLine()) != null) {
                String[]info=line.split(":");
                if (info.length!=4){
                    continue;
                }
                deps.append(info[0]).append(":").append(info[1]).append(":").append(info[3]).append(",");
            }
        }
        String string = deps.toString();
        return string.endsWith(",")?string.substring(0,string.length()-1):string;
    }
    public static String readPom(Resource resource) throws IOException {
        StringBuilder deps=new StringBuilder();
        try(BufferedReader br=new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            String line;
            // 通过readLine()方法按行读取字符串
            while ((line = br.readLine()) != null) {
                String[]info=line.split(":");
                if (info.length!=4){
                    continue;
                }
                deps.append("\t").append("<dependency>").append("\n")
                        .append("\t") .append("\t").append("\t").append("<groupId>").append(info[0]).append("</groupId>").append("\n")
                        .append("\t") .append("\t").append("\t") .append("<artifactId>").append(info[1]).append("</artifactId>").append("\n")
                        .append("\t") .append("\t").append("\t").append("<version>").append(info[3]).append("</version>").append("\n")
                        .append("\t").append("\t").append("</dependency>").append("\n");
            }
        }
        return (POM_PREFIX + deps + POM_SUFFIX);
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
