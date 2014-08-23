package org.ludumdare30.components;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.entityflow.component.ComponentBase;

import static org.flowutils.Check.notNull;

/**
 * Represents position and direction of the entity
 */
public class Location extends ComponentBase {

    private final Matrix4 worldTransform = new Matrix4();

    public Location() {
        this(0,0,0);
    }

    public Location(Vector3 pos) {
        setPosition(pos);
    }

    public Location(float x, float y, float z) {
        setPosition(x, y, z);
    }

    public Location(float x, float y, float z, Quaternion direction) {
        set(x, y, z, direction);
    }

    public Location(Vector3 pos, Quaternion direction) {
        set(pos, direction);
    }

    public Location(Matrix4 worldTransform) {
        this.worldTransform.set(worldTransform);
    }

    public void setPosition(float x, float y, float z) {
        worldTransform.setTranslation(x, y, z);
    }

    public void setPosition(Vector3 position) {
        worldTransform.setTranslation(position);
    }

    public void setDirection(Quaternion direction) {
        final Vector3 position = new Vector3();
        worldTransform.getTranslation(position);
        worldTransform.set(direction);
        worldTransform.translate(position);
    }

    public void set(float x, float y, float z, Quaternion direction) {
        worldTransform.set(x, y, z, direction.x, direction.y, direction.z, direction.w);
    }

    public void set(Vector3 position, Quaternion direction) {
        worldTransform.set(position, direction);
    }

    public Vector3 getPosition(Vector3 positionOut) {
        if (positionOut == null) positionOut = new Vector3();

        worldTransform.getTranslation(positionOut);

        return positionOut;
    }

    public Quaternion getDirection(Quaternion directionOut) {
        if (directionOut == null) directionOut = new Quaternion();

        worldTransform.getRotation(directionOut);

        return directionOut;
    }

    /**
     * Write the world transformation for this location in the specified transformation matrix.
     */
    public Matrix4 getWorldTransform(Matrix4 worldTransform) {
        notNull(worldTransform, "worldTransform");

        worldTransform.set(this.worldTransform);

        return worldTransform;
    }

    /**
     * Return the mutable worldTransform of this location.
     * The scaling should not be changed.
     */
    public Matrix4 getWorldTransform() {
        return worldTransform;
    }
}
