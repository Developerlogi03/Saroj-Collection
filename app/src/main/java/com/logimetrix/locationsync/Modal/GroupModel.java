package com.logimetrix.locationsync.Modal;

public class GroupModel {
    String groupId;
    String groupColor;
    String groupName;
    boolean isColor;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
    }

    public String getGroupColor() {
        return groupColor;
    }

    public void setGroupColor(String groupColor) {
        this.groupColor = groupColor;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
