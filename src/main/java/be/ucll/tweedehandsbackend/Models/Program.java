package be.ucll.tweedehandsbackend.Models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Entity
@TypeDef(name="json", typeClass = JsonBinaryType.class)
public class Program implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID uuid = UUID.randomUUID();

    private String region;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<String, String> name = new HashMap<>();

    @ManyToMany(mappedBy = "programs")
    private List<Course> courses;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Program() {}

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void deleteCourse(Course course) {
        this.courses.remove(course);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
