package cc.catman.plugin.describe.handler.mapper;

import ma.glasnost.orika.DefaultFieldMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingException;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.IntrospectorPropertyResolver;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test2 {
    public static void main(String[] args) {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .propertyResolverStrategy(new ElementPropertyResolver())
                .build();
        A a = new A();
        a.setName("name");
        Map<String, List<String>> dynamic = new HashMap<>();
        dynamic.put("items", Arrays.asList("1", "@"));
        dynamic.put("item", Arrays.asList("2", "@"));
        dynamic.put("it", Arrays.asList("r", "@"));
        a.setDynamic(dynamic);
        // 这个可以
//        factory.classMap(A.class, B.class)
//                .fieldMap("dynamic[value]", "items").add()
////                .fieldMap("dynamic['items']","items").add()
//                .byDefault()
//                .register();

        MapperFacade mapper = factory.getMapperFacade();
        B map = mapper.map(a, B.class);
        System.out.println(map);
    }

    public static class ElementPropertyResolver extends IntrospectorPropertyResolver {

        protected Property getProperty(java.lang.reflect.Type type, String expr,
                                       boolean isNestedLookup, Property owner) throws MappingException {
            Property property = null;
            try {
                property = super.getProperty(type, expr, isNestedLookup, null);
            } catch (MappingException e) {
                try {
                    property = super.resolveInlineProperty(type, expr +
                                                                 ":{getAttribute(\"" + expr + "\")|setAttribute(\"" + expr + "\",%s)|type=" +
                                                                 (isNestedLookup ? Element.class.getName() : "Object") + "}");
                } catch (MappingException e2) {
                    throw e; // throw the original exception
                }
            }
            return property;
        }
    }
}
