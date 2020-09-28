package com.trojanmd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Symptom {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private boolean published;
    private String bodyPartName;
    private boolean requiresPhoto;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String description;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String cause;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String treatment;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String descriptionHtml;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String causeHtml;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String treatmentHtml;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @JsonIgnore
    @ManyToOne
    private BodyPart bodyPart;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Disease> diseases = new ArrayList<>();

//    @ManyToMany(mappedBy = "symptoms")
//    private List<Doctor> doctors = new ArrayList<>();

    public Symptom(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBodyPartName() {
        return bodyPartName;
    }

    public void setBodyPartName(String bodyPartName) {
        this.bodyPartName = bodyPartName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<Disease> diseases) {
        this.diseases = diseases;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public boolean isRequiresPhoto() {
        return requiresPhoto;
    }

    public void setRequiresPhoto(boolean requiresPhoto) {
        this.requiresPhoto = requiresPhoto;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public String getCauseHtml() {
        return causeHtml;
    }

    public void setCauseHtml(String causeHtml) {
        this.causeHtml = causeHtml;
    }

    public String getTreatmentHtml() {
        return treatmentHtml;
    }

    public void setTreatmentHtml(String treatmentHtml) {
        this.treatmentHtml = treatmentHtml;
    }
}
