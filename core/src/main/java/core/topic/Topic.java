package core.topic;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by jacobfalzon on 26/12/2016.
 */
public abstract class Topic implements Serializable {

    private final TopicType topicType;
    private final String topic;
    private final List<String> subtopics;

    public Topic(final TopicType topicType, final Path topic) {
        this.topicType = topicType;
        this.topic = topic.toString();
        this.subtopics = Arrays.asList(this.topic.split(Pattern.quote(File.separator)));

        validate(topic);
    }

    public final void validate(final Path path) {
        if(!isValid(path)) {
            throw new IllegalArgumentException("Invalid Topic [" + getTopic() + "] for Topic Level [" + getTopicType() +"]");
        }
    }

    public abstract boolean isValid(final Path path);

    public final boolean matches(final Topic topic) {

        if(topic.isSingleLevelWildcard()) {
            return matches((SingleLevelWildcardTopic) topic);
        }

        if(topic.isMultiLevelWildcard()) {
            return matches((MultiLevelWildcardTopic) topic);
        }

        return matches((StandardTopic) topic);
    }

    public abstract boolean matches(final SingleLevelWildcardTopic singleLevelWildcardTopic);

    public abstract boolean matches(final MultiLevelWildcardTopic multiLevelWildcardTopic);

    public abstract boolean matches(final StandardTopic standardTopic);

    public boolean isSingleLevelWildcard() {
        return getTopicType().equals(TopicType.SINGLE_LEVEL_WILDCARD);
    }

    public boolean isMultiLevelWildcard() { return getTopicType().equals(TopicType.MULTI_LEVEL_WILDCARD); }

    public boolean isStandard() {
        return getTopicType().equals(TopicType.STANDARD);
    }

    public TopicType getTopicType() {
        return topicType;
    }

    public String getTopic() {
        return topic;
    }

    public ArrayList<String> getSubtopics() {
        return new ArrayList<>(subtopics);
    }

    @Override
    public String toString() {
        return "Topic = " + topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Topic otherTopic = (Topic) o;

        if (getTopicType() != otherTopic.getTopicType()) return false;
        if (!getTopic().equals(otherTopic.getTopic())) return false;
        return getSubtopics().equals(otherTopic.getSubtopics());
    }

    @Override
    public int hashCode() {
        int result = getTopicType().hashCode();
        result = 31 * result + getTopic().hashCode();
        result = 31 * result + getSubtopics().hashCode();
        return result;
    }
}

