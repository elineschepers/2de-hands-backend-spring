/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 02/03/2022 20:17
 */

package be.ucll.tweedehandsbackend.DTOs;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class CourseDTO {

    @JsonProperty("id")
    private String uuid;

    @JsonProperty("opoCodes")
    private List<String> opoCodes;

    @JsonProperty("name")
    private Map<String, String> name;

    @JsonProperty("extra")
    private String extra;

    @JsonProperty("programs")
    @JsonManagedReference
    private List<ProgramDTO> programs;

    public CourseDTO() {}

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getOpoCodes() {
        return opoCodes;
    }

    public void setOpoCodes(List<String> opoCodes) {
        this.opoCodes = opoCodes;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public List<ProgramDTO> getPrograms() {
        return programs;
    }

    public void setPrograms(List<ProgramDTO> programs) {
        this.programs = programs;
    }
}
