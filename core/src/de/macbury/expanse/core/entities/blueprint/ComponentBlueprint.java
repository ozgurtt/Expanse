package de.macbury.expanse.core.entities.blueprint;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;

/**
 * This class is used as model for blueprints
 */
public abstract class ComponentBlueprint<T extends Component> implements Disposable {
  public Class<? extends Component> componentKlass;
  /**
   * Pass all dependencies that are needed to be loaded by {@link de.macbury.expanse.core.assets.Assets}
   * @return
   */
  public abstract void prepareDependencies(Array<AssetDescriptor> dependencies);

  /**
   * Assign all dependencies from assets
   * @param assets
   */
  public abstract void assignDependencies(Assets assets);

  /**
   * Apply all blueprint configuration to component
   * @param component
   */
  public abstract void applyTo(T component, Entity owner, Messages messages);
}
