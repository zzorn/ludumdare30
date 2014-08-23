package org.ludumdare30.processors;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import org.entityflow.entity.Entity;
import org.entityflow.processors.EntityProcessorBase;
import org.flowutils.Check;
import org.flowutils.time.Time;
import org.ludumdare30.components.Location;
import org.ludumdare30.components.Physical;

import java.util.HashMap;
import java.util.Map;

import static org.flowutils.Check.notNull;

/**
 *
 */
public class BulletPhysicsProcessor extends EntityProcessorBase {

    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private ContactListener contactListener;
    private btDynamicsWorld dynamicsWorld;
    private btConstraintSolver constraintSolver;
    private btBroadphaseInterface broadphase;

    private Map<Integer, Entity> entitiesLookup = new HashMap<Integer, Entity>();
    private int nextEntityRef = 1;
    private final Vector3 temp = new Vector3();


    public BulletPhysicsProcessor() {
        super(Physical.class, Location.class);
    }

    @Override protected void onInit() {
        // Initialize physics library
        Bullet.init();
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();

        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, 0, 0));

        /*
        contactListener = new ContactListener() {
            @Override public boolean onContactAdded(int userValue0,
                                                    int partId0,
                                                    int index0,
                                                    boolean match0,
                                                    int userValue1,
                                                    int partId1,
                                                    int index1,
                                                    boolean match1) {

                final Entity entity1 = entitiesLookup.get(userValue0);
                final Entity entity2 = entitiesLookup.get(userValue1);

                System.out.println("Collision between entities " + entity1 + " and " + entity2);

                return true;
            }
        };
        */

    }

    @Override protected void handleAddedEntity(Entity entity) {
        final Physical physical = entity.get(Physical.class);
        final Location location = entity.get(Location.class);
        notNull(location, "location");
        notNull(physical, "physical");
        notNull(physical.collisionShape, "physical.collisionShape");
        Check.equals(physical.rigidBody, "physical.rigidBody", null, "<not initialized yet>"); // Assert that the physics object is not yet created

        // Create rigid body object for this entity
        final btRigidBody rigidBody = createRigidBody(physical);
        physical.rigidBody = rigidBody;

        // Put the object in the correct position
        final Matrix4 worldTrans = new Matrix4();
        location.getWorldTransform(worldTrans);
        rigidBody.setWorldTransform(worldTrans);

        // Store a quick lookup value used to find the entity for a given collision object
        int ref = nextEntityRef++;
        rigidBody.setUserValue(ref);
        entitiesLookup.put(ref, entity);

        // Listen to collisions
        rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() |
                                    btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

        /*
        // DEBUG:
        final Vector3 pos = location.getPosition(new Vector3());
        rigidBody.applyCentralForce(new Vector3(((float) Math.random() * 1 + 2) * pos.z * 10000,
                                                0,
                                                ((float) Math.random() * -1 - 2) * pos.x * 10000));
                                                */


        // Add rigid body object to physics simulation
        dynamicsWorld.addRigidBody(rigidBody);

    }

    private btRigidBody createRigidBody(Physical physical) {
        final float mass_kg = (float) physical.getMass_kg();

        // Calculate inertia
        Vector3 localInertia = new Vector3();
        if (mass_kg > 0f) {
            physical.collisionShape.calculateLocalInertia(mass_kg, localInertia);
        }
        else {
            localInertia.set(0, 0, 0);
        }

        // Create instance
        final btRigidBody rigidBody = new btRigidBody(mass_kg, null, physical.collisionShape, localInertia);
        //rigidBody.setRollingFriction(0.99f);
        //rigidBody.setFriction(0.99f);
        //rigidBody.setDamping(0.999f, 0.999f);

        return rigidBody;
    }

    @Override protected void handleRemovedEntity(Entity entity) {
        final Physical physical = entity.get(Physical.class);
        notNull(physical, "physical");

        // Remove from physics simulation
        dynamicsWorld.removeRigidBody(physical.rigidBody);

        // Remove lookup entry
        entitiesLookup.remove(physical.rigidBody.getUserValue());

        // Dispose the rigidBody object.
        physical.rigidBody.dispose();
        physical.rigidBody = null;
    }

    @Override protected void preProcess(Time time) {
        dynamicsWorld.stepSimulation(time.getSecondsSinceLastStepAsFloat(), 5, 1f / 60f);
    }

    @Override protected void processEntity(Time time, Entity entity) {
        final Physical physical = entity.get(Physical.class);
        final Location location = entity.get(Location.class);

        // Update location based on physics
        physical.rigidBody.getWorldTransform(location.getWorldTransform());

        // DEBUG: Add force towards mid
        location.getPosition(temp);
        temp.scl(-1000000f);
        physical.rigidBody.applyCentralForce(temp);
    }

    @Override protected void postProcess(Time time) {
    }

    @Override protected void onShutdown() {
        /*
        contactListener.dispose();
        */
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();

    }
}
