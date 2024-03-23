package pen.history;

import java.util.*;

@SuppressWarnings("unused")
public class HistoryBank
    extends AbstractHistory implements History
{
    //-------------------------------------------------------------------------
    // Constructor

    public HistoryBank() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Accessors


    public Map<String,Entity> getEntityMap() {
        return entityMap();
    }

    public void addEntity(Entity entity) {
        entityMap().put(entity.id(), entity);
    }

    public Optional<Entity> removeEntity(String id) {
        return Optional.ofNullable(entityMap().remove(id));
    }

    public Optional<Entity> getEntity(String id) {
        return Optional.ofNullable(entityMap().get(id));
    }

    @Override
    public List<Incident> getIncidents() {
        return incidents();
    }
}

