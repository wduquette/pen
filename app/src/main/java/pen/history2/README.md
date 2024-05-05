# pen.history2

This isn't a finished package, but rather a sketch for what the history
data structures might look like.  The goal is to ease both queries and 
computations.

Ideally, I want structures that can be used with Quell as lists of native
records.

## Model

### Entities

`Entity`: `Entity.Person`, `Entity.Place`, `Entity.Group`, `Entity.Period`

- Provides the entity ID and display name, plus predicates, e.g., `isPerson()`.

### Static Relations

`within(placeEntity, surroundingEntity)`: A place's containing region.  This
is a genuine relation.

### Dynamic Relations

These would be used in incident or time range queries.  These would be computed
from the incident and entity data, and would be able to answer questions like,

- Where is Joe at time T?
- Is Joe in location X at time T?
- When has Joe been in location X?

`location(personEntity, placeEntity)`:  Where a person is at present, and whether
they are in a location at present.

`member(person,groupEntity)`: Whether a person belongs to a group at present.

`relationship(person, person, type)`: The two people are in a relationship of
a given type.

### Incident types

Every incident should have a unique integer ID, assigned at definition, and 
used during queries and such like.

`movesTo(id, moment, person, place)`: A person moves to the given place.

`start(id, moment, entity, cap)`, `end(moment, entity, cap)`: An entity's start and 
end in the history. Not clear we really need these, unless they are memorials.

`joins(id, moment, entity, group)`, `leaves(moment,entity,group)`

`birth(id, moment, person)`, `death(moment, person)`: Start and end for a person.

`memorial(id, moment, entity)`: Some other memorial associated with an entity.  
Founding of a group or nation; discovery of a land; beginning or end of a 
war.




