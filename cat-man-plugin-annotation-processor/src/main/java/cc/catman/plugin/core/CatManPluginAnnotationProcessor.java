package cc.catman.plugin.core;

import cc.catman.plugin.core.annotations.ExtensionPoint;
import cc.catman.plugin.core.annotations.Plugin;
import cc.catman.plugin.core.annotations.Prop;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeVisitor;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
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

    public static final String PLUGIN_DESC_FILE_NAME = "cat-man-plugin.json";

    private Messager messager;
    private Gson gson;
    private JsonWriter jsonWriter;
    private JsonObject jsonPlugin;
    private JsonArray jsonExtensionPoints;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
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
        return false;
    }

    private void handlerExtensionPoint(Element element) {
        ExtensionPoint extensionPoint = element.getAnnotation(ExtensionPoint.class);
        if (null==extensionPoint){
            return;
        }
        TypeElement typeElement= (TypeElement) element;
        this.jsonExtensionPoints.add(typeElement.getQualifiedName().toString());

    }

    protected void handlerPlugin(Element element) {
        // 当前元素是一个插件
        Plugin plugin = element.getAnnotation(Plugin.class);
        if (null == plugin) {
            return;
        }
        jsonPlugin.addProperty("name", plugin.name());
        jsonPlugin.addProperty("group", plugin.group());
        jsonPlugin.addProperty("version", plugin.version());
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

