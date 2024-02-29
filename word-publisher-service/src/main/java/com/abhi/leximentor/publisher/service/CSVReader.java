package com.abhi.leximentor.publisher.service;

import com.abhi.leximentor.publisher.dto.CsvDTO;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class CSVReader {
    private String csvFilePath;

    public void read() {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(csvFilePath);

        try (FileReader fileReader = new FileReader(new ClassPathResource(csvFilePath).getFile())) {
            List<CsvDTO> records = new CsvToBeanBuilder<CsvDTO>(fileReader)
                    .withType(CsvDTO.class)
                    .build()
                    .parse();
            for (CsvDTO record : records) {
                System.out.println(record); // Process each record as a Java object
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        CSVReader csvReader = new CSVReader("gre_words.csv");
        csvReader.read();
    }
}
