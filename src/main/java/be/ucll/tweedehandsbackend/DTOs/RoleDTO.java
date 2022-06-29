/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 08/03/2022 14:34
 */

package be.ucll.tweedehandsbackend.DTOs;

import be.ucll.tweedehandsbackend.Enums.ERole;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class RoleDTO {

    @JsonProperty("value")
    private ERole name;

    @JsonProperty("name")
    private Map<String, String> map;

    public RoleDTO() {}

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
