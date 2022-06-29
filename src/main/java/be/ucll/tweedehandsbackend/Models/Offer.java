package be.ucll.tweedehandsbackend.Models;

import be.ucll.tweedehandsbackend.Enums.ECondition;
import be.ucll.tweedehandsbackend.Validators.EnumValue;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Offer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID uuid = UUID.randomUUID();

    @NotBlank(message ="title.is.missing")
    @Size(min = 1, max = 50, message = "description.size.error")
    private String title;

    @NotBlank(message ="description.is.missing")
    @Size(min = 1, max = 2000, message = "description.size.error")
    private String description;

    private String isbn;

    @NotNull(message ="price.is.missing")
    @DecimalMin("0.0")
    private Double price;

    @NotBlank(message ="condition.is.missing")
    @EnumValue(enumClass= ECondition.class, enumMethod="isValidEnum", message = "condition.not.exists")
    private String condition;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "offer_courses",
            joinColumns = @JoinColumn(name = "offer_id", referencedColumnName = "id"),
            inverseJoinColumns =  @JoinColumn(name = "course_id", referencedColumnName = "id"))
    private List<Course> courses = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<Media> images;

    @Transient
    private List<UUID> coursesUuid;

    private Boolean sold = false;

    public Offer(){}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean isSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getPrice() {
        if(price==null)
            return 0;
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getSold() {
        return sold;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void deleteCourse(Course course) {
        this.courses.remove(course);
    }

    public List<UUID> getCoursesUuid() {
        return coursesUuid;
    }

    public void setCoursesUuid(List<UUID> coursesUuid) {
        this.coursesUuid = coursesUuid;
    }

    public List<Media> getImages() {
        return images;
    }

    public void setImages(List<Media> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", condition=" + condition +
                '}';
    }

}
