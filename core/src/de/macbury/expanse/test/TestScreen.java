package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.*;
import de.macbury.expanse.core.entities.states.RobotInstructionState;
import de.macbury.expanse.core.entities.states.RobotMotorState;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * Created on 18.01.16.
 */
public class TestScreen extends ScreenBase {
  private static final String TAG = "TestScreen";
  private Texture texture;
  private SpriteBatch spriteBatch;
  private OrthographicCamera camera;
  private ScriptRunner scriptRunner;
  private FPSLogger fpsLogger;
  private int frameNo;
  private EntityManager entities;

  @Override
  public void preload() {
    this.assets.load("textures:bot.png", Texture.class);
  }

  @Override
  public void create() {
    this.camera      = new OrthographicCamera();
    this.texture     = this.assets.get("textures:bot.png", Texture.class);
    this.spriteBatch = new SpriteBatch();
    this.entities    = new EntityManager(camera, messages);
    this.fpsLogger   = new FPSLogger();

    //Gdx.audio.newSound(Gdx.files.internal("sound/electric_motor.ogg")).loop();

    Entity robotEntity                  = entities.createEntity();

    MotorComponent motorComponent                 = entities.createComponent(MotorComponent.class);
    motorComponent.init(robotEntity, messages, null);
    motorComponent.changeState(RobotMotorState.Idle);
    motorComponent.speed                          = 1;

    RobotInstructionStateComponent robotInstructionStateComponent = entities.createComponent(RobotInstructionStateComponent.class);
    robotInstructionStateComponent.init(robotEntity, messages, RobotInstructionState.Living);
    robotInstructionStateComponent.changeState(RobotInstructionState.WaitForInstruction);

    RobotScriptComponent robotScriptComponent = entities.createComponent(RobotScriptComponent.class);
    robotScriptComponent.setSource(Gdx.files.internal("scripts/move_test.js").readString());

    //setScriptRunner(new ScriptRunner(, globalObjectFunctions, true));
    PositionComponent positionComponent = entities.createComponent(PositionComponent.class);
    positionComponent.set(250, 0, 250);

    SpriteComponent spriteComponent     = entities.createComponent(SpriteComponent.class);
    spriteComponent.setSize(texture.getWidth(), texture.getHeight());
    spriteComponent.setOrigin(texture.getWidth()/2, texture.getHeight()/2);
    spriteComponent.setRotation(0);
    spriteComponent.setRegion(texture);

    robotEntity.add(entities.createComponent(TimerComponent.class));
    robotEntity.add(robotInstructionStateComponent);
    robotEntity.add(robotScriptComponent);
    robotEntity.add(positionComponent);
    robotEntity.add(spriteComponent);
    robotEntity.add(motorComponent);

    entities.addEntity(robotEntity);

    //this.scriptRunner = new ScriptRunner(Gdx.files.internal("scripts/move.js").readString(), globalObjectFunctions, true);
    //long startAt      = System.currentTimeMillis();
    //scriptRunner.start();
    //Gdx.app.log(TAG, "Speed of script: " + (System.currentTimeMillis() - startAt) + " miliseconds!");

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    camera.update();
    entities.update(delta);

    fpsLogger.log();
    //scriptRunner.resume("random result");

  }

  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }

  @Override
  public void unload() {
    this.assets.unload("textures:bot.png");
  }

  @Override
  public void dispose() {
    spriteBatch.dispose();
    scriptRunner.dispose();
    entities.dispose();
  }
}
