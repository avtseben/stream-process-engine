import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Properties;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "yelling_app_id");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dev-showone-01.etr.eastbanctech.ru:9092");
        StreamsConfig streamingConfig = new StreamsConfig(props);
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> simpleFirstStream = builder.stream("smart-log",  Consumed.with(stringSerde, stringSerde));
        KStream<String, String> upperCasedStream = simpleFirstStream.mapValues(
                value -> {
                    return value;
                }
        );

        upperCasedStream.peek((s,r) -> {
            System.out.println();

        });

        upperCasedStream.to( "out-topic", Produced.with(stringSerde, stringSerde));
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(),streamingConfig);
        kafkaStreams.start();
        Thread.sleep(35000);
//        LOG.info("Shutting down the Yelling APP now"); kafkaStreams.close();
    }
}
