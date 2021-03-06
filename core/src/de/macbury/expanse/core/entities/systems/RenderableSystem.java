package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.OctreeIteratingSystem;
import de.macbury.expanse.core.entities.components.*;
import de.macbury.expanse.core.graphics.Lod;
import de.macbury.expanse.core.graphics.LodModelBatch;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.graphics.framebuffer.Fbo;
import de.macbury.expanse.core.graphics.framebuffer.FrameBufferManager;
import de.macbury.expanse.core.octree.LevelOctree;
import de.macbury.expanse.core.octree.OctreeNode;

/**
 * This system finds all {@link Entity} with components {@link de.macbury.expanse.core.entities.components.RenderableComponent} and {@link de.macbury.expanse.core.entities.components.PositionComponent}
 * and adds it to {@link com.badlogic.gdx.graphics.g3d.ModelBatch}
 */
//TODO separate systems for rendering in color, reflection and glow batch, We can use components to make it use diffrent systems
public class RenderableSystem extends OctreeIteratingSystem implements Disposable, EntityListener {
  private Environment env;
  private FrameBufferManager fb;
  private LodModelBatch modelBatch;
  private GameCamera camera;
  private BoundingBox tempBox = new BoundingBox();
  private Vector3 tempVec     = new Vector3();
  private Frustum currentFrustrum;

  public RenderableSystem(LevelOctree<PositionComponent> octree, GameCamera camera, LodModelBatch modelBatch, FrameBufferManager fb, Environment env) {
    super(octree, Family.all(
      PositionComponent.class
    ).one(
      ModelComponent.class,
      TerrainRenderableComponent.class
    ).get());

    this.fb         = fb;
    this.camera     = camera;
    this.modelBatch = modelBatch;
    this.env        = env;
  }

  @Override
  public void update(float deltaTime) {
    fb.begin(Fbo.FinalResult); {
      Gdx.gl.glClearColor(1,1,1,1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      modelBatch.begin(camera); {
        camera.extendFov(); {
          currentFrustrum = camera.normalOrDebugFrustrum();
          super.update(deltaTime);
        } camera.restoreFov();
      } modelBatch.end();
    } fb.end();
  }

  /**
   * Update position and rotation from {@link PositionComponent} and render with current {@link Environment}
   * @param entity
   * @param deltaTime
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent positionComponent     = Components.Position.get(entity);

    if (Components.Model.has(entity)) {
      ModelComponent modelComponent = Components.Model.get(entity);

      if (!Components.Static.has(entity))
        calculateTransformMatrix(modelComponent, positionComponent);

      modelBatch.render(modelComponent.modelInstance, env);
    } else if (Components.TerrainRenderable.has(entity)) {
      modelBatch.render(
        Components.TerrainRenderable.get(entity),
        env,
        Lod.High
      );
    }
  }

  private void calculateTransformMatrix(ModelComponent modelComponent, PositionComponent positionComponent) {
    Matrix4 transMat = modelComponent.modelInstance.transform;
    transMat.idt();
    transMat.rotate(Vector3.Y, positionComponent.rotationDeg);
    transMat.trn(positionComponent);
  }

  @Override
  public void dispose() {
    super.dispose();
    modelBatch = null;
    camera     = null;
    fb         = null;
    env        = null;
  }

  /**
   * Now we check if {@link Entity} {@link BodyComponent} is in frustrum
   * @param entity enity to check
   * @return
   */
  @Override
  public boolean checkEntity(Entity entity) {
    return camera.normalOrDebugFrustrum().boundsInFrustum(Components.Position.get(entity).boundingBox);
  }

  /**
   * First we check if node is in frustrum
   * @param node
   * @return
   */
  @Override
  public boolean checkNode(OctreeNode node) {
    return currentFrustrum.boundsInFrustum(node.getBounds());
  }

  @Override
  public void entityAdded(Entity entity) {
    if (Components.Model.has(entity)) {
      ModelComponent modelComponent = Components.Model.get(entity);

      if (Components.Static.has(entity)) {
        calculateTransformMatrix(modelComponent, Components.Position.get(entity));
      }
    }
  }

  @Override
  public void entityRemoved(Entity entity) {

  }
}
