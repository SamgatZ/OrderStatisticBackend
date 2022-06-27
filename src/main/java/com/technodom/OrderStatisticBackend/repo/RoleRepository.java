package com.technodom.OrderStatisticBackend.repo;

import com.technodom.OrderStatisticBackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String role);
}
