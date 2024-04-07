[[Home]] > [[Ideas]] #idea 

It would be interesting to represent incidents and entities in a history using relational tables.

## To Do

- [x] Define `Quell`, with static helpers:
    - [x] `getColumns(Class<R>)`
    - [x] `getColumnType(Class<R>, String)`
    - [x] `getColumnValue(R, String)`
- [x] Define `QuellRow`
    - [x] `QuellRow()` -- empty row
    - [x] `QuellRow(R)` -- row for a record
    - [x] `QuellRow::toRecord(Class<R>)` -- Produce a record from a row.
    - [ ] `QuellRow::{set,get}`, taking column types into account.
        - Not yet tested.
- [ ] Define joining operators.  If possible, avoid adding a "keys" annotation.
## Basic notion

- Every table has a key and one or more data values.
- The key may be a scalar, a pair, or a triple.
- The key must have a type.
- Tables may be joined on foreign keys.

Not at all sure how to do this neatly in Java.

## Queries

What queries do I want to be able to answer?

- What incidents occurred?
- What entities do we know about?
- What incidents occurred relative to this entity? Relative to this set of entities?
- What entities were involved in this incident? In this set of incidents?

## Brain Storming

What could we do with Java records, reflection, and maybe some annotations? In pseudo-Java:

```
@Keys("entityId")               record Entity(id,name,type)                {}
@Keys("incidentId")             record Incident(incidentId, moment, label) {}
@Keys("entityId", "incidentId") record Concerns(incidentId, entityId)      {}
```

- Convert list of records to list of Rows, where `Row` is a map with metadata.
- Join two lists of Rows on a key, producing joined list of `Rows`.
- Filter, based on content.
- Sort, based on content.
- Produce list of records of a given type from a list of Records.

What do I need to make this happen?

- Record manipulation methods:
    - Get column names
    - Get key column names
    - Get column value
    - Record-to-Row method.
    - Row-to-Record method (given record class)
- Row class
    - Row methods to ease filtering.
- Value class
    - More or less a TclObject, with support for integers and strings.
- Query DSL

This looks shockingly plausible.

Names

- pen.quell
- Quell (if I need a static class for the query DSL)
- Row (the basis row map type)
- Store (if I need an object containing a list of Rows)
- Value (the value type)



