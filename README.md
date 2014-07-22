This project is a quick attempt at beginning to replace built-in GWT generators
with APT code generation, allowing the javac process to do the code generation
up front and out of the gwtc process. In theory this should give compilation
to JS a bit less to worry about.

This code relies on the `<generate-with>` statements to remain in GWT, and
assumes that they will continue to generate to the same class. That assumption
lets us generate code, and lets the old GWT Generator see that its work has
already been done - use code does not need to change at all, it can still
call `GWT.create(MyTemplate.class)` to get a real implementation.

Projects that use this will need to be sure that their
`target/generated-sources/annotations` directory is available for gwtc
compilation.