This project contains helper classes for easier creating of Tools (TopComponents).
Some of the code is copied from the openide/core and adapted for use in non-IDE
(non filesystem based) application.

The subproject eview contains a module providing a component configurable
from the layer that displays a form with expandable handles (similar
to what the new options dialog does).

This module depends on some non-stable modules from core and contrib ---
openide/convertors, contrib/bookmarks etc. Please check the project dependencies
for the details.