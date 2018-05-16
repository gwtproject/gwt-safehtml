/*
 * Copyright 2011 Google Inc.
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
package org.gwtproject.safecss.shared;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT Unit tests for {@link SafeStylesHostedModeUtilsJvm}.
 */
public class GwtSafeStylesHostedModeUtilsTest extends GWTTestCase {

  private static final String ERROR_MESSAGE_MISMATCH =
      "Expected error message does not match actual error message";

  @Override
  public String getModuleName() {
    return "org.gwtproject.safecss.SafeCss";
  }

  public void testIsValidStyleName() {
    if (isProdMode()) {
      // isValidStyleName always returns true in prod mode.
      return;
    }

    // Valid names.
    for (String s : GwtSafeStylesUtilsTest.VALID_STYLE_NAMES) {
      String error = SafeStylesHostedModeUtilsJvm.isValidStyleValue(s);
      assertNull("'" + s + "' incorrectly reported as an invalid style name: " + error, error);
    }

    // Invalid names.
    for (String s : GwtSafeStylesUtilsTest.INVALID_STYLE_NAMES) {
      assertNotNull("'" + s + "' incorrectly reported as an valid style name",
          SafeStylesHostedModeUtilsJvm.isValidStyleName(s));
    }
  }

  public void testIsValidStyleValue() {
    if (isProdMode()) {
      // isValidStyleValue always returns true in prod mode.
      return;
    }

    // Valid values.
    for (String s : GwtSafeStylesUtilsTest.VALID_STYLE_VALUES) {
      String error = SafeStylesHostedModeUtilsJvm.isValidStyleValue(s);
      assertNull("'" + s + "' incorrectly reported as an invalid style value: " + error, error);
    }

    // Invalid values.
    for (String s : GwtSafeStylesUtilsTest.INVALID_STYLE_VALUES) {
      assertNotNull("'" + s + "' incorrectly reported as an valid style value",
          SafeStylesHostedModeUtilsJvm.isValidStyleValue(s));
    }
  }

  public void testMaybeCheckValidStyleName() {
    if (isProdMode()) {
      /*
       * SafeStylesHostedModeUtilsJvm.maybeCheckValidStyleName is a no-op in prod
       * mode.
       */
      SafeStylesHostedModeUtilsJvm.maybeCheckValidStyleName(GwtSafeStylesUtilsTest.INVALID_STYLE_NAME);
    } else {
      // Check a valid name.
      SafeStylesHostedModeUtilsJvm.maybeCheckValidStyleName("name");

      // Check an invalid name.
      String expectedError =
          SafeStylesHostedModeUtilsJvm.isValidStyleName(GwtSafeStylesUtilsTest.INVALID_STYLE_NAME);
      assertNotNull(expectedError);
      boolean caught = false;
      try {
        SafeStylesHostedModeUtilsJvm
            .maybeCheckValidStyleName(GwtSafeStylesUtilsTest.INVALID_STYLE_NAME);
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
  }

  public void testMaybeCheckValidStyleValue() {
    if (isProdMode()) {
      /*
       * SafeStylesHostedModeUtilsJvm.maybeCheckValidStyleValue is a no-op in prod
       * mode.
       */
      SafeStylesHostedModeUtilsJvm
          .maybeCheckValidStyleValue(GwtSafeStylesUtilsTest.INVALID_STYLE_VALUE);
    } else {
      // Check a valid value.
      SafeStylesHostedModeUtilsJvm.maybeCheckValidStyleValue("value");

      String expectedError =
          SafeStylesHostedModeUtilsJvm.isValidStyleValue(GwtSafeStylesUtilsTest.INVALID_STYLE_VALUE);
      assertNotNull(expectedError);
      boolean caught = false;
      try {
        SafeStylesHostedModeUtilsJvm
            .maybeCheckValidStyleValue(GwtSafeStylesUtilsTest.INVALID_STYLE_VALUE);
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

  private boolean isProdMode() {
    return System.getProperty("superdevmode") != null;
  }

}
