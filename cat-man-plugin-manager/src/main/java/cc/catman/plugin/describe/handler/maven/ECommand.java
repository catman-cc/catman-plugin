package cc.catman.plugin.describe.handler.maven;

import lombok.Getter;

public enum ECommand {
    DEPENDENCY_GET("dependency:get"),
    DEPENDENCY_COPY("dependency:copy"),
    DEPENDENCY_BUILD_CLASSPATH("dependency:build-classpath"),
    DEPENDENCY_COPY_DEPENDENCIES("dependency:copy-dependencies"),
    ;
    @Getter
    private String command;


    ECommand(String command) {
        this.command = command;
    }
}
