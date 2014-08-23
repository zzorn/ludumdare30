package org.ludumdare30.components.appearance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import static org.flowutils.Check.*;

/**
 * Spherical appearance
 */
public class SphereAppearance extends ModelAppearance {

    private static final Color DEFAULT_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.5f);
    private static final int DEFAULT_SIZE = 100;
    private final Color color;
    private final float size;

    public SphereAppearance() {
        this(DEFAULT_COLOR);
    }

    public SphereAppearance(Color color) {
        this(color, DEFAULT_SIZE);
    }

    public SphereAppearance(Color color, float size) {
        notNull(color, "color");
        positive(size, "size");

        this.color = color;
        this.size = size;
    }

    @Override protected Model createBaseModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createSphere(size, size, size,
                                         2*12, 2*12,
                                         new Material(),
                                         VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }


    @Override protected void configureInstance(ModelInstance appearance) {
        appearance.materials.get(0).set(ColorAttribute.createDiffuse(color));
    }
}
