# LibrarySoftware

Features: 

REST API that satisfies the following requirements.

1. Returns all users who have actually borrowed at least one book.
   **(localhost:8080/library/borrowed-user)**
2. Returns all non-terminated users who have not currently borrowed anything.
   **(localhost:8080/library/no-history-user)**
3. Returns all users who have borrowed a book on a given date.
   **(localhost:8080/library/borrowed-user?date=mm-dd-yyyy)**
4. Returns all books borrowed by a given user in a given date range.
   **(localhost:8080/library/books/user?firstName=foo&lastName=bar&startDate=mm-dd-yyyy&endDate=mm-dd-yyyy)**
5. Returns all available (not borrowed) books. 
   **(localhost:8080/library/books/unborrowed)**
