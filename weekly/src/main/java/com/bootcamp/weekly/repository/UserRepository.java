package com.bootcamp.weekly.repository;

import com.bootcamp.weekly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findUserByEmployeeNip(String nip);
}
