# GWT Safe-HTML

A future-proof port of the `org.gwtproject.safehtml.SafeHtml` GWT module, with no dependency on `gwt-user` (besides the Java Runtime Emulation), to prepare for GWT 3 / J2Cl.

##  Migrating from `com.google.gwt.safehtml.SafeHtml`

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
   <inherits name="org.gwtproject.safehtml.SafeHtml" />
   ```

3. Change the `import`s in your Java source files:

   ```java
   import org.gwtproject.safehtml.client.SafeHtmlTemplates;
   ```
## System Requirements

**GWT SafeHTML requires GWT 2.9.0 or newer!**
