package yackeen.com.daleel.home.model;

/**
 * Created by Ibrahim on 11/02/2018.
 */

public class SpinnerAdapterModel {

    private String name, id;

    public SpinnerAdapterModel() {

    }

    public SpinnerAdapterModel(String name, String id) {
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
