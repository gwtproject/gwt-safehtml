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

import static junit.framework.TestCase.assertEquals;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Test;

/** Unit tests for {@link SafeHtmlBuilder}. */
@J2clTestInput(SafeHtmlBuilderTest.class)
public class SafeHtmlBuilderTest {

  private static final String FOOBARBAZ_HTML = "foo<em>bar</em>baz";

  @Test
  public void testEmpty() {
    SafeHtmlBuilder b = new SafeHtmlBuilder();
    assertEquals("", b.toSafeHtml().asString());
  }

  @Test
  public void testFromSafeHtml() {
    SafeHtml html = SafeHtmlUtils.fromSafeConstant(FOOBARBAZ_HTML);
    SafeHtmlBuilder b = new SafeHtmlBuilder().append(html);
    assertEquals(FOOBARBAZ_HTML, b.toSafeHtml().asString());
  }

  @Test
  public void testAppend() {
    SafeHtml html = SafeHtmlUtils.fromSafeConstant(FOOBARBAZ_HTML);
    SafeHtmlBuilder b =
        new SafeHtmlBuilder()
            .appendHtmlConstant("Yabba dabba &amp; doo\n")
            .appendEscaped("What's up so&so\n")
            .append(html);

    String expected = "Yabba dabba &amp; doo\n" + "What&#39;s up so&amp;so\n" + FOOBARBAZ_HTML;
    assertEquals(expected, b.toSafeHtml().asString());
  }

  @Test
  public void testAppendHtmlConstant_innerHtml() {
    SafeHtml html =
        new SafeHtmlBuilder()
            .appendHtmlConstant("<div id=\"div_0\">")
            .appendEscaped("0 < 1")
            .appendHtmlConstant("</div>")
            .toSafeHtml();
    assertEquals("<div id=\"div_0\">0 &lt; 1</div>", html.asString());
  }

  @Test
  public void testAppendChars() {
    SafeHtmlBuilder b = new SafeHtmlBuilder();
    b.append('a');
    b.append('&');
    b.append('b');
    b.append('<');
    b.append('c');
    b.append('>');
    b.append('d');
    b.append('"');
    b.append('e');
    b.append('\'');
    b.append('f');

    SafeHtml html = b.toSafeHtml();
    assertEquals("a&amp;b&lt;c&gt;d&quot;e&#39;f", html.asString());
  }
}
