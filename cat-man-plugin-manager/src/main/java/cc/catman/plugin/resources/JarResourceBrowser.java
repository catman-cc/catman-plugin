package cc.catman.plugin.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class JarResourceBrowser implements IResourceBrowser {
    @Override
    public boolean support(Resource resource) {
        if (!resource.isFile()) {
            return false;
        }
        return Optional.ofNullable(resource.getFilename()).map(filename -> filename.endsWith(".jar")).orElse(false);
    }

    @Override
    public ResourceBrowserResult browser(Resource resource, ResourceVisitor visitor) {
        boolean vc=true;
        try {
            File jar = resource.getFile();
            String path = jar.toString();
            try (JarFile jarFile = new JarFile(jar)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    // 回传给验证器验证,并根据验证器的返回值决定是否继续处理
                    if (!visitor.visitor(new UrlResource(new URL(wrapperToJarURL(path, jarEntry.getName()))))) {
                        vc=false;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.warn("Unable to correctly process the resource: {} , the exception information is:", resource, e);
            return ResourceBrowserResult.of(false,vc);
        }
        return ResourceBrowserResult.of(true,vc);
    }

    public static String wrapperToJarURL(String jarPath, String innerPath) {
        return ResourceUtils.JAR_URL_PREFIX + ResourceUtils.FILE_URL_PREFIX +
               jarPath + ResourceUtils.JAR_URL_SEPARATOR + innerPath;
    }
}
