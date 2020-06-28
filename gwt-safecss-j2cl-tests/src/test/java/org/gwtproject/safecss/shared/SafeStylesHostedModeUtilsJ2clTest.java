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

@J2clTestInput(SafeStylesHostedModeUtilsJ2clTest.class)
public class SafeStylesHostedModeUtilsJ2clTest {

  private static final String ERROR_MESSAGE_MISMATCH =
      "Expected error message does not match actual error message";

  @Test
  public void testIsValidStyleName() {
    if (!"on".equals(System.getProperty("superdevmode"))) {
      // isValidStyleName always returns true in prod mode.
      return;
    }

    // Valid names.
    for (String s : SafeStylesUtilsJ2clTest.VALID_STYLE_NAMES) {
      String error = SafeStylesHostedModeUtils.isValidStyleValue(s);
      assertNull("'" + s + "' incorrectly reported as an invalid style name: " + error, error);
    }

    // Invalid names.
    for (String s : SafeStylesUtilsJ2clTest.INVALID_STYLE_NAMES) {
      assertNotNull(
          "'" + s + "' incorrectly reported as an valid style name",
          SafeStylesHostedModeUtils.isValidStyleName(s));
    }
  }

  @Test
  public void testIsValidStyleValue() {
    if (!"on".equals(System.getProperty("superdevmode"))) {
      // isValidStyleValue always returns true in prod mode.
      return;
    }

    // Valid values.
    for (String s : SafeStylesUtilsJ2clTest.VALID_STYLE_VALUES) {
      String error = SafeStylesHostedModeUtils.isValidStyleValue(s);
      assertNull("'" + s + "' incorrectly reported as an invalid style value: " + error, error);
    }

    // Invalid values.
    for (String s : SafeStylesUtilsJ2clTest.INVALID_STYLE_VALUES) {
      assertNotNull(
          "'" + s + "' incorrectly reported as an valid style value",
          SafeStylesHostedModeUtils.isValidStyleValue(s));
    }
  }

  @Test
  public void testMaybeCheckValidStyleName() {
    // Check a valid name.
    SafeStylesHostedModeUtils.maybeCheckValidStyleName("name");

    // Check an invalid name.
    String expectedError =
        SafeStylesHostedModeUtils.isValidStyleName(SafeStylesUtilsJ2clTest.INVALID_STYLE_NAME);
    assertNotNull(expectedError);
    boolean caught = false;
    try {
      SafeStylesHostedModeUtils.maybeCheckValidStyleName(
          SafeStylesUtilsJ2clTest.INVALID_STYLE_NAME);
    } catch (IllegalArgumentException e) {
      /*
       * Expected - maybeCheckValidStyleName() use either
       * Preconditions.checkArgument() (which throws an
       * IllegalArgumentException), or an assert (which throws an
       * AssertionError).
       */
      assertEquals(ERROR_MESSAGE_MISMATCH, expectedError, e.getMessage());
      caught = true;
    } catch (AssertionError e) {
      // Expected - see comment above.
      assertEquals(ERROR_MESSAGE_MISMATCH, expectedError, e.getMessage());
      caught = true;
    }

    if (!caught) {
      fail("Expected an exception for invalid style name.");
    }
  }

  @Test
  public void testMaybeCheckValidStyleValue() {
    SafeStylesHostedModeUtils.maybeCheckValidStyleValue("value");

    String expectedError =
        SafeStylesHostedModeUtils.isValidStyleValue(SafeStylesUtilsJ2clTest.INVALID_STYLE_VALUE);
    assertNotNull(expectedError);
    boolean caught = false;
    try {
      SafeStylesHostedModeUtils.maybeCheckValidStyleValue(
          SafeStylesUtilsJ2clTest.INVALID_STYLE_VALUE);
    } catch (IllegalArgumentException e) {
      /*
       * Expected - maybeCheckValidStyleValue() use either
       * Preconditions.checkArgument() (which throws an
       * IllegalArgumentException), or an assert (which throws an
       * AssertionError).
       */
      assertEquals(ERROR_MESSAGE_MISMATCH, expectedError, e.getMessage());
      caught = true;
    } catch (AssertionError e) {
      // Expected - see comment above.
      assertEquals(ERROR_MESSAGE_MISMATCH, expectedError, e.getMessage());
      caught = true;
    }

    if (!caught) {
      fail("Expected an exception for invalid style value.");
    }
  }
}
