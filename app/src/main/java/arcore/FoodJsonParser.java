package arcore;

import android.content.Context;
import android.support.annotation.NonNull;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.common.camera.CameraSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FoodJsonParser {

    private static final String CATEGORY_NAME = "category_name";
    private static final String CATEGORY_FOOD_STORE = "food_store";
    private static final String STORE_NAME = "name";
    private static final String STORE_PATH = "path";
    private static final String REQUIRED = "requiredFeatures";
    private static final String REQUIRED_GEO = "geo";
    private static final String REQUIRED_IMAGE_TRACKING = "image_tracking";
    private static final String REQUIRED_INSTANT_TRACKING = "instant_tracking";
    private static final String REQUIRED_OBJECT_TRACKING = "object_tracking";
    private static final String STARTUP_CONFIGURATION = "startupConfiguration";
    private static final String CAMERA_POSITION = "camera_position";
    private static final String CAMERA_POSITION_FRONT = "front";
    private static final String CAMERA_POSITION_BACK = "back";
    private static final String CAMERA_RESOLUTION = "camera_resolution";
    private static final String CAMERA_RESOLUTION_AUTO = "auto";
    private static final String CAMERA_RESOLUTION_SD = "sd_640x480";
    private static final String CAMERA_2_ENABLED = "camera_2_enabled";
    private static final String EXTENSION_REQUIRED = "required_extensions";

    @NonNull
    public static String loadStringFromAssets(@NonNull Context context, @NonNull String path) {

        try {
            final InputStream inputStream = context.getAssets().open(path);
            final int size = inputStream.available();
            final byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    public static List<FoodStoreCategory> getCategoriesFromJsonString(@NonNull String jsonString) {
        final List<FoodStoreCategory> list = new ArrayList<>();

        try {
            JSONArray categoryArray = new JSONArray(jsonString);
            for (int i = 0; i < categoryArray.length(); i++) {
                final JSONObject category = categoryArray.getJSONObject(i);

                final String category_name = category.getString(CATEGORY_NAME);
                final JSONArray storeArray = category.getJSONArray(CATEGORY_FOOD_STORE);

                final List<FoodStoreData> foodStoreDataList = new ArrayList<>();

                for (int u = 0; u < storeArray.length(); u++) {
                    final JSONObject store = storeArray.getJSONObject(u);

                    final String name = store.getString(STORE_NAME);
                    final String path = store.getString(STORE_PATH);

                    final JSONArray requiredFeatures = store.getJSONArray(REQUIRED);
                    final int features = parseRequiredFeatures(requiredFeatures);

                    final JSONObject startupConfig = store.getJSONObject(STARTUP_CONFIGURATION);

                    final CameraSettings.CameraPosition cameraPosition = parseCameraPositionFromConfig(startupConfig);
                    final CameraSettings.CameraResolution cameraResolution = parseCameraResolutionFromConfig(startupConfig);
                    final boolean camera2Enabled = parseCamera2EnabledFromConfig(startupConfig);
                    Class activityClass = MyArActivity.class;

                    final FoodStoreData data = new FoodStoreData.Builder(name, path)
                            .activityClass(activityClass)
                            .arFeatures(features)
                            .cameraPosition(cameraPosition)
                            .cameraResolution(cameraResolution)
                            .camera2Enabled(camera2Enabled)
                            .build();

                    foodStoreDataList.add(data);
                }
                list.add(new FoodStoreCategory(foodStoreDataList, category_name));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static int parseRequiredFeatures(@NonNull JSONArray requiredFeatures) {
        try {
            if (requiredFeatures.length() > 0) {
                int features = 0;
                for (int i = 0; i < requiredFeatures.length(); i++) {
                    final String feature = requiredFeatures.getString(i);

                    switch (feature) {
                        case REQUIRED_GEO: {
                            features |= ArchitectStartupConfiguration.Features.Geo;
                            break;
                        }
                        case REQUIRED_IMAGE_TRACKING: {
                            features |= ArchitectStartupConfiguration.Features.ImageTracking;
                            break;
                        }
                        case REQUIRED_INSTANT_TRACKING: {
                            features |= ArchitectStartupConfiguration.Features.InstantTracking;
                            break;
                        }
                        case REQUIRED_OBJECT_TRACKING: {
                            features |= ArchitectStartupConfiguration.Features.ObjectTracking;
                            break;
                        }
                    }
                }
                return features;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //15 is default
        return 15;
    }

    private static CameraSettings.CameraPosition parseCameraPositionFromConfig(@NonNull JSONObject startupConfig) {
        try {
            if (startupConfig.has(CAMERA_POSITION)){
                final String position = startupConfig.getString(CAMERA_POSITION);
                switch (position) {
                    case CAMERA_POSITION_BACK:
                        return CameraSettings.CameraPosition.BACK;
                    case CAMERA_POSITION_FRONT:
                        return CameraSettings.CameraPosition.FRONT;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static CameraSettings.CameraResolution parseCameraResolutionFromConfig(@NonNull JSONObject startupConfig) {
        try {
            if (startupConfig.has(CAMERA_RESOLUTION)){
                final String position = startupConfig.getString(CAMERA_RESOLUTION);
                switch (position) {
                    case CAMERA_RESOLUTION_AUTO:
                        return CameraSettings.CameraResolution.AUTO;
                    case CAMERA_RESOLUTION_SD:
                        return CameraSettings.CameraResolution.SD_640x480;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean parseCamera2EnabledFromConfig(@NonNull JSONObject startupConfig) {
        try {
            if (startupConfig.has(CAMERA_2_ENABLED)){
                final String enabled = startupConfig.getString(CAMERA_2_ENABLED);
                return Boolean.valueOf(enabled);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return default is false
        return false;
    }
}
