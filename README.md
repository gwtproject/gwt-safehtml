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