[[Home]] > [[Ideas]] #idea 

Goal: a way to represent fantasy calendars in Java code, providing easy conversion between calendars, date formatting and parsing, and so forth.

## Next Steps

- [ ] Ponder how to associate weeks with calendars.
- [ ] Ponder the date formatting/parsing API
- [ ] Ponder the Tcl API for defining and managing calendars

## Needs

- [ ] A general `Calendar` interface
    - Purpose
        - So that various components can use a calendar without knowing any of the details.
    - The `Calendar` should
        - [ ] Convert fundamental days to date strings
        - [ ] Parse date strings yielding fundamental days
        - [ ] Provide all support needed for the date formatting code
        - [ ] TBD: see [[#The `Calendar` API]].
- [x] `FundamentalCalendar` 
    - Purpose
        - A simple calendar for use in circumstances where only relative dates matter.
        - A basis for conversion between other calendars
    - The calendar should provide
        - [x] A fundamental epoch day 0
        - [x] Conversion between fundamental days and year/day-of-year
        - [x] Support for leap-year-like patterns
        - [x] Dirt simple date formatting and parsing, for use with a Tcl extension
        - [ ] A week
- [ ] `SimpleCalendar`
    - Purpose
        - A calendar with months of variable lengths, for use when actually dates are required.
    - The calendar should provide
        - [x] Conversion to `FundamentalCalendar` days
        - [x] Support for leap-year-like patterns, month by month
        - [x] Conversion between fundamental days and year/month-of-year/day-of-month
        - [x] Simple date formatting and parsing, for use with a Tcl extension
        - [ ] Easy look-up of month names in various formats
        - [ ] A week
- [ ] `Week`
    - Purpose
        - A cycle of week days, tied to the `FundamentalCalendar`'s epoch day.
    - The class should provide
        - [x] Conversion from a fundamental day to a day of the week.
        - [x] Access to the list of week days and the epoch day offset into the list.
        - [ ] Easy look-up of day names in various formats
- [ ] `EraCalendar`
    - Purpose
        - Support for [[Regnal Calendars]] and similar eras
    - The calendar should
        - [ ] Be based on an underlying simple calendar.
        - [ ] Convert between fundamental days and calendar dates
        - [ ] Support an arbitrary new year's day
- [ ] High-quality pattern-based date formatting and parsing
    - Purpose
        - For use in drawing timelines, calendars, etc.
    - Ideally, the patterns should be identical to those used by Java's `DateTimeFormatter`.
        - Insofar as they make sense in this context. 

## The `Calendar` API

Some important questions:

- **Q**: what methods must the `Calendar` interface provide?
- **Q**: Do we need two calendar interfaces, a dirt simple interface, and a fancier interface?

At least we need:

- `String formatDate(fundamentalDay)`
- `int parseDate(dateString)`
 
## Initial Concept 

I'm looking here for the simplest way to represent fantasy calendars in Java code.

Here's what I think I need:

- Time representation
    - Days since fundamental epoch.
- Fundamental calendar
    - A very simple calendar, probably not used in setting
    - Dates represented by year/day-of-year
    - Epoch is the first day of year 0.
    - Can represent any date unambiguously.
    - Possibly includes function to specify the number of days in the year.
    - Includes a weekly cycle of days.
    - Basically a set of functions:
        - Day to year/day-of-year
        - year/day-of-year to year
        - Day to formatted string
        - Formatted string to day
- Eras
    - An era is a dependent calendar.
    - The era's epoch is a date on the fundamental calendar.
    - The era may define
        - A cycle of months of various lengths
        - Era symbol, e.g., AD, BC

### Fundamental Calendars vs. Eras

What kinds of variation do I want to see in setting?

- Typically I want a range of "eras" all rooted in the same basic calendar
    - Same cycle of week days (day names can vary)
    - Same months and month lengths (month names can vary)
    - Different new year's day and reckoning of years, e.g., coronation of King So-and-So.
    - Here I'm translating the years only, based on the date.
- Sometimes I want want two distinct calendars—different month lengths, etc.

So what I need is this:

- A fundamental calendar, for translating between calendars.
- A well-behaved monthly calendar tied to the fundamental calendar
- Era calendars modifying the well-behaved monthly calendar.
