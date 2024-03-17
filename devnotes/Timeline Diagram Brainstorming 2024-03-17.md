[[Home]] > [[Timeline Diagram]]

Idea for doing history queries.

- `History` is renamed to `HistoryBase` that implements interface `History`.
- The history interface includes a query API
    - Entity predicate
    - Incident predicate
    - Incident labeler (i.e., add date)
    - Possibly other things
- Using these returns a `HistoryQuery` object as an instance of the `History` type.
    - All basic queries, e.g., `getIncidents`, make use of the predicates.

Then, a `dump()` method or a `toString()` method can return a nice text representation of the data using Unicode for the symbols, as a UTF-8 text string.

