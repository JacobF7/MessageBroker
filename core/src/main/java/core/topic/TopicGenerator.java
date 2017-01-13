package core.topic;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public interface TopicGenerator {

    Topic generate();

    StandardTopic generateStandardTopic();

    SingleLevelWildcardTopic generateSingleLevelWildcardTopic();

    MultiLevelWildcardTopic generateMultiLevelWildcardTopic();
}
