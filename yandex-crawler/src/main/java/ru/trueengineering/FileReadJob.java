package ru.trueengineering;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileReadJob {

    private final Producer producer;

    public FileReadJob(Producer producer) {
        this.producer = producer;

    }


    @Scheduled(fixedRate = 1000)
    public void doJob() throws IOException {
        FileInputStream fis = new FileInputStream(new File("/opt/salplan-logs/sales-planner.log"));
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        List<String> lines = br.lines().collect(Collectors.toList());

        for (String l : lines) {
            producer.sendMessage("planner-log1",l);
        }
    }
}
