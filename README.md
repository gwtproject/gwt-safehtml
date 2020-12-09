# GWT Safe-HTML & Safe-CSS

![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)  [![License](https://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Chat on Gitter](https://badges.gitter.im/hal/elemento.svg)](https://gitter.im/gwtproject/gwt-modules) ![CI](https://github.com/gwtproject/gwt-safehtml/workflows/CI/badge.svg)

A future-proof port of the `com.google.gwt.safehtml.SafeHtml` and `com.google.gwt.safecssl.SafeCss` GWT module, with no dependency on `gwt-user` (besides the Java Runtime Emulation), to prepare for GWT 3 / J2Cl.

##  Migrating from `com.google.gwt.safehtml.SafeHtml`

See: [SafeHTML-Module](https://github.com/gwtproject/gwt-safehtml/tree/master/gwt-safehtml)

##  Migrating from `com.google.gwt.safecss.SafeCss`

See: [SafeCSS-Module](https://github.com/gwtproject/gwt-safehtml/tree/master/gwt-safecss)

## Instructions

To build gwt-safehtml:

* run `mvn clean verify`

on the parent directory. This will build the artifact and run tests against the JVM, J2CL, and GWT2.

