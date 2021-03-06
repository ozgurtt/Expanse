package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.StaticComponent;
import de.macbury.expanse.core.entities.components.TerrainRenderableComponent;
import de.macbury.expanse.core.graphics.Lod;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.camera.RTSCameraListener;

/**
 * This class wraps all stuff for terrain manipulation
 */
public class Terrain implements Disposable, RTSCameraListener {
  private static final String TAG = "Terrain";
  private static final float MIN_CAMERA_DISTANCE_TO_TERRAIN = 15;
  private static final float DIMENSION_EXTRA = 2;
  private BoundingBox cameraBoundingBox;
  private ElevationHelper elevation;
  private Vector3 tempVecA = new Vector3();
  private Vector3 tempVecB = new Vector3();
  private TerrainData terrainData;
  private TerrainAssembler terrainAssembler;

  public Terrain(TerrainData terrainData) {
    this.terrainData      = terrainData;
    this.terrainAssembler = new TerrainAssembler(terrainData, GL20.GL_TRIANGLES);
    this.elevation        = new ElevationHelper(terrainData);
    calculateCameraBoundingBox();
  }

  private void calculateCameraBoundingBox() {
    cameraBoundingBox = new BoundingBox();
    float cameraMaxOffset = 10 * TerrainAssembler.TRIANGLE_SIZE;
    tempVecA.set(-cameraMaxOffset, -1, -cameraMaxOffset);
    tempVecB.set(
      terrainData.getWidth() * TerrainAssembler.TRIANGLE_SIZE + cameraMaxOffset,
      terrainData.getMaxElevation() * TerrainAssembler.TRIANGLE_SIZE + cameraMaxOffset,
      terrainData.getHeight() * TerrainAssembler.TRIANGLE_SIZE + cameraMaxOffset
    );
    cameraBoundingBox.set(tempVecA, tempVecB);
  }

  /**
   * Creates all tile entities
   */
  public void addToEntityManager(EntityManager entityManager) {
    BoundingBox tempBoundingBox = new BoundingBox();
    for (TerrainRenderableComponent terrainRenderableComponent : terrainAssembler.getComponents()) {
      terrainRenderableComponent.lodTiles.get(Lod.High).meshPart.mesh.calculateBoundingBox(tempBoundingBox);

      PositionComponent positionComponent = entityManager.createComponent(PositionComponent.class);
      tempBoundingBox.getCenter(positionComponent);
      positionComponent.dimension.set(TerrainAssembler.TILE_SIZE * TerrainAssembler.TRIANGLE_SIZE + DIMENSION_EXTRA, terrainData.getMaxElevation()+ DIMENSION_EXTRA, TerrainAssembler.TILE_SIZE * TerrainAssembler.TRIANGLE_SIZE+ DIMENSION_EXTRA);

      Entity tileEntity = entityManager.createEntity();
      tileEntity.add(terrainRenderableComponent);
      tileEntity.add(positionComponent);
      tileEntity.add(entityManager.createComponent(StaticComponent.class));

      entityManager.addEntity(tileEntity);
    }
  }


  @Override
  public void dispose() {
    terrainData.dispose();
    terrainAssembler.dispose();
    elevation.dispose();
  }

  /**
   * Get world center
   * @return
   */
  public Vector2 getCenter() {
    return terrainData.getCenter().scl(TerrainAssembler.TRIANGLE_SIZE);
  }

  /**
   * Calculates bounding box and returns it
   * @return
   * @param out
   */
  public BoundingBox getBoundingBox(BoundingBox out) {
    return out.set(
      tempVecA.set(0, -terrainData.getMaxElevation(), 0),
      tempVecB.set(terrainData.getWidth() * TerrainAssembler.TRIANGLE_SIZE, terrainData.getMaxElevation(), terrainData.getHeight() * TerrainAssembler.TRIANGLE_SIZE)
    );
  }

  /**
   * Returns elevation for this world cordinates
   * @param x
   * @param z
   * @return
   */
  public ElevationHelper getElevation(float x, float z) {
    elevation.set(x,z);
    return elevation;
  }

  @Override
  public BoundingBox getCameraBounds(BoundingBox out) {
    return out.set(cameraBoundingBox);
  }

  @Override
  public float getCameraElevation(RTSCameraController cameraController, Vector3 cameraPosition) {
    return getElevation(cameraPosition.x, cameraPosition.z).get() + MIN_CAMERA_DISTANCE_TO_TERRAIN;
  }

  /**
   * Returns terrain size in world units
   * @return
   */
  public float getWidth() {
    return terrainData.getWidth() * TerrainAssembler.TRIANGLE_SIZE;
  }

  /**
   * Returns terrain size in world units
   * @return
   */
  public float getHeight() {
    return terrainData.getHeight() * TerrainAssembler.TRIANGLE_SIZE;
  }
}
