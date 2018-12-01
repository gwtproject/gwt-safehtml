package com.google.gwt.safehtml.shared.test;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface SampleTemplatesWithOldName extends SafeHtmlTemplates {
  @Template("<div>{0}</div>")
  SafeHtml render(String thing);
}
