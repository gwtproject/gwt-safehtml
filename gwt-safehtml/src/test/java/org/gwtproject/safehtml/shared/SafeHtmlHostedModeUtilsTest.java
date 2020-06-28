/*
 * Copyright © 2019 The GWT Project Authors
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
package org.gwtproject.safehtml.shared;

import junit.framework.TestCase;

/** GWT Unit tests for {@link SafeHtmlHostedModeUtils}. */
public class SafeHtmlHostedModeUtilsTest extends TestCase {

  {
    // Since we can't assume assertions are enabled, force
    // SafeHtmlHostedModeUtils#maybeCheckCompleteHtml to perform its check
    // when running in JRE.
    System.setProperty(SafeHtmlHostedModeUtils.FORCE_CHECK_COMPLETE_HTML, "true");
    SafeHtmlHostedModeUtils.setForceCheckCompleteHtmlFromProperty();
  }

  public void testMaybeCheckCompleteHtml() {
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("blah");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("<foo>blah");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("<>blah");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("baz");

    assertCheckCompleteHtmlFails("baz<");
    assertCheckCompleteHtmlFails("baz<em>foo</em> <x");
    assertCheckCompleteHtmlFails("baz<em>foo</em> <x a=b");
    assertCheckCompleteHtmlFails("baz<em>foo</em> <x a=\"b");
    assertCheckCompleteHtmlFails("baz<em>foo</em> <x a=\"b\"");
    assertCheckCompleteHtmlFails("baz<em>foo</em> <x a=\"b\" ");

    assertCheckCompleteHtmlFails("<script>");
    assertCheckCompleteHtmlFails("<style>");

    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("baz<em>foo</em> <x a=\"b\"> ");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("baz<em>foo</em> <x a=\"b\">sadf");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("baz<em>foo</em> <x a=\"b\">");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("baz<em>foo</em> <x a=\"b\"/>");
    SafeHtmlHostedModeUtils.maybeCheckCompleteHtml("baz<em>foo</em> <x a=\"b\"/>bbb");
  }

  private void assertCheckCompleteHtmlFails(String html) {
    try {
      SafeHtmlHostedModeUtils.maybeCheckCompleteHtml(html);
    } catch (IllegalArgumentException e) {
      // expected
      return;
    } catch (AssertionError e) {
      // expected
      return;
    }
    // This must be outside the try/catch, as it throws an AssertionFailedError which, in some
    // versions of JUnit, extends AssertionError
    fail("maybeCheckCompleteHtml failed to throw exception for: " + html);
  }
}
