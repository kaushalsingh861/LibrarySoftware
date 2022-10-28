package com.prismatest.library.service;

import com.prismatest.library.LibraryApplicationTests;
import com.prismatest.library.Service.LibraryService;
import com.prismatest.library.commons.DataFile;
import com.prismatest.library.csvimporter.CsvImporter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(Lifecycle.PER_CLASS)
class ServiceTest extends LibraryApplicationTests {

    @Autowired
    LibraryService libraryService;

    @Autowired
    CsvImporter csvImporter;

    @BeforeEach
    @AfterEach
    void cleanup(){
        borrowTransactionRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

    }

    @Test
    void shouldReturnUsersWhoAtLeastBorrowedOneBook() throws IOException {
        String bookCsv = "Title,Author,Genre,Publisher\n"
            + "\"Data Smart\",\"Drucker, Peter\",economics,Random House\n"
            + "\"Birth of a Theorem\",\"Eraly, Abraham\",history,Penguin\n";

        String userCsv = "Name,First name,Member since,Member till,Gender\n"
            + "Aexi,Liam,01-01-2010,,m\n" //Borrowed book
            + "Zhungwang,Noah,11/24/2020,,m\n" // Borrowed book
            + "Odum,Oliver,05-08-1999,,m\n"; //Didnt borrowed book

        String borrowed = "Borrower,Book,borrowed from,borrowed to\n"
            + "\"Aexi,Liam\",Data Smart,05/14/2008,05/29/2008\n"
            + "\"Zhungwang,Noah\",Birth of a Theorem,06/28/2021,07/13/2021\n";

        saveUser(userCsv);
        saveBook(bookCsv);
        saveBorrow(borrowed);

        List<String> users = libraryService.getAllUsersWithAtLeastOneBookBorrowHistory();

        Assertions.assertThat(users)
            .containsExactlyInAnyOrder("Liam Aexi", "Noah Zhungwang");


    }

    @Test
    void shouldReturnUsersWhoDidNotBorrowedBookAtAll() throws IOException {
        String bookCsv = "Title,Author,Genre,Publisher\n"
            + "\"Data Smart\",\"Drucker, Peter\",economics,Random House\n"
            + "\"Birth of a Theorem\",\"Eraly, Abraham\",history,Penguin\n";

        String userCsv = "Name,First name,Member since,Member till,Gender\n"
            + "Aexi,Liam,01-01-2010,,m\n" //Borrowed book
            + "Zhungwang,Noah,11/24/2020,,m\n" // Borrowed book
            + "Odum,Oliver,05-08-1999,,m\n" //Didnt borrowed book
             + "Odum, Jack,05-08-1999,01-01-2021,m\n"; //Didnt borrowed book but terminated

        String borrowed = "Borrower,Book,borrowed from,borrowed to\n"
            + "\"Aexi,Liam\",Data Smart,05/14/2008,05/29/2008\n"
            + "\"Zhungwang,Noah\",Birth of a Theorem,06/28/2021,07/13/2021\n";

        saveUser(userCsv);
        saveBook(bookCsv);
        saveBorrow(borrowed);

        List<String> users = libraryService.getAllUsersWithNoBookHistory(true);

        Assertions.assertThat(users)
            .containsExactlyInAnyOrder("Oliver Odum");

        users = libraryService.getAllUsersWithNoBookHistory(false);

        Assertions.assertThat(users)
            .containsExactlyInAnyOrder("Oliver Odum", "Jack Odum");

    }

    @Test
    void shouldReturnAllUsersBorrowedOnGivenDate() throws IOException {
        String bookCsv = "Title,Author,Genre,Publisher\n"
            + "\"Title1\",\"Drucker, Peter\",economics,Random House\n" //Borrowed
            + "\"Title2\",\"Eraly, Abraham\",history,Penguin\n" //Borrowed
            + "\"Title3\",\"Eraly, Abraham\",history,Penguin\n" // Borrowed on different month than above
            + "\"Title4\",\"Eraly, Abraham\",history,Penguin\n"; //Not borrowed at all

        String userCsv = "Name,First name,Member since,Member till,Gender\n"
            + "Aexi,Liam,01-01-2010,,m\n"
            + "Zhungwang,Noah,11/24/2020,,m\n"
            + "Odum,Oliver,05-08-1999,,m\n"
            + "Odum, Jack,05-08-1999,,m\n";

        String borrowed = "Borrower,Book,borrowed from,borrowed to\n"
            + "\"Aexi,Liam\",Title1,05/14/2021,07/28/2021\n"
            + "\"Zhungwang,Noah\",Title2,05/14/2021,07/30/2021\n"
            + "\"Odum,Oliver\",Title3,03/28/2021,04/30/2021\n";

        saveUser(userCsv);
        saveBook(bookCsv);
        saveBorrow(borrowed);

        Collection<String> books = libraryService.getAllUsersWithBookOnDate(LocalDate.of(2021, 7, 25));

        Assertions.assertThat(books)
            .containsExactlyInAnyOrder("Liam Aexi", "Noah Zhungwang");

        books = libraryService.getAllUsersWithBookOnDate(LocalDate.of(2021, 4, 25));

        Assertions.assertThat(books)
            .containsExactlyInAnyOrder("Oliver Odum");
    }

    @Test
    void shouldGiveAllTheBooksBorrowedByUserInGivenRange() throws IOException {
        String bookCsv = "Title,Author,Genre,Publisher\n"
            + "\"Title1\",\"Drucker, Peter\",economics,Random House\n" //Borrowed
            + "\"Title2\",\"Eraly, Abraham\",history,Penguin\n" //Borrowed
            + "\"Title3\",\"Eraly, Abraham\",history,Penguin\n" // Borrowed on different month than above
            + "\"Title4\",\"Eraly, Abraham\",history,Penguin\n"; //Not borrowed at all

        String userCsv = "Name,First name,Member since,Member till,Gender\n"
            + "Aexi,Liam,01-01-2010,,m\n";

        String borrowed = "Borrower,Book,borrowed from,borrowed to\n"
            + "\"Aexi,Liam\",Title1,05/14/2021,07/28/2021\n"
            + "\"Aexi,Liam\",Title2,05/14/2021,07/30/2021\n"
            + "\"Aexi,Liam\",Title3,03/28/2021,04/30/2021\n";

        saveUser(userCsv);
        saveBook(bookCsv);
        saveBorrow(borrowed);

        Collection<String> books = libraryService.getAllBooksByUserInRange("Liam", "Aexi",
            LocalDate.of(2021, 5, 1), LocalDate.of(2021, 8, 1));

        Assertions.assertThat(books)
            .containsExactlyInAnyOrder("Title1","Title2");

        books = libraryService.getAllBooksByUserInRange("Liam", "Aexi",
            LocalDate.of(2021, 2, 1), LocalDate.of(2021, 5, 1));

        Assertions.assertThat(books)
            .containsExactlyInAnyOrder("Title3");
    }

    @Test
    void shouldReturnAllBooksUnborrowed() throws IOException {
        String bookCsv = "Title,Author,Genre,Publisher\n"
            + "\"Title1\",\"Drucker, Peter\",economics,Random House\n" //Borrowed
            + "\"Title2\",\"Eraly, Abraham\",history,Penguin\n" //Borrowed
            + "\"Title3\",\"Eraly, Abraham\",history,Penguin\n" // Borrowed on different month than above
            + "\"Title4\",\"Eraly, Abraham\",history,Penguin\n"; //Not borrowed at all

        String userCsv = "Name,First name,Member since,Member till,Gender\n"
            + "Aexi,Liam,01-01-2010,,m\n"
            + "Zhungwang,Noah,11/24/2020,,m\n"
            + "Odum,Oliver,05-08-1999,,m\n"
            + "Odum, Jack,05-08-1999,,m\n";

        String borrowed = "Borrower,Book,borrowed from,borrowed to\n"
            + "\"Aexi,Liam\",Title1,05/14/2021,07/28/2021\n"
            + "\"Zhungwang,Noah\",Title2,05/14/2021,07/30/2021\n"
            + "\"Odum,Oliver\",Title3,03/28/2021,04/30/2021\n";

        saveUser(userCsv);
        saveBook(bookCsv);
        saveBorrow(borrowed);

        Collection<String> books = libraryService.getAllUnBorrowedBooks();

        Assertions.assertThat(books)
            .containsExactlyInAnyOrder("Title4");

    }




    void saveUser(String csv) throws IOException {

        Reader reader = new StringReader(csv);

        Iterable<CSVRecord> records =   CSVFormat.Builder.create()
            .setHeader( DataFile.User.headers)
            .setSkipHeaderRecord(true)
            .setDelimiter(",").build()
            .parse(reader);

        csvImporter.saveUsersFromCSV(records);
        reader.close();

    }

    void saveBook(String csv) throws IOException {

        Reader reader = new StringReader(csv);

        Iterable<CSVRecord> records =   CSVFormat.Builder.create()
            .setHeader( DataFile.Books.headers)
            .setSkipHeaderRecord(true)
            .setDelimiter(",").build()
            .parse(reader);

        csvImporter.saveBooksFromCsv(records);

        reader.close();
    }

    void saveBorrow(String csv) throws IOException {
        Reader reader = new StringReader(csv);

        Iterable<CSVRecord> records =   CSVFormat.Builder.create()
            .setHeader( DataFile.Borrowed.headers)
            .setSkipHeaderRecord(true)
            .setDelimiter(",").build()
            .parse(reader);

        csvImporter.saveBookTransactionsFromCSV(records);

        reader.close();
    }


}
