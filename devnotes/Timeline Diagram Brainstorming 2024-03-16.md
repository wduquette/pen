[[Home]] > [[Timeline Diagram]]

## Observations

**Data Model:** This task is complicated by my current data model, which I'm trying to make serve several purposes:

1. A convenient model for accumulating history (entities and incidents) via a DSL.
2. A model for reading/writing (e.g., to/from XML/CSV/etc.) with minimal processing, so that I could define a history database on a topic, and load it as a unit and produce diagrams for any queried subset.
3. A model for editing (in theory): adding/deleting events and entities over time.
4. A model for drawing, with minimal effort.

The model as it is currently stands serves purpose 1 but not 3, and maybe not 2:

- Incidents depend on entities
    - Each incident is tagged with the entities to which it belongs.
- Entities depend on incidents
    - An entity's interval (the specific start and end times) can depend on the incidents tagged by it.

We need to be sure we have all the data before we compute the incident start and end times; this can't be done as we accumulate the entities and incidents.

- This implies a dataflow: `historyAsAccumulated -> historyAsFinalized`
- And this implies that the classes I use to accumulate the data and the classes I need to display the data are different.

## What Each Purpose Wants

Model #1 wants classes that can be built up bit by bit, with maximum convenience to the client.

Model #3 wants simple records with all the questions answered.

Model #2 could use either; but using simple records would make adding to the data more difficult.

## Data Model Classes

I can resolve this using several different patterns.

- I can provide successive versions of each type for subsequent stages of the dataflow
    - Use `History`, `Incident`, `Entity` for gathering data
    - On demand, `History` produces a `ConcreteHistory` class using `ConcreteIncident`, `ConcreteEntity`
- I can define additional types, e.g., `Period`, which augment the existing data.

## Solution?

Suppose the `History` contains this kind of data:

- `Entity`: an ID, a name for display, and a type...but no interval information at all.
- `Incident`: a timestamped event.
    - Each has a moment and a label
    - There are multiple types
        - `Start`: references a single entity by ID, and a `Cap` type.
        - `End`: references a single entity by ID, and a `Cap` type.
        - `General` references any number of entities.

Then, given a time frame, the `History` can produce an `Period` for an entity.

- The `Period` includes
    - The `Entity` 
    - The start and end times, as computed from the `History` and the time frame
    - The start and end `Cap` values, as computed from the `History` and the time frame.
- The start and end of the interval are based on the incidents.
- No interval is produced if the entity's time range is outside the desired timeframe.
- If the entity begins or ends outside the desired timeframe, it gets a `SOFT` cap at that end.
- If the entity begins/ends with a `Start/End` within the timeframe, it gets the `Start/End`'s cap.
- If the entity begins/ends with a `General` within the timeframe, it gets a `FUZZY` cap.

 Thus, every `Period` starts at either the top of the diagram or at a known `Incident` and ends at the bottom of the diagram or at a known `Incident`.

## History and Calendars

I have two use cases for timelines: with and without a calendar.

For many stories, the calendar dates simply are not important.  The incidents are just a sequence of events in the story, and have no dates associated with them.

For other stories (e.g., Armorica) the calendar dates are vital.

Entering dates as strings is tricky because the client needs access to the defined calendar; he can't just create the incidents separately.

Suggestion: 

- Have a `History` class that accumulates the data.
    - This is the format that can be saved to disk and then re-read.
- Have a `HistoryDSL` class that provides the DSL interface and can parse time strings.

## So, What to Do

- Putting all of the time data into the incident list makes sense.
    - Computing Periods on the fly is what's needed by the drawing code
    - And it means that all times pertinent to the diagram are in the incidents list.
- I might want to write a history processor
    - The DSL/scripting API is for manipulating a history DB
    - The timeline diagram loads and displays all or part of the history.
- I'm beginning to look at a project structure.
    - History file (possibly split into entity and incident files)
    - Calendar file(s)
- I don't need the whole ecosystem in place to get started.
    - A DSL/script can build the history and give it to the diagram.
    - A script can easily define the history using Tcl commands.
    - XML would also work, but I'd want something like SimpleDOM.
        - That comes later.
- But I can define the required components to allow the future to happen someday.

## Next Steps

- Define `History`, `Incident`, `Entity`, and `Period` classes as described above, without worrying about the DSL.
    - Just basic Java read/write classes.
- Define a `History` DSL to make defining the history easier.
- Define a `HistoryExtension` to build histories in a Tcl script.
- Update `TimelineDiagram` to take a `History` object, from which it will get `Periods`.

