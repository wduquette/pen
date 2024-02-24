[[Home]] > [[Ideas]] > [[Calendars]]

Date formatting is similar to Java's `DataTimeFormatter`, though the format conversion characters differ.

## Relevant Conversions

| Symbol   | Meaning                   | Type   | Example  |
| -------- | ------------------------- | ------ | -------- |
| `d`      | day-of-month              | number | `1`      |
| `D`      | day-of-year               | number | `123`    |
| `E`      | era                       | text   | `AD`     |
| `m`      | month-of-year             | number | `12`     |
| `M`      | month-of-year             | text   | `JAN`    |
| `W`      | weekday                   | text   | `Monday` |
| `y`      | year-of-era               | number | `2024`   |
| `'text'` | Escape for arbitrary text | text   | `'foo'`  |
In `DateTimeFormatter`, the number of characters in a field has significance, depending on the data item and format.  `DateFormatter` will follow the same patterns, as reasonable.

**For Numbers:** a single character field (e.g., `y`) presents the number without padding.  Multiple characters enables padding (e.g., `yyyy`).  If the number is too long for the field, the whole number will be displayed.

**For Years**: `DateTimeFormatter` understands `yy` as a two-digit year starting at `2000`.  `DateFormatter` does not, as it's not at all clear what base century to use in general.

**For Text:** `DateTimeFormatter` understands four output `Forms`: `FULL`, `SHORT`, `UNAMBIGUOUS`, and `TINY`.

| Symbol | Full, Short, Unambigous, Tiny   | Field                    |
| ------ | ------------------------------- | ------------------------ |
| `E`    | `Anno Domini`, `AD`, `AD`, `AD` | `EEEE`, `EEE`, `EE`, `E` |
| `M`    | `January`, `Jan`, `Jan`, `J`    | `MMMM`, `MMM`, `MM`, `M` |
| `W`    | `Tuesday`, `Tue`, `Tu`, `T`     | `WWWW`, `WWW`, `WW`, `W` |
This scheme is somewhat simpler than `DateTimeFormatter`'s.

- One character gives the `TINY` form, which is usually the initial character of the name.
- Two characters gives the `UNAMBIGUOUS` form, which is the shortest form that makes every name unambiguous, e.g., `Tu` vs. `Th` for days.
- Three characters gives the `SHORT` form, which is usually the first three characters of the name with an initial cap.
- Four or more characters gives the `FULL` form, the entire name.

The examples shown in the table above reflect standard abbreviations for the Gregorian calendar; each fictional calendar can define these forms as it likes.

## DateFormatter/Calendar Relationship

`Calendars` and `DateFormatters` are loosely coupled.

- An instance of `DateFormatter` isn't tied to a specific `Calendar`
- The `DateFormatter` remembers whether it needs weeks and/or months, and can check whether it is compatible with a given `Calendar`.
- All forms of `DateFormatter::format` takes a `Calendar` as the first argument.
- `Calendars` do not have a built-in formatter.

It's very tempting to give calendars built-in formatters, but it leads to chicken-and-egg definition problems.  The above design was reached after a great deal of floundering.
