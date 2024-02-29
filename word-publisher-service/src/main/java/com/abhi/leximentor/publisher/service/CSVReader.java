package com.abhi.leximentor.publisher.service;

import com.abhi.leximentor.publisher.dto.CsvDTO;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class CSVReader {
    private String csvFilePath = "gre_words.csv";
    private List<String> getWords;


    public List<String> getGetWords() {
        if (CollectionUtils.isNotEmpty(getWords)) {
            return getWords;
        }
        read();
        return getWords;
    }

    private void read() {
        getWords = new LinkedList<>();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(csvFilePath);
        try (FileReader fileReader = new FileReader(new ClassPathResource(csvFilePath).getFile())) {
            List<CsvDTO> records = new CsvToBeanBuilder<CsvDTO>(fileReader)
                    .withType(CsvDTO.class)
                    .build()
                    .parse();
            for (CsvDTO record : records) {
                getWords.add(record.getCol1().split(" ")[0]);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
