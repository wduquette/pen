[[Home]] > [[Ideas]] > [[Calendars]]

This page is for pondering what the calendar definition APIs should look like.  These are tricky, because there's nesting involved.  That's easy in Java, less easy in Tcl.

## Topics

- [ ] `Week` definition API
- [ ] `FundamentalCalendar` definition API
- [ ] `SimpleCalendar` definition API

## Principles

- Calendars are not mutable.

## Questions

Some possible terminology changes

- Replace the phrases *fundamental calendar* and *fundamental day* with *epoch calendar* and *epoch day*.
    - The "epoch calendar" is the calendar that defines the epoch day 0 used as the basis for conversions in a given set of calendars.
- The `FundamentalCalendar` class gets renamed.
    - It isn't actually needed to define the epoch day 0.
    - Not sure what to call it. 
    - It's useful when all you need are days and maybe years.
    - Maybe `BasicCalendar` or `TrivialCalendar`
- The `SimpleCalendar` probably needs to be renamed as well.
    - `Month` calendar?
- The `SimpleCalendar`'s `epochDay` should be `epochOffset`.

## Week Definition API

- A `Week` is defined by
    - A sequence of N weekdays.
    - An `offset` relative to the fundamental day, i.e., day 0 might not be the first weekday.
- A `Weekday` is defined by
    - Its place in the sequence
    - A full name, e.g. `Thursday`
    - A short name, e.g., `Thu`
    - A narrow name, e.g., `T` or `Th`
    - The narrowest name, e.g., `T`
- A set of `Calendars` could use distinct `Weeks`, so we need to support multiple simultaneous weeks.

**Q:** Standalone Week objects or a `week` manager object?
- We use manager objects for styles; it's a familiar pattern.

```tcl
# Create week given individual arguments
week create standard \
    -offset 2
    -day {Sunday    Sun Su S} \
    -day {Monday    Mon M  M} \
    -day {Tuesday   Tue Tu T} \
    -day {Wednesday Wed W  W} \
    -day {Thursday  Thu Th T} \
    -day {Friday    Fri F  F} \
    -day {Saturday  Sat Sa S}

# Create week given list
week create standard {
    -offset 2
    -day {Sunday    Sun Su S}
    -day {Monday    Mon M  M}
    -day {Tuesday   Tue Tu T}
    -day {Wednesday Wed W  W}
    -day {Thursday  Thu Th T}
    -day {Friday    Fri F  F}
    -day {Saturday  Sat Sa S}
}

# Get names of defined weeks
puts [week names]

## Get details: returns definition list
week cget standard
```

## FundamentalCalendar Definition API

The `FundamentalCalendar` is defined by

- The `era` and `priorEra`
- The `Week`
- The length of the year
- At present the number of digits for dayOfYear, but that will go away.

**Q:** How to represent the length-of-year function?

- A constant integer
- A Tcl function that takes one argument and returns the length of the year.
    - Haven't done this in JTcl

```tcl
calendar create fun {
    -type fundamental
    -era   {FE "Fundamental Era"} 
    -prior {BFE "Before Fundamental Era"}
    -daysinyear 365
}

proc leapYearDays {year} {
    ...
    return $days
}

calendar create fun {
    -type fundamental
    -era   {FE "Fundamental Era"} 
    -prior {BFE "Before Fundamental Era"}
    -daysinyear leapYearDays
}
```





