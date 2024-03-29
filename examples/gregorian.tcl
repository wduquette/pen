# Standard Days
weekday define sunday    -full "Sunday"    -short "Sun" -unambiguous "Su" -tiny "S"
weekday define monday    -full "Monday"    -short "Mon" -unambiguous "M"  -tiny "M"
weekday define tuesday   -full "Tuesday"   -short "Tue" -unambiguous "Tu" -tiny "T"
weekday define wednesday -full "Wednesday" -short "Wed" -unambiguous "W"  -tiny "W"
weekday define thursday  -full "Thursday"  -short "Thu" -unambiguous "Th" -tiny "T"
weekday define friday    -full "Friday"    -short "Fri" -unambiguous "F"  -tiny "F"
weekday define saturday  -full "Saturday"  -short "Sat" -unambiguous "Sa" -tiny "S"

# Standard Week
week define standard -offset 1 \
    -days {sunday monday tuesday wednesday thursday friday saturday}

# Eras
era define ad -full "Anno Domini"
era define bc -full "Before Christ"

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


# Gregorian Calendar

calendar basic gregorian \
    -era    ad            \
    -prior  bc            \
    -week   standard      \
    -months {january february march april may june july august september october november december}



