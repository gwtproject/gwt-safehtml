package org.gwtproject.safecss.shared;

/**
 * Used to configure test executions that should come after regular tests.
 *
 * Note that we don't put the GWTTestCases in here because we want to run after them, so we can peak
 * at the compiled output without having to bother with a second compilation.
 */
public interface IntegrationTest {

}
