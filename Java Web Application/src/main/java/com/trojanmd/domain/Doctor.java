package com.trojanmd.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Doctor {
    @Id
    @GeneratedValue
    private Long id;

    private String fname;
    private String lname;
    private String email;
    private String password;
    private String gender;
    private String profileImg;
    private boolean active;
    private String roles;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String description;

//    @ManyToMany(cascade = {CascadeType.PERSIST})
//    private List<Symptom> symptoms = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Disease> diseases = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

    public Doctor(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

//    public List<Symptom> getSymptoms() {
//        return symptoms;
//    }
//
//    public void setSymptoms(List<Symptom> symptoms) {
//        this.symptoms = symptoms;
//    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<Disease> diseases) {
        this.diseases = diseases;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                ", profileImg='" + profileImg + '\'' +
                '}';
    }
}
