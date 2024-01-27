[[Home]] > [[Ideas]]

Provide an API (Java, Tcl) for drawing trees.

## The Tree Design Space

These are the features that different kinds of tree structure can have

- What do nodes in the tree look like?
    - Boxes, possibly varying in shape, containing labels
    - Underlined labels (as in FreePlane) 
        - This works best if the tree depth grows horizontally.
- Where is the root of the tree?
    - West side, with children to the right of parents, stacking siblings up and down
    - Northwest corner, with children to the right of parents, stacking siblings downwards.
    - In the center, with children in rings on two or four sides
    - And the other logical variants of these
- How are parents connected to children?
    - Straight-lines, parent to child
    - Bezier curves, parent to child
    - Orthogonal lines, parent to "bus" to children
- Connector decorations

## Some Examples

A class inheritance diagram, root at north side, boxes, straight lines:

![[Drawing 2024-01-21 10.40.23.excalidraw]]

The same, with orthogonal connectors.

![[Drawing 2024-01-21 10.41.23.excalidraw]]

An outline, root west side, underlined labels, straight lines (which should really be bezier lines, but those are a pain to draw by hand)

![[Tree Diagrams 2024-01-21 10.49.22.excalidraw]]

The same, but with the root in the northwest corner.

![[Tree Diagrams 2024-01-21 10.54.56.excalidraw]]

In this case, it can be useful to make the root a tiny stub

![[Tree Diagrams 2024-01-21 10.58.53.excalidraw]]

I've drawn these so that all the children of a given depth stack vertically, but there's no real reason to do that.

## Layout

Each set of siblings can be thought of as a bounding box. Given the outline examples above,

- Each sibling set must be far enough to the right of its parent to allow the connector to be draw.
- Each sibling must be given enough vertical space for its immediately children.

So, starting at the leaves, determine the vertical extent of the sibling set, and work to the left.

There are some tradeoffs to be made.  On overlap between cousins, a sibling set can be moved to the right, or it itself can be granted more vertical space.

I'm sure that there's a good algorithm for this.

A simple way to do it is to treat each child subtree as having a bounding box, and stacking those vertically.  But this will waste space in some cases.  (I think I decided that it's what FreePlane does by default, though.)