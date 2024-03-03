[[Home]] > [[Ideas]] > [[Calendars]]

This page is for pondering what the calendar definition APIs should look like.  These are tricky, because there's nesting involved.  That's easy in Java, less easy in Tcl.

**Status**: Use the [[#Alternative Approach]]; it's more flexible.
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
week details standard
```

## TrivialCalendar Definition API

The `TrivialCalendar` is defined by

- The `era` and `priorEra`
- The `Week`
- The length of the year

**Q:** How to represent the length-of-year function?

- A constant integer
- A Tcl function that takes one argument and returns the length of the year.
    - Haven't done this in JTcl

```tcl
calendar create triv {
    -type trivial
    -era   {TE "Trivial Era"} 
    -prior {BTE "Before Trivial Era"}
    -week standardWeek
    -daysinyear 365
}

proc leapYearDays {year} {
    ...
    return $days
}

calendar create triv {
    -type       trivial
    -era        {TE "Trivial Era"} 
    -prior      {BTE "Before Trivial Era"}
    -week       standardWeek
    -daysinyear leapYearDays
}
```

## BasicCalendar Definition API

The `BasicCalendar` is defined by

- The `era` and `priorEra`
- The `epochOffset`
- The months
- The `Week`

```tcl
calendar create gregorian {
    -type   basic
    -offset 12345
    -era    {AD "Anno Domini"} 
    -prior  {BC "Before Christ"}
    -week   standardWeek
    -month {
        -name {"January" "Jan" "Jan" "J"}
        -days 31
    }
    -month {
        -name {"February" "Feb" "Feb" "F"}
        -days februaryDays
    }
    ...
}

proc februaryDays {year} {
    ...
    return $days
}
```

## Alternative Approach

```tcl
# Standard Days
day define sunday    -full "Sunday"    -short "Sun" -unambiguous "Su" -tiny "S"
day define monday    -full "Monday"    -short "Mon" -unambiguous "M"  -tiny "M"
day define tuesday   -full "Tuesday"   -short "Tue" -unambiguous "Tu" -tiny "T"
day define wednesday -full "Wednesday" -short "Wed" -unambiguous "W"  -tiny "W"
day define thursday  -full "Thursday"  -short "Thu" -unambiguous "Th" -tiny "T"
day define friday    -full "Friday"    -short "Fri" -unambiguous "F"  -tiny "F"
day define saturday  -full "Saturday"  -short "Sat" -unambiguous "Sa" -tiny "S"

# Standard Week
week define standard -offset 2 \
    -days {sunday monday tuesday wednesday thursday friday saturday}

# Standard Months
month define january   -days 31           -full "January"   -short "Jan" -unambiguous "Jan" -tiny "J"
month define february  -days februaryDays -full "February"  -short "Feb" -unambiguous "Feb" -tiny "F"
month define march     -days 31           -full "March"     -short "Mar" -unambiguous "Mar" -tiny "M"
month define april     -days 30           -full "April"     -short "Apr" -unambiguous "Apr" -tiny "A"
month define may       -days 31           -full "May"       -short "May" -unambiguous "May" -tiny "M"
month define june      -days 30           -full "June"      -short "Jun" -unambiguous "Jun" -tiny "J"
month define july      -days 31           -full "July"      -short "Jul" -unambiguous "Jul" -tiny "J"
month define august    -days 31           -full "August"    -short "Aug" -unambiguous "Aug" -tiny "A"
month define september -days 30           -full "September" -short "Sep" -unambiguous "Sep" -tiny "S" 
month define october   -days 31           -full "October"   -short "Oct" -unambiguous "Oct" -tiny "O" 
month define november  -days 30           -full "November"  -short "Nov" -unambiguous "Nov" -tiny "N" 
month define december  -days 31           -full "December"  -short "Dec" -unambiguous "Dec" -tiny "D" 

# Eras
era define ad -full "Anno Domini"
era define bc -full "Before Christ"

# Gregorian Calendar
calendar define gregorian \
    -type   basic         \
    -offset 12345         \
    -era    ad            \
    -prior  bc            \
    -week   standard      \
    -months {january february march april may june july august september october november december}
```








