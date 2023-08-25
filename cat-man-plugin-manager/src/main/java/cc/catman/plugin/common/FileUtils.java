package cc.catman.plugin.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    public static List<File> deepFindFiles(Path path, FileFilter fileFilter) {
        File file = path.toFile();
        if (!file.exists()) {
            return Collections.emptyList();
        }
        return Arrays.stream(Optional.ofNullable(path.toFile().listFiles(fileFilter)).orElse(new File[0]))
                .flatMap(f -> {
                    if (f.isDirectory()) {
                        return deepFindFiles(f.toPath(), fileFilter).stream();
                    }
                    return Stream.of(f);
                }).collect(Collectors.toList());
    }

    public static List<File> deepFindDirs(Path path, FileFilter fileFilter) {
        File file = path.toFile();
        if (!file.exists()) {
            return Collections.emptyList();
        }
        return Arrays.stream(Optional.ofNullable(path.toFile().listFiles(fileFilter)).orElse(new File[0]))
                .flatMap(f -> {
                    if (!f.isDirectory()) {
                        return Stream.empty();
                    }

                    List<File> matchers = deepFindDirs(f.toPath(), fileFilter);
                    matchers.add(f);
                    return matchers.stream();
                }).collect(Collectors.toList());
    }

    @SneakyThrows
    public static List<File> deepFindFiles(Resource resource, FileFilter fileFilter) {
        if (resource.isFile()) {
            return new ArrayList<>(FileUtils.deepFindFiles(resource.getFile().toPath(), fileFilter));
        }
        return Collections.emptyList();

    }

    @SneakyThrows
    public static <T> List<T> deepFindFilesHandler(Resource resource, FileFilter fileFilter, Function<File, T> handler) {
        if (resource.isFile()) {
            return FileUtils.deepFindFiles(resource.getFile().toPath(), fileFilter)
                    .stream()
                    .map(handler::apply).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    public static <T> List<T> deepFindDirsHandler(Resource resource, FileFilter fileFilter, Function<File, T> handler) {
        if (resource.isFile()) {
            return FileUtils.deepFindDirs(resource.getFile().toPath(), fileFilter)
                    .stream()
                    .map(handler).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}