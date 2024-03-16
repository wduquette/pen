package pen.diagram.timeline;

import pen.history.Cap;

@SuppressWarnings("unused")
public sealed interface Limit
    permits Limit.Fixed, Limit.EventBased, Limit.External
{
    Cap cap();

    record Fixed(Cap cap, int day) implements Limit {}
    record EventBased(Cap cap)   implements Limit {}
    record External(Cap cap)     implements Limit {}

    static Limit fixed(int day)          { return new Fixed(Cap.HARD, day); }
    static Limit fixed(Cap cap, int day) { return new Fixed(cap, day); }
    static Limit eventBased()            { return new EventBased(Cap.HARD); }
    static Limit eventBased(Cap cap)     { return new EventBased(cap); }
    static Limit external()              { return new External(Cap.SOFT);  }
}
