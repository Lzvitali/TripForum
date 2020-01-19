package com.lzvitali.tripforum;

import java.io.Serializable;

public class Trip implements Serializable
{
    private String id;
    private String countryName;
    private String city1;
    private String city2;
    private String city3;
    private String duration;
    private String tripPopulationCategory;
    private String tripTypeCategory;
    private String userName;
    private String userEmail;
    private String tripDescription;
    private String imageUrl;
    private String imageName;
    private String userUid;  // the ID that firebase is giving to the users


    // ----------------------------------- Constructors -------------------------------------------
    public Trip(){}  // default constructor


    public Trip(String countryName, String city1, String city2, String city3, String duration,
                String tripPopulationCategory, String tripTypeCategory, String userName,
                String userEmail, String tripDescription)
    {
        this.id = null;
        this.countryName = countryName;
        this.city1 = city1;
        this.city2 = city2;
        this.city3 = city3;
        this.duration = duration;
        this.tripPopulationCategory = tripPopulationCategory;
        this.tripTypeCategory = tripTypeCategory;
        this.userName = userName;
        this.userEmail = userEmail;
        this.tripDescription = tripDescription;
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);
    }
    // End: Constructors --------------------------------------------------------------------------


    // ----------------------------------- Getters -----------------------------------------------
    public String getCountryName() {
        return countryName;
    }

    public String getCity1() {
        return city1;
    }

    public String getCity2() {
        return city2;
    }

    public String getCity3() {
        return city3;
    }

    public String getDuration() {
        return duration;
    }

    public String getTripPopulationCategory() {
        return tripPopulationCategory;
    }

    public String getTripTypeCategory() {
        return tripTypeCategory;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public String getUserUid() {
        return userUid;
    }
    // End: Getters ----------------------------------------------------------------------------


    // ----------------------------------- Setters -----------------------------------------------
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCity1(String city1) {
        this.city1 = city1;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public void setCity3(String city3) {
        this.city3 = city3;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTripPopulationCategory(String tripPopulationCategory) {
        this.tripPopulationCategory = tripPopulationCategory;
    }

    public void setTripTypeCategory(String tripTypeCategory) {
        this.tripTypeCategory = tripTypeCategory;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setUserUid(String uid) {
        this.userUid = uid;
    }
    // End: Setters ----------------------------------------------------------------------------


}
