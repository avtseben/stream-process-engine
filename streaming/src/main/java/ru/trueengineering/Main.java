package ru.trueengineering;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Windowed;
import ru.trueengineering.model.Action;
import ru.trueengineering.model.Flow;
import ru.trueengineering.model.LogRow;

import java.time.Duration;
import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.EARLIEST;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "smart_ticketing_log_analyze_app44");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-demo.trueengineering.ru:9092");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, CustomTimestampExtractor.class);

        Duration twentySeconds = Duration.ofSeconds(20);
        Serde<LogRow> logSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(LogRow.class));
        Serde<Action> actionSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Action.class));
        Serde<Flow> flowSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Flow.class));

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, LogRow> rowLogs = builder.stream(
                "smart-ticketing-logs",
                Consumed.with(Serdes.String(), logSerde).withOffsetResetPolicy(EARLIEST)
        );

        KStream<String, LogRow> filteredLogs = rowLogs
                .filter((k, row) -> row.getApplicationName() != null)
                .filter((k, row) -> row.getApplicationName().equals("PORTAL"))
                .filter((k, row) -> Mapping.hasSemantic(row.getMessage()))
                .peek((k, v) -> System.out.println(
                        "--1." + " time: " + v.getTimestamp() + " message : " + v.getMessage()));

        KStream<String, Action> enrichedLogs = filteredLogs
                .map((k, row) ->
                        new KeyValue<>(k, new Action(row, Mapping.getSemantic(row.getMessage()))))
                .peek((k, v) -> System.out.println(
                        "--2." + "time: " + v.getLogRow().getTimestamp() + " action: " + v.getSemantic()));

        KTable<Windowed<String>, Flow> kTable = enrichedLogs
                .groupBy((k, action) -> action.getLogRow().getUsername() + "_" + action.getLogRow().getSessionId().substring(0,3),
                        Serialized.with(Serdes.String(), actionSerde))
                .windowedBy(SessionWindows.with(twentySeconds))
                .aggregate(Flow::new,
                        (key, value, aggregate) -> aggregate.addAction(value),
                        (aggKey, aggOne, aggTwo) -> aggTwo,
                        Materialized.with(Serdes.String(), flowSerde));

        kTable
//                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
//                .suppress(Suppressed.untilTimeLimit(twentySeconds,Suppressed.BufferConfig.unbounded()))
                .toStream()
                .peek((kw, v) -> System.out.println("--3. key " + kw + " aggregated actions: " + v.getActions()))
                .filter((key, value) -> value != null)
                .map((k, v) -> new KeyValue<>(v.buildKey(), v))
                .to("smart-ticketing-business-actions", Produced.with(Serdes.String(), flowSerde));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), props);
        kafkaStreams.start();

        Thread.sleep(35000);
    }
}
