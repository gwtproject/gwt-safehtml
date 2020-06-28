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
package org.gwtproject.safecss;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import org.gwtproject.safecss.shared.SafeStylesBuilderGwt2Test;
import org.gwtproject.safecss.shared.SafeStylesHostedModeUtilsGwt2Test;
import org.gwtproject.safecss.shared.SafeStylesStringGwt2Test;
import org.gwtproject.safecss.shared.SafeStylesUtilsGwt2Test;

/** Test suite for SafeCss GWTTestCases. */
public class SafeCssGwt2TestSuite {

  private SafeCssGwt2TestSuite() {}

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test suite for safe css  GWTTestCases");

    suite.addTestSuite(SafeStylesBuilderGwt2Test.class);
    suite.addTestSuite(SafeStylesHostedModeUtilsGwt2Test.class);
    suite.addTestSuite(SafeStylesStringGwt2Test.class);
    suite.addTestSuite(SafeStylesUtilsGwt2Test.class);

    return suite;
  }
}
