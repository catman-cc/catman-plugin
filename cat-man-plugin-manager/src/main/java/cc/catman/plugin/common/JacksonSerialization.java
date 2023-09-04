package cc.catman.plugin.common;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
public class JacksonSerialization {

    public static ObjectMapper AddResourceSerializatin(ObjectMapper om){
        SimpleModule simpleModule=new SimpleModule();
        simpleModule.addSerializer(Resource.class,new ResourceSerialization(Resource.class));
        simpleModule.addDeserializer(Resource.class,new ResourceDeSerialization(Resource.class));
        om.registerModule(simpleModule);
        return om;
    }

    public static class ResourceSerialization extends StdSerializer<Resource>{
        protected ResourceSerialization(Class<Resource> t) {
            super(t);
        }

        @Override
        public void serialize(Resource resource, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(String.valueOf(resource.getURI()));
        }
    }
    public static class ResourceDeSerialization extends StdDeserializer<Resource>{

        protected ResourceDeSerialization(Class<?> vc) {
            super(vc);
        }

        protected ResourceDeSerialization(JavaType valueType) {
            super(valueType);
        }

        protected ResourceDeSerialization(StdDeserializer<?> src) {
            super(src);
        }

        @Override
        public Resource deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return new UrlResource(jsonParser.getValueAsString());
        }
    }

}
