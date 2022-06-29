package be.ucll.tweedehandsbackend.Models;

import com.fasterxml.jackson.annotation.*;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private UUID uuid = UUID.randomUUID();

    @Type(type = "json")
    @Column(columnDefinition = "json")
    @NotNull(message = "opoCodes;validation.messages.required")
    @Size(min = 1, message = "opoCodes;validation.messages.required")
    @Size(max = 10, message = "opoCodes;validation.messages.max;10")
    private List<String> opoCodes;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    @Size(min = 1, message = "name.cant_be_blank")
    private Map<String, String> name = new HashMap<>();

    private String extra;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "course_programs",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "program_id", referencedColumnName = "id"))
    private List<Program> programs = new ArrayList<>();

    @Transient
    private List<UUID> programsUuid;

    @ManyToMany(mappedBy = "courses")
    private List<Offer> offers;

    public Course() {}

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

    public List<String> getOpoCodes() {
        return opoCodes;
    }

    public void setOpoCodes(List<String> olaCode) {
        this.opoCodes = olaCode;
    }

    public void addOlaCode(String olaCode) {
        this.opoCodes.add(olaCode);
    }

    public void deleteOlaCode(String olaCode) {
        this.opoCodes.remove(olaCode);
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public void deleteProgram(Program program) {
        this.programs.remove(program);
    }

    public void addProgram(Program program){
        this.programs.add(program);
    }

    public void deleteOffer(Offer offer) {
        this.offers.remove(offer);
    }

    public void addOffer(Offer offer) {
        this.offers.add(offer);
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public List<UUID> getProgramsUuid() {
        return programsUuid;
    }

    public void setProgramsUuid(List<UUID> programsUuid) {
        this.programsUuid = programsUuid;
    }

}
