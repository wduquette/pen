[[Home]] > [[Ideas]] > [[Calendars]]

Add a notion of Calendar Items, i.e., holidays, meetings, season changes—anything that might get marked on the calendar.  For Armorica, that should include annual dates like:

- The _Deuxieme Debarquement_ and Christmas (holidays)
- The day Armand first arrives in Armorica
- Armand's wedding day; Amelia's wedding day
- Battles, i.e., the Battle of the Approaches, where Amelia saves the day
- Moveable holidays, like Easter (tricky!)

But it could also include specific single dates; or recurring dates (every second and fourth Thursday of the month), etc.

These can be defined using a `Date` predicate.

- `CalendarItem(String,Predicate<Date>)`

There will be many kinds of predicate; we probably want a sealed interface of record types, one of which can call out to a Tcl script.

- `IsDate(Date)`: Match this specific year, month, day
- `Annual(Month, Day)`: Match this date every year
- `Anniversary(Date)`: Match the same month and day every year after the first.
- `Moveable(Function<Year,YearDay>)`: Compute the date differently each year.
    - Need a way to relate ancillary days to this day (e.g., Ash Wednesday from Easter)

A better way to do it would be this:

- `CalendarItem(String, Function<Year,Optional<Date>>)`

A list of these items define the dates to be specially marked on the calendar.  Loop over the list, calling each item for the current year, to get the items for the current year.  

We could use the same list of sealed record types, actually; but the function would be, "Do we have this date this year? What is it?"

That would be better, as it could be computed once per year.