/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 04/03/2022 23:42
 */

package be.ucll.tweedehandsbackend.Models;

import be.ucll.tweedehandsbackend.Enums.ERole;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    public Role() {
    }

    public Role(ERole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("en", name.en);
        map.put("nl", name.nl);
        return map;
    }

    public String getNL() { return name.nl; }

    public String getEN() { return name.en; }

    public void setName(ERole name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
