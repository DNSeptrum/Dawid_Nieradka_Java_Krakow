package com.ocado.helperclass;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class JsonLoader {
    public static List<String> loadItems(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Paths.get(filePath).toFile(), List.class);
    }
}
