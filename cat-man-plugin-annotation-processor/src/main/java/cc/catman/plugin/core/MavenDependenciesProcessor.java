package cc.catman.plugin.core;

import lombok.SneakyThrows;
import org.apache.maven.shared.invoker.*;

import java.nio.file.Paths;
import java.util.Arrays;

public class MavenDependenciesProcessor {
    @SneakyThrows
    public void invoke(){
        InvocationRequest request=new DefaultInvocationRequest();
        String[] strings = {
                "dependency:get",
                "--debug",
                "-DrepoUrl=https://repo1.maven.org/maven2"+
                "-DgroupId=org.apache.maven.plugin-tools",
                "-DartifactId=maven-plugin-annotations",
                "-Dversion=3.9.0",
        };
        request.setGoals(Arrays.asList(strings));
        request.setDebug(true);
        Invoker invoker=new DefaultInvoker();
//        invoker.setMavenHome(Paths.get(System.getProperty("user.dir")).toFile());
        invoker.setMavenExecutable(Paths.get("/Users/jpanda/.sdkman/candidates/maven/current/bin/mvn").toFile());
        invoker.setLocalRepositoryDirectory(Paths.get("/Users/jpanda/.m2/repository").toFile());
        InvocationResult result = invoker.execute(request);
        if ( result.getExitCode() != 0 )
        {
            throw new IllegalStateException( "Build failed." );
        }
    }

    public static void main(String[] args) {

    }
}
