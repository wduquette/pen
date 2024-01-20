package pen.stencil;

/**
 * This is abstract base class for all Stencil shapes that have no particular
 * shared pattern.
 * @param <Self> The concrete shape class.
 */
public abstract class SimpleShape<Self extends SimpleShape<Self>>
    extends StyleBase<Self>
    implements Drawable
{
    //---------------------------------------------------------------------
    // Constructor

    public SimpleShape() {
        // Nothing to do
    }
}
