import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
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

public class RawToStepMain {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "raw_to_step_app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dev-showone-01.etr.eastbanctech.ru:9092");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, CustomTimestampExtractor.class);

        Serde<LogRow> logSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(LogRow.class));
        Serde<Step> stepSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Step.class));

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, LogRow> allDataStream = builder.stream("smart-log", Consumed.with(Serdes.String(), logSerde)
                .withOffsetResetPolicy(EARLIEST)
        );

        allDataStream
                //Фильтрация
                .filter((k, row) -> row.getApplicationName() != null)
                .filter((k, row) -> row.getApplicationName().equals("PORTAL"))
                .filter((k, row) -> Mapping.hasSemantic(row.getMessage()))

                //Map Добавление семантики. Ключ - пользовательская сессия
                .map((k, row) ->
                        new KeyValue<>(row.getUsername() + "_" + row.getSessionId().substring(0, 3),
                                new Step(row, Mapping.getSemantic(row.getMessage()))))
                .peek((s, w) -> System.out.println(
                        "---1." +
                        "\n\tt: " + w.getLogRow().getTimestamp() +
                                " l: " + w.getLogRow().getUsername() +
                                " sid: " + w.getLogRow().getSessionId().substring(0, 3) +
                                " s: " + w.getSemantic()))



                .to(  "smart-business-steps-log",  Produced.with(Serdes.String(), stepSerde));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), props);
        kafkaStreams.start();

        Thread.sleep(35000);
    }
}
