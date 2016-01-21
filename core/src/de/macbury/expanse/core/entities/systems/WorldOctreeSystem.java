package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This class refreshes bounding boxes for each {@link BodyComponent} and additionaly
 * refresh octree
 */
public class WorldOctreeSystem extends IteratingSystem implements Disposable {
  private WorldOctree octree;
  private Vector3 tempVec = new Vector3();
  public WorldOctreeSystem(WorldOctree octree) {
    super(Family.all(PositionComponent.class, BodyComponent.class).get());
    this.octree = octree;
  }

  @Override
  public void dispose() {
    octree = null;
  }

  @Override
  public void update(float deltaTime) {
    octree.clear();
    super.update(deltaTime);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent positionComponent = Components.Position.get(entity);
    BodyComponent bodyComponent         = Components.Body.get(entity);
    bodyComponent.set(positionComponent, tempVec.set(positionComponent).add(bodyComponent.dimensions));

    octree.insert(bodyComponent);
  }
}