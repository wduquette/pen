package pen.history2;

import java.util.ArrayList;
import java.util.List;

/** Information about a specific person. */
@SuppressWarnings("unused")
public class Person {

    //-------------------------------------------------------------------------
    // Instance Variables

    private final Entity.Person entity;

    // The location could also be represented as a table(placeID, personID)
    private Entity.Place location;

    // The group list could also be represented as a table(groupID, personID),
    // and possibly should be.
    private final List<Entity.Group> groups = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public Person(Entity.Person entity) {
        this.entity = entity;
    }

    //-------------------------------------------------------------------------
    // Accessors

    // Read-only property
    public Entity getEntity() {
        return entity;
    }

    // Read-only property
    public String getId() {
        return entity.id();
    }

    // Read-only property
    public String getName() {
        return entity.name();
    }

    // Read-write property: unless handled as relation
    public Entity.Place getLocation() {
        return location;
    }

    // Read-write property: unless handled as relation
    public void setLocation(Entity.Place value) {
        this.location = value;
    }

    // Read/write property: unless handled as relation
    public List<Entity.Group> groups() {
        return groups;
    }
}
