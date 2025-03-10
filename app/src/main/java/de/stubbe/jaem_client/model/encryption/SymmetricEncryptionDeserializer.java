package de.stubbe.jaem_client.model.encryption;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class SymmetricEncryptionDeserializer implements JsonDeserializer<SymmetricEncryption> {
    @Override
    public SymmetricEncryption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String algorithm = json.getAsString();
        return SymmetricEncryption.Companion.fromAlgorithm(algorithm);
    }
}
