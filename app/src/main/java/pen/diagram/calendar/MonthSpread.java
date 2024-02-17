package pen.diagram.calendar;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.text.FontWeight;
import pen.calendars.Calendar;
import pen.stencil.*;

import static pen.stencil.Stencil.rectangle;
import static pen.stencil.Stencil.text;

public class MonthSpread extends ContentShape<MonthSpread> {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The calendar we are drawing months and years for.
    private Calendar calendar;

    // The year and month-of-year, 1 to N
    private int year = 1;
    private int monthOfYear = 1;

    // The title, e.g., January
    private String title;

    // Padding between a title and what it titles
    private double titlePad = 10;

    // Padding between dates in a monthly layout
    private double datePad = 5;

    // TODO: Add getters and setters
    private PenFont titleFont = new PenFont.Builder("title")
        .family("sans-serif").weight(FontWeight.BOLD).size(14).build();
    private PenFont dayFont = new PenFont.Builder("day")
        .family("sans-serif").weight(FontWeight.BOLD).size(12).build();
    private PenFont dateFont = PenFont.SANS12;

    //-------------------------------------------------------------------------
    // Constructor

    public MonthSpread() {
        // Nothing to do yet.
    }

    //-------------------------------------------------------------------------
    // Getters

    public Calendar getCalendar() {
        return calendar;
    }

    public int getYear() {
        return year;
    }

    public int monthOfYear() {
        return monthOfYear;
    }

    public String getTitle() {
        return title;
    }

    public double getTitlePad() {
        return titlePad;
    }

    public double getDatePad() {
        return datePad;
    }

    //-------------------------------------------------------------------------
    // DSL

    public MonthSpread calendar(Calendar calendar) {
        if (!calendar.hasWeeks()) {
            // TODO: Provide helper
            throw new UnsupportedOperationException("Calendar lacks a weekly cycle.");
        }
        this.calendar = calendar;
        return this;
    }

    public MonthSpread year(int year) {
        // TODO: validate year
        this.year = year;
        return this;
    }

    public MonthSpread monthOfYear(int monthOfYear) {
        // TODO: validate month
        this.monthOfYear = monthOfYear;
        return this;
    }

    public MonthSpread title(String title) {
        this.title = title;
        return this;
    }

    public MonthSpread titlePad(double value) {
        this.titlePad = value;
        return this;
    }

    public MonthSpread datePad(double value) {
        this.datePad = value;
        return this;
    }

    //-------------------------------------------------------------------------
    // Helpers

    private int weeksToDraw() {
        var date = calendar.date(year, monthOfYear, 1);
        var daysInMonth = date.daysInMonth();
        var daysInWeek = calendar.daysInWeek();
        var startDayOfWeek = date.dayOfWeek();

        int startDate = 1 - (startDayOfWeek - 1);
        int numberOfWeeks = 0;
        do {
            ++numberOfWeeks;
            startDate += daysInWeek;
        } while (startDate < daysInMonth);

        return numberOfWeeks;
    }

    //-------------------------------------------------------------------------
    // ContentShape methods

    @Override
    public Dimension2D getSize() {
        var dateWidth = Pen.getTextWidth(dateFont, "99");
        var w = calendar.daysInWeek()*(datePad + dateWidth) - datePad;
        var h = titleFont.getHeight() + titlePad
            + dayFont.getHeight()
            + weeksToDraw()*(dateFont.getHeight() + datePad);

        return new Dimension2D(
            Math.max(minWidth, w),
            Math.max(minHeight, h)
        );
    }

    @Override
    public Bounds draw(Stencil sten) {
        var bounds = getBounds();
        System.out.println("at     =" + getAt());
        System.out.println("size   =" + getSize());
        System.out.println("bounds =" + bounds);
        sten.draw(rectangle()
            .at(getAt())
            .size(bounds.getWidth(), bounds.getHeight())
        );

        // FIRST, draw the title
        sten.draw(text()
            .at(bounds.getCenterX(), bounds.getMinY())
            .text(title)
            .font(titleFont)
            .tack(Tack.NORTH)
        );

        // NEXT, draw the days.
        var daysInWeek = calendar.daysInWeek();
        var dateWidth = Pen.getTextWidth(dateFont, "99");
        var x = bounds.getMinX();
        var y = bounds.getMinY() + titleFont.getHeight() + titlePad;

        for (var i = 0; i < daysInWeek; i++) {
            var dx = x + dateWidth + i*(dateWidth + datePad);
            sten.draw(text()
                .at(dx, y)
                .text(calendar.week().weekdays().get(i).narrowForm())
                .tack(Tack.NORTHEAST)
                .font(dayFont)
            );
        }

        // NEXT, draw the dates
        var numWeeks = weeksToDraw();
        var dayHeight = dayFont.getHeight();
        var dateHeight = dateFont.getHeight();
        var date = calendar.date(year, monthOfYear, 1);
        var daysInMonth = date.daysInMonth();
        var startDayOfWeek = date.dayOfWeek();
        int startDate = 1 - (startDayOfWeek - 1);

        y += dayHeight + datePad;

        for (int w = 0; w < numWeeks; w++) {
            var dy = y + w*(dateHeight + datePad);
            for (int i = 0; i < daysInWeek; i++) {
                var dx = x + dateWidth + i*(dateWidth + datePad);

                var dayOfMonth = startDate + w*daysInWeek + i;
                if (dayOfMonth < 1 || dayOfMonth > daysInMonth) {
                    continue;
                }
                sten.draw(text()
                    .at(dx, dy)
                    .text(Integer.toString(dayOfMonth))
                    .tack(Tack.NORTHEAST)
                    .font(dateFont)
                );
            }
        }



        return null;
    }
}
