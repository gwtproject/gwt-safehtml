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

/** GWT Unit tests for {@link SafeUriHostedModeUtils}. */
public class SafeUriHostedModeUtilsTest extends TestCase {

  public void testIsValidUriCharset() {
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset(""));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset("blah"));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset("blah<>foo"));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset("blah%foo"));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset("blah%25foo"));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset(SharedUriUtilsTest.CONSTANT_URL));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset(SharedUriUtilsTest.MAILTO_URL));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset(SharedUriUtilsTest.EMPTY_GIF_DATA_URL));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset(SharedUriUtilsTest.LONG_DATA_URL));
    assertTrue(SafeUriHostedModeUtils.isValidUriCharset(SharedUriUtilsTest.JAVASCRIPT_URL));

    assertFalse(
        SafeUriHostedModeUtils.isValidUriCharset(
            SharedUriUtilsTest.INVALID_URL_UNPAIRED_SURROGATE));
  }

  public void testMaybeCheckValidUri() {
    SafeUriHostedModeUtils.maybeCheckValidUri("");
    SafeUriHostedModeUtils.maybeCheckValidUri("blah");
    SafeUriHostedModeUtils.maybeCheckValidUri("blah<>foo");
    SafeUriHostedModeUtils.maybeCheckValidUri("blah%foo");
    SafeUriHostedModeUtils.maybeCheckValidUri("blah%25foo");
    SafeUriHostedModeUtils.maybeCheckValidUri(SharedUriUtilsTest.CONSTANT_URL);
    SafeUriHostedModeUtils.maybeCheckValidUri(SharedUriUtilsTest.MAILTO_URL);
    SafeUriHostedModeUtils.maybeCheckValidUri(SharedUriUtilsTest.EMPTY_GIF_DATA_URL);
    SafeUriHostedModeUtils.maybeCheckValidUri(SharedUriUtilsTest.LONG_DATA_URL);
    SafeUriHostedModeUtils.maybeCheckValidUri(SharedUriUtilsTest.JAVASCRIPT_URL);

    assertCheckValidUriFails(SharedUriUtilsTest.INVALID_URL_UNPAIRED_SURROGATE);
    assertCheckValidUriFails("http://");
  }

  private void assertCheckValidUriFails(String uri) {
    try {
      SafeUriHostedModeUtils.maybeCheckValidUri(uri);
    } catch (IllegalArgumentException e) {
      // expected
      return;
    } catch (AssertionError e) {
      // expected
      return;
    }
    // This must be outside the try/catch, as it throws an AssertionFailedError which, in some
    // versions of JUnit, extends AssertionError
    fail("maybeCheckValidUri failed to throw exception for: " + uri);
  }
}
