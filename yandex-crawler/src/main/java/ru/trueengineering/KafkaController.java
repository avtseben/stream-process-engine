package ru.trueengineering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    private final Producer producer;
    private final FileReadJob job;

    @Autowired
    KafkaController(Producer producer, FileReadJob job) {
        this.producer = producer;
        this.job = job;
    }

    @PostMapping(value = "/job")
    public void job() throws IOException {
        this.job.doJob();
    }
}