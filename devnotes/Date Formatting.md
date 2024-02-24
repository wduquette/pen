[[Home]] > [[Ideas]] > [[Calendars]]

Date formatting is similar to Java's `DataTimeFormatter`.


## Relevant Conversions

| Symbol | Meaning | Type | Example |
| ---- | ---- | ---- | ---- |
| `d` | day-of-month | number | `1` |
| `D` | day-of-year | number | `123` |
| `E` | era | text | `AD` |
| `m` | month-of-year | number | `12` |
| `M` | month-of-year | text | `JAN` |
| `W` | weekday | text | `Monday` |
| `y` | year-of-era | number | `2024` |
| `'text'` | Escape for arbitrary text | text | `'foo'` |
In `DateTimeFormatter`, the number of characters in a field has significance, depending on the data item and format.  `DateFormatter` will follow the same patterns, as reasonable.

**For Numbers:** a single character field (e.g., `y`) presents the number without padding.  Multiple characters enables padding (e.g., `yyyy`).  If the number is too long for the field, the whole number will be displayed.

**For Years**: `DateTimeFormatter` understands `yy` as a two-digit year starting at `2000`.  `DateFormatter` does not, as it's not at all clear what base century to use in general.

**For Text:** `DateTimeFormatter` understands four output `Forms`: `FULL`, `SHORT`, `UNAMBIGUOUS`, and `TINY`.

| Symbol | Full, Short, Unambigous, Tiny | Field |
| ---- | ---- | ---- |
| `E` | `Anno Domini`, `AD`, `AD`, `AD` | `EEEE`, `EEE`, `EE`, `E` |
| `M` | `January`, `Jan`, `Jan`, `J` | `MMMM`, `MMM`, `MM`, `M`  |
| `W` | `Tuesday`, `Tue`, `Tu`, `T` | `WWWW`, `WWW`, `WW`, `W` |
This scheme is somewhat simpler than `DateTimeFormatter`'s.

- One character gives the `TINY` form, which is usually the initial character of the name.
- Two characters gives the `UNAMBIGUOUS` form, which is the shortest form that makes every name unambiguous, e.g., `Tu` vs. `Th` for days.
- Three characters gives the `SHORT` form, which is usually the first three characters of the name with an initial cap.
- Four or more characters gives the `FULL` form, the entire name.

The examples shown in the table above reflect standard abbreviations for the Gregorian calendar; each fictional calendar can define these forms as it likes.

## DateFormatter/Calendar Relationship

At present each `DateFormatter` wraps a calendar, and format strings cannot be validated until the calendar exists.  But we often want to define a format string as part of building the calendar.  Chicken and egg.

I've finessed this for the moment: 

- The builder accepts a format string.
- The Calendar retains the format string.
- The `BasicCalendar::formatter()` method builds the formatter in a lazy fashion.
- The builder calls `formatter()` right after building the Calendar, so that any error appears immediately.

But this is clunky.

Another approach: 

- The `DateFormatter` is calendar independent.
- The `DateFormatter` remembers whether it needs weeks and/or months.
- A `DateFormatter` can be associated with a Calendar when the Calendar is constructed; it's an error if it needs weeks/months and the Calendar doesn't have them.
- A `DateFormatter` requires a Calendar to format; it assumes that the calendar has what's needed.  Throws an error on format if it doesn't.
- The check on format is incidental.

Yet another approach:

- Calendars don't provide formatting/parsing.
- The client has to create their own formatter, when they need it.
- No chicken and egg in this case.

Think about this.