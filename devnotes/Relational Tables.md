[[Home]] > [[Ideas]] #idea 

It would be interesting to represent incidents and entities in a history using relational tables.

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