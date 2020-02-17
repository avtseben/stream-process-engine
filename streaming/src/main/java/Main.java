import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "smart_log_app_id");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dev-showone-01.etr.eastbanctech.ru:9092");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, CustomTimestampExtractor.class);
        StreamsConfig streamingConfig = new StreamsConfig(props);

        JsonSerializer<LogRow> logRowSerializer = new JsonSerializer<>();
        JsonDeserializer<LogRow> logRowJsonDeserializer = new JsonDeserializer<>(LogRow.class);
        Serde<LogRow> logSerde = Serdes.serdeFrom(logRowSerializer,logRowJsonDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<LogRow, LogRow> simpleFirstStream = builder.stream("smart-log",  Consumed.with(logSerde, logSerde));
        KStream<LogRow, LogRow> upperCasedStream = simpleFirstStream.mapValues(
                value -> {
                    return value;
                }
        );

        upperCasedStream
                .filter((k,row) -> row.getApplicationName().equals("PORTAL"))
                .peek((s,r) -> {
                    System.out.println(r);

                })


        ;

        upperCasedStream.to( "portal-log", Produced.with(logSerde, logSerde));
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(),streamingConfig);
        kafkaStreams.start();
        Thread.sleep(35000);
//        LOG.info("Shutting down the Yelling APP now"); kafkaStreams.close();
    }
}
