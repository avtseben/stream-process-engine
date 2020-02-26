package ru.trueengineering;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import ru.trueengineering.model.LogRow;

import java.time.Instant;

public class CustomTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        return Instant.parse(((LogRow)record.value()).getTimestamp()).toEpochMilli();
    }
}
