package com.google.gwt.safehtml.apt;

import com.google.common.primitives.Primitives;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.apt.ParsedHtmlTemplate.HtmlContext;
import com.google.gwt.safehtml.apt.ParsedHtmlTemplate.LiteralChunk;
import com.google.gwt.safehtml.apt.ParsedHtmlTemplate.ParameterChunk;
import com.google.gwt.safehtml.apt.ParsedHtmlTemplate.TemplateChunk;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Method body code generator for implementations of
 * {@link com.google.gwt.safehtml.client.SafeHtmlTemplates}.
 */
public class SafeHtmlTemplatesImplMethodCreator {

  /**
   * Fully-qualified class name of the {@link String} class.
   */
  private static final String JAVA_LANG_STRING_FQCN = String.class.getName();

  /**
   * Simple class name of the {@link com.google.gwt.safehtml.shared.SafeUri} interface.
   */
  private static final String SAFE_URI_CN = SafeUri.class.getSimpleName();

  /**
   * Fully-qualified class name of the {@link SafeUri} interface.
   */
  private static final String SAFE_URI_FQCN = SafeUri.class.getName();

  /**
   * Simple class name of the {@link com.google.gwt.safecss.shared.SafeStyles} interface.
   */
  private static final String SAFE_STYLES_CN = SafeStyles.class.getSimpleName();

  /**
   * Fully-qualified class name of the {@link SafeStyles} interface.
   */
  private static final String SAFE_STYLES_FQCN = SafeStyles.class.getName();

  /**
   * Simple class name of the {@link com.google.gwt.safehtml.shared.SafeHtml} interface.
   */
  private static final String SAFE_HTML_CN = SafeHtml.class.getSimpleName();

  /**
   * Fully-qualified class name of the {@link SafeHtml} interface.
   */
  private static final String SAFE_HTML_FQCN = SafeHtml.class.getName();

  /**
   * Fully-qualified class name of the StringBlessedAsSafeHtml class.
   */
  private static final String BLESSED_STRING_FQCN =
          OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml.class.getName();

  /**
   * Fully-qualified class name of the {@link com.google.gwt.safehtml.shared.SafeHtmlUtils} class.
   */
  private static final String SAFE_HTML_UTILS_FQCN = SafeHtmlUtils.class.getName();

  /**
   * Fully-qualified class name of the {@link com.google.gwt.safehtml.shared.UriUtils} class.
   */
  private static final String URI_UTILS_FQCN = UriUtils.class.getName();


  private final SourceWriter writer;
  private final Messager messager;

  public SafeHtmlTemplatesImplMethodCreator(SourceWriter writer, Messager messager) {
    this.writer = writer;
    this.messager = messager;
  }

  public SourceWriter getWriter() {
    return writer;
  }

  public void createMethodFor(Template templateAnnotation, String[] params) throws UnableToCompleteException {
    String template = templateAnnotation.value();
    emitMethodBodyFromTemplate(template, params);
  }

  /**
   * Emits an expression corresponding to a template variable in "attribute"
   * context.
   *
   * <p>The expression emitted applies appropriate escaping and/or sanitization
   * to the parameter's value depending the Java type of the corresponding
   * template method parameter:
   *
   * <ul>
   *   <li>If the parameter is of type {@link SafeStyles}, it is converted to a
   *       string using {@link SafeStyles#asString()}.
   *   <li>Otherwise, if the parameter is of type {@link SafeUri}, it is converted to a
   *       string using {@link SafeUri#asString()}.
   *   <li>Otherwise, if the parameter is not of type {@link String}, it is
   *       first converted to {@link String}.
   *   <li>If the template parameter occurs at the start, or as the entire value,
   *       of a URI-valued attribute within the template, and the parameter isn't
   *       of type {@link SafeUri}, it is sanitized to ensure that it is safe in
   *       this context.  This is done by passing the value through
   *       {@link UriUtils#sanitizeUri(String)}.
   *   <li>The result is then HTML-escaped by passing it through
   *       {@link SafeHtmlUtils#htmlEscape(String)}.
   * </ul>
   *
   * <i>Note</i>: Template method parameters of type {@link SafeHtml} are
   * <i>not</i> treated specially in an attribute context, and will be HTML-
   * escaped like regular strings. This is because {@link SafeHtml} values can
   * contain non-escaped HTML markup, which is not valid within attributes.
   *
   * @param htmlContext the HTML context in which the corresponding template
   *        variable occurs in
   * @param formalParameterName the name of the template method's formal
   *        parameter corresponding to the expression being emitted
   * @param parameterType the Java type of the corresponding template method's
   *        parameter
   */
  private void emitAttributeContextParameterExpression(HtmlContext htmlContext,
                                                       String formalParameterName, String parameterType) {

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
      if (!JAVA_LANG_STRING_FQCN.equals(parameterType)) {
        // The parameter's value must be explicitly converted to String unless it
        // is already of that type.
        expression = "String.valueOf(" + expression + ")";
      }

      if ((htmlContext.getType() == HtmlContext.Type.URL_ATTRIBUTE_START) ||
              (htmlContext.getType() == HtmlContext.Type.URL_ATTRIBUTE_ENTIRE)) {
        expression = URI_UTILS_FQCN + ".sanitizeUri(" + expression + ")";
      }
    }

    // TODO(xtof): Handle EscapedString subtype of SafeHtml, once it's been
    //     introduced.
    expression = SAFE_HTML_UTILS_FQCN + ".htmlEscape(" + expression + ")";

    print(expression);
  }

  /**
   * Generates code that renders the provided HTML template into an instance
   * of the {@link SafeHtml} type.
   *
   * <p>The template is parsed as a HTML template (see
   * {@link com.google.gwt.safehtml.rebind.HtmlTemplateParser}).  From the template's parsed form, code is
   * generated that, when executed, will emit an instantiation of the template.
   * The generated code appropriately escapes and/or sanitizes template
   * parameters such that evaluating the emitted string as HTML in a browser
   * will not result in script execution.
   *
   * <p>As such, strings emitted from generated template methods satisfy the
   * type contract of the {@link SafeHtml} type, and can therefore be returned
   * wrapped as {@link SafeHtml}.
   *
   * @param template the (X)HTML template to generate code for
   * @param params the parameters of the corresponding template method
   * @throws UnableToCompleteException if an error occurred that prevented
   *         code generation for the template
   */
  private void emitMethodBodyFromTemplate(String template, String[] params)
          throws UnableToCompleteException {
    println("StringBuilder sb = new java.lang.StringBuilder();");

    com.google.gwt.safehtml.apt.HtmlTemplateParser parser = new com.google.gwt.safehtml.apt.HtmlTemplateParser();
    parser.parseTemplate(template);

    for (TemplateChunk chunk : parser.getParsedTemplate().getChunks()) {
      if (chunk.getKind() == TemplateChunk.Kind.LITERAL) {
        emitStringLiteral(((LiteralChunk) chunk).getLiteral());
      } else if (chunk.getKind() == TemplateChunk.Kind.PARAMETER) {
        ParameterChunk parameterChunk = (ParameterChunk) chunk;

        int formalParameterIndex = parameterChunk.getParameterIndex();
        if (formalParameterIndex < 0 || formalParameterIndex >= params.length) {
          throw error("Argument " + formalParameterIndex + " beyond range of arguments: "
                  + template);
        }
        String formalParameterName = "arg" + formalParameterIndex;
        String paramType = params[formalParameterIndex];

        emitParameterExpression(parameterChunk.getContext(), formalParameterName, paramType);
      } else {
        throw error("Unexpected chunk kind in parsed template " + template);
      }
    }
    outdent();
    outdent();
    println("return new " + BLESSED_STRING_FQCN + "(sb.toString());");
  }

  /**
   * Emits an expression corresponding to a template parameter.
   *
   * <p>
   * The expression emitted applies appropriate escaping/sanitization to the
   * parameter's value, depending on the parameter's HTML context, and the Java
   * type of the corresponding template method parameter.
   *
   * @param htmlContext the HTML context in which the corresponding template
   *          variable occurs in
   * @param formalParameterName the name of the template method's formal
   *          parameter corresponding to the expression being emitted
   * @param parameterType the Java type of the corresponding template method's
   *          parameter
   * @throws UnableToCompleteException if the parameterType is not valid for the
   *           htmlContext
   */
  private void emitParameterExpression(HtmlContext htmlContext,
                                       String formalParameterName, String parameterType) throws UnableToCompleteException {

    /*
     * Verify that the parameter type is used in the correct context. Safe
     * expressions are only safe in specific contexts.
     */
    HtmlContext.Type contextType = htmlContext.getType();
    if (isSafeHtml(parameterType) && HtmlContext.Type.TEXT != contextType) {
      /*
       * SafeHtml used in a non-text context. SafeHtml is escaped for a text
       * context. In a non-text context, the string is not guaranteed to be
       * safe.
       */
      throw error(SAFE_HTML_CN + " used in a non-text context. Did you mean to use "
              + JAVA_LANG_STRING_FQCN + " or " + SAFE_STYLES_CN + " instead?");
    } else if (isSafeStyles(parameterType) && HtmlContext.Type.CSS_ATTRIBUTE_START != contextType) {
      if (HtmlContext.Type.CSS_ATTRIBUTE == contextType) {
        // SafeStyles can only be used at the start of a CSS attribute.
        throw error(SAFE_STYLES_CN + " cannot be used in the middle of a CSS attribute. "
                + "It must be used at the start a CSS attribute.");
      } else {
        /*
         * SafeStyles used in a non-css attribute context. SafeStyles is only
         * safe in a CSS attribute context. We could treat it as a normal
         * parameter and escape the string value of the parameter, but it almost
         * definitely isn't what the developer intended to do.
         */
        throw error(SAFE_STYLES_CN
                + " used in a non-CSS attribute context. Did you mean to use " + JAVA_LANG_STRING_FQCN
                + " or " + SAFE_HTML_CN + " instead?");
      }
    } else if (isSafeUri(parameterType) && HtmlContext.Type.URL_ATTRIBUTE_ENTIRE != contextType) {
      // TODO(xtof): refactor HtmlContext with isStart/isEnd/isEntire accessors and simplified type.
      if (HtmlContext.Type.URL_ATTRIBUTE_START == contextType) {
        // SafeUri can only be used as the entire value of an URL attribute.
        throw error(SAFE_URI_CN + " cannot be used in a URL attribute if it isn't the "
                + "entire attribute value.");
      } else {
        /*
         * SafeUri outside a URL-attribute context (or in a URL-attribute, but
         * not at start). SafeUri is only safe if it comprises the entire URL
         * attribute's value. We could treat it as a normal parameter and escape
         * the string value of the parameter, but it almost definitely isn't
         * what the developer intended to do.
         */
        throw error(SAFE_URI_CN + " can only be used as the entire value of a URL "
                + "attribute. Did you mean to use " + JAVA_LANG_STRING_FQCN + " or " + SAFE_HTML_CN
                + " instead?");
      }
    }

    print("sb.append(");
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
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Template with variable in CSS context: "
                + "The template code generator cannot guarantee HTML-safety of "
                + "the template -- please inspect manually");
        emitTextContextParameterExpression(formalParameterName, parameterType);
        break;
      case TEXT:
        emitTextContextParameterExpression(formalParameterName, parameterType);
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
          messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                  "Template with variable in CSS attribute context: The template code generator cannot"
                          + " guarantee HTML-safety of the template -- please inspect manually or use "
                          + SAFE_STYLES_CN + " to specify arguments in a CSS attribute context");
        }
        emitAttributeContextParameterExpression(htmlContext, formalParameterName,
                parameterType);
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
          messager.printMessage(Diagnostic.Kind.WARNING,
                  "Template with variable in URL attribute context: The template code generator will"
                          + " sanitize the URL.  Use " + SAFE_URI_CN
                          + " to specify arguments in a URL attribute context that should not be"
                          + " sanitized.");
        }
        emitAttributeContextParameterExpression(htmlContext, formalParameterName,
                parameterType);
        break;
      case ATTRIBUTE_VALUE:
        emitAttributeContextParameterExpression(htmlContext, formalParameterName,
                parameterType);
        break;

      default:
        throw error("unknown HTML context for formal template parameter "
                + formalParameterName + ": " + htmlContext);
    }
    println(");");
  }

  /**
   * Emits a string literal.
   *
   * @param str the {@link String} to emit as a literal
   */
  private void emitStringLiteral(String str) {
    print("sb.append(");
    print(wrap(str));
    println(");");
  }

  /**
   * Emits an expression corresponding to a template variable in "inner text"
   * context.
   *
   * <p>The expression emitted applies appropriate escaping to the parameter's
   * value depending the Java type of the corresponding template method
   * parameter:
   *
   * <ul>
   *   <li>If the parameter is of a primitive (e.g., numeric, boolean) type, or
   *       of type {@link SafeHtml}, it is emitted as is, without escaping.
   *   <li>Otherwise, an expression that passes the parameter's value through
   *       {@link SafeHtmlUtils#htmlEscape(String)} is emitted. If the value is
   *       of type {@link SafeUri}, it is converted to string using
   *       {@link SafeUri#asString()}.
   * </ul>
   *
   * @param formalParameterName the name of the template method's formal
   *        parameter corresponding to the expression being emitted
   * @param parameterType the Java type of the corresponding template method's
   *        parameter
   */
  private void emitTextContextParameterExpression(String formalParameterName, String parameterType) {
    boolean parameterIsPrimitiveType = isPrimitive(parameterType);
    boolean parameterIsNotStringTyped = !(JAVA_LANG_STRING_FQCN.equals(parameterType));

    if (isSafeHtml(parameterType)) {
      // The parameter is of type SafeHtml and its wrapped string can
      // therefore be emitted safely without escaping.
      print(formalParameterName + ".asString()");
    } else if (parameterIsPrimitiveType) {
      // The string representations of primitive types never contain HTML
      // special characters and can therefore be emitted without escaping.
      print(formalParameterName);
    } else {
      // The parameter is of some other type, and its value must be HTML
      // escaped. Furthermore, unless the parameter's type is {@link String},
      // it must be explicitly converted to {@link String}.
      String expression = formalParameterName;
      if (parameterIsNotStringTyped) {
        expression = "String.valueOf(" + expression + ")";
      }
      print(SAFE_HTML_UTILS_FQCN + ".htmlEscape(" + expression + ")");
    }
  }

  private boolean isPrimitive(String parameterType) {
    try {
      return Primitives.isWrapperType(Class.forName(parameterType));
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  /**
   * Check if the specified parameter type represents a {@link SafeHtml}.
   *
   * @param parameterType the Java parameter type
   * @return true if the type represents a {@link SafeHtml}
   */
  private boolean isSafeHtml(String parameterType) {
    return parameterType.equals(SAFE_HTML_FQCN);
  }

  /**
   * Check if the specified parameter type represents a {@link SafeStyles}.
   *
   * @param parameterType the Java parameter type
   * @return true if the type represents a {@link SafeStyles}
   */
  private boolean isSafeStyles(String parameterType) {
    return parameterType.equals(SAFE_STYLES_FQCN);
  }

  /**
   * Check if the specified parameter type represents a {@link SafeUri}.
   *
   * @param parameterType the Java parameter type
   * @return true if the type represents a {@link SafeUri}
   */
  private boolean isSafeUri(String parameterType) {
    return parameterType.equals(SAFE_URI_FQCN);
  }
  
  
  
  
  //TODO move these to superclass

  /**
   * Helper method used to wrap a string constant with quotes. Must use to
   * enable string escaping.
   *
   * @param wrapMe String to wrap
   * @return wrapped String
   */
  protected static String wrap(String wrapMe) {
    return "\"" + escape(wrapMe) + "\"";
  }

  /**
   * Escapes string content to be a valid string literal.
   *
   * @param unescaped
   * @return an escaped version of <code>unescaped</code>, suitable for being
   *         enclosed in double quotes in Java source
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

  /**
   * Convenience method to use TreeLogger error pattern.
   * @param msg msg
   * @return the exception to throw
   */
  protected UnableToCompleteException error(String msg) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg);
    return new UnableToCompleteException();
  }

  /**
   * Convenience method to use TreeLogger error pattern.
   * @param msg msg
   * @return the exception to throw
   */
  protected UnableToCompleteException error(String msg, Throwable cause) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg + ": " + cause.getMessage());
    return new UnableToCompleteException();
  }

  /**
   * Convenience method to use TreeLogger error pattern.
   * @param e throwable
   * @return th exception to throw
   */
  protected UnableToCompleteException error(Throwable e) {
    messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage() + ": " + e.getMessage());
    return new UnableToCompleteException();
  }





  /**
   * Prints to the current <code>AbstractGeneratorClassCreator</code>.
   *
   * @param printMe <code>Object</code> to print
   */
  public void println(Object printMe) {
    getWriter().println(printMe.toString());
  }

  /**
   * Indent subsequent lines.
   */
  protected void indent() {
    getWriter().indent();
  }

  /**
   * Outdent subsequent lines.
   */
  protected void outdent() {
    getWriter().outdent();
  }

  /**
   * Prints to the current <code>AbstractGeneratorClassCreator</code>.
   *
   * @param printMe
   */
  protected void print(String printMe) {
    getWriter().print(printMe);
  }
}
