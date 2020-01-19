package ru.trueengineering.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArrayInferringUntypedObjectDeserializer extends UntypedObjectDeserializer {

    @Override
    protected Map<String,Object> mapObject(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        if (t == JsonToken.END_OBJECT) { // empty map, eg {}
            // empty LinkedHashMap might work; but caller may want to modify... so better just give small modifiable.
            return new LinkedHashMap<String,Object>(2);
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        do {
            String fieldName = jp.getCurrentName();
            jp.nextToken();
            result.put(fieldName, handleMultipleValue(result, fieldName, deserialize(jp, ctxt)));
        } while (jp.nextToken() != JsonToken.END_OBJECT);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object handleMultipleValue(Map<String, Object> map, String key, Object value) {
        if (!map.containsKey(key)) {
            return value;
        }

        Object originalValue = map.get(key);
        if (originalValue instanceof List) {
            ((List) originalValue).add(value);
            return originalValue;
        }
        else {
            ArrayList newValue = new ArrayList();
            newValue.add(originalValue);
            newValue.add(value);
            return newValue;
        }
    }

}