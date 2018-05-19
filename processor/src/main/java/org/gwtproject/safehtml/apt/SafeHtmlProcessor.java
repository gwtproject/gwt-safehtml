package org.gwtproject.safehtml.apt;

import com.google.gwt.codegen.server.AbortablePrintWriter;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.SourceWriter;
import org.gwtproject.safehtml.client.SafeHtmlTemplates;
import org.gwtproject.safehtml.shared.SafeHtml;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.ElementScanner7;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("*")
public class SafeHtmlProcessor extends AbstractProcessor {

  private class T {
    DeclaredType jlObject = (DeclaredType) processingEnv.getElementUtils().getTypeElement(Object.class.getCanonicalName()).asType();
    DeclaredType safeHtml = (DeclaredType) processingEnv.getElementUtils().getTypeElement(SafeHtml.class.getCanonicalName()).asType();
    DeclaredType safeHtmlTemplates = (DeclaredType) processingEnv.getElementUtils().getTypeElement(SafeHtmlTemplates.class.getCanonicalName()).asType();

    boolean isSameType(TypeMirror t1, TypeMirror t2) {
      return processingEnv.getTypeUtils().isSameType(t1, t2);
    }
    boolean isAssignable(TypeMirror t1, TypeMirror t2) {
      return processingEnv.getTypeUtils().isAssignable(t1, t2);
    }
  }

  private class TemplatesFinder extends ElementScanner7<Void, Set<TypeElement>> {
    private final T types;

    private TemplatesFinder(T types) {
      this.types = types;
    }

    @Override
    public Void visitType(TypeElement e, Set<TypeElement> templateTypes) {
      if (e.getKind().equals(ElementKind.INTERFACE) &&
              types.isAssignable(e.asType(), types.safeHtmlTemplates)) {
        templateTypes.add(e);
      }
      return super.visitType(e, templateTypes);
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Messager messager = processingEnv.getMessager();
    T types = new T();
    Set<TypeElement> templateTypes = new HashSet<>();

    new TemplatesFinder(types).scan(ElementFilter.typesIn(roundEnv.getRootElements()), templateTypes);

    for (TypeElement templateType : templateTypes) {
      try {
        String packageName = processingEnv.getElementUtils().getPackageOf(templateType).getQualifiedName().toString();
        String className = templateType.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '_') + "Impl";
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + className);


        AbortablePrintWriter writer = new AbortablePrintWriter(new PrintWriter(jfo.openWriter()));
        JavaSourceWriterBuilder writerBuilder = new JavaSourceWriterBuilder(writer, packageName, className);
        writerBuilder.setJavaDocCommentForClass("This class is generated from " + templateType.getQualifiedName().toString() + ", do not edit manually");
        writerBuilder.addImplementedInterface(templateType.getQualifiedName().toString());

        SourceWriter sourceWriter = writerBuilder.createSourceWriter();

        SafeHtmlTemplatesImplMethodCreator methodCreator = new SafeHtmlTemplatesImplMethodCreator(sourceWriter, messager);
        for (Element element : templateType.getEnclosedElements()) {
          if (element instanceof ExecutableElement) {
            ExecutableElement method = (ExecutableElement) element;

            if (types.isSameType(method.getEnclosingElement().asType(), types.jlObject)) {
              continue;
            }

            SafeHtmlTemplates.Template template = method.getAnnotation(SafeHtmlTemplates.Template.class);
            com.google.gwt.safehtml.client.SafeHtmlTemplates.Template templateOld = method.getAnnotation(com.google.gwt.safehtml.client.SafeHtmlTemplates.Template.class);
            if (template == null && templateOld == null) {
              messager.printMessage(Kind.ERROR, "SafeHtmlTemplates method is missing @Template annotation", method);
              continue;
            }
            if (template != null && templateOld != null) {
              messager.printMessage(Kind.ERROR, "Cannot use both old and new template");
              continue;
            }
            String templateString;
            if (templateOld != null) {
              messager.printMessage(Kind.MANDATORY_WARNING, "Using old @Template, please update to new");
              templateString = templateOld.value();
            } else {
              templateString = template.value();
            }

            if (!types.isSameType(method.getReturnType(), types.safeHtml)) {
              messager.printMessage(Kind.ERROR, "SafeHtmlTemplates method must return SafeHtml", method);
              continue;
            }

            sourceWriter.beginJavaDocComment();
            sourceWriter.print("@Template(");
            sourceWriter.print("\"" + escape(templateString) + "\"");
            sourceWriter.print(")");
            sourceWriter.endJavaDocComment();

            printMethodDecl(sourceWriter, method);
            sourceWriter.println(" {");
            sourceWriter.indent();

            methodCreator.createMethodFor(templateString, getParamTypes(method));

            sourceWriter.outdent();
            sourceWriter.println("}");
            sourceWriter.println();

          }
        }
        sourceWriter.close();
      } catch (IOException | UnableToCompleteException e) {
        messager.printMessage(Kind.ERROR, e.getMessage());
        e.printStackTrace();
      }
    }

    return true;
  }

  private String[] getParamTypes(ExecutableElement method) {
    String[] params = new String[method.getParameters().size()];
    int i = 0;
    for (VariableElement variableElement : method.getParameters()) {
      params[i++] = variableElement.asType().toString();
    }
    return params;
  }

  private void printMethodDecl(SourceWriter sourceWriter, ExecutableElement method) {
    sourceWriter.println("public " + method.getReturnType().toString() + " " + method.getSimpleName() + "(");
    sourceWriter.indent();
    sourceWriter.indent();
    sourceWriter.indent();
    boolean first = true;
    List<? extends VariableElement> parameters = method.getParameters();
    for (int i = 0; i < parameters.size(); i++) {
      VariableElement variableElement = parameters.get(i);
      if (first) {
        first = false;
      } else {
        sourceWriter.println(", ");
      }
      sourceWriter.print(variableElement.asType().toString() + " arg" + i);
    }
    sourceWriter.outdent();
    sourceWriter.outdent();
    sourceWriter.outdent();
    sourceWriter.print(")");
  }

  public static String escape(String string) {
    return SafeHtmlTemplatesImplMethodCreator.escape(string);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
