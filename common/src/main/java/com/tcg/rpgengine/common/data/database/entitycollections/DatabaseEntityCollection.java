package com.tcg.rpgengine.common.data.database.entitycollections;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.Entity;
import com.tcg.rpgengine.common.data.JSONCollection;
import org.json.JSONArray;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class DatabaseEntityCollection<E extends Entity> implements JSONCollection, BinaryDocument {

    private final Map<UUID, E> table;
    private final Map<UUID, Integer> referenceCount;
    private final List<ForeignKey<?>> foreignKeys;

    protected DatabaseEntityCollection() {
        this.table = new HashMap<>();
        this.referenceCount = new HashMap<>();
        this.foreignKeys = new ArrayList<>();
    }

    public int size() {
        return this.table.size();
    }

    public void add(E entity) {
        Objects.requireNonNull(entity);
        if (!this.table.containsKey(entity.id)) {
            this.table.put(entity.id, entity);
            this.referenceCount.put(entity.id, 0);
        }
    }

    public E get(UUID id) {
        return Objects.requireNonNull(this.table.get(Objects.requireNonNull(id)));
    }

    public void incrementReferenceCount(E entity) {
        Objects.requireNonNull(entity);
        this.referenceCount.put(entity.id, this.getReferenceCount(entity.id) + 1);
    }

    public void decrementReferenceCount(E entity) {
        Objects.requireNonNull(entity);
        this.referenceCount.put(entity.id, Math.max(0, this.getReferenceCount(entity.id) - 1));
    }

    public boolean containsId(UUID id) {
        return this.table.containsKey(id);
    }

    public void remove(AssetLibrary assetLibrary, E entity) {
        Objects.requireNonNull(entity);
        if (this.table.containsKey(entity.id)) {
            this.throwIfReferenced(entity.id);
            this.decrementAllReferences(entity);
            this.removeReferencesFromAssetLibrary(assetLibrary, entity);
            this.table.remove(entity.id);
            this.referenceCount.remove(entity.id);
        }
    }

    public void clear(AssetLibrary assetLibrary) {
        this.getAll().forEach(e -> this.remove(assetLibrary, e));
    }

    public List<E> getAll() {
        return new ArrayList<>(this.table.values());
    }

    public List<E> getAll(Comparator<E> comparator) {
        return this.table.values()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public JSONArray toJSON() {
        final JSONArray jsonArray = new JSONArray();
        this.table.values()
                .stream()
                .map(Entity::toJSON)
                .forEach(jsonArray::put);
        return jsonArray;
    }

    protected <K extends Entity> void addForeignKey(Supplier<DatabaseEntityCollection<K>> tableSupplier,
                                                   Function<E, UUID> referenceColumnSupplier) {
        this.foreignKeys.add(new ForeignKey<>(Objects.requireNonNull(tableSupplier),
                Objects.requireNonNull(referenceColumnSupplier)));
    }

    protected abstract void removeReferencesFromAssetLibrary(AssetLibrary assetLibrary, E entity);

    private int getReferenceCount(UUID id) {
        return this.referenceCount.getOrDefault(id, 0);
    }

    private void throwIfReferenced(UUID id) {
        if (id != null && this.getReferenceCount(id) > 0) {
            throw new IllegalStateException("The entity is referenced somewhere else and cannot be deleted.");
        }
    }

    private void decrementAllReferences(E entity) {
        for (ForeignKey<?> foreignKey : this.foreignKeys) {
            this.decrementReferenceCountInForeignKey(entity, foreignKey);
        }
    }

    private void decrementReferenceCountInForeignKey(E entity, ForeignKey<?> foreignKey) {
        final DatabaseEntityCollection<?> referenceTable = Objects.requireNonNull(foreignKey.tableSupplier.get());
        final UUID referenceId = Objects.requireNonNull(foreignKey.referenceColumnSupplier.apply(entity));
        if (referenceTable.containsId(referenceId)) {
            final int referenceCount = referenceTable.referenceCount.get(referenceId);
            referenceTable.referenceCount.put(referenceId, Math.max(0, referenceCount - 1));
        }
    }

    protected class ForeignKey<K extends Entity> {

        final Supplier<DatabaseEntityCollection<K>> tableSupplier;
        final Function<E, UUID> referenceColumnSupplier;

        public ForeignKey(Supplier<DatabaseEntityCollection<K>> tableSupplier,
                          Function<E, UUID> referenceColumnSupplier) {
            this.tableSupplier = tableSupplier;
            this.referenceColumnSupplier = referenceColumnSupplier;
        }
    }

}
