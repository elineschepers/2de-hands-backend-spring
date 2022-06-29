/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 06/03/2022 20:31
 */

package be.ucll.tweedehandsbackend.Repositories;

import be.ucll.tweedehandsbackend.Enums.ERole;
import be.ucll.tweedehandsbackend.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(ERole name);
}
