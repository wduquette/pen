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

    public void clear() {
        entityMap().clear();
        incidents().clear();
        setMomentFormatter(null);
    }

    public Map<String,EntityType> getTypeMap() {
        return typeMap();
    }

    public void addEntityType(EntityType type) {
        typeMap().put(type.id(), type);
    }

    public Optional<EntityType> removeEntityType(String id) {
        return Optional.ofNullable(typeMap().remove(id));
    }

    public Optional<EntityType> getEntityType(String id) {
        return Optional.ofNullable(typeMap().get(id));
    }

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

