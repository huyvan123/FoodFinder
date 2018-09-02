package arcore;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class FoodStoreCategory implements Serializable {

    private final List<FoodStoreData> foodStoreDatas;
    private final String name;

    public FoodStoreCategory(@NonNull List<FoodStoreData> foodStoreDatas, @NonNull String name) {
        this.foodStoreDatas = foodStoreDatas;
        this.name = name;
    }

    public List<FoodStoreData> getSamples() {
        return foodStoreDatas;
    }

    public String getName() {
        return name;
    }
}
