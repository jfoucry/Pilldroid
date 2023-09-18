package net.foucry.pilldroid.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drugs")
public class Medicine {
    @PrimaryKey
    @NonNull
    private Integer _id;
    private String cis;
    private String cip13;
    private String cip7;
    private String administration_mode;
    private String name;
    private String presentation;
    private String label_group;
    private Integer generic_type;

    @NonNull
    public String getCis() {
        return cis;
    }

    public void setCis(@NonNull String cis) {
        this.cis = cis;
    }

    public String getCip13() {
        return cip13;
    }

    public void setCip13(String cip13) {
        this.cip13 = cip13;
    }

    public String getCip7() {
        return cip7;
    }

    public void setCip7(String cip7) {
        this.cip7 = cip7;
    }

    public String getAdministration_mode() {
        return administration_mode;
    }

    public void setAdministration_mode(String administration_mode) {
        this.administration_mode = administration_mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getLabel_group() {
        return label_group;
    }

    public void setLabel_group(String label_group) {
        this.label_group = label_group;
    }

    public Integer getGeneric_type() {
        return generic_type;
    }

    public void setGeneric_type(Integer generic_type) {
        this.generic_type = generic_type;
    }

    @NonNull
    public Integer get_id() {
        return _id;
    }

    public void set_id(@NonNull Integer _id) {
        this._id = _id;
    }
}
