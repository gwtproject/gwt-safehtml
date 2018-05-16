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
package org.gwtproject.safecss;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import org.gwtproject.safecss.shared.SafeStylesBuilderTest;
import org.gwtproject.safecss.shared.SafeStylesHostedModeUtilsTest;
import org.gwtproject.safecss.shared.SafeStylesStringTest;
import org.gwtproject.safecss.shared.SafeStylesUtilsTest;

/**
 * Test suite for SafeCss GWTTestCases.
 */
public class SafeCssJreSuite {

  private SafeCssJreSuite() {
  }

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test suite for safe css tests that require the JRE");

    suite.addTestSuite(SafeStylesBuilderTest.class);
    suite.addTestSuite(SafeStylesHostedModeUtilsTest.class);
    suite.addTestSuite(SafeStylesStringTest.class);
    suite.addTestSuite(SafeStylesUtilsTest.class);

    return suite;
  }
}
