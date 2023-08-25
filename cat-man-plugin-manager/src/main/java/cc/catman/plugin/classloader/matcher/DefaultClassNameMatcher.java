package cc.catman.plugin.classloader.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultClassNameMatcher implements IClassNameMatcher {
    private static final String CLASS_FILE_SUFFIX = ".class";

    protected List<String> excludePackages;

    protected List<String> excludeClasses;

    protected Map<String, Pattern> excludeRegex;
    public DefaultClassNameMatcher() {
        this.excludePackages = new ArrayList<>();
        this.excludeClasses = new ArrayList<>();
        this.excludeRegex = new HashMap<>();
    }


    public static DefaultClassNameMatcher create() {
        return new DefaultClassNameMatcher();
    }

    public static DefaultClassNameMatcher of(String... packages) {
        return new DefaultClassNameMatcher().addExcludePackage(packages);
    }

    public DefaultClassNameMatcher addExcludePackage(String... packages) {
        for (String p : packages) {
            if (!this.excludePackages.contains(p)) {
                this.excludePackages.add(p);
            }
        }
        return this;
    }

    protected DefaultClassNameMatcher addExcluudeClasses(String classNames) {
        for (String c : excludeClasses) {
            if (!this.excludeClasses.contains(c)) {
                this.excludeClasses.add(c);
            }
        }
        return this;
    }

    protected DefaultClassNameMatcher addExcluudeRegexs(String... regexs) {
        for (String r : regexs) {
            if (!this.excludeRegex.containsKey(r)) {
                this.excludeRegex.put(r, Pattern.compile(r));
            }
        }
        return this;
    }

    @Override
    public boolean match(String className) {
        final String cn = className.endsWith(CLASS_FILE_SUFFIX)
                ? className.substring(0, className.length() - CLASS_FILE_SUFFIX.length())
                : className;

        return this.excludePackages.stream().anyMatch(p -> {
            return cn.startsWith(p);
        }) ||
                this.excludeClasses.stream().anyMatch(c -> {
                    return cn.equals(c);
                }) ||
                this.excludeRegex.values().stream().anyMatch(r -> {
                    return r.matcher(cn).matches();
                });
    }
}
