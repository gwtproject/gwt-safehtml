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
package org.gwtproject.safehtml.client;

import org.gwtproject.safecss.shared.SafeStyles;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.SafeUri;

/** A SafeHtmlTemplates interface for testing. */
public interface SafeHtmlTestTemplates extends SafeHtmlTemplates {

  @Template("<span><b>{0}</b><span>{1}</span></span>")
  SafeHtml simpleTemplate(String foo, SafeHtml bar);

  @Template("<span><a href=\"{0}\"><b>{1}</b></a></span>")
  SafeHtml templateWithUriAttribute(String url, SafeHtml html);

  @Template("<span><a href=\"{0}\"><b>{1}</b></a></span>")
  SafeHtml templateWithUriAttribute(SafeUri url, SafeHtml html);

  @Template("<div id=\"{0}\">{1}</div>")
  SafeHtml templateWithRegularAttribute(String id, SafeHtml html);

  @Template("<div style=\"{0}\">{1}</div>")
  SafeHtml templateWithSafeStyleAttributeComplete(SafeStyles styles, SafeHtml html);

  @Template("<div style=\"{0}height:{1}px;\">{2}</div>")
  SafeHtml templateWithSafeStyleAttributeStart(
      SafeStyles styles, int height /* generates a compile time warning */, SafeHtml html);

  @Template("<span><img src=\"{0}/{1}\"/></span>")
  SafeHtml templateWithTwoPartUriAttribute(String baseUrl, String urlPart);

  @Template("<span style='{0}; color: green;'></span>")
  SafeHtml templateWithStyleAttribute(String style);
}
