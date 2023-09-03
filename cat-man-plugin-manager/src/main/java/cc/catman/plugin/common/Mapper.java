package cc.catman.plugin.common;

import cc.catman.plugin.core.describe.PluginParseInfo;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;

public class Mapper {
    private static ModelMapper FACTORY;
    public static ModelMapper get(){
        if (FACTORY==null){
            synchronized (DescribeMapper.class){
                if (FACTORY==null){
                   FACTORY=new ModelMapper();
                   FACTORY.addConverter(new Converter<Queue, Queue>() {
                       @Override
                       public Queue convert(MappingContext<Queue, Queue> mappingContext) {
                           return mappingContext.getSource();
                       }
                   });
                }
            }
        }
        return FACTORY;
    }
    public static <T> T map(PluginParseInfo parseInfo, Class<T> target, String ...values ){
        T map = get().map(parseInfo, target);
        Arrays.asList(values).stream().forEach(v->{
            Optional.ofNullable(parseInfo.getDynamicValues().get(v)).ifPresent(val->{
              try {
                  Field field = ReflectionUtils.findField(target, v);
                  ReflectionUtils.makeAccessible(field);
                  ReflectionUtils.setField(field,map,get().map(val,field.getType()));
              }catch (RuntimeException e){
                  e.printStackTrace();
              }
            });
        });
        return map;
    }


}
