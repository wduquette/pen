[[Home]] > [[Ideas]] #idea 

Goal: a way to represent fantasy calendars in Java code, providing easy conversion between calendars, date formatting and parsing, and so forth.

## Next Steps

- [x] Ponder how to associate weeks with calendars.
- [x] Ponder the date formatting/parsing API
- [x] Implement the  date formatting code
- [ ] Implement the date parsing code.
- [ ] Ponder the Tcl API for defining and managing calendars

## Needs

- `Calendar` interface
    - Purpose
        - So that various components can use a calendar without knowing any of the details.
    - The `Calendar`
        - Supports calendars lacking weeks and/or months.
        - Provides conversion between epoch days, `Dates`, and `YearDays`.
        - Provides all support needed for the date formatting code
        - Provides easy look-up of era, month, and weekday names in various formats
- `TrivialCalendar`  class
    - Purpose
        - A simple calendar for use in circumstances where only relative dates matter.
        - A basis for conversion between other calendars
    - The calendar provides
        - Standard `Calendar` operations
        - Support for variable year lengths, i.e., leap-year-like patterns
        - Optional support for a `Week` cycle.
- `BasicCalendar`
    - Purpose
        - A calendar with months of variable lengths, for use when actual dates are required.
    - The calendar provides
        - Standard `Calendar` operations
        - An epoch offset, so that `0001-01-01` need not be day 0.
        - Support for variable-length months, i.e., leap-year-like patterns 
        - Optional support for a `Week` cycle.
- `Week`
    - Purpose
        - A cycle of week days
    - The class should provide
        - An epoch offset, so that day 0 need not be the first day of the week.
        - Conversion from epoch days to weekdays
        - Easy look-up of weekday names in various formats
- [ ] `DateFormatter` 
    - Purpose
        - High-quality pattern-based [[Date Formatting]] and parsing
        - For use in drawing timelines, calendars, etc.
        - Similar to Java's standard `DateTimeFormatter`
    - [x] Formatting
    - [ ] Parsing
- [ ] `EraCalendar`
    - Purpose
        - Support for [[Regnal Calendars]] and similar eras
    - The calendar should
        - [ ] Be based on an underlying `BasicCalendar`
        - [ ] Support an arbitrary new year's day
        - [ ] Provide all standard `Calendar` operations
- [ ] Yearly cycle (no design yet)
    - [ ] Cycle of year names, e.g., "Year of the Dragon"
    - [ ] Epoch offset, so that day 0 need not be the first day of the first year in the cycle.
    - [ ] Ability to determine the number of years of a given name since the epoch offset, e.g., The 717th Year of the Dragon

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
