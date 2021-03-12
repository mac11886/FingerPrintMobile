
package com.example.fingerprinttest.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class GroupData {

    @SerializedName("group_data")
    private List<GroupDatum> mGroupData;
    @SerializedName("job_data")
    private List<JobDatum> mJobData;

    public List<GroupDatum> getGroupData() {
        return mGroupData;
    }

    public void setGroupData(List<GroupDatum> groupData) {
        mGroupData = groupData;
    }

    public List<JobDatum> getJobData() {
        return mJobData;
    }

    public void setJobData(List<JobDatum> jobData) {
        mJobData = jobData;
    }

}
