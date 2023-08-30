package cc.catman.plugin.describe;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

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
    }

    public static MapperFacade mapper() {
        return FACTORY.getMapperFacade();
    }

}
