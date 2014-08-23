package org.ludumdare30.utils.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ShortArray;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.VertexAttributes.*;

/**
 * Common functionality for shape builders.
 */
public abstract class ShapeBuilderBase implements ShapeBuilder {

    private MeshData currentMeshData = new MeshData();

    protected void startMesh() {

    }

    protected short addVertex(Vector3 pos, Vector3 normal, Vector2 texCoordinate, Color color) {
        return currentMeshData.addVertex(pos, normal, texCoordinate, color);
    }

    protected int addTriangle(short vertexA, short vertexB, short vertexC) {
        return currentMeshData.addTriangle(vertexA, vertexB, vertexC);
    }

    protected int addQuad(short vertexA, short vertexB, short vertexC, short vertexD) {
        return currentMeshData.addQuad(vertexA, vertexB, vertexC, vertexD);
    }


    protected Mesh buildMesh() {
        return currentMeshData.buildMesh(true);
    }


    private static final class MeshData {
        private short vertexCount = 0;
        private int indexCount = 0;

        FloatArray vertexes = new FloatArray();
        ShortArray indexes = new ShortArray();

        short addVertex(Vector3 pos, Vector3 normal, Vector2 textureCoordinate, Color color) {
            vertexes.add(pos.x);
            vertexes.add(pos.y);
            vertexes.add(pos.z);

            vertexes.add(normal.x);
            vertexes.add(normal.y);
            vertexes.add(normal.z);

            vertexes.add(color.r);
            vertexes.add(color.g);
            vertexes.add(color.b);
            vertexes.add(color.a);

            vertexes.add(textureCoordinate.x);
            vertexes.add(textureCoordinate.y);

            if (((int)vertexCount) + 1 >= (int) Short.MAX_VALUE) {
                throw new IllegalStateException("Too many vertexes in shape " + vertexCount + ", surpassing the size of shorts, so can not be used as indexes");
            }

            return vertexCount++;
        }

        int addIndex(short index) {
            indexes.add(index);

            return indexCount++;
        }

        int addTriangle(short vertexA, short vertexB, short vertexC) {
            addIndex(vertexA);
            addIndex(vertexB);
            return addIndex(vertexC);
        }

        int addQuad(short vertexA, short vertexB, short vertexC, short vertexD) {
            addIndex(vertexA);
            addIndex(vertexB);
            addIndex(vertexC);

            addIndex(vertexC);
            addIndex(vertexD);
            return addIndex(vertexA);
        }


        public int getVertexCount() {
            return vertexCount;
        }

        public int getIndexCount() {
            return indexCount;
        }


        Mesh buildMesh(boolean isStatic) {
            Mesh mesh = new Mesh(isStatic, vertexCount, indexCount,
                                 new VertexAttribute(Usage.Position, 3, "a_position"),
                                 new VertexAttribute(Usage.Normal, 3, "a_normal"),
                                 new VertexAttribute(Usage.Color, 4, "a_color"),
                                 new VertexAttribute(Usage.TextureCoordinates, 2, "a_texcoords"));

            mesh.setVertices(vertexes.items);
            mesh.setIndices(indexes.items);

            return mesh;
        }
    }
}
