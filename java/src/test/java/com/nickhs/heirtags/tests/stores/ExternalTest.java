package com.nickhs.heirtags.tests.stores;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;
import com.nickhs.heirtags.stores.TagBagStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Reads from the external JSON representation
 *
 * Created by nickhs on 4/21/17.
 */
@Slf4j
public class ExternalTest {
    public static final Path testDirectory = Paths.get("../tests/core");
    private final Supplier<TagBagStore<String>> implFactory;

    public ExternalTest(Supplier<TagBagStore<String>> implFactory)  {
        this.implFactory = implFactory;
    }

    public void runExternalTests() throws Exception {
        List<Path> paths = Files.walk(testDirectory)
            .filter(Files::isRegularFile)
            .filter(x -> x.toString().endsWith(".json"))
            .collect(Collectors.toList());

        for (Path path : paths) {
            runExternalTest(path);
        }
    }

    public void runExternalTest(Path filePath) throws Exception {
        log.info("Running test for {}", filePath);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode operations = mapper.readTree(Files.readAllBytes(filePath));
        TagBagStore<String> tagBag = this.implFactory.get();

        for (JsonNode operation : operations) {
            log.info("operation is: " + operation);

            String command = operation.get("command").asText();
            if (command.equals("insert")) {
                tagBag.insert(
                    new TagPath(operation.get("key").asText()),
                    operation.get("value").asText()
                );
            }

            else if (command.equals("find_matching")) {
                Set<String> expected = new HashSet<>();
                for (JsonNode entry : operation.get("results")) {
                    expected.add(entry.asText());
                }
                Set<String> actual = tagBag.findMatching(new TagSearchPath(operation.get("key").asText()));
                assertEquals(expected, actual);
            }

            else {
                throw new RuntimeException("No defintion for command " + command);
            }
        }
    }
}
