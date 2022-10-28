package com.prismatest.library.csvimporter;

import com.prismatest.library.LibraryApplicationTests;
import com.prismatest.library.commons.DataFile;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CSVImportTest extends LibraryApplicationTests {

    @Autowired
    CsvImporter csvImporter;

    @Test
    void csvFileShouldBeImported() throws IOException {

        String csvData = "Name,First name,Member since,Member till,Gender\n"
            + "Aexi,Liam,01-01-2010,,m";

        File file = File.createTempFile("temp", null);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(csvData);

        Iterable<CSVRecord> data = csvImporter.fetchFile(file.getAbsolutePath(), DataFile.User.headers);

        for (CSVRecord csvRecord: data){
            Assertions.assertThat(csvRecord.get(DataFile.User.headers[0])).isEqualTo("Aexi");
        }



    }



}
