package com.github.distiya.swaggercustomizer.processor;

import com.github.distiya.swaggercustomizer.annotation.SwaggerApiModel;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class SwaggerModelPropertyOrderProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(SwaggerApiModel.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(SwaggerApiModel.class)) {
            TypeElement typeElement = (TypeElement) annotatedElement;
            List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
            annotationMirrors.stream().forEach(m->{
                Set<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> entries = ((AnnotationMirror) m).getElementValues().entrySet();

                Object b = entries.stream().filter(e -> e.getKey().getSimpleName().toString().equals("classOrder")).map(e -> e.getValue().getValue())
                        .filter(o -> o instanceof List)
                        .findFirst();

                Class<?> mainCLass = entries.stream().filter(e -> e.getKey().getSimpleName().toString().equals("mainCLass")).map(e -> e.getValue().getValue())
                        .map(o->(Class<?>)o)
                        .findFirst()
                        .orElse(null);
                /*try {
                    if(b != null && b.size() > 0 && mainCLass != null && mainCLass != Object.class)
                        generateApiModel(b,mainCLass);
                } catch (IOException e) {
                }*/
            });
        }
        return false;
    }

    private void generateApiModel(List<? extends Class<?>> propertyClassList,Class<?> selfClass) throws IOException {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(ApiModelProperty.class).addMember("name","\"s\"").addMember("position","1").build();
        FieldSpec name = FieldSpec
                .builder(String.class, "name")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(annotationSpec)
                .build();
        TypeSpec person = TypeSpec
                .classBuilder("Person")
                .addModifiers(Modifier.PUBLIC)
                .addField(name)
                .build();
        JavaFile javaFile = JavaFile.builder("com.dbs.cmcp", person)
                .build();
        javaFile.writeTo(filer);
    }
}
