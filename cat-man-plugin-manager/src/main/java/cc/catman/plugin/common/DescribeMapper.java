package cc.catman.plugin.common;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class DescribeMapper {
    private static MapperFactory FACTORY = new DefaultMapperFactory.Builder()
            .build();
    public static MapperFactory getFactory(){
        if (FACTORY==null){
            synchronized (DescribeMapper.class){
                if (FACTORY==null){
                    new DescribeMapper();
                }
            }
        }
        return FACTORY;
    }
    public static MapperFacade getMapper() {
        return getFactory().getMapperFacade();
    }

    private DescribeMapper() {
        createFactory();
    }

    protected void createFactory() {
        FACTORY= new DefaultMapperFactory.Builder()
                .build();
        FACTORY.getConverterFactory().registerConverter(new BidirectionalConverter<String,Resource>() {
            @Override
            public Resource convertTo(String source, Type<Resource> destinationType, MappingContext mappingContext) {
                return new FileSystemResource(source);
            }

            @Override
            public String convertFrom(Resource source, Type<String> destinationType, MappingContext mappingContext) {
                try {
                    return source.getFile().toPath().toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static MapperFacade mapper() {
        return FACTORY.getMapperFacade();
    }

}
