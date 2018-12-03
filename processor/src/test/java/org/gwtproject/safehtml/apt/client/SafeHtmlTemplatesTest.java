/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gwtproject.safehtml.apt.client;

import junit.framework.TestCase;
import org.gwtproject.safecss.shared.SafeStylesUtils;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.gwtproject.safehtml.shared.UriUtils;

import junit.framework.Assert;

/**
 * Tests the SafeHtmlTemplates compile-time binding mechanism.
 */
public class SafeHtmlTemplatesTest extends TestCase {

  private static final String HTML_MARKUP = "woo <i>whee</i>";

  private static final String GOOD_URL_ESCAPED = "http://foo.com/foo&lt;bar&gt;&amp;baz=dootz";
  private static final String GOOD_URL_ENCODED = "http://foo.com/foo%3Cbar%3E&amp;baz=dootz";
  private static final String GOOD_URL = "http://foo.com/foo<bar>&baz=dootz";
  private static final String BAD_URL = "javascript:evil(1<2)";
  private static final String BAD_URL_ESCAPED = "javascript:evil(1&lt;2)";

  private TestTemplates templates = new TestTemplatesImpl();

  public void testSimpleTemplate() {
    Assert.assertEquals(
        "<span><b>foo&lt;bar</b><span>woo <i>whee</i></span></span>",
        templates.simpleTemplate("foo<bar",
            SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
  }

  public void testTemplateWithUriAttribute() {
    // as String: sanitized by the template
    Assert.assertEquals(
        "<span><a href=\"" + GOOD_URL_ENCODED + "\"><b>" + HTML_MARKUP
            + "</b></a></span>",
        templates.templateWithUriAttribute(
            GOOD_URL, SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
    Assert.assertEquals(
        "<span><a href=\"#\"><b>" + HTML_MARKUP + "</b></a></span>",
        templates.templateWithUriAttribute(
            BAD_URL, SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
    // UriUtils.fromString: sanitized by fromString
    Assert.assertEquals(
        "<span><a href=\"" + GOOD_URL_ENCODED + "\"><b>" + HTML_MARKUP
            + "</b></a></span>",
        templates.templateWithUriAttribute(
            UriUtils.fromString(GOOD_URL), SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
    Assert.assertEquals(
        "<span><a href=\"#\"><b>" + HTML_MARKUP + "</b></a></span>",
        templates.templateWithUriAttribute(
            UriUtils.fromString(BAD_URL), SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
    // UriUtils.fromTrustedString: not sanitized
    Assert.assertEquals(
        "<span><a href=\"" + GOOD_URL_ESCAPED + "\"><b>" + HTML_MARKUP
            + "</b></a></span>",
        templates.templateWithUriAttribute(
            UriUtils.fromTrustedString(GOOD_URL),
            SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
    Assert.assertEquals(
        "<span><a href=\"" + BAD_URL_ESCAPED + "\"><b>" + HTML_MARKUP + "</b></a></span>",
        templates.templateWithUriAttribute(
            UriUtils.fromTrustedString(BAD_URL),
            SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
  }

  public void testTemplateWithRegularAttribute() {
    Assert.assertEquals(
        "<div id=\"" + GOOD_URL_ESCAPED + "\">" + HTML_MARKUP + "</div>",
        templates.templateWithRegularAttribute(
            GOOD_URL, SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());

    // Values inserted into non-URI-valued attributes should not be sanitized,
    // but still escaped.
    Assert.assertEquals(
        "<div id=\"" + BAD_URL_ESCAPED + "\">" + HTML_MARKUP + "</div>",
        templates.templateWithRegularAttribute(
            BAD_URL, SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
  }

  public void testTemplateWithSafeStyleAttributeComplete() {
    Assert.assertEquals("<div style=\"width:10px;\">" + HTML_MARKUP + "</div>",
        templates.templateWithSafeStyleAttributeComplete(
            SafeStylesUtils.fromTrustedString("width:10px;"),
            SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
  }

  public void testTemplateWithSafeStyleAttributeStart() {
    Assert.assertEquals("<div style=\"width:10px;height:15px;\">" + HTML_MARKUP + "</div>",
        templates.templateWithSafeStyleAttributeStart(
            SafeStylesUtils.fromTrustedString("width:10px;"), 15,
            SafeHtmlUtils.fromSafeConstant(HTML_MARKUP)).asString());
  }

  public void testTemplateWithTwoPartUriAttribute() {
    // sanitized by the template
    Assert.assertEquals(
        "<span><img src=\"" + GOOD_URL_ENCODED + "/x&amp;y\"/></span>",
        templates.templateWithTwoPartUriAttribute(
            GOOD_URL, "x&y").asString());
    Assert.assertEquals(
        "<span><img src=\"#/x&amp;y\"/></span>",
        templates.templateWithTwoPartUriAttribute(
            BAD_URL, "x&y").asString());
  }

  public void testTemplateWithStyleAttribute() {
    Assert.assertEquals(
        "<span style='background: purple; color: green;'></span>",
        templates.templateWithStyleAttribute("background: purple").asString());
  }
}
