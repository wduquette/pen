package pen.calendars;

public record Week<E extends Enum<E>>(E[] weekDays, int offset) {
    public E day2weekDay(int day) {
        int ndx = (day + offset) % weekDays.length;
        return weekDays[ndx];
    }

    public int weekDay2day(E weekDay) {
        for (int i = 0; i < weekDays.length; i++) {
            if (weekDays[i] == weekDay) {
                return i + offset;
            }
        }
        throw new CalendarException(
            "Invalid weekday value: \"" + weekDay + "\"");
    }
}
