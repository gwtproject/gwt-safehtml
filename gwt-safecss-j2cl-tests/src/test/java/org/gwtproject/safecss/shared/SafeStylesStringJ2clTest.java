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

import static junit.framework.TestCase.*;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Test;

@J2clTestInput(SafeStylesStringJ2clTest.class)
public class SafeStylesStringJ2clTest {

  /**
   * Test that {@link org.gwtproject.safecss.shared.SafeStyles} throws an assertion error if the
   * string contains a bracket.
   */
  @Test
  public void testCloseBracket() {
    if (!SafeStylesUtilsJ2clTest.isAssertionEnabled()) {
      return;
    }

    String invalid = "contains:close>;";
    boolean caught = false;
    try {
      new SafeStylesString(invalid);
    } catch (AssertionError e) {
      // Expected.
      caught = true;
    }
    if (!caught) {
      fail("Expected AssertionError for: " + invalid);
    }
  }

  @Test
  public void testEquals() {
    SafeStylesString safe1 = new SafeStylesString("string:same;");
    SafeStylesString safe2 = new SafeStylesString("string:same;");
    SafeStylesString safe3 = new SafeStylesString("string:diff;");
    assertEquals(safe1, safe2);
    assertFalse(safe1.equals(safe3));
  }

  @Test
  public void testHashCode() {
    SafeStylesString safe1 = new SafeStylesString("string:same;");
    SafeStylesString safe3 = new SafeStylesString("string:diff;");
    SafeStylesString safe2 = new SafeStylesString("string:same;");
    assertEquals("string:same;".hashCode(), safe1.hashCode());
    assertEquals(safe1.hashCode(), safe2.hashCode());
    assertEquals("string:diff;".hashCode(), safe3.hashCode());
  }

  /**
   * Test that {@link org.gwtproject.safecss.shared.SafeStyles} throws an assertion error if the
   * string is missing a semi-colon.
   */
  @Test
  public void testMissingSemiColon() {
    if (!SafeStylesUtilsJ2clTest.isAssertionEnabled()) {
      return;
    }

    // Verify that the empty string is okay.
    new SafeStylesString(""); // no error expected.
    new SafeStylesString(" "); // no error expected.

    String invalid = "missing:semicolon";
    boolean caught = false;
    try {
      new SafeStylesString(invalid);
    } catch (AssertionError e) {
      // Expected.
      caught = true;
    }
    if (!caught) {
      fail("Expected AssertionError for: " + invalid);
    }
  }

  @Test
  public void testNull() {
    try {
      new SafeStylesString(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      // Expected.
    }
  }

  /**
   * Test that {@link org.gwtproject.safecss.shared.SafeStyles} throws an assertion error if the
   * string contains a bracket.
   */
  @Test
  public void testOpenBracket() {
    if (!SafeStylesUtilsJ2clTest.isAssertionEnabled()) {
      return;
    }

    String invalid = "contains:open<;";
    boolean caught = false;
    try {
      new SafeStylesString(invalid);
    } catch (AssertionError e) {
      // Expected.
      caught = true;
    }
    if (!caught) {
      fail("Expected AssertionError for: " + invalid);
    }
  }

  /** Test that {@link org.gwtproject.safecss.shared.SafeStyles} allows quotes. */
  @Test
  public void testQuotes() {
    if (!SafeStylesUtilsJ2clTest.isAssertionEnabled()) {
      return;
    }

    // Verify that a string containing single quotes does not cause an
    // exception.
    new SafeStylesString("name:'value';");

    // Verify that a string containing double quotes does not cause an
    // exception.
    new SafeStylesString("name:\"value\";");
  }
}
