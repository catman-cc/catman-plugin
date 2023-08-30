package cc.catman.plugin.describe.handler.mapper;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class A {
    private String  name;
    private Map<String, List<String>> dynamic;
}
