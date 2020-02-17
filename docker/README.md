0. CREATE STREAM yandex (text VARCHAR) WITH (KAFKA_TOPIC='yandex-crawler', VALUE_FORMAT='AVRO'); 
1. CREATE TABLE yandex_last20x (text VARCHAR) WITH (KAFKA_TOPIC='yandex-crawler', VALUE_FORMAT='AVRO', KEY = 'text');

2. Push запрос, отображает вывод только когда данные пришли
SELECT * FROM yandex_last20x WHERE text = 'Гугл' EMIT CHANGES;

Обычные pull запросы возможны только на materialized таблицах

3. DESCRIBE EXTENDED pageviews_female_like_89;

CREATE STREAM yandex3 (text VARCHAR) WITH (kafka_topic='yandex-crawler', value_format='JSON');