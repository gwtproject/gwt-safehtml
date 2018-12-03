package org.gwtproject.safehtml.apt;

public enum SafeApiPackage {
  COM_GOOGLE_GWT_SAFEHTML("com.google.gwt.safehtml", "com.google.gwt.safecss"),
  ORG_GWTPROJECT_SAFEHTML("org.gwtproject.safehtml", "org.gwtproject.safecss");

  private final String safeHtmlPackageName;
  private final String safeCssPackageName;

  SafeApiPackage(String safeHtmlPackageName, String safeCssPackageName) {
    this.safeHtmlPackageName = safeHtmlPackageName;
    this.safeCssPackageName = safeCssPackageName;
  }

  public String getSafeHtmlTemplatesInterfaceFQN() {
    return safeHtmlPackageName + ".client.SafeHtmlTemplates";
  }

  public String getSafeHtmlTemplateAnnotationFQN() {
    return getSafeHtmlTemplatesInterfaceFQN() + ".Template";
  }

  public String getSafeHtmlInterfaceFQN() {
    return safeHtmlPackageName + ".shared.SafeHtml";
  }

  public String getSafeStylesInterfaceFQN() {
    return safeCssPackageName + ".shared.SafeStyles";
  }

  public String getSafeUriInterfaceFQN() {
    return safeHtmlPackageName + ".shared.SafeUri";
  }

  public String getBlessedStringFQN() {
    return safeHtmlPackageName + ".shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml";
  }

  public String getSafeHtmlUtilsFQN() {
    return safeHtmlPackageName + ".shared.SafeHtmlUtils";
  }

  public String getUriUtilsFqn() {
    return safeHtmlPackageName + ".shared.UriUtils";
  }
}
