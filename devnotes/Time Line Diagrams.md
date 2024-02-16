[[Home]] > [[Ideas]] #idea 

I've spent a lot of time thinking about time lines over the years.  It always ends up in a morass, because real world events are fuzzy, their endpoints are often imprecisely specified if they are known at all, and calendars change over time.  A general database of events and periods is really difficult to come up with.

What I am looking for here is something much more modest.

- A way to diagram time lines given a script.
- A timeline consists of
    - Entities that exist for a span of time.
        - Historical figures or institutions or periods
        - Characters in a novel; start/end is earliest/latest appearance
    - An entity can have an indeterminate beginning or end
        - Extending to the beginning or end of the diagram, indicating that it started before or ended after
        - Or fading out at a particular time, indicating that it started previously or ended subsequently, but we don't know quite when.
    - An ordered set of events
        - Optional timestamp
            - A diagram of events in a novel doesn't need to have 
        - Optional name
        - Related entities
    - Events include the beginning and end of each entity
- At its simplest, the events need only be listed and presented in order; any time relations are output only, and do not affect the diagram at all.
- In some cases, we might want to preserve some time scale
    - VERY TRICKY
    - I.e., order events by a timestamp
    - Compute distance between drawn events based on the interval between them.
    - Needs a mapping from time string to some integer time.
    - "Easy" for reasonably modern events.
    - Difficult for fantasy [[Calendars]].
- Appearance
    - Graphical table
    - Events are rows, entities are columns
        - The first column contains the event name/timestamp
        - The first row contains entity names, rotated to be vertical
    - In each entity column, the entity is drawn as a narrow vertical band that extends from the entity's beginning event to its ending event.
        - The two ends of the band can be drawn specially to indicate how it terminates.