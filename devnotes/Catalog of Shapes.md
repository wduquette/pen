#idea #rejected

Styles are effectively prototypes.  Perhaps Shapes should be prototypes as well.

- Maintain a catalog of named shapes with default parameters set.
- Drawing a shape involves
    - Pulling it from the catalog by name and making a copy.
        - Rather than creating a new instance.
        - Effectively allows non-style parameters to be styled.
    - Customizing its shape and style parameters.
    - Drawing it.
- Client can add a modified shape to the catalog at any time.
- Still need a way to create "vanilla" shapes.

## Pros

- Works well with the [[Open Shape DSL]]
    - The factory function can pull it from the catalog by name
- Can work with Tcl API, provided that introspection is available.

## Cons

- How many shape parameters of interest are there for this use case?
- All the same cons as for [[Open Shape DSL]]

## Bottom Line

For now, I think this is overkill.