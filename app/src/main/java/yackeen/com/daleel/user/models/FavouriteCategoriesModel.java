package yackeen.com.daleel.user.models;

/**
 * Created by Ibrahim on 14/02/2018.
 */

public class FavouriteCategoriesModel {

    private String name, id;

    public FavouriteCategoriesModel() {

    }

    public FavouriteCategoriesModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
