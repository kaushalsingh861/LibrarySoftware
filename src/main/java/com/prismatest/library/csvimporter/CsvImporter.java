package com.prismatest.library.csvimporter;

import com.prismatest.library.commons.DataFile;
import com.prismatest.library.commons.Gender;
import com.prismatest.library.entity.Book;
import com.prismatest.library.entity.BorrowTransaction;
import com.prismatest.library.entity.User;
import com.prismatest.library.repository.BookRepository;
import com.prismatest.library.repository.BorrowTransactionRepository;
import com.prismatest.library.repository.UserRepository;
import java.io.FileReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
@Slf4j
public class CsvImporter {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private final BorrowTransactionRepository borrowTransactionRepository;

    private final ResourceLoader resourceLoader;

    //visible fo test
    public Iterable<CSVRecord> fetchFile(String path, String[] headers){
        Resource resource = resourceLoader.getResource(path);
        List<CSVRecord> data = new ArrayList<>();

        try(Reader in = new FileReader(resource.getFile())) {

            Iterable<CSVRecord> records =   CSVFormat.Builder.create()
                .setHeader(headers)
                .setSkipHeaderRecord(true)
                .setDelimiter(",").build()
                .parse(in);

            for (CSVRecord csvRecord: records){
                data.add(csvRecord);
            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections::emptyIterator;
    }

    Iterable<CSVRecord> fetchFile(DataFile dataFile){

        return fetchFile(dataFile.filePath, dataFile.headers);

    }


    //VisibleForTesting
    public void saveUsersFromCSV(Iterable<CSVRecord> records){
        for (CSVRecord csvRecord : records){
            try{
                String lastName = csvRecord.get(DataFile.User.headers[0]).trim();
                String firstName = csvRecord.get(DataFile.User.headers[1]).trim();
                String memberSince = csvRecord.get(DataFile.User.headers[2]).trim();
                String memberTill = csvRecord.get(DataFile.User.headers[3]).trim();
                String gender = csvRecord.get(DataFile.User.headers[4]).trim();

                LocalDate memberSinceDate =  parseDate(memberSince);

                if (memberSinceDate == null){
                    log.error("memberSinceDate for {} is null, skipping", csvRecord);
                    continue;
                }

                LocalDate memberTillDate =  parseDate(memberTill);

                Gender genderEnum = Objects.equals(gender, "m") ? Gender.MALE : Gender.FEMALE;

                User user = User
                    .builder()
                    .lastName(lastName)
                    .firstName(firstName)
                    .memberSince(memberSinceDate)
                    .memberTill(memberTillDate)
                    .gender(genderEnum)
                    .build();

                userRepository.save(user);

            } catch (Exception e){
                log.error("error occurred during user import {} , skipping csvRecord", csvRecord, e);
            }

        }
    }

    //VisibleForTesting
    public void saveBooksFromCsv(Iterable<CSVRecord> records){

        for (CSVRecord csvRecord : records){
            try{
                String title = csvRecord.get(DataFile.Books.headers[0]).trim();
                String author = csvRecord.get(DataFile.Books.headers[1]).trim();
                String genre = csvRecord.get(DataFile.Books.headers[2]).trim();
                String publisher = csvRecord.get(DataFile.Books.headers[3]).trim();

                if (!StringUtils.hasLength(title)){
                    log.error("blank title book record in CSV");
                    continue;
                }

                Book book = Book
                    .builder()
                    .title(title)
                    .author(author)
                    .genre(genre)
                    .publisher(publisher)
                    .build();

                bookRepository.save(book);

            } catch (Exception e){
                log.error("error occurred during book import {} , skipping csvRecord", csvRecord, e);
            }

        }
    }

    //VisibleForTesting
    public void saveBookTransactionsFromCSV(Iterable<CSVRecord> records){

        for (CSVRecord csvRecord: records){
            try {

                String borrower = csvRecord.get(DataFile.Borrowed.headers[0]).trim();
                String title = csvRecord.get(DataFile.Borrowed.headers[1]).trim();
                String from = csvRecord.get(DataFile.Borrowed.headers[2]).trim();
                String to = csvRecord.get(DataFile.Borrowed.headers[3]).trim();



                String[] nameSplit = borrower.split(",");

                String firstName = nameSplit[1];
                String lastName = nameSplit[0];

                User user = userRepository.findByFirstNameAndLastName(firstName, lastName).get(0);

                if (user==null){
                    log.error("no user found for transaction {} skipping record",csvRecord);
                    continue;

                }

                Book bookObject = bookRepository.findByTitle(title).orElse(null);

                if (bookObject == null){
                    log.error("no book found for transaction {}, skipping record", csvRecord);
                    continue;
                }

                LocalDate fromDate = parseDate(from);
                LocalDate toDate = parseDate(to);

                BorrowTransaction borrowTransaction = BorrowTransaction.builder()
                    .book(bookObject)
                    .user(user)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .build();

                borrowTransactionRepository.save(borrowTransaction);

            }catch (Exception e){
                log.error("error occurred during transaction import {} , skipping csvRecord", csvRecord, e);
            }
        }

    }

    public static LocalDate parseDate(String date){

        if (!StringUtils.hasLength(date)){
            return null;
        }

        try{
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (Exception e){
           log.info("date failed to match pattern MM/dd/yyyy, trying with dd-MM-yyyy");
        }

        try {
           return  LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }catch (Exception e){
            log.info("date failed to match pattern with dd-MMM-yyyy, unable to parse date");
        }

        return null;

    }

    public void importDataToDb(){
        saveUsersFromCSV(fetchFile(DataFile.User));
        saveBooksFromCsv(fetchFile(DataFile.Books));
        saveBookTransactionsFromCSV(fetchFile(DataFile.Borrowed));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        log.info("data import starting");

        importDataToDb();

    }

}
