package com.prismatest.library.repository;

import com.prismatest.library.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    List<User> findByFirstNameOrLastName(String firstName, String lastName);
}
