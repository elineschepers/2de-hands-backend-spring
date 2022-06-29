/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 07/03/2022 17:55
 */

package be.ucll.tweedehandsbackend.DTOs;

import be.ucll.tweedehandsbackend.Enums.ECondition;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OfferAuthDTO {

    @JsonProperty("id")
    private String uuid;

    @JsonProperty("title")
    private String title;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("condition")
    private ECondition condition;

    @JsonProperty("sold")
    private Boolean sold;

    @JsonProperty("user")
    private UserOfferDTO user;

    @JsonProperty("courses")
    private List<CourseDTO> courses;

    @JsonProperty("images")
    private List<MediaDTO> images;

    public OfferAuthDTO() {}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ECondition getCondition() {
        return condition;
    }

    public void setCondition(ECondition condition) {
        this.condition = condition;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public UserOfferDTO getUser() {
        return user;
    }

    public void setUser(UserOfferDTO user) {
        this.user = user;
    }

    public List<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDTO> courses) {
        this.courses = courses;
    }

    public List<MediaDTO> getImages() {
        return images;
    }

    public void setImages(List<MediaDTO> images) {
        this.images = images;
    }
}
