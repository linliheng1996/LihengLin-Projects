package com.trojanmd.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Disease {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private boolean published;
    private String bodySystemName;

    @ManyToOne
    private BodySystem bodySystem;


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
    private String prevention;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String descriptionHtml;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String causeHtml;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String treatmentHtml;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String preventionHtml;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToMany(mappedBy = "diseases")
    private List<Symptom> symptoms = new ArrayList<>();

    @ManyToMany(mappedBy = "diseases")
    private List<Doctor> doctors = new ArrayList<>();

    public Disease(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
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

    public String getPrevention() {
        return prevention;
    }

    public void setPrevention(String prevention) {
        this.prevention = prevention;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public String getBodySystemName() {
        return bodySystemName;
    }

    public void setBodySystemName(String bodySystemName) {
        this.bodySystemName = bodySystemName;
    }

    public BodySystem getBodySystem() {
        return bodySystem;
    }

    public void setBodySystem(BodySystem bodySystem) {
        this.bodySystem = bodySystem;
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

    public String getPreventionHtml() {
        return preventionHtml;
    }

    public void setPreventionHtml(String preventionHtml) {
        this.preventionHtml = preventionHtml;
    }
}
