package ru.trueengineering;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

    public YandexCrawlerJob(Producer producer) {
        this.producer = producer;
    }

    @Scheduled(fixedRate = 500)
    public void doJob() throws IOException, XMLStreamException {
        String xmlInput = restTemplate.getForObject("https://export.yandex.ru/last/last20x.xml",String.class);
        InputStream is = new ByteArrayInputStream(xmlInput.getBytes(StandardCharsets.UTF_8));


        RootSniffingXMLStreamReader sr = new RootSniffingXMLStreamReader(XMLInputFactory.newFactory().createXMLStreamReader(is));
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new SimpleModule().addDeserializer(Object.class, new ArrayInferringUntypedObjectDeserializer()));

        Map map = (Map) xmlMapper.readValue(sr, Object.class);
        List<Object> items = (List)((Map)map.get("last20x")).get("item");

        items.stream()
                .map(i -> ((Map)i).get(""))
                .map(s -> (String)s)
                .forEach(producer::sendMessage);
    }
}
