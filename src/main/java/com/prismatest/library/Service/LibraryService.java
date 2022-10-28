package com.prismatest.library.Service;

import com.prismatest.library.entity.Book;
import com.prismatest.library.entity.User;
import com.prismatest.library.repository.BookRepository;
import com.prismatest.library.repository.BorrowTransactionRepository;
import com.prismatest.library.repository.UserRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LibraryService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowTransactionRepository borrowTransactionRepository;

    public List<String> getAllUsersWithAtLeastOneBookBorrowHistory(){

        return userRepository.findAll().stream().filter(user -> !user.getTransactions().isEmpty()).map(user->user.getFirstName() + " " + user.getLastName()).collect(
            Collectors.toList());

    }

    public  List<String> getAllUsersWithNoBookHistory(boolean removeTerminatedUsers){
        List<User> users = userRepository.findAll().stream().filter(user -> user.getTransactions().isEmpty()).collect(
            Collectors.toList());

        if (removeTerminatedUsers){
            users = users.stream().filter(user -> user.getMemberTill() == null || user.getMemberTill().isAfter(
                LocalDate.now())).collect(Collectors.toList());
        }

        return users.stream().map(user->user.getFirstName() + " " + user.getLastName()).collect(Collectors.toList());
    }

    public Collection<String> getAllUsersWithBookOnDate(LocalDate date){
        Collection<String> users = borrowTransactionRepository.findByFromDateLessThanEqualAndToDateGreaterThanEqual(date,date)
            .stream()
            .map(trxn->trxn.getUser().getFirstName() + " " + trxn.getUser().getLastName())
            .collect(Collectors.toSet());

        return users;
    }

    public Collection<String> getAllBooksByUserInRange(String firstName, String lastName, LocalDate startDate, LocalDate endDate){

        User user = userRepository.findByFirstNameAndLastName(firstName, lastName).stream().findFirst().orElse(null);

        if (user==null){
            return Collections.emptyList();
        }

        List<String> books = borrowTransactionRepository.findByUserAndFromDateToDateBetween(user, startDate, endDate)
            .stream()
            .map(trxn->trxn.getBook().getTitle())
            .collect(Collectors.toList());

        return books;

    }

    public Collection<String> getAllUnBorrowedBooks(){
        List<String> books = bookRepository.findAll().stream().filter(book -> book.getBorrowTransactions().isEmpty()).map(
            Book::getTitle).collect(
            Collectors.toList());

        return books;
    }


}
