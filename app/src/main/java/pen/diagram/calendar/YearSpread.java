package pen.diagram.calendar;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.text.FontWeight;
import pen.calendars.Calendar;
import pen.stencil.*;

import java.util.ArrayList;
import java.util.List;

import static pen.stencil.Stencil.text;

/**
 * A shape for drawing one year for a particular calendar, including the
 * title (e.g., the year and era) and a rectangular array of month spreads
 */
@SuppressWarnings("unused")
public class YearSpread extends ContentShape<YearSpread> {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The calendar we are drawing months and years for.
    private Calendar calendar;

    // The year
    private int year = 1;

    // The title, e.g., 2024 AD
    private String title;

    // Padding between a title and what it titles
    private double titlePad = 10;

    // Padding between a month and its title
    private double monthTitlePad = 10;

    // Padding between months
    private double monthPad = 20;

    // Padding between dates in a month spread
    private double datePad = 5;

    // Number of months across
    private int columns = 3;

    // TODO: Add getters and setters
    private PenFont titleFont = new PenFont.Builder("title")
        .family("sans-serif").weight(FontWeight.BOLD).size(18).build();
    private PenFont monthFont = new PenFont.Builder("month")
        .family("sans-serif").weight(FontWeight.BOLD).size(14).build();
    private PenFont dayFont = new PenFont.Builder("day")
        .family("sans-serif").weight(FontWeight.BOLD).size(12).build();
    private PenFont dateFont = PenFont.SANS12;

    //-------------------------------------------------------------------------
    // Constructor

    public YearSpread() {
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

    public String getTitle() {
        return title;
    }

    public double getTitlePad() {
        return titlePad;
    }

    public double getMonthPad() {
        return monthPad;
    }

    public double getDatePad() {
        return datePad;
    }

    public int getColumns() {
        return columns;
    }

    //-------------------------------------------------------------------------
    // DSL

    public YearSpread calendar(Calendar calendar) {
        if (!calendar.hasMonths()) {
            throw Calendar.noMonthlyCycle();
        }
        if (!calendar.hasWeeks()) {
            throw Calendar.noWeeklyCycle();
        }
        this.calendar = calendar;
        return this;
    }

    public YearSpread year(int year) {
        // TODO: validate year
        this.year = year;
        return this;
    }

    public YearSpread title(String title) {
        this.title = title;
        return this;
    }

    public YearSpread titlePad(double value) {
        this.titlePad = value;
        return this;
    }

    public YearSpread monthTitlePad(double value) {
        this.monthTitlePad = value;
        return this;
    }

    public YearSpread monthPad(double value) {
        this.monthPad = value;
        return this;
    }

    public YearSpread datePad(double value) {
        this.datePad = value;
        return this;
    }

    public YearSpread columns(int columns) {
        this.columns = columns;
        return this;
    }

    //-------------------------------------------------------------------------
    // Helpers

    private List<MonthSpread> getMonths() {
        var list = new ArrayList<MonthSpread>();
        for (int i = 1; i <= calendar.monthsInYear(); i++) {
            list.add(getMonth(i));
        }
        return list;
    }

    private MonthSpread getMonth(int monthOfYear) {
        return new MonthSpread()
            .calendar(calendar)
            .year(year)
            .monthOfYear(monthOfYear)
            .title(calendar.month(monthOfYear).fullForm())
            .titlePad(monthTitlePad)
            .datePad(datePad)
            // TODO: Set fonts!
            ;
    }

    private int getRows() {
        var monthCount = calendar.monthsInYear();
        return (monthCount % columns == 0)
            ? monthCount/columns
            : 1 + monthCount/columns;
    }

    private double getMonthWidth(List<MonthSpread> months) {
        assert !months.isEmpty();
        return months.getFirst().getRealSize().getWidth();
    }

    private double getMonthHeight(List<MonthSpread> months) {
        return months.stream()
            .mapToDouble(m -> m.getRealSize().getHeight())
            .max().orElse(0);
    }

    //-------------------------------------------------------------------------
    // ContentShape methods

    public Dimension2D getRealSize() {
        var months = getMonths();
        var rows = getRows();
        var monthWidth = getMonthWidth(months);
        var monthHeight = getMonthHeight(months);

        var w = columns*monthWidth + (columns - 1)*monthPad;
        var h = titleFont.getHeight() + titlePad
            + rows*monthHeight + (rows - 1)*monthPad;
        return new Dimension2D(w, h);
    }

    @Override
    public Bounds draw(Stencil sten) {
        // FIRST, draw the title
        var bounds = getRealBounds();
        sten.draw(text()
            .at(bounds.getCenterX(), bounds.getMinY())
            .text(title)
            .font(titleFont)
            .tack(Tack.NORTH)
        );

        // NEXT, draw the months
        var x0 = bounds.getMinX();
        var y0 = bounds.getMinY() + titleFont.getHeight() + titlePad;
        var months = getMonths();
        var rows = getRows();
        var monthWidth = getMonthWidth(months);
        var monthHeight = getMonthHeight(months);

        for (var j = 0; j < rows; j++) {
            var y = y0 + j*(monthHeight + monthPad);

            for (var i = 0; i < columns; i++) {
                var x = x0 + i*(monthWidth + monthPad);
                var ndx = j*columns + i;

                if (ndx < months.size()) {
                    sten.draw(months.get(ndx).at(x, y));
                }
            }
        }

        return getBounds();
    }
}
