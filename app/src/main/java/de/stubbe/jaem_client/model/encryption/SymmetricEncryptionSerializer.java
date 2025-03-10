package de.stubbe.jaem_client.model.encryption;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class SymmetricEncryptionSerializer implements JsonSerializer<SymmetricEncryption> {
    @Override
    public JsonElement serialize(SymmetricEncryption src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getAlgorithm());
    }
}
