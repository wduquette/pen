[[Home]] > [[Ideas]] > [[Calendars]]

Date formatting is similar to Java's `DataTimeFormatter`.

## Relevant Conversions

| Symbol | Meaning | Type | Example |
| ---- | ---- | ---- | ---- |
| `E` | era | text | `AD` |
| `y` | year-of-era | number | `2024` |
| `D` | day-of-year | number | `123` |
| `m` | month-of-year | text | `12` |
| `M` | month-of-year | text | `JAN` |
| `d` | day-of-month | number | `1` |
| `w` | day-of-week | text | `Monday` |
| `'text'` | Escape for arbitrary text | text | `'foo'` |
In `DateTimeFormatter`, the number of characters in a field has significance, depending on the data item and format.  `DateFormatter` will follow the same patterns, as reasonable.

**For Numbers:** a single character field (e.g., `y`) presents the number without padding.  Multiple characters enables padding (e.g., `yyyy`).  If the number is too long for the field, the whole number will be displayed.

**For Years**: `DateTimeFormatter` understands `yy` as a two-digit year starting at `2000`.  `DateFormatter` does not, as it's not at all clear what base year to use in general.

**For Text:** `DateTimeFormatter` understands several output types for text fields: narrow, short, full.

| Symbol | Narrow, Short, Full | Field |
| ---- | ---- | ---- |
| `G` | `AD`, `AD`, `Anno Domini` | `G`, `GGG`, `GGGG` |
| `L` | `J`, `Jan`, `January` | `L`,`LLL`,`LLLL` |
| `E` | `M`, `Mon`, `Monday` | `E`, `EEE`, `EEEE` |
This scheme is somewhat simpler than `DateTimeFormatter`'s.

- One character gives the "narrow" form, which is usually the initial character of the name.
- Two or three characters gives the "short" form, which is usually the first three characters of the name with an initial cap.
- Four or more characters gives the "full" form, the entire name.

