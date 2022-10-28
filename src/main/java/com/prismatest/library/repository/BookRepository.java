package com.prismatest.library.repository;

import com.prismatest.library.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);

}
