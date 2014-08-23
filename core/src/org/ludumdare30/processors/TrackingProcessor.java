package org.ludumdare30.processors;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.entityflow.entity.Entity;
import org.entityflow.processors.EntityProcessorBase;
import org.flowutils.time.Time;
import org.ludumdare30.components.Location;
import org.ludumdare30.components.TrackingComponent;

/**
 *
 */
public final class TrackingProcessor extends EntityProcessorBase {

    private final Vector3 tempV = new Vector3();
    private final Quaternion tempQ = new Quaternion();
    private final Vector3 tempPos = new Vector3();
    private final Quaternion tempDir = new Quaternion();


    public TrackingProcessor() {
        super(TrackingComponent.class, Location.class);
    }

    @Override protected void processEntity(Time time, Entity entity) {
        final TrackingComponent tracking = entity.get(TrackingComponent.class);
        final Location location = entity.get(Location.class);

        final Entity trackedEntity = tracking.trackedEntity;
        if (trackedEntity != null) {
            final Location trackedLocation = trackedEntity.get(Location.class);
            if (trackedLocation != null) {

                // Calculate relative position in target entity model space
                tempV.set(tracking.relativePosition);
                trackedLocation.getDirection(tempDir).transform(tempV);

                // Add target entity pos
                tempV.add(trackedLocation.getPosition(tempPos));

                // Update location
                location.setPosition(tempV);

                // Add relative direction
                tempQ.set(tracking.relativeDirection);
                tempQ.mul(trackedLocation.getDirection(tempDir));

                // Update direction
                location.setDirection(tempQ);
            }
        }
    }
}
