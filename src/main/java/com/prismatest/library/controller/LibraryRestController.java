package com.prismatest.library.controller;

import com.prismatest.library.Service.LibraryService;
import com.prismatest.library.csvimporter.CsvImporter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import net.bytebuddy.asm.Advice.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/library")
public class LibraryRestController {

private final LibraryService libraryService;

    @GetMapping("/borrowed-user")
    public ResponseEntity<Collection<String>> getAllUsersWithAtLeastOneBookBorrowHistory(@RequestParam(name = "date", required = false)
                                                                                       String date){


        if (date == null){
            return ResponseEntity.ok(libraryService.getAllUsersWithAtLeastOneBookBorrowHistory());
        } else {
            LocalDate dateVal = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return ResponseEntity.ok(libraryService.getAllUsersWithBookOnDate(dateVal));
        }

    }

    @GetMapping("/no-history-user")
    public ResponseEntity<List<String>> getAllUserWithNoBorrowHistory(@RequestParam(name = "removeTerminatedUser", defaultValue = "true") boolean removeTerminatedUser){
        return ResponseEntity.ok(libraryService.getAllUsersWithNoBookHistory(removeTerminatedUser));
    }

    @GetMapping("/books/user")
    public ResponseEntity<Collection<String>> getAllBooksBorrowedByUserInRange(@RequestParam(name="firstName") String firstName,
                                                                         @RequestParam(name="lastName") String lastName,
                                                                         @RequestParam(name="startDate") String startDate,
                                                                         @RequestParam(name="endDate") String endDate){
        LocalDate startDateVal = CsvImporter.parseDate(startDate);
        LocalDate endDateVal = CsvImporter.parseDate(endDate);
        return ResponseEntity.ok(libraryService.getAllBooksByUserInRange(firstName, lastName, startDateVal, endDateVal));
    }

    @GetMapping("/books/unborrowed")
    public ResponseEntity<Collection<String>> getAllUnBorrowedBooks(){
        return ResponseEntity.ok(libraryService.getAllUnBorrowedBooks());
    }
}
