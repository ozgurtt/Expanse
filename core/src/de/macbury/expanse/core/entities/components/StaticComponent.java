package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;

/**
 * Tells if entity is static. Static entity is inserted into static octree and its position is not updated etc.
 */
public class StaticComponent implements Component, Pool.Poolable {

  @Override
  public void reset() {

  }

  public static class Blueprint extends ComponentBlueprint<StaticComponent> {
    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {

    }

    @Override
    public void assignDependencies(Assets assets) {

    }

    @Override
    public void applyTo(StaticComponent component, Entity target, Messages messages) {

    }

    @Override
    public void load(JsonValue source, Json json) {

    }

    @Override
    public void save(Json target, StaticComponent source) {

    }

    @Override
    public void dispose() {

    }
  }
}
