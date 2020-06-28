package org.gwtproject.safehtml.processor.client.empty.annotation;

import org.gwtproject.safehtml.client.SafeHtmlTemplates;
import org.gwtproject.safehtml.shared.SafeHtml;

/**
 * A SafeHtmlTemplates interface for testing.
 */
public interface TestEmptyAnnotationTemplates
    extends SafeHtmlTemplates {
  
  @Template()
  SafeHtml simpleTemplate(String foo,
                          SafeHtml bar);
  
}
