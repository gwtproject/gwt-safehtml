/*
 * Copyright © 2018 The GWT Project Authors
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
package org.gwtproject.safehtml.processor;

import static com.google.testing.compile.Compiler.javac;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import org.junit.Test;

public class SafeHtmlProcessorTest {

  @Test
  public void testTemplates() {
    Compilation compilation =
        javac()
            .withProcessors(new SafeHtmlProcessor())
            .compile(
                Arrays.asList(
                    JavaFileObjects.forResource(
                        "org/gwtproject/safehtml/processor/client/templates/TestTemplates.java")));
    CompilationSubject.assertThat(compilation).succeeded();
    CompilationSubject.assertThat(compilation)
        .generatedSourceFile("org/gwtproject/safehtml/processor/client/templates/TestTemplatesImpl")
        .hasSourceEquivalentTo(
            JavaFileObjects.forResource(
                "org/gwtproject/safehtml/processor/client/templates/TestTemplatesImpl.java"));
  }

  @Test
  public void testEmptyAnnotation() {
    Compilation compilation =
        javac()
            .withProcessors(new SafeHtmlProcessor())
            .compile(
                Arrays.asList(
                    JavaFileObjects.forResource(
                        "org/gwtproject/safehtml/processor/client/empty/annotation/TestEmptyAnnotationTemplates.java")));
    CompilationSubject.assertThat(compilation).failed();
    CompilationSubject.assertThat(compilation)
        .hadErrorContaining(
            "annotation @org.gwtproject.safehtml.client.SafeHtmlTemplates.Template is missing a default value for the element 'value'");
  }
}
