package org.ludumdare30.processors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.entityflow.entity.Entity;
import org.entityflow.processors.EntityProcessorBase;
import org.flowutils.time.Time;
import org.ludumdare30.components.Location;
import org.ludumdare30.components.appearance.Appearance;

import java.util.List;

/**
 *
 */
public class RenderingProcessor extends EntityProcessorBase {

    private static final int ATTRIBUTES = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;
    public Environment environment;

    private final Shader shader;

    private ModelInstance skySphere;

    /*
    private final Renderable tempRenderable = new Renderable();
     */

    private Entity cameraHostEntity;

    /*
    private ModelInstance waterSurface;
    private ModelInstance waterUnderside;
    */

    public RenderingProcessor() {
        this(null);
    }

    public RenderingProcessor(Shader shader) {
        super(Appearance.class, Location.class);
        this.shader = shader;
    }

    @Override protected void onInit() {
        // Setup model batching
        modelBatch = new ModelBatch();

        // Setup camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 100000f;
        camera.update();

        // Setup shader
        if (shader != null) {
            shader.init();
        }

        // Setup lighting
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.01f, 0.02f, 0.03f, 1f));
        environment.add(new PointLight().set(1, 1, 1, 0, 0, 0, 100000));
        environment.add(new DirectionalLight().set(0.05f, 0.3f, 0.5f, 0.1f, -1f, 0.3f));
        environment.add(new DirectionalLight().set(0.35f, 0.23f, 0.1f, -0.8f, 0.7f, 0f));

        // Create background skysphere
        ModelBuilder modelBuilder = new ModelBuilder();
        float skySize = 1000f;
        final Model skySphereModel = modelBuilder.createSphere(-skySize,
                                                          -skySize,
                                                          -skySize,
                                                          128,
                                                          128,
                                                          new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                                                          ATTRIBUTES);
        skySphere = new ModelInstance(skySphereModel);

        /*
        // Create water surface
        float waterSize = 1000;
        waterSurface = new ModelInstance(createSurface(modelBuilder, waterSize, true, SpecialAttribute.waterSurface()));
        waterUnderside = new ModelInstance(createSurface(modelBuilder,
                                                         waterSize,
                                                         false,
                                                         SpecialAttribute.waterUnderside()));
        */


        // Setup camera control
        //camController = new CameraInputController(camera);
        //Gdx.input.setInputProcessor(camController);
    }

    private Model createSurface(ModelBuilder modelBuilder,
                                float waterSize,
                                boolean flip) {
        float f = 1;//flip ? -1 : 1;
        final Model rect = modelBuilder.createRect(-waterSize*f, 0, -waterSize*f,
                                                   waterSize*f, 0, -waterSize*f,
                                                   waterSize*f, 0, waterSize*f,
                                                   -waterSize*f, 0, waterSize*f,
                                                   0, 1, 0,
                                                   new Material(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.5f)),
                                                   ATTRIBUTES);
        if (flip) {
            rect.nodes.get(0).rotation.setFromAxis(1, 0, 0, 180);
            rect.calculateTransforms();
        }
        return rect;
    }

    @Override protected void preProcess(Time time) {
        // Clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Render scene
        modelBatch.begin(camera);

        /*
        // Move water to camera x,z and water surface y
        final float seaLevel = sea.getSeaLevel(camera.position);
        waterSurface.transform.setTranslation(camera.position.x, seaLevel, camera.position.z);
        waterUnderside.transform.setTranslation(camera.position.x, seaLevel, camera.position.z);
        */

        // Move sky to center on camera
        skySphere.transform.setTranslation(camera.position);


        // Render space background
        /*
        modelBatch.render(waterSurface, shader);
        modelBatch.render(waterUnderside, shader);
        */
        //modelBatch.render(skySphere, shader);
//        modelBatch.render(skySphere, environment);
    }

    @Override protected void processEntity(Time time, Entity entity) {
        final Location location = entity.get(Location.class);
        final Appearance appearance = entity.get(Appearance.class);

        if (appearance != null && location != null && appearance.isVisible()) {
            // Do any appearance updates
            appearance.update(time);

            // Render all parts of the appearance
            final List<ModelInstance> modelInstances = appearance.getModelInstances();
            for (ModelInstance modelInstance : modelInstances) {
                // Apply direction
                location.getWorldTransform(modelInstance.transform);

                // Apply scaling
                modelInstance.transform.scl(appearance.getScale());

                // Render
//                modelBatch.render(modelInstance, shader);
                modelBatch.render(modelInstance, environment);
            }
        }
    }

    @Override protected void postProcess(Time time) {
        modelBatch.end();
    }

    @Override protected void onShutdown() {
        modelBatch.dispose();

        if (shader != null) {
            shader.dispose();
        }
    }

    public Entity getCameraHostEntity() {
        return cameraHostEntity;
    }

    public void setCameraHostEntity(Entity cameraHostEntity) {
        this.cameraHostEntity = cameraHostEntity;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }
}
