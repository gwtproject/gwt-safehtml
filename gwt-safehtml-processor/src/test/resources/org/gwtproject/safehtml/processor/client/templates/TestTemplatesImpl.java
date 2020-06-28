package org.gwtproject.safehtml.processor.client.templates;
import java.lang.String;
import java.lang.StringBuilder;
import org.gwtproject.safecss.shared.SafeStyles;
import org.gwtproject.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.gwtproject.safehtml.shared.SafeUri;
/**
 * This class is generated from org.gwtproject.safehtml.processor.client.templates.TestTemplatesImpl do not edit manually
 */
public class TestTemplatesImpl implements TestTemplates {
  /**
   * @Template("<span><b>{0}</b><span>{1}</span></span>")
   */
  public SafeHtml simpleTemplate(String arg0, SafeHtml arg1) {
    StringBuilder sb = new StringBuilder();
    sb.append("<span><b>");
    sb.append(SafeHtmlUtils.htmlEscape(arg0));
    sb.append("</b><span>");
    sb.append(arg1.asString());
    sb.append("</span></span>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<span><a href="{0}"><b>{1}</b></a></span>")
   */
  public SafeHtml templateWithUriAttribute(String arg0, SafeHtml arg1) {
    StringBuilder sb = new StringBuilder();
    sb.append("<span><a href=\"");
    sb.append(SafeHtmlUtils.htmlEscape(org.gwtproject.safehtml.shared.UriUtils.sanitizeUri(arg0)));
    sb.append("\"><b>");
    sb.append(arg1.asString());
    sb.append("</b></a></span>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<span><a href="{0}"><b>{1}</b></a></span>")
   */
  public SafeHtml templateWithUriAttribute(SafeUri arg0, SafeHtml arg1) {
    StringBuilder sb = new StringBuilder();
    sb.append("<span><a href=\"");
    sb.append(SafeHtmlUtils.htmlEscape(arg0.asString()));
    sb.append("\"><b>");
    sb.append(arg1.asString());
    sb.append("</b></a></span>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<div id="{0}">{1}</div>")
   */
  public SafeHtml templateWithRegularAttribute(String arg0, SafeHtml arg1) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id=\"");
    sb.append(SafeHtmlUtils.htmlEscape(arg0));
    sb.append("\">");
    sb.append(arg1.asString());
    sb.append("</div>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<div style="{0}">{1}</div>")
   */
  public SafeHtml templateWithSafeStyleAttributeComplete(SafeStyles arg0, SafeHtml arg1) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div style=\"");
    sb.append(SafeHtmlUtils.htmlEscape(arg0.asString()));
    sb.append("\">");
    sb.append(arg1.asString());
    sb.append("</div>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<div style="{0}height:{1}px;">{2}</div>")
   */
  public SafeHtml templateWithSafeStyleAttributeStart(SafeStyles arg0, int arg1, SafeHtml arg2) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div style=\"");
    sb.append(SafeHtmlUtils.htmlEscape(arg0.asString()));
    sb.append("height:");
    sb.append(SafeHtmlUtils.htmlEscape(String.valueOf(arg1)));
    sb.append("px;\">");
    sb.append(arg2.asString());
    sb.append("</div>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<span><img src="{0}/{1}"/></span>")
   */
  public SafeHtml templateWithTwoPartUriAttribute(String arg0, String arg1) {
    StringBuilder sb = new StringBuilder();
    sb.append("<span><img src=\"");
    sb.append(SafeHtmlUtils.htmlEscape(org.gwtproject.safehtml.shared.UriUtils.sanitizeUri(arg0)));
    sb.append("/");
    sb.append(SafeHtmlUtils.htmlEscape(arg1));
    sb.append("\"/></span>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
  /**
   * @Template("<span style='{0}; color: green;'></span>")
   */
  public SafeHtml templateWithStyleAttribute(String arg0) {
    StringBuilder sb = new StringBuilder();
    sb.append("<span style='");
    sb.append(SafeHtmlUtils.htmlEscape(arg0));
    sb.append("; color: green;'></span>");
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
  }
}
