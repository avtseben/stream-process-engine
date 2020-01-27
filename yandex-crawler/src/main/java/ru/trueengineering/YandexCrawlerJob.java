package ru.trueengineering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jdk.nashorn.internal.ir.ObjectNode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.trueengineering.xml.ArrayInferringUntypedObjectDeserializer;
import ru.trueengineering.xml.RootSniffingXMLStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YandexCrawlerJob {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Producer producer;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;

    public YandexCrawlerJob(Producer producer) {
        this.producer = producer;
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(
                new SimpleModule().addDeserializer(Object.class,
                        new ArrayInferringUntypedObjectDeserializer()));

    }

//    @Scheduled(fixedRate = 500)
    public void doJob() throws IOException, XMLStreamException {
        String xmlInput = restTemplate.getForObject("https://export.yandex.ru/last/last20x.xml",String.class);
        InputStream is = new ByteArrayInputStream(xmlInput.getBytes(StandardCharsets.UTF_8));

        RootSniffingXMLStreamReader sr = new RootSniffingXMLStreamReader(
                XMLInputFactory.newFactory().createXMLStreamReader(is));

        Map map = (Map) xmlMapper.readValue(sr, Object.class);
        List<Object> items = (List)((Map)map.get("last20x")).get("item");

        for (Object i : items) {
            String text = (String)((Map) i).get("");
            YandexItem yandexItem = new YandexItem(text);
            producer.sendMessage("yandex-crawler",jsonMapper.writeValueAsString(yandexItem));
        }
    }
}
