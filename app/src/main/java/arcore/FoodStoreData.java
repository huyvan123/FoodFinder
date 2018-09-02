package arcore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wikitude.common.camera.CameraSettings;

import java.io.Serializable;

public class FoodStoreData implements Serializable {

    private final String name;
    private final String path;
    private final Class activityClass;

    private final int arFeatures;
    private final CameraSettings.CameraPosition cameraPosition;
    private final CameraSettings.CameraResolution cameraResolution;
    private final CameraSettings.CameraFocusMode cameraFocusMode;
    private final boolean camera2Enabled;

    private FoodStoreData(@NonNull Builder builder) {
        name = builder.name;
        path = builder.path;
        activityClass = builder.activityClass;
        arFeatures = builder.arFeatures;
        cameraPosition = builder.cameraPosition;
        cameraResolution = builder.cameraResolution;
        cameraFocusMode = builder.cameraFocusMode;
        camera2Enabled = builder.camera2Enabled;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public int getArFeatures() {
        return arFeatures;
    }

    public CameraSettings.CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    public CameraSettings.CameraResolution getCameraResolution() {
        return cameraResolution;
    }

    public CameraSettings.CameraFocusMode getCameraFocusMode() {
        return cameraFocusMode;
    }

    public boolean isCamera2Enabled() {
        return camera2Enabled;
    }

    public static class Builder {

        private final @NonNull String name;
        private final @NonNull String path;
        private Class activityClass = MyArActivity.class;
        private int arFeatures = 15;
        private @NonNull
        CameraSettings.CameraPosition cameraPosition = CameraSettings.CameraPosition.DEFAULT;
        private @NonNull
        CameraSettings.CameraResolution cameraResolution = CameraSettings.CameraResolution.SD_640x480;
        private @NonNull
        CameraSettings.CameraFocusMode cameraFocusMode = CameraSettings.CameraFocusMode.CONTINUOUS;
        private boolean camera2Enabled = false;

        public Builder(@NonNull String name, @NonNull String path) {
            this.name = name;
            this.path = path;
        }

        public Builder arFeatures(int arFeatures) {
            this.arFeatures = arFeatures;
            return this;
        }

        public Builder activityClass(@Nullable Class activityClass) {
            if (activityClass != null) {
                this.activityClass = activityClass;
            }
            return this;
        }

        public Builder cameraPosition(@Nullable CameraSettings.CameraPosition cameraPosition) {
            if (cameraPosition != null) {
                this.cameraPosition = cameraPosition;
            }
            return this;
        }

        public Builder cameraResolution(@Nullable CameraSettings.CameraResolution cameraResolution) {
            if (cameraResolution != null) {
                this.cameraResolution = cameraResolution;
            }
            return this;
        }

        public Builder cameraFocusMode(@Nullable CameraSettings.CameraFocusMode cameraFocusMode) {
            if (cameraFocusMode != null) {
                this.cameraFocusMode = cameraFocusMode;
            }
            return this;
        }

        public Builder camera2Enabled(boolean camera2Enabled) {
            this.camera2Enabled = camera2Enabled;
            return this;
        }
        public FoodStoreData build() {
            return new FoodStoreData(this);
        }
    }
}
