package org.gwtproject.safehtml.processor.client.templates;

import org.gwtproject.safecss.shared.SafeStyles;
import org.gwtproject.safehtml.client.SafeHtmlTemplates;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.SafeUri;

/**
 * A SafeHtmlTemplates interface for testing.
 */
public interface TestTemplates
    extends SafeHtmlTemplates {
  
  @Template("<span><b>{0}</b><span>{1}</span></span>")
  SafeHtml simpleTemplate(String foo,
                          SafeHtml bar);
  
  @Template("<span><a href=\"{0}\"><b>{1}</b></a></span>")
  SafeHtml templateWithUriAttribute(String url,
                                    SafeHtml html);
  
  @Template("<span><a href=\"{0}\"><b>{1}</b></a></span>")
  SafeHtml templateWithUriAttribute(SafeUri url,
                                    SafeHtml html);
  
  @Template("<div id=\"{0}\">{1}</div>")
  SafeHtml templateWithRegularAttribute(String id,
                                        SafeHtml html);
  
  @Template("<div style=\"{0}\">{1}</div>")
  SafeHtml templateWithSafeStyleAttributeComplete(SafeStyles styles,
                                                  SafeHtml html);
  
  @Template("<div style=\"{0}height:{1}px;\">{2}</div>")
  SafeHtml templateWithSafeStyleAttributeStart(SafeStyles styles,
                                               int height /* generates a compile time warning */,
                                               SafeHtml html);
  
  @Template("<span><img src=\"{0}/{1}\"/></span>")
  SafeHtml templateWithTwoPartUriAttribute(String baseUrl,
                                           String urlPart);
  
  @Template("<span style='{0}; color: green;'></span>")
  SafeHtml templateWithStyleAttribute(String style);
  
}
