package core.topic;

import core.utilities.LoggingUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Thread.*;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public class TopicGeneratorImpl implements TopicGenerator {

    private static final SecureRandom GENERATOR = new SecureRandom();
    private static final String STANDARD_TOPICS_FILE_NAME = "SampleStandardTopics.txt";
    private static final String SINGLE_LEVEL_TOPICS_FILE_NAME = "SampleSingleLevelWildcardTopics.txt";
    private static final String MULTI_LEVEL_TOPICS_FILE_NAME = "SampleMultiLevelWildcardTopics.txt";

    private static final List<String> SAMPLE_STANDARD_TOPICS = getSampleStandardTopics();
    private static final List<String> SAMPLE_SINGLE_LEVEL_TOPICS = getSampleSingleLevelWildcardTopics();
    private static final List<String> SAMPLE_MULTI_LEVEL_TOPICS = getSampleMultiLevelWildcardTopics();

    @Override
    public Topic generate() {
        final TopicType[] topics = TopicType.values();
        final int randomIndex = GENERATOR.nextInt(topics.length);

        switch (topics[randomIndex]) {

            case STANDARD:
                return generateStandardTopic();

            case SINGLE_LEVEL_WILDCARD:
                return generateSingleLevelWildcardTopic();

            case MULTI_LEVEL_WILDCARD:
                return generateMultiLevelWildcardTopic();

            default:
                throw new UnsupportedOperationException("Unsupported Message Type Generated [" + topics[randomIndex] + "]");
        }
    }

    @Override
    public StandardTopic generateStandardTopic() {
        final int randomIndex = GENERATOR.nextInt(SAMPLE_STANDARD_TOPICS.size());
        return new StandardTopic(Paths.get(SAMPLE_STANDARD_TOPICS.get(randomIndex)));
    }

    @Override
    public SingleLevelWildcardTopic generateSingleLevelWildcardTopic() {
        final int randomIndex = GENERATOR.nextInt(SAMPLE_SINGLE_LEVEL_TOPICS.size());
        return new SingleLevelWildcardTopic(Paths.get(SAMPLE_SINGLE_LEVEL_TOPICS.get(randomIndex)));
    }

    @Override
    public MultiLevelWildcardTopic generateMultiLevelWildcardTopic() {
        final int randomIndex = GENERATOR.nextInt(SAMPLE_MULTI_LEVEL_TOPICS.size());
        return new MultiLevelWildcardTopic(Paths.get(SAMPLE_MULTI_LEVEL_TOPICS.get(randomIndex)));
    }

    private static List<String> getSampleStandardTopics() {

        return getTopics(STANDARD_TOPICS_FILE_NAME).orElseGet( () -> Arrays.asList("work/kitchen/temperature",
                                                                              "school/kitchen/temperature",
                                                                              "home/kitchen/temperature",
                                                                              "home/kitchen/refrigerator/temperature",
                                                                              "home/kitchen/freezer/temperature",
                                                                              "home/kitchen/freezer/food",
                                                                              "home/kitchen/freezer/drinks"));
    }

    private static List<String> getSampleSingleLevelWildcardTopics() {
        return getTopics(SINGLE_LEVEL_TOPICS_FILE_NAME).orElseGet(() -> Arrays.asList("+/kitchen/temperature",
                                                                                 "home/kitchen/freezer/+",
                                                                                 "home/kitchen/+/temperature"));
    }

    private static List<String> getSampleMultiLevelWildcardTopics() {
        return getTopics(MULTI_LEVEL_TOPICS_FILE_NAME).orElseGet(() -> Arrays.asList("home/kitchen/#",
                                                                                "home/#",
                                                                                "#"));
    }

    private static Optional<List<String>> getTopics(final String fileName) {

        try (final InputStream stream = currentThread().getContextClassLoader().getResourceAsStream(fileName);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            final List<String> topics = reader.lines()
                                               .map(String::trim)
                                               .filter(topic -> !topic.isEmpty())
                                               .collect(Collectors.toList());

            return Optional.ofNullable(topics.isEmpty() ? null : topics);

        } catch (final IOException e) {
            LoggingUtils.error("Failed to read from " + fileName);
        }

        return Optional.empty();
    }
}
