package org.ludumdare30.utils.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.flowutils.MathUtils;
import org.flowutils.raster.field.single.Field;

import java.util.*;

/**
 * Builds cylindrical branching shapes
 */
public class TreeShapeBuilder extends ShapeBuilderBase {

    private TreeShapeState currentState = new TreeShapeState();
    private Deque<TreeShapeState> stateStack = new ArrayDeque<TreeShapeState>();

    TreeShapeBuilder move(double x, double y, double z) {
        return move(x, y, z, 0, 0, 0);
    }

    TreeShapeBuilder turn(double yaw, double pitch, double roll) {
        return move(0, 0, 0, yaw, pitch, roll);
    }

    TreeShapeBuilder move(double x, double y, double z, double yaw, double pitch, double roll) {

        // TODO: Figure out correct rotations etc
        currentState.transform.translate((float) x, (float) y, (float) z);
        currentState.transform.rotate(1, 0, 0, (float) (yaw * 180));
        currentState.transform.rotate(0, 1, 0, (float) (pitch * 180));
        currentState.transform.rotate(0, 0, 1, (float) (roll * 180));

        return this;
    }

    TreeShapeBuilder pushBranch() {
        stateStack.push(currentState.copy());
        return this;
    }

    TreeShapeBuilder popBranch() {
        currentState = stateStack.pop();
        return this;
    }

    TreeShapeBuilder ring(double relativePosition) {
        return ring(relativePosition, currentState.color);
    }

    TreeShapeBuilder ring(double relativePosition, Color color) {
        return ring(relativePosition, color, 1);
    }

    TreeShapeBuilder ring(double relativePosition, Color color, double radius) {
        // TODO: Add vertexes, calculate normals

        return this;
    }

    TreeShapeBuilder startRing(double radius, int pointCount) {
        return startRing(radius, pointCount, Color.WHITE);
    }

    TreeShapeBuilder startRing(double radius, int pointCount, Color color) {
        return startRing(radius, pointCount, color, null, null, null, null);
    }

    TreeShapeBuilder startRing(double radius, int pointCount, Color color, TextureRegion textureRegion, Vector2 startTexel, Vector2 endTexel, Vector2 texelDirection) {
        return startRing(radius, pointCount, color, textureRegion, startTexel, endTexel, texelDirection, null, false, 0);
    }

    TreeShapeBuilder startRing(double radius, int pointCount, Color color, TextureRegion textureRegion, Vector2 startTexel, Vector2 endTexel, Vector2 texelDirection, Field surfaceHeight, boolean invertFaces, double relativePosition) {
        // Setup current state
        currentState = new TreeShapeState(pointCount, currentState.transform, color, radius, relativePosition, textureRegion, startTexel, endTexel, texelDirection);

        // Create ring
        ring(0);

        return this;
    }

    TreeShapeBuilder fillRing() {
        // TODO: Add vertexes, calculate normals

        return this;
    }

    TreeShapeBuilder fillRing(TextureRegion textureRegion) {
        // TODO: Add vertexes, calculate normals


        return this;
    }




    public Shape buildShape() {
        final Mesh mesh = buildMesh();

        // TODO

        return null;
    }


    private static final class TreeShapeState {
        Matrix4 transform = new Matrix4();
        Color color = new Color(1f, 1f, 1f, 1f);
        double radius = 1;
        double relativePosition = 0;
        List<Vector3> ringPoints = new ArrayList<Vector3>();
        TextureRegion textureRegion;
        Vector2 startTexel;
        Vector2 endTexel;
        Vector2 texelDirection;
        int pointCount;



        private TreeShapeState() {
        }

        private TreeShapeState(int pointCount,
                               Matrix4 transform,
                               Color color,
                               double radius,
                               double relativePosition,
                               TextureRegion textureRegion,
                               Vector2 startTexel,
                               Vector2 endTexel,
                               Vector2 texelDirection) {
            this(pointCount, transform, color, radius, relativePosition, textureRegion, startTexel, endTexel, texelDirection, null);
        }

        private TreeShapeState(int pointCount,
                               Matrix4 transform,
                               Color color,
                               double radius,
                               double relativePosition,
                               TextureRegion textureRegion,
                               Vector2 startTexel,
                               Vector2 endTexel,
                               Vector2 texelDirection, List<Vector3> ringPoints) {
            this.pointCount = pointCount;
            this.transform.set(transform);
            this.color.set(color);
            this.radius = radius;
            this.relativePosition = relativePosition;
            this.textureRegion = textureRegion;
            this.startTexel = startTexel;
            this.endTexel = endTexel;
            this.texelDirection = texelDirection;

            if (ringPoints != null) {
                this.ringPoints.addAll(ringPoints);
            }
        }

        TreeShapeState copy() {
            return new TreeShapeState(pointCount,
                                      transform,
                                      color,
                                      radius,
                                      relativePosition,
                                      textureRegion,
                                      startTexel,
                                      endTexel,
                                      texelDirection,
                                      ringPoints);
        }
    }
}
