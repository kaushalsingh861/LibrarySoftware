package com.prismatest.library.repository;

import com.prismatest.library.entity.BorrowTransaction;
import com.prismatest.library.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Long> {

  List<BorrowTransaction> findByFromDateLessThanEqualAndToDateGreaterThanEqual(LocalDate starDate, LocalDate endDate);

  @Query(value = "from BorrowTransaction t where user=:user AND (fromDate BETWEEN :startDate AND :endDate) OR (toDate BETWEEN :startDate AND :endDate)")
  List<BorrowTransaction> findByUserAndFromDateToDateBetween(User user, LocalDate startDate, LocalDate endDate);

 }
