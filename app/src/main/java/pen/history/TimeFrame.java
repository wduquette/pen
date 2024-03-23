package pen.history;

public record TimeFrame(
    int start,
    int end
) {
    public static TimeFrame of(int start, int end) {
        return new TimeFrame(start, end);
    }
}
