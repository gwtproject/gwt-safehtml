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
package org.gwtproject.safecss.shared;

import com.google.gwt.junit.client.GWTTestCase;

/** GWT Unit tests for {@link SafeStylesBuilder}. */
public class SafeStylesBuilderGwt2Test extends GWTTestCase {

  private static final String CSS0 = "foo:bar;";
  private static final String CSS1 = "baz:biz;";
  private static final String INVALID_CSS = "baz:biz";

  @Override
  public String getModuleName() {
    return "org.gwtproject.safecss.SafeCssGwt2Test";
  }

  public void testAppendSafeStyles() {
    SafeStyles string0 = SafeStylesUtils.fromTrustedString(CSS0);
    SafeStyles string1 = SafeStylesUtils.fromTrustedString(CSS1);

    SafeStylesBuilder sb = new SafeStylesBuilder();
    sb.append(string0);
    sb.append(string1);
    assertEquals("foo:bar;baz:biz;", sb.toSafeStyles().asString());
  }

  public void testAppendTrustedString() {
    SafeStylesBuilder sb = new SafeStylesBuilder();
    sb.appendTrustedString(CSS0);
    sb.appendTrustedString(CSS1);
    assertEquals("foo:bar;baz:biz;", sb.toSafeStyles().asString());
  }

  public void testAppendTrustedStringWithInvalidCss() {
    if (!SafeStylesUtilsGwt2Test.isAssertionEnabled()) {
      return;
    }

    SafeStylesBuilder sb = new SafeStylesBuilder();
    sb.appendTrustedString(CSS0);

    boolean caught = false;
    try {
      sb.appendTrustedString(INVALID_CSS);
    } catch (AssertionError e) {
      // Expected.
      caught = true;
    }
    if (!caught) {
      fail("Expected AssertionError for invalid css: " + INVALID_CSS);
    }
  }

  public void testEmpty() {
    SafeStylesBuilder sb = new SafeStylesBuilder();
    assertEquals("", sb.toSafeStyles().asString());
  }
}
