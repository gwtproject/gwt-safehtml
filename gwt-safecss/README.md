# Safe-CSS

A future-proof port of the `org.gwtproject.safehtml.SafeCss` GWT module, with no dependency on `gwt-user` (besides the Java Runtime Emulation), to prepare for GWT 3 / J2Cl.

##  Migrating from `org.gwtproject.safecss.SafeCss`

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.safehtml</groupId>
     <artifactId>gwt-safecss</artifactId>
     <version>HEAD-SNAPSHOT</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.safehtml:gwt-safecss:HEAD-SNAPSHOT")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.safecss.SafeCss" />
   ```

3. Change the `import`s in your Java source files:

   ```java
   import org.gwtproject.safecss.shared.SafeStyles;
   ```

## System Requirements

**GWT SafeCSS requires GWT 2.9.0 or newer!**


## Dependencies

GWT Safe CSS depends on gwt-dom-style-definitions.
