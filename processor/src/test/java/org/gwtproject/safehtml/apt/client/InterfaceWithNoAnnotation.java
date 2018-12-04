package org.gwtproject.safehtml.apt.client;

/**
 * This exists to check for a bug where the processor
 * gets a little overzealous with running on classes
 * it doesn't own.
 */
public interface InterfaceWithNoAnnotation {
  Object foo();
}
