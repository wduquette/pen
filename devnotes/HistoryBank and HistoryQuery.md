[[Home]] > [[Timeline Diagram]]

## Fleshed-out Concept

- `HistoryBank` is the class for managing a set of incidents and entities.
    - API to add/remove incidents and entities.
    - Save/restore data to/from disk.
- `HistoryQuery` is a class for building canned history queries.
    - Each instance is a distinct query, which can be applied to a `History`
    - The query results in a `HistoryView`
- `HistoryView` is a view of a `HistoryBank`'s content, usually produced by a `HistoryQuery`.
    - It contains a static copy of the query result.
    - It can be queried but not modified.
    - Maybe can be saved to disk?
- `HistoryBank` and `HistoryView` are both `History` objects.
    - `History` is currently an interface; might be an abstract base class.


## Initial Concept

Idea for doing history queries.

- `History` is renamed to `HistoryBank` that implements interface `History`.
- The history interface includes a query API
    - Entity predicate
    - Incident predicate
    - Incident labeler (i.e., add date)
    - Possibly other things
- Using these returns a `HistoryQuery` object as an instance of the `History` type.
    - All basic queries, e.g., `getIncidents`, make use of the predicates.

Then, a `dump()` method or a `toString()` method can return a nice text representation of the data using Unicode for the symbols, as a UTF-8 text string.

