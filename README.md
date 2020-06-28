# GWT Safe-HTML & Safe-CSS

![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)  [![License](https://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Chat on Gitter](https://badges.gitter.im/hal/elemento.svg)](https://gitter.im/gwtproject/gwt-modules) ![CI](https://github.com/gwtproject/gwt-tsafehtml/workflows/CI/badge.svg)

A future-proof port of the `org.gwtproject.safehtml.SafeHtml` and `org.gwtproject.safecssl.SafeCss` GWT module, with no dependency on `gwt-user` (besides the Java Runtime Emulation), to prepare for GWT 3 / J2Cl.

##  Migrating from `org.gwtproject.safehtml.SafeHtml`

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.safehtml</groupId>
     <artifactId>gwt-safehtml</artifactId>
     <version>HEAD-SNAPSHOT</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.safehtml:gwt-safehtml:HEAD-SNAPSHOT")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.safehtml.SafeHTML" />
   ```

3. Change the `import`s in your Java source files:

   ```java
   import org.gwtproject.safehtml.client.SafeHtmlTemplates;
   ```

##  Migrating from `org.gwtproject.safehcss.SafeCss`

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.safecss</groupId>
     <artifactId>gwt-safecss</artifactId>
     <version>HEAD-SNAPSHOT</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.safecss:gwt-safecss:HEAD-SNAPSHOT")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.safecss.SafeCSS" />
   ```

3. Change the `import`s in your Java source files:

   ```java
   import org.gwtproject.safecss.shared.SafeStyles;
   ```

## Instructions

To build gwt-safehtml:

* run `mvn clean verify`

on the parent directory. This will build the artifact and run tests against the JVM, J2CL, and GWT2.

## System Requirements

**GWT SafeHTML & SafeCSS require GWT 2.9.0 or newer!**


## Dependencies

GWT SafeHTML does not depend on any other module.

## Notes

Migration of GWT 2.x's SafeHtml packages to an external project, including rewriting
the Generator into an annotation process for use in general Java, not just within
GWT client code. 

The processor was written before I really knew what I was doing, and it needs another
look before usage in real projects.

Before this gets to 1.0, a deep look should be taken at the differences and 
similarities between this and https://github.com/google/safe-html-types/, looking for
opportunities for reuse and code sharing.

At this point, tests pass and it appears usable, but additional GwtTestCases would
be good to have. SafeCss needs to be migrated out as well, and this annotation
processor should switch to generally available versions of the html parser to 
properly eliminated dependencies on upstream GWT.