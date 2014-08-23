package org.ludumdare30.components.appearance;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 */
public abstract class ModelAppearance extends Appearance {

    private Model model;

    @Override protected final ModelInstance createAppearance() {
        return new ModelInstance(getModel());
    }

    private Model getModel() {
        if (model == null) {
            model = createBaseModel();
        }

        return model;
    }

    protected abstract Model createBaseModel();


}
