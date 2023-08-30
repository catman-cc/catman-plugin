package cc.catman.plugin.describe.handler.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class B extends A{
    private String name;
    private List<String> items;
}
