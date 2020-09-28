package com.trojanmd.vo;

public class SymptomQuery {
    private String bodyPart;
    private boolean published;

    public SymptomQuery(){}

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
