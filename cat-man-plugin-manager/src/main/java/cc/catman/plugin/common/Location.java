package cc.catman.plugin.common;

import lombok.Data;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 一个用于表示地址的信息对象
 */
@Data
public class Location {
    protected  String location;
    protected Path path;
    protected File file;
    protected URI uri;

    public Location(String location) {
        this.location = location;
        this.path= Paths.get(location);
        this.file=path.toFile();
        this.uri=path.toUri();
    }
}
