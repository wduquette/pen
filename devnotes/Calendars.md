[[Home]] > [[Ideas]] #idea 

**Status:** I've implemented a basic `FundamentalCalendar` class, and started writing tests.

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
