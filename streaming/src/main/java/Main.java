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

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "smart_log_app_id1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dev-showone-01.etr.eastbanctech.ru:9092");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, CustomTimestampExtractor.class);
        StreamsConfig streamingConfig = new StreamsConfig(props);

        long fiveMinutes = 1000 * 60 * 5;
        long fifteenMinutes = 1000 * 60 * 15;
        long day = 1000 * 60 * 60 * 24;
        long oneMinutes = 1000 * 60;
        long twoMinutes = 1000 * 60 * 2;
        long twentySeconds = 1000 * 20;
        long tenSeconds = 1000 * 10;

        JsonSerializer<LogRow> logRowSerializer = new JsonSerializer<>();
        JsonDeserializer<LogRow> logRowJsonDeserializer = new JsonDeserializer<>(LogRow.class);
        Serde<LogRow> logSerde = Serdes.serdeFrom(logRowSerializer, logRowJsonDeserializer);

        JsonSerializer<Step> stepJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Step> stepJsonDeserializer = new JsonDeserializer<>(Step.class);
        Serde<Step> stepSerde = Serdes.serdeFrom(stepJsonSerializer, stepJsonDeserializer);

        JsonSerializer<Flow> flowJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Flow> flowJsonDeserializer = new JsonDeserializer<>(Flow.class);
        Serde<Flow> flowSerde = Serdes.serdeFrom(flowJsonSerializer, flowJsonDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<LogRow, LogRow> allDataStream = builder.stream("smart-log", Consumed.with(logSerde, logSerde)
                .withOffsetResetPolicy(EARLIEST)
        );



        allDataStream
                //Фильтрация
                .filter((k, row) -> row.getApplicationName() != null)
                .filter((k, row) -> row.getApplicationName().equals("PORTAL"))
                .filter((k, row) -> Mapping.hasSemantic(row.getMessage()))

                //Map Добавление семантики. Ключ - пользовательская сессия
                .map((k, v) ->
                        new KeyValue<>(v.getUsername() + "_" + v.getSessionId().substring(0, 3),
                                new Step(v, Mapping.getSemantic(v.getMessage()))))
                .peek((s, w) -> System.out.println(
                        "---1." +
                        "\n\tt: " + w.getLogRow().getTimestamp() +
                                " l: " + w.getLogRow().getUsername() +
                                " sid: " + w.getLogRow().getSessionId().substring(0, 3) +
                                " s: " + w.getSemantic()))



                //Группировка во ru.trueengineering.model.Flow на основе пользовательской сессии
                .groupBy((k, v) -> k, Serialized.with(Serdes.String(), stepSerde))
                .windowedBy(SessionWindows.with(twentySeconds))
                .aggregate(Flow::new,
                        (key, value, aggregate) -> aggregate.addStep(value),
                        (aggKey, aggOne, aggTwo) -> aggTwo,
                        Materialized.with(Serdes.String(), flowSerde))
                .toStream()
//                .filter((key, value) -> value != null)//попадаются null
                .peek((kw, v) -> {
                    long window = kw.window().end() - kw.window().start();
                    System.out.println(
                            "---2." +
                                    "\n\twindow " + window + " ms " +
                                    "\n\tstart " + new Date(kw.window().start()) +
                                    "\n\t  end " + new Date(kw.window().end()) +

                                    "\n\tk: " + kw +
                                    " v: " + (v==null ? v : v.getSteps().size()));
                })
//
                .map((k, v) ->
                        new KeyValue<>(k.key(), v))

                .to(  "raw-flows-log",  Produced.with(Serdes.String(), flowSerde));//Записываем сценарии
//

                //TODO научится кастомизировать отсечку:
                //  - по смене локатора
                //  - по смене направлений в Avail
                //  - по наступлению Issue

                //Map Смена ключа - теперь ключом является сценарий
//                .map((k, v) ->
//                        new KeyValue<>(v.buildKey(), v))
//                .peek((k, v) -> System.out.println(
//                        "---3. k: " + k +
//                                " v: " + v))
//
//                .to(  "flows-log",  Produced.with(Serdes.String(), flowSerde));//Записываем сценарии
//

//                .groupBy((k, v) -> k, Serialized.with(Serdes.String(), flowSerde))
//                .count()
//                .toStream()
//
//                .peek((k, w) -> System.out.println(
//                        "k: " + k +
//                                " count: " + w));
//                .print(Printed.<Windowed<String>, Long>toSysOut()
//                .withLabel("Customer Ops Counts"));

//                .peek((s, w) -> System.out.println(
//                        "t: " + w.getLogRow().getTimestamp() +
//                                " l: " + w.getLogRow().getUsername() +
//                                " sid: " + w.getLogRow().getSessionId().substring(0, 3) +
//                                " s: " + w.getSemantic()));
//                .to("step-log",  Produced.with(logSerde, wrapperSerde));

//<<<BRANCHING & JOINING
//        Predicate<String, ru.trueengineering.model.Step> avails = (key, wrapper) -> wrapper.getSemantic().equalsIgnoreCase("Availability");
//        Predicate<String, ru.trueengineering.model.Step> pricings = (key, wrapper) -> wrapper.getSemantic().equalsIgnoreCase("Pricing");
//
//        final int AVAILS = 0;
//        final int PRICINGS = 1;
//        KStream<String, ru.trueengineering.model.Step>[] branchedSteps = stepsKStream.selectKey((k,v) -> k).branch(avails, pricings);
//
//        branchedSteps[AVAILS].peek((k,v)-> System.out.println("avails: " + v.getLogRow()));
//        branchedSteps[PRICINGS].peek((k,v)-> System.out.println("pricings: " + v.getLogRow()));
//
//
//        ValueJoiner<ru.trueengineering.model.Step, ru.trueengineering.model.Step, ru.trueengineering.model.Flow> joiner = new StepsJoiner();
//
//        JoinWindows twentyMinuteWindow =  JoinWindows.of(60 * 1000 * 20);
//
//        KStream<String, ru.trueengineering.model.Flow> joinedKStream = branchedSteps[AVAILS].join(branchedSteps[PRICINGS],
//                joiner,
//                twentyMinuteWindow,
//                Joined.with(Serdes.String(), wrapperSerde, wrapperSerde));//join по ключу hrdt_ult_qaa_iVb
//
//        joinedKStream.peek(((key, value) -> System.out.println("joined: k " + key + " val: " + value)));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamingConfig);
        kafkaStreams.setStateListener(((newState, oldState) -> {
            System.out.println("STATE CHANGE old " + oldState +" new " + newState);
            if (newState == KafkaStreams.State.RUNNING && oldState == KafkaStreams.State.REBALANCING) {
                System.out.println("REBALANCING");
            } else if (newState != KafkaStreams.State.RUNNING) {
                System.out.println("NOT RUNNING");
            } }));
        kafkaStreams.start();
        Thread.sleep(35000);
    }
}
