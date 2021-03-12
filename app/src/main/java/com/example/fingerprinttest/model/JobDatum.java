
package com.example.fingerprinttest.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class JobDatum {

    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("id")
    private Long mId;
    @SerializedName("id_group")
    private Long mIdGroup;
    @SerializedName("name")
    private String mName;
    @SerializedName("updated_at")
    private String mUpdatedAt;

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Long getIdGroup() {
        return mIdGroup;
    }

    public void setIdGroup(Long idGroup) {
        mIdGroup = idGroup;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

}
