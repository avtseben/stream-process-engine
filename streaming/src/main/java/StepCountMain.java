import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import ru.trueengineering.CustomTimestampExtractor;
import ru.trueengineering.JsonDeserializer;
import ru.trueengineering.JsonSerializer;
import ru.trueengineering.Mapping;
import ru.trueengineering.model.Flow;
import ru.trueengineering.model.LogRow;
import ru.trueengineering.model.Step;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.EARLIEST;

public class StepCountMain {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "step_count_app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dev-showone-01.etr.eastbanctech.ru:9092");


        final Map<String, Integer> stepCounts = new HashMap<>();

        Serde<Step> stepSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Step.class));

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Step> allDataStream = builder.stream("smart-business-steps-log",
                Consumed.with(Serdes.String(), stepSerde)
                .withOffsetResetPolicy(EARLIEST)
        );


        allDataStream
                .peek((k,v) -> {
                    Integer count = stepCounts.computeIfAbsent(v.getSemantic(), s -> 0);
                    stepCounts.put(v.getSemantic(),++count);
                    System.out.println("StepCounts: " + stepCounts);
                });
//        .groupBy((k,v) -> v.getSemantic())
//                .aggregate(new HashMap<String, Integer>(),
//
//
//                        new Aggregator<String, Step, Map>() {
//                            @Override
//                            public Map apply(String key, Step value, Map aggregate) {
//                                return null;
//                            }
//                        }
//
//                )


        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), props);
        kafkaStreams.start();
        Thread.sleep(35000);
    }
}
