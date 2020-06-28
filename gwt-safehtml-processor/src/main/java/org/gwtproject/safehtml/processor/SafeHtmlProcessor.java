/*
 * Copyright © 2018 The GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.safehtml.processor;

import static java.util.stream.Collectors.toSet;

import com.google.auto.service.AutoService;
import com.google.common.base.Stopwatch;
import com.google.common.primitives.Primitives;
import com.squareup.javapoet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import org.gwtproject.safecss.shared.SafeStyles;
import org.gwtproject.safehtml.client.SafeHtmlTemplates;
import org.gwtproject.safehtml.shared.*;

@AutoService(Processor.class)
public class SafeHtmlProcessor extends AbstractProcessor {

  private Messager messager;
  private Filer filer;
  private Types types;
  private Elements elements;
  private Stopwatch stopwatch;

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(SafeHtmlTemplates.Template.class.getCanonicalName()).collect(toSet());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.messager = processingEnv.getMessager();
    this.filer = processingEnv.getFiler();
    this.types = processingEnv.getTypeUtils();
    this.elements = processingEnv.getElementUtils();

    this.createMessage(Kind.NOTE, "GWT-SafeHTML-Processor (version: HEAD-SNAPSHOT) started ...");

    this.stopwatch = Stopwatch.createStarted();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      this.createMessage(
          Kind.NOTE,
          "GWT-editor-Processor finished ... processing takes: "
              + this.stopwatch.stop().toString());
    } else {
      if (annotations.size() > 0) {
        T types = new T();
        Set<TypeElement> templateTypes =
            annotations.stream()
                .filter(
                    annotation ->
                        SafeHtmlTemplates.Template.class
                            .getCanonicalName()
                            .equals(annotation.toString()))
                .flatMap(
                    annotation ->
                        roundEnv.getElementsAnnotatedWith(SafeHtmlTemplates.Template.class)
                            .stream())
                .map(element -> (TypeElement) element.getEnclosingElement())
                .collect(toSet());
        for (TypeElement templateType : templateTypes) {
          String packageName = elements.getPackageOf(templateType).getQualifiedName().toString();
          String className =
              templateType
                      .getQualifiedName()
                      .toString()
                      .substring(packageName.length() + 1)
                      .replace('.', '_')
                  + "Impl";

          TypeSpec.Builder templateTypeSpec =
              TypeSpec.classBuilder(className)
                  .addOriginatingElement(templateType)
                  .addModifiers(Modifier.PUBLIC)
                  .addSuperinterface(ClassName.get(templateType))
                  .addJavadoc(
                      "This class is generated from "
                          + packageName
                          + "."
                          + className
                          + " do not edit manually");

          for (Element element : templateType.getEnclosedElements()) {
            if (element instanceof ExecutableElement) {
              ExecutableElement method = (ExecutableElement) element;
              if (method.isDefault()) {
                continue;
              }
              if (types.isSameType(method.getEnclosingElement().asType(), types.jlObject)) {
                continue;
              }
              AnnotationMirror template =
                  getAnnotationWithName(
                      method, SafeHtmlTemplates.Template.class.getCanonicalName());
              if (template == null) {
                this.createMessage(
                    Kind.ERROR, "SafeHtmlTemplates method is missing @Template annotation", method);
                continue;
              }
              if (!types.isSameType(
                  method.getReturnType(),
                  elements.getTypeElement(SafeHtml.class.getCanonicalName()).asType())) {
                this.createMessage(
                    Kind.ERROR, "SafeHtmlTemplates method must return SafeHtml", method);
                continue;
              }
              try {
                templateTypeSpec.addMethod(this.generateMethod(templateType, template, method));
              } catch (SafeHtmlProcessorException e) {
                this.createMessage(
                    Kind.ERROR,
                    "Exception: type >>"
                        + method.toString()
                        + " << - trying to write: >>"
                        + packageName
                        + "."
                        + className
                        + "<< -> message >>"
                        + e.getMessage()
                        + "<< ==> generation failed");
              }
            }
          }
          JavaFile templateFile = JavaFile.builder(packageName, templateTypeSpec.build()).build();
          try {
            templateFile.writeTo(this.filer);
          } catch (IOException e) {
            this.createMessage(
                Diagnostic.Kind.WARNING,
                "Exception: trying to write: >>"
                    + packageName
                    + "."
                    + className
                    + "<< -> message >>"
                    + e.getMessage()
                    + "<< multiple times");
          }
        }
      }
    }
    return true;
  }

  private AnnotationMirror getAnnotationWithName(ExecutableElement elt, String name) {
    return elt.getAnnotationMirrors().stream()
        .filter(a -> name.equals(ClassName.get(a.getAnnotationType()).toString()))
        .findAny()
        .orElse(null);
  }

  private MethodSpec generateMethod(
      TypeElement templateType, AnnotationMirror template, ExecutableElement method)
      throws SafeHtmlProcessorException {
    final String templateString = getTemplateString(template);
    MethodSpec.Builder templateMethod =
        MethodSpec.methodBuilder(method.getSimpleName().toString())
            .returns(ClassName.get(SafeHtml.class))
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("@Template(\"" + templateString + "\")")
            .addStatement(
                "$T sb = new $T()",
                ClassName.get(StringBuilder.class),
                ClassName.get(StringBuilder.class));
    String[] parameterTypes = addParameters(method, templateMethod);
    HtmlTemplateParser parser = new HtmlTemplateParser();
    parser.parseTemplate(templateString);
    for (ParsedHtmlTemplate.TemplateChunk chunk : parser.getParsedTemplate().getChunks()) {
      if (chunk.getKind() == ParsedHtmlTemplate.TemplateChunk.Kind.LITERAL) {
        emitStringLiteral(templateMethod, ((ParsedHtmlTemplate.LiteralChunk) chunk).getLiteral());
      } else if (chunk.getKind() == ParsedHtmlTemplate.TemplateChunk.Kind.PARAMETER) {
        ParsedHtmlTemplate.ParameterChunk parameterChunk =
            (ParsedHtmlTemplate.ParameterChunk) chunk;
        int formalParameterIndex = parameterChunk.getParameterIndex();
        if (formalParameterIndex < 0 || formalParameterIndex >= parameterTypes.length) {
          throw error(
              "Argument " + formalParameterIndex + " beyond range of arguments: " + template,
              method);
        }
        String formalParameterName = "arg" + formalParameterIndex;
        String parameterType = parameterTypes[formalParameterIndex];
        emitParameterExpression(
            templateMethod,
            parameterChunk.getContext(),
            formalParameterName,
            parameterType,
            method);
      } else {
        throw error("Unexpected chunk kind in parsed template " + template, method);
      }
    }
    templateMethod.addStatement(
        "return new $T(sb.toString())",
        ClassName.get(OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml.class));
    return templateMethod.build();
  }

  private String getTemplateString(AnnotationMirror template) {
    return template.getElementValues().values().iterator().next().getValue().toString();
  }

  private String[] addParameters(ExecutableElement method, MethodSpec.Builder templateMethod) {
    String[] parameterTypes = new String[method.getParameters().size()];
    for (int i = 0; i < method.getParameters().size(); i++) {
      VariableElement variableElement = method.getParameters().get(i);

      if (isPrimitive(variableElement.asType().toString())) {
        templateMethod.addParameter(
            ParameterSpec.builder(ClassName.get(variableElement.asType()), "arg" + i).build());
      } else {
        templateMethod.addParameter(
            ParameterSpec.builder(
                    ClassName.get(elements.getTypeElement(variableElement.asType().toString())),
                    "arg" + i)
                .build());
      }
      parameterTypes[i] = variableElement.asType().toString();
    }
    return parameterTypes;
  }

  /**
   * Emits a string literal.
   *
   * @param templateMethod method builder for adding statement
   * @param literal the {@link String} to emit as a literal
   */
  private void emitStringLiteral(MethodSpec.Builder templateMethod, String literal) {
    templateMethod.addStatement("sb.append($L)", wrap(literal));
  }

  /**
   * Convenience method to use TreeLogger error pattern.
   *
   * @param msg message
   * @param element element causing error
   * @return the exception to throw
   */
  private SafeHtmlProcessorException error(String msg, Element element) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
    return new SafeHtmlProcessorException();
  }

  /**
   * Emits an expression corresponding to a template parameter.
   *
   * <p>The expression emitted applies appropriate escaping/sanitization to the parameter's value,
   * depending on the parameter's HTML context, and the Java type of the corresponding template
   * method parameter.
   *
   * @param templateMethod method builder for adding statement
   * @param context the HTML context in which the corresponding template variable occurs in
   * @param formalParameterName the name of the template method's formal parameter corresponding to
   *     the expression being emitted
   * @param parameterType the Java type of the corresponding template method's parameter
   * @param method executatble element currently processed
   * @throws SafeHtmlProcessorException if the parameterType is not valid for the htmlContext
   */
  private void emitParameterExpression(
      MethodSpec.Builder templateMethod,
      ParsedHtmlTemplate.HtmlContext context,
      String formalParameterName,
      String parameterType,
      ExecutableElement method)
      throws SafeHtmlProcessorException {
    /*
     * Verify that the parameter type is used in the correct context. Safe
     * expressions are only safe in specific contexts.
     */
    ParsedHtmlTemplate.HtmlContext.Type contextType = context.getType();
    if (isSafeHtml(parameterType) && ParsedHtmlTemplate.HtmlContext.Type.TEXT != contextType) {
      /*
       * SafeHtml used in a non-text context. SafeHtml is escaped for a text
       * context. In a non-text context, the string is not guaranteed to be
       * safe.
       */
      throw error(
          SafeHtml.class.getCanonicalName()
              + " used in a non-text context. Did you mean to use "
              + String.class.getName()
              + " or "
              + SafeStyles.class.getCanonicalName()
              + " instead?",
          method);
    } else if (isSafeStyles(parameterType)
        && ParsedHtmlTemplate.HtmlContext.Type.CSS_ATTRIBUTE_START != contextType) {
      if (ParsedHtmlTemplate.HtmlContext.Type.CSS_ATTRIBUTE == contextType) {
        // SafeStyles can only be used at the start of a CSS attribute.
        throw error(
            SafeStyles.class.getCanonicalName()
                + " cannot be used in the middle of a CSS attribute. "
                + "It must be used at the start a CSS attribute.",
            method);
      } else {
        /*
         * SafeStyles used in a non-css attribute context. SafeStyles is only
         * safe in a CSS attribute context. We could treat it as a normal
         * parameter and escape the string value of the parameter, but it almost
         * definitely isn't what the developer intended to do.
         */
        throw error(
            SafeStyles.class.getCanonicalName()
                + " used in a non-CSS attribute context. Did you mean to use "
                + String.class.getName()
                + " or "
                + SafeHtml.class.getCanonicalName()
                + " instead?",
            method);
      }
    } else if (isSafeUri(parameterType)
        && ParsedHtmlTemplate.HtmlContext.Type.URL_ATTRIBUTE_ENTIRE != contextType) {
      // TODO(xtof): refactor HtmlContext with isStart/isEnd/isEntire accessors and simplified type.
      if (ParsedHtmlTemplate.HtmlContext.Type.URL_ATTRIBUTE_START == contextType) {
        // SafeUri can only be used as the entire value of an URL attribute.
        throw error(
            SafeUri.class.getCanonicalName()
                + " cannot be used in a URL attribute if it isn't the "
                + "entire attribute value.",
            method);
      } else {
        /*
         * SafeUri outside a URL-attribute context (or in a URL-attribute, but
         * not at start). SafeUri is only safe if it comprises the entire URL
         * attribute's value. We could treat it as a normal parameter and escape
         * the string value of the parameter, but it almost definitely isn't
         * what the developer intended to do.
         */
        throw error(
            SafeUri.class.getCanonicalName()
                + " can only be used as the entire value of a URL "
                + "attribute. Did you mean to use "
                + String.class.getName()
                + " or "
                + SafeHtml.class.getCanonicalName()
                + " instead?",
            method);
      }
    }

    switch (contextType) {
      case CSS:
        /*
         * TODO(jlabanca): Handle CSS in a text context.
         *
         * The stream parser does not parse CSS; we could however improve safety
         * via sub-formats that specify the in-css context.
         *
         * SafeStyles is safe in a CSS context when used inside of a CSS style
         * rule, but they are not always safe. We could implement SafeCssRules,
         * which would consist of SafeStyles inside of a CSS style rules found
         * in a style tag.
         */
        messager.printMessage(
            Diagnostic.Kind.MANDATORY_WARNING,
            "Template with variable in CSS context: "
                + "The template code generator cannot guarantee HTML-safety of "
                + "the template -- please inspect manually",
            method);
        emitTextContextParameterExpression(templateMethod, formalParameterName, parameterType);
        break;
      case TEXT:
        emitTextContextParameterExpression(templateMethod, formalParameterName, parameterType);
        break;

      case CSS_ATTRIBUTE:
      case CSS_ATTRIBUTE_START:
        /*
         * We already checked if the user tried to use SafeStyles in an invalid
         * (non-CSS_ATTRIBUTE) context, but now we check if the user could have
         * used SafeStyles in the current context.
         */
        if (!isSafeStyles(parameterType)) {
          // WARNING against using unsafe parameters in a CSS attribute context.
          messager.printMessage(
              Diagnostic.Kind.MANDATORY_WARNING,
              "Template with variable in CSS attribute context: The template code generator cannot"
                  + " guarantee HTML-safety of the template -- please inspect manually or use "
                  + SafeStyles.class.getCanonicalName()
                  + " to specify arguments in a CSS attribute context",
              method);
        }
        emitAttributeContextParameterExpression(
            templateMethod, context, formalParameterName, parameterType);
        break;
      case URL_ATTRIBUTE_START:
      case URL_ATTRIBUTE_ENTIRE:
        /*
         * We already checked if the user tried to use SafeUri in an invalid
         * (non-URL_ATTRIBUTE) context, but now we check if the user could have
         * used SafeUri in the current context.
         */
        if (!isSafeUri(parameterType)) {
          // WARNING against using unsafe parameters in a URL attribute context.
          messager.printMessage(
              Diagnostic.Kind.MANDATORY_WARNING,
              "Template with variable in URL attribute context: The template code generator will"
                  + " sanitize the URL.  Use "
                  + SafeUri.class.getCanonicalName()
                  + " to specify arguments in a URL attribute context that should not be"
                  + " sanitized.",
              method);
        }
        emitAttributeContextParameterExpression(
            templateMethod, context, formalParameterName, parameterType);
        break;
      case ATTRIBUTE_VALUE:
        emitAttributeContextParameterExpression(
            templateMethod, context, formalParameterName, parameterType);
        break;

      default:
        throw error(
            "unknown HTML context for formal template parameter "
                + formalParameterName
                + ": "
                + context,
            method);
    }
  }

  private boolean isPrimitive(String parameterType) {
    if (Primitives.allPrimitiveTypes().stream()
        .map(Class::getCanonicalName)
        .anyMatch(typeName -> Objects.equals(typeName, parameterType))) {
      return true;
    }
    try {
      return Primitives.isWrapperType(Class.forName(parameterType));
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  /**
   * Helper method used to wrap a string constant with quotes. Must use to enable string escaping.
   *
   * @param wrapMe String to wrap
   * @return wrapped String
   */
  protected static String wrap(String wrapMe) {
    return "\"" + escape(wrapMe) + "\"";
  }

  /**
   * Check if the specified parameter type represents a {@link SafeHtml}.
   *
   * @param parameterType the Java parameter type
   * @return true if the type represents a {@link SafeHtml}
   */
  private boolean isSafeHtml(String parameterType) {
    return parameterType.equals(SafeHtml.class.getCanonicalName());
  }

  /**
   * Check if the specified parameter type represents a {@link SafeStyles}.
   *
   * @param parameterType the Java parameter type
   * @return true if the type represents a {@link SafeStyles}
   */
  private boolean isSafeStyles(String parameterType) {
    return parameterType.equals(SafeStyles.class.getCanonicalName());
  }

  /**
   * Check if the specified parameter type represents a {@link SafeUri}.
   *
   * @param parameterType the Java parameter type
   * @return true if the type represents a {@link SafeUri}
   */
  private boolean isSafeUri(String parameterType) {
    return parameterType.equals(SafeUri.class.getCanonicalName());
  }

  /**
   * Emits an expression corresponding to a template variable in "inner text" context.
   *
   * <p>The expression emitted applies appropriate escaping to the parameter's value depending the
   * Java type of the corresponding template method parameter:
   *
   * <ul>
   *   <li>If the parameter is of a primitive (e.g., numeric, boolean) type, or of type {@link
   *       SafeHtml}, it is emitted as is, without escaping.
   *   <li>Otherwise, an expression that passes the parameter's value through {@link
   *       SafeHtmlUtils#htmlEscape(String)} is emitted. If the value is of type {@link SafeUri}, it
   *       is converted to string using {@link SafeUri#asString()}.
   * </ul>
   *
   * @param templateMethod method to be build
   * @param formalParameterName the name of the template method's formal parameter corresponding to
   *     the expression being emitted
   * @param parameterType the Java type of the corresponding template method's
   */
  private void emitTextContextParameterExpression(
      MethodSpec.Builder templateMethod, String formalParameterName, String parameterType) {
    boolean parameterIsPrimitiveType = isPrimitive(parameterType);
    boolean parameterIsNotStringTyped = !(String.class.getCanonicalName().equals(parameterType));

    if (isSafeHtml(parameterType)) {
      // The parameter is of type SafeHtml and its wrapped string can
      // therefore be emitted safely without escaping.
      templateMethod.addStatement("sb.append($L.asString())", formalParameterName);
    } else if (parameterIsPrimitiveType) {
      // The string representations of primitive types never contain HTML
      // special characters and can therefore be emitted without escaping.
      templateMethod.addStatement("sb.append($L)", formalParameterName);
    } else {
      // The parameter is of some other type, and its value must be HTML
      // escaped. Furthermore, unless the parameter's type is {@link String},
      // it must be explicitly converted to {@link String}.
      String expression = formalParameterName;
      if (parameterIsNotStringTyped) {
        expression = "String.valueOf(" + expression + ")";
      }
      templateMethod.addStatement(
          "sb.append($T.htmlEscape($L))", ClassName.get(SafeHtmlUtils.class), expression);
    }
  }

  /**
   * Emits an expression corresponding to a template variable in "attribute" context.
   *
   * <p>The expression emitted applies appropriate escaping and/or sanitization to the parameter's
   * value depending the Java type of the corresponding template method parameter:
   *
   * <ul>
   *   <li>If the parameter is of type {@link SafeStyles}, it is converted to a string using {@link
   *       SafeStyles#asString()}.
   *   <li>Otherwise, if the parameter is of type {@link SafeUri}, it is converted to a string using
   *       {@link SafeUri#asString()}.
   *   <li>Otherwise, if the parameter is not of type {@link String}, it is first converted to
   *       {@link String}.
   *   <li>If the template parameter occurs at the start, or as the entire value, of a URI-valued
   *       attribute within the template, and the parameter isn't of type {@link SafeUri}, it is
   *       sanitized to ensure that it is safe in this context. This is done by passing the value
   *       through {@link UriUtils#sanitizeUri(String)}.
   *   <li>The result is then HTML-escaped by passing it through {@link
   *       SafeHtmlUtils#htmlEscape(String)}.
   * </ul>
   *
   * <i>Note</i>: Template method parameters of type {@link SafeHtml} are <i>not</i> treated
   * specially in an attribute context, and will be HTML- escaped like regular strings. This is
   * because {@link SafeHtml} values can contain non-escaped HTML markup, which is not valid within
   * attributes.
   *
   * @param templateMethod method to be build
   * @param htmlContext the HTML context in which the corresponding template variable occurs in
   * @param formalParameterName the name of the template method's formal parameter corresponding to
   *     the expression being emitted
   * @param parameterType the Java type of the corresponding template method's
   */
  private void emitAttributeContextParameterExpression(
      MethodSpec.Builder templateMethod,
      ParsedHtmlTemplate.HtmlContext htmlContext,
      String formalParameterName,
      String parameterType) {
    /*
     * Build up the expression from the "inside out", i.e. start with the formal
     * parameter, convert to string if necessary, then wrap in validators if
     * necessary, and finally HTML-escape if necessary.
     */
    String expression = formalParameterName;

    if (isSafeUri(parameterType) || isSafeStyles(parameterType)) {
      // SafeUri is safe in a URL context, and SafeStyles is safe in a CSS context,
      // so we can use their string (but we still escape it).
      expression = expression + ".asString()";
    } else {
      if (!String.class.getCanonicalName().equals(parameterType)) {
        // The parameter's value must be explicitly converted to String unless it
        // is already of that type.
        expression = "String.valueOf(" + expression + ")";
      }

      if ((htmlContext.getType() == ParsedHtmlTemplate.HtmlContext.Type.URL_ATTRIBUTE_START)
          || (htmlContext.getType() == ParsedHtmlTemplate.HtmlContext.Type.URL_ATTRIBUTE_ENTIRE)) {
        expression = UriUtils.class.getCanonicalName() + ".sanitizeUri(" + expression + ")";
      }
    }

    // TODO(xtof): Handle EscapedString subtype of SafeHtml, once it's been
    //     introduced.
    templateMethod.addStatement(
        "sb.append($T.htmlEscape($L))", ClassName.get(SafeHtmlUtils.class), expression);
  }

  /**
   * Escapes string content to be a valid string literal.
   *
   * @param unescaped
   * @return an escaped version of <code>unescaped</code>, suitable for being enclosed in double
   *     quotes in Java source
   */
  public static String escape(String unescaped) {
    int extra = 0;
    for (int in = 0, n = unescaped.length(); in < n; ++in) {
      switch (unescaped.charAt(in)) {
        case '\0':
        case '\n':
        case '\r':
        case '\"':
        case '\\':
          ++extra;
          break;
      }
    }

    if (extra == 0) {
      return unescaped;
    }

    char[] oldChars = unescaped.toCharArray();
    char[] newChars = new char[oldChars.length + extra];
    for (int in = 0, out = 0, n = oldChars.length; in < n; ++in, ++out) {
      char c = oldChars[in];
      switch (c) {
        case '\0':
          newChars[out++] = '\\';
          c = '0';
          break;
        case '\n':
          newChars[out++] = '\\';
          c = 'n';
          break;
        case '\r':
          newChars[out++] = '\\';
          c = 'r';
          break;
        case '\"':
          newChars[out++] = '\\';
          c = '"';
          break;
        case '\\':
          newChars[out++] = '\\';
          c = '\\';
          break;
      }
      newChars[out] = c;
    }

    return String.valueOf(newChars);
  }

  private void createMessage(Kind kind, String message) {
    this.createMessage(kind, message, null);
  }

  private void createMessage(Kind kind, String message, ExecutableElement method) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    String messageValue = message;
    if (method != null) {
      message = message + " --> " + method.toString();
    }
    pw.println(message);
    pw.close();
    this.messager.printMessage(kind, sw.toString());
  }

  private String[] getParamTypes(ExecutableElement method) {
    String[] params = new String[method.getParameters().size()];
    int i = 0;
    for (VariableElement variableElement : method.getParameters()) {
      params[i++] = variableElement.asType().toString();
    }
    return params;
  }

  /**
   * Convenience method to use TreeLogger error pattern.
   *
   * @param msg msg
   * @param element element causing error
   * @return the exception to throw
   */
  private SafeHtmlProcessorException error(String msg, Throwable cause, Element element) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg + ": " + cause.getMessage(), element);
    return new SafeHtmlProcessorException();
  }

  /**
   * Convenience method to use TreeLogger error pattern.
   *
   * @param e throwable
   * @param element element causing error
   * @return th exception to throw
   */
  private SafeHtmlProcessorException error(Throwable e, Element element) {
    messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage() + ": " + e.getMessage(), element);
    return new SafeHtmlProcessorException();
  }

  private class T {

    DeclaredType jlObject =
        (DeclaredType)
            processingEnv
                .getElementUtils()
                .getTypeElement(Object.class.getCanonicalName())
                .asType();

    boolean isSameType(TypeMirror t1, TypeMirror t2) {
      return processingEnv.getTypeUtils().isSameType(t1, t2);
    }
  }
}
