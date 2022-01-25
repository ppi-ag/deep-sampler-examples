/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package recorder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A custom serializer for Jackson. DeepSampler is merely passing the serializer through to Jackson. The implementation
 * is not part of DeepSampler. See <a href="https://www.baeldung.com/jackson-custom-serialization">jackson custom serialization</a>
 * for more information.
 */
public class StarDateJsonSerializer extends StdSerializer<LocalDateTime> {

    private static final DateTimeFormatter STAR_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyDDD.AAAA");

    public StarDateJsonSerializer() {
        super(LocalDateTime.class);
    }


    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String starDate = STAR_DATE_FORMATTER.format(value);
        gen.writeString(starDate);
    }

    @Override
    public void serializeWithType(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
        serialize(value, gen, serializers);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
}
