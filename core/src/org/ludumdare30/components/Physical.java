package org.ludumdare30.components;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import org.entityflow.component.ComponentBase;
import org.entityflow.entity.Entity;

/**
 * Something with a physical body, simulated by the physics engine.
 */
// TODO: Should this include rigid body dynamics + mass as well, or should this just be for collisions (could be renamed then)
public class Physical extends ComponentBase {

    private double mass_kg;
    public btCollisionShape collisionShape;

    public btRigidBody rigidBody;


    public Physical() {
        this(1);
    }

    public Physical(double mass_kg) {
        this(mass_kg, new btSphereShape(1));
    }

    public Physical(double mass_kg, btCollisionShape collisionShape) {
        this.collisionShape = collisionShape;
        this.mass_kg = mass_kg;
    }

    public double getMass_kg() {
        return mass_kg;
    }

    @Override protected void handleRemoved(Entity entity) {

        // Dispose native classes

        if (collisionShape != null) {
            collisionShape.dispose();
            collisionShape = null;
        }

        if (rigidBody != null) {
            rigidBody.dispose();
            rigidBody = null;
        }
    }
}
