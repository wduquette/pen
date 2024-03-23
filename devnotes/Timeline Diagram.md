[[Home]] > [[Ideas]]

- [[Timeline Diagram Initial Concept]]
- [[Timeline Diagram Brainstorming 2024-03-16]]
- [[HistoryBank and HistoryQuery]]

## Requirements

- A Time Line Diagram is a diagram of events and entity lifetimes.
- An event is something that happens at a *moment* in time, with a label.
- An entity is anything with a lifetime.
    - A historical figure
    - A historical period
    - A fictional character
    - A subplot in a novel
- Entities can be tagged with an (or several?) entity types, allowing them to be grouped in the diagram.
- A lifetime can represent:
    - An actual lifetime
    - A fuzzy internal
    - An interval of interest for something whose lifetime is actually longer
        - I.e., the interval in which a minor character has a presence in a novel.
- Events can be related to entities.
    - E.g., an event involves two characters in a particular subplot.
- A lifetime can be capped at either end in one of the following modes:
    - Explicitly, at specific moment
        - With a fuzzy cap or a hard cap.
    - Implicitly, at first/last related event.
        - Again, with a fuzzy cap or hard cap.
    - Explicitly before/after the diagram begins/ends.
- A "fuzzy" cap implies a visual indication that the lifetime begins/ends at some indeterminate time in the past/future.
- A Time Line Diagram can be tied to a specific calendar
    - In this case, the moments in time are epoch days WRT the calendar.
    - The event text will include the date, using a format provided by the programmer.
- Otherwise, the moments are just integers indicating sequence in time.
    - The event text will not include the date.
- Two events can occur at the same moment.
- A collection of events and entities can be saved to disk, potentially in a variety of ways.
    - As a Tcl script.
    - As an XML file
    - As two CSV files (ugh)
    - As an SQLite database.  (Potentially ugh, but also potentially useful.)
- It would be useful for the collection of time line data to be distinct from the Time Line Diagram itself.
- It would be useful to:
    - Be able to register calendars with `pen` so that tools can access them by name.
    - Be able to easily apply filtering to a "time DB"
        - Start/end limits
        - By entity
        - By entity type
## Related Notes

- [[Timeline Diagram Initial Concept]]
 