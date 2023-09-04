package cc.catman.plugin.core;

import cc.catman.plugin.core.annotations.ExtensionPoint;
import cc.catman.plugin.core.annotations.Gav;
import cc.catman.plugin.core.annotations.Plugin;
import cc.catman.plugin.core.annotations.Prop;
import com.google.auto.service.AutoService;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.apache.maven.shared.utils.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static cc.catman.plugin.core.CatManPluginAnnotationProcessor.EXTENSION_POINT_ANNOTATION_NAME;
import static cc.catman.plugin.core.CatManPluginAnnotationProcessor.PLUGIN_ANNOTATION_NAME;

@AutoService(Processor.class)
@SupportedAnnotationTypes(
        {
                PLUGIN_ANNOTATION_NAME,
                EXTENSION_POINT_ANNOTATION_NAME,
        }
)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CatManPluginAnnotationProcessor extends AbstractProcessor {
    public static final String PLUGIN_ANNOTATION_NAME = "cc.catman.plugin.core.annotations.Plugin";
    public static final String EXTENSION_POINT_ANNOTATION_NAME = "cc.catman.plugin.core.annotations.ExtensionPoint";

    public static final String PLUGIN_DESC_FILE_NAME = "META-INF/cat-man-plugin/cat-man-plugin.json";

    private Messager messager;
    private Gson gson;
    private JsonWriter jsonWriter;
    private JsonObject jsonPlugin;
    private JsonArray jsonExtensionPoints;
    private Map<String, String> options;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
         options = processingEnv.getOptions();
        this.messager = processingEnv.getMessager();
        Filer filer = processingEnv.getFiler();
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.jsonPlugin = new JsonObject();
        this.jsonExtensionPoints = new JsonArray();
        try {
            Writer writer = filer.createResource(StandardLocation.CLASS_OUTPUT, "", PLUGIN_DESC_FILE_NAME).openWriter();
            this.jsonWriter = gson.newJsonWriter(writer);
        } catch (IOException ex) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + "unable to create plugin description file: " + PLUGIN_DESC_FILE_NAME);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                if (element.getKind() == ElementKind.CLASS) {
                    // 当前元素是一个插件
                    handlerPlugin(element);
                    handlerExtensionPoint(element);
                }
            }
        }
        if (roundEnv.processingOver()){
            this.jsonPlugin.add("extensionsPoints", jsonExtensionPoints);
            save();
        }
        return true;
    }

    private void handlerExtensionPoint(Element element) {
        ExtensionPoint extensionPoint = element.getAnnotation(ExtensionPoint.class);
        if (null==extensionPoint){
            return;
        }
        TypeElement typeElement= (TypeElement) element;
        // 如果一个类上同时标注了 插件 和 扩展点 的注解,那么他会被多次扫描到,所以这里要去重.
        JsonPrimitive ep = new JsonPrimitive(typeElement.getQualifiedName().toString());
        if (!this.jsonExtensionPoints.contains(ep)){
            this.jsonExtensionPoints.add(ep);
        }

    }

    protected void handlerPlugin(Element element) {
        // 当前元素是一个插件
        Plugin plugin = element.getAnnotation(Plugin.class);
        if (null == plugin) {
            return;
        }
        // 支持编译时通过-D的方式传参来修改版本号
        String version = Optional.ofNullable(options.get("cc.catman.plugin.version")).orElse(plugin.version());
        String group = Optional.ofNullable(options.get("cc.catman.plugin.group")).orElse(plugin.group());
        jsonPlugin.addProperty("name", plugin.name());
        jsonPlugin.addProperty("group", group);
        jsonPlugin.addProperty("version", version);
        jsonPlugin.addProperty("source", plugin.source());
        jsonPlugin.addProperty("kind", plugin.kind());
        jsonPlugin.addProperty("relativePath", plugin.relativePath());
        JsonArray jsonLibs = new JsonArray();
        Arrays.stream(plugin.libs()).forEach(jsonLibs::add);
        jsonPlugin.add("libAntPatterns", jsonLibs);
        JsonObject props = new JsonObject();
        for (Prop p : plugin.properties()) {
            props.addProperty(p.name(), p.value());
        }
        jsonPlugin.add("properties",props);
        JsonArray dependencies = new JsonArray();
        jsonPlugin.add("dependencies",dependencies);
        for (Gav g:plugin.dependencies()){
            JsonObject dependency = new JsonObject();
            dependency.addProperty("group", !g.group().isEmpty()?g.group():plugin.group());
            dependency.addProperty("name",g.name());
            dependency.addProperty("version",!g.version().isEmpty()?g.version():version);
            dependencies.add(dependency);
        }
        resavePom();
    }

    private void resavePom() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document n = documentBuilder.newDocument();
            Document d = documentBuilder.parse("/Users/jpanda/work/codes/customer/cat-man/cat-man-plugin/cat-man-plugin-core/pom.xml");
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

    private void save() {
        try {
            gson.toJson(jsonPlugin, this.jsonWriter);
            this.jsonWriter.flush();
        } catch (IOException e) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + "unable to save plugin description file: " + PLUGIN_DESC_FILE_NAME);
        }
    }
}

