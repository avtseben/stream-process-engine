import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import sun.awt.SunHints;

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
        Serde<LogRow> logSerde = Serdes.serdeFrom(logRowSerializer, logRowJsonDeserializer);

        JsonSerializer<Wrapper> wrapperSerializer = new JsonSerializer<>();
        JsonDeserializer<Wrapper> wrapperDeserializer = new JsonDeserializer<>(Wrapper.class);
        Serde<Wrapper> wrapperSerde = Serdes.serdeFrom(wrapperSerializer, wrapperDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<LogRow, LogRow> allDataStream = builder.stream("smart-log", Consumed.with(logSerde, logSerde));

        KStream<String,Wrapper> wrapperKStream = allDataStream
                .filter((k, row) -> row.getApplicationName() != null)
                .filter((k, row) -> row.getApplicationName().equals("PORTAL"))
                .filter((k, row) -> Mapping.hasSemantic(row.getMessage()))
                .map((k,v) -> new KeyValue<>(
                        v.getUsername()+"_"+v.getSessionId().substring(0,3),
                        new Wrapper(v, Mapping.getSemantic(v.getMessage()))))
                .peek((s, w) -> System.out.println(
                                "t: " + w.getLogRow().getTimestamp() +
                                " l: "+ w.getLogRow().getUsername() +
                                " sid: " + w.getLogRow().getSessionId().substring(0,3) +
                                " s: " + w.getSemantic()));
//                .to("step-log",  Produced.with(logSerde, wrapperSerde));


        Predicate<String, Wrapper> avails = (key, wrapper) -> wrapper.getSemantic().equalsIgnoreCase("Availability");
        Predicate<String, Wrapper> pricings = (key, wrapper) -> wrapper.getSemantic().equalsIgnoreCase("Pricing");

        final int AVAILS = 0;
        final int PRICINGS = 1;
        KStream<String, Wrapper>[] branchedSteps = wrapperKStream.selectKey((k,v) -> k).branch(avails, pricings);

        branchedSteps[AVAILS].peek((k,v)-> System.out.println("avails: " + v.getLogRow()));
        branchedSteps[PRICINGS].peek((k,v)-> System.out.println("pricings: " + v.getLogRow()));


        ValueJoiner<Wrapper, Wrapper, Scenario> joiner = new StepsJoiner();

        JoinWindows twentyMinuteWindow =  JoinWindows.of(60 * 1000 * 20);

        KStream<String, Scenario> joinedKStream = branchedSteps[AVAILS].join(branchedSteps[PRICINGS],
                joiner,
                twentyMinuteWindow,
                Joined.with(Serdes.String(), wrapperSerde, wrapperSerde));//join по ключу hrdt_ult_qaa_iVb

        joinedKStream.peek(((key, value) -> System.out.println("joined: k " + key + " val: " + value)));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamingConfig);
        kafkaStreams.start();
        Thread.sleep(35000);
    }
}
