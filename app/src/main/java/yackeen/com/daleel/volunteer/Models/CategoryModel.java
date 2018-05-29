package yackeen.com.daleel.volunteer.Models;

/**
 * Created by Ibrahim on 05/02/2018.
 */

public class CategoryModel {

    private String id, name;
    private boolean checked;

    public CategoryModel() {
    }

    public CategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
