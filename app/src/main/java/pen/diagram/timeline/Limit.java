package pen.diagram.timeline;

public sealed interface Limit
    permits Limit.Fixed, Limit.EventBased, Limit.External, Limit.Fuzzy
{
    record Fixed(int day) implements Limit {}
    record EventBased()   implements Limit {}
    record External()     implements Limit {}
    record Fuzzy(int day) implements Limit {}

    static Limit fixed(int day) { return new Fixed(day); }
    static Limit eventBased()   { return new EventBased(); }
    static Limit external()     { return new External();  }
    static Limit fuzzy(int day) { return new Fuzzy(day); }
}
