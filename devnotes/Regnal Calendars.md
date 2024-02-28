[[Home]] > [[Ideas]] > [[Calendars]] #idea 

A *regnal calendar* shares the weekly cycle of days and the yearly cycle of months with a more general calendar, but only applies from a given start date, which is also the new year, e.g., the years as reckoned from the coronation of King So-and-So. 

A regnal calendar has a start date and (ultimately) an end date. Fundamental days prior to the regnal epoch have no representation in the regnal calendar; and days following the coronation of the next king would be represented in the new monarch's calendar.

In a fictional or historical setting, we have the luxury of knowing when a regnal calendar was supplanted, and by what.  Thus, we can implement regnal calendars in several ways.

## Option A: Single Calendars

A regnal calendar is implemented as a `Calendar` that wraps a `BasicCalendar` and supplies a start date and an end date.  The start date is the new year's day of the regnal calendar; it begins year 1, whatever the month and day might be.

## Option B: Regnal Set

A regnal set is a set of named regnal era, with start dates.  The regnal set can:

- Return a `Calendar` for each regnal era
- Return the start and end dates for each regnal era
- Return a list of regnal eras for a given epoch day

This would allow a client to display, e.g., a tooltip showing all of the relevant dates, by regnal era (and basic calendar) for a given date.
