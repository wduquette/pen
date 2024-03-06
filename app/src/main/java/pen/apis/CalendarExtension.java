package pen.apis;

import pen.calendars.*;
import pen.calendars.Calendar;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import pen.tcl.TclExtension;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.util.*;

/**
 * A TclEngine extension for defining calendars.
 */
public class CalendarExtension implements TclExtension {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The TclEngine in use.  Set by initialize().
    private TclEngine tcl;

    // Data stores
    private final Map<String, Era> eras = new TreeMap<>();
    private final LinkedHashMap<String, Weekday> weekdays = new LinkedHashMap<>();
    private final LinkedHashMap<String, MonthInfo> months = new LinkedHashMap<>();
    private final Map<String, Week> weeks = new TreeMap<>();
    private final LinkedHashMap<String, Calendar> calendars = new LinkedHashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new TclEngine extension defining calendars
     */
    public CalendarExtension() {
        reset();
    }

    public void initialize(TclEngine tcl) {
        this.tcl = tcl;

        // Individual Commands
        tcl.add("isLeapYear", this::cmd_isLeapYear);
        tcl.add("februaryDays", this::cmd_februaryDays);

        // calendar *
        var cal = tcl.ensemble("calendar");

        cal.add("basic",  this::cmd_calendarBasic);
        cal.add("names",   this::cmd_calendarNames);

        // era *
        var era = tcl.ensemble("era");

        era.add("define",  this::cmd_eraDefine);
        era.add("names",   this::cmd_eraNames);

        // month *
        var month = tcl.ensemble("month");

        month.add("define",  this::cmd_monthDefine);
        month.add("names",   this::cmd_monthNames);

        // weekday *
        var weekday = tcl.ensemble("weekday");

        weekday.add("define",  this::cmd_weekdayDefine);
        weekday.add("names",   this::cmd_weekdayNames);

        // week *
        var week = tcl.ensemble("week");

        week.add("define",  this::cmd_weekDefine);
        week.add("names",   this::cmd_weekNames);
    }

    @SuppressWarnings("unused")
    public void reset() {
        eras.clear();
        weekdays.clear();
        months.clear();
        weeks.clear();
        calendars.clear();
    }

    public Map<String,Era>                 getEras()      { return eras; }
    public LinkedHashMap<String,Weekday>   getWeekdays()  { return weekdays; }
    public LinkedHashMap<String,MonthInfo> getMonths()    { return months; }
    public Map<String,Week>                getWeeks()     { return weeks; }
    public LinkedHashMap<String,Calendar>  getCalendars() { return calendars; }

    //-------------------------------------------------------------------------
    // Individual Commands

    // isLeapYear year
    //
    // Returns true if year is a leap year according to the Gregorian
    // calendar, and false otherwise.
    private void cmd_isLeapYear(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 1, 1, "year");

        var year = tcl.toInteger(argq.next());
        tcl.setResult(Gregorian.isLeapYear(year));
    }

    // februaryDays year
    //
    // Returns the number of days in February in the given year, according
    // to the Gregorian calendar.
    private void cmd_februaryDays(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 1, 1, "year");

        var year = tcl.toInteger(argq.next());
        tcl.setResult(Gregorian.februaryDays(year));
    }

    //-------------------------------------------------------------------------
    // Ensemble: calendar *

    // calendar basic symbol ?options...?
    private void cmd_calendarBasic(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "?option value...?");
        var symbol = argq.next().toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        // NEXT, get the details.
        var basic = new BasicCalendar.Builder();
        Argq monq = null;

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-offset":
                    basic.epochOffset(tcl.toInteger(opt, argq));
                    break;
                case "-era":
                    basic.era(tcl.toMapEntry("era", eras, opt, argq));
                    break;
                case "-prior":
                    basic.priorEra(tcl.toMapEntry("era", eras, opt, argq));
                    break;
                case "-week":
                    basic.week(tcl.toMapEntry("week", weeks, opt, argq));
                    break;
                case "-months":
                    monq = tcl.toArgq(opt, argq);
                    break;
                default:
                    throw tcl.unknownOption(opt);
            }
        }

        // TODO: Check required fields

        if (monq == null || !monq.hasNext()) {
            throw tcl.error("expected -months, but no months defined.");
        }

        while (monq.hasNext()) {
            var info = tcl.toMapEntry("month", months, monq.next());
            basic.month(info.month(), info.length);
        }

        calendars.put(symbol, basic.build());

        tcl.setResult(symbol);
    }

    // calendar names
    private void cmd_calendarNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(new ArrayList<>(calendars.keySet()));
    }

    //-------------------------------------------------------------------------
    // Ensemble: era *

    // era define symbol -short name -full name
    //
    private void cmd_eraDefine(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "?option value...?");
        var symbol = argq.next().toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        // NEXT, get the names
        var shortName = symbol.toUpperCase();
        var fullName = shortName;

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-short":
                    shortName = tcl.toString(opt, argq);
                    break;
                case "-full":
                    fullName = tcl.toString(opt, argq);
                    break;
                default:
                    throw tcl.unknownOption(opt);
            }
        }

        var era = new Era(shortName, fullName);
        eras.put(symbol, era);

        tcl.setResult(symbol);
    }

    // era names
    private void cmd_eraNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(new ArrayList<>(eras.keySet()));
    }


    //-------------------------------------------------------------------------
    // Ensemble: month *

    // month define symbol -short name -full name
    //
    private void cmd_monthDefine(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "?option value...?");
        var symbol = argq.next().toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        // NEXT, get the names
        var fullName = "";
        var shortName = "";
        var unambiguousName = "";
        var tinyName = "";
        YearDelta lengthFunc = null;

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-full":
                    fullName = tcl.toString(opt, argq);
                    break;
                case "-short":
                    shortName = tcl.toString(opt, argq);
                    break;
                case "-unambiguous":
                    unambiguousName = tcl.toString(opt, argq);
                    break;
                case "-tiny":
                    tinyName = tcl.toString(opt, argq);
                    break;
                case "-days":
                    lengthFunc = toMonthLength(opt, argq);
                    break;
                default:
                    throw tcl.unknownOption(opt);
            }
        }

        if (fullName.isEmpty()) { fullName = capitalize(symbol); }
        if (shortName.isEmpty()) { shortName = first(fullName, 3); }
        if (unambiguousName.isEmpty()) { unambiguousName = shortName; }
        if (tinyName.isEmpty()) { tinyName = first(fullName, 1); }
        if (lengthFunc == null) {
            throw tcl.error("Missing -days function");
        }

        var month = new SimpleMonth(
            fullName, shortName, unambiguousName, tinyName);
        var monthInfo = new MonthInfo(month, lengthFunc);
        months.put(symbol, monthInfo);

        tcl.setResult(symbol);
    }

    // month names
    private void cmd_monthNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(new ArrayList<>(months.keySet()));
    }

    //-------------------------------------------------------------------------
    // Ensemble: weekday *

    // weekday define symbol -short name -full name
    //
    private void cmd_weekdayDefine(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "?option value...?");
        var symbol = argq.next().toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        // NEXT, get the names
        var fullName = "";
        var shortName = "";
        var unambiguousName = "";
        var tinyName = "";

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-full":
                    fullName = tcl.toString(opt, argq);
                    break;
                case "-short":
                    shortName = tcl.toString(opt, argq);
                    break;
                case "-unambiguous":
                    unambiguousName = tcl.toString(opt, argq);
                    break;
                case "-tiny":
                    tinyName = tcl.toString(opt, argq);
                    break;
                default:
                    throw tcl.unknownOption(opt);
            }
        }

        if (fullName.isEmpty()) { fullName = capitalize(symbol); }
        if (shortName.isEmpty()) { shortName = first(fullName, 3); }
        if (unambiguousName.isEmpty()) { unambiguousName = shortName; }
        if (tinyName.isEmpty()) { tinyName = first(fullName, 1); }

        var weekday = new Weekday(
            fullName, shortName, unambiguousName, tinyName);
        weekdays.put(symbol, weekday);

        tcl.setResult(symbol);
    }

    // weekday names
    private void cmd_weekdayNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(new ArrayList<>(weekdays.keySet()));
    }

    //-------------------------------------------------------------------------
    // Ensemble: week *

    // week define symbol -offset days -days list
    //
    private void cmd_weekDefine(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "?option value...?");
        var symbol = argq.next().toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        // NEXT, get the names
        var offset = 0;
        Argq dayq = null;

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-offset":
                    offset = tcl.toInteger(opt, argq);
                    break;
                case "-days":
                    dayq = tcl.toArgq(opt, argq);
                    break;
                default:
                    throw tcl.unknownOption(opt);
            }
        }

        if (dayq == null || !dayq.hasNext()) {
            throw tcl.error("expected -days, but no days defined.");
        }

        List<Weekday> days = new ArrayList<>();
        while (dayq.hasNext()) {
            days.add(tcl.toMapEntry("weekday", weekdays, dayq.next()));
        }

        var week = new Week(days, offset);
        weeks.put(symbol, week);

        tcl.setResult(symbol);
    }

    // week names
    private void cmd_weekNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(new ArrayList<>(weeks.keySet()));
    }

    private YearDelta toMonthLength(TclObject arg)
        throws TclException
    {
        try {
            var days = Integer.parseInt(arg.toString());
            if (days < 1) {
                throw tcl.expected("Month length", arg);
            }
            return year -> days;
        } catch (IllegalArgumentException ex)  {
            // Nothing to do
        }
        var prefix = arg.toString();

        return year -> tclIntegerFunc(prefix, year);
    }

    private YearDelta toMonthLength(String opt, Argq argq)
        throws TclException
    {
        return toMonthLength(tcl.toOptArg(opt, argq));
    }

    private int tclIntegerFunc(String prefix, int input) {
        try {
            var command = prefix + " " + input;
            return tcl.toInteger(tcl.eval(command));
        } catch (TclException ex) {
            throw new CalendarException("Invalid Tcl command prefix: \"" +
                prefix + "\"", ex);
        }
    }

    private String first(String source, int numberOfChars) {
        if (source.length() >= numberOfChars) {
            return source.substring(0, numberOfChars);
        } else {
            return source;
        }
    }

    private String capitalize(String text) {
        var first = text.substring(0,1).toUpperCase();
        var rest = text.substring(1).toLowerCase();
        return first + rest;
    }

    //-------------------------------------------------------------------------
    // Records

    /**
     * Information needed to define a month in a calendar
     * @param month The month's name data
     * @param length The month's length-in-days function
     */
    public record MonthInfo(Month month, YearDelta length) { }
}
