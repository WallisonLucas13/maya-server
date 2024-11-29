package com.example.ia.mayaAI.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDDeserializer extends JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String objectIdHex = p.getText();  // Lê como String

        if (ObjectId.isValid(objectIdHex)) {
            ObjectId objectId = new ObjectId(objectIdHex);
            return convertObjectIdToUUID(objectId);
        } else {
            throw new IllegalArgumentException("Invalid ObjectId format: " + objectIdHex);
        }
    }

    private UUID convertObjectIdToUUID(ObjectId objectId) {
        byte[] objectIdBytes = objectId.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.put(objectIdBytes);
        buffer.putInt(12, 0); // Preenche os últimos 4 bytes com zeros

        return new UUID(buffer.getLong(0), buffer.getLong(8));
    }
}
