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

## Fundamental Calendars vs. Eras

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
