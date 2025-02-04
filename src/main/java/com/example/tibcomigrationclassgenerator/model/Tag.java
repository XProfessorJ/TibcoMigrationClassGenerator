package com.example.tibcomigrationclassgenerator.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class Tag {
    private String tagSequence;
    private String tagName;
    private String outsideTag;
    private String outsideCondition;
    private String insideTag;
    private String insideCondition;
    private boolean ifWhenCondition;
    private String whenCondition;
    private String combileLogic;

    public String getCombileLogic() {
        return combileLogic;
    }

    public void setCombileLogic(String combileLogic) {
        this.combileLogic = combileLogic;
    }

    public boolean isIfWhenCondition() {
        return ifWhenCondition;
    }

    public void setIfWhenCondition(boolean ifWhenCondition) {
        this.ifWhenCondition = ifWhenCondition;
    }

    public String getWhenCondition() {
        return whenCondition;
    }

    public void setWhenCondition(String whenCondition) {
        this.whenCondition = whenCondition;
    }

    public Tag(String tagName, String insideTag, String insideCondition) {
        this.tagName = tagName;
        this.insideTag = insideTag;
        this.insideCondition = insideCondition;
    }

    public Tag(String tagName, String outsideTag, String outsideCondition, String insideTag, String insideCondition) {
        this.tagName = tagName;
        this.outsideTag = outsideTag;
        this.outsideCondition = outsideCondition;
        this.insideTag = insideTag;
        this.insideCondition = insideCondition;
    }

    public Tag(String tagSequence, String tagName, String outsideTag, String outsideCondition, String insideTag, String insideCondition) {
        this.tagSequence = tagSequence;
        this.tagName = tagName;
        this.outsideTag = outsideTag;
        this.outsideCondition = outsideCondition;
        this.insideTag = insideTag;
        this.insideCondition = insideCondition;
    }

    public Tag() {
    }

    public String getTagSequence() {
        return tagSequence;
    }

    public void setTagSequence(String tagSequence) {
        this.tagSequence = tagSequence;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getOutsideTag() {
        return outsideTag;
    }

    public void setOutsideTag(String outsideTag) {
        this.outsideTag = outsideTag;
    }

    public String getOutsideCondition() {
        return outsideCondition;
    }

    public void setOutsideCondition(String outsideCondition) {
        this.outsideCondition = outsideCondition;
    }

    public String getInsideTag() {
        return insideTag;
    }

    public void setInsideTag(String insideTag) {
        this.insideTag = insideTag;
    }

    public String getInsideCondition() {
        return insideCondition;
    }

    public void setInsideCondition(String insideCondition) {
        this.insideCondition = insideCondition;
    }
}
