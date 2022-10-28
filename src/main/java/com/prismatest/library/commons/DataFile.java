package com.prismatest.library.commons;

public enum DataFile {
    User("classpath:user.csv", new String[]{"Name", "First name", "Member since", "Member till", "Gender"}),
    Books("classpath:books.csv", new String[]{"Title", "Author", "Genre", "Publisher"}),
    Borrowed("classpath:borrowed.csv", new String[]{"Borrower", "Book", "borrowed from", "borrowed to"});

    public final String filePath;

    public String[] headers;

    DataFile(String fileName, String[] headers){
        this.filePath = fileName;
        this.headers = headers;
    }
}