WHAT'S THIS STUFF?

A few libraries - some of which were factored out of the 4.0 output window 
(though it still has its own copies of these), which are generally semi-useful.

None of them are currently set up as NetBeans modules, just as standard 
J2SE projects.


storage - An abstraction for byte-based data storage, with heap and 
  memory-mapped file implementations

numeric_collections - A few collections-like classes which use primitive
  numeric types, useful in performance or memory-critical situations

cache - A generic framework for creating/using caches of data over a
  memory-mapped file.  Basically operates like a big List of NIO ByteBuffers,
  and handles the bookkeeping of tracking/saving file offsets, and tries
  to be reasonably robust.

  The cache library depends on the storage and numeric_collections libraries.
