package com.technodom.OrderStatisticBackend.repo;

import com.technodom.OrderStatisticBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Long> {
    User findByIin(String userName);
}
