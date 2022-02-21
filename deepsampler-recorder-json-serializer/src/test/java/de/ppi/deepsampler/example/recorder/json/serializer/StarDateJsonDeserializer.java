/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.example.recorder.json.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A custom deserializer for Jackson. DeepSampler is merely passing the deserializer through to Jackson. The implementation
 * is not part of DeepSampler. See <a href="https://www.baeldung.com/jackson-deserialization">jackson custom deserialization</a>
 * for more information.
 */
public class StarDateJsonDeserializer extends StdDeserializer<LocalDateTime> {

    private static final DateTimeFormatter STAR_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyDDD.AAAA");

    public StarDateJsonDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec objectCodec = p.getCodec();
        JsonNode node = objectCodec.readTree(p);

        String starDate = node.asText();

        return LocalDateTime.parse(starDate, STAR_DATE_FORMATTER);
    }
}
