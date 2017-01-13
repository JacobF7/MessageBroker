package core.topic;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public class MultiLevelWildcardTopic extends Topic {

    public static final String MULTI_LEVEL_WILDCARD = "#";

    public MultiLevelWildcardTopic(final Path path) {
        super(TopicType.MULTI_LEVEL_WILDCARD, path);
    }

    @Override
    public boolean isValid(final Path path) {
        return Collections.frequency(this.getSubtopics(), MULTI_LEVEL_WILDCARD) == 1 && path.endsWith(MULTI_LEVEL_WILDCARD);
    }

    @Override
    public boolean matches(final SingleLevelWildcardTopic otherTopic) {
        return otherTopic.matches(this);
    }

    @Override
    public boolean matches(final MultiLevelWildcardTopic otherTopic) {

        final int multiLevelWildcardIndex = this.getSubtopics().indexOf(MultiLevelWildcardTopic.MULTI_LEVEL_WILDCARD);
        final int otherMultiLevelWildcardIndex = otherTopic.getSubtopics().indexOf(MultiLevelWildcardTopic.MULTI_LEVEL_WILDCARD);

        final int firstMultiLevelWildcardIndex = multiLevelWildcardIndex < otherMultiLevelWildcardIndex ? multiLevelWildcardIndex : otherMultiLevelWildcardIndex;

        final ArrayList<String> thisSubtopics = this.getSubtopics();
        final ArrayList<String> otherSubtopics = otherTopic.getSubtopics();

        // Remove any subtopics beyond the index of firstMultiLevelWildcardIndex
        otherSubtopics.removeIf(s -> otherSubtopics.indexOf(s) >= firstMultiLevelWildcardIndex);
        thisSubtopics.removeIf(s -> thisSubtopics.indexOf(s) >= firstMultiLevelWildcardIndex);

        // A match occurs if both subtopics and are of the same size and have the same values
        return thisSubtopics.size() == otherSubtopics.size() &&
               otherSubtopics.stream()
                             .map(otherSubtopics::indexOf)
                             .allMatch(index -> otherSubtopics.get(index).equals(thisSubtopics.get(index)));
    }

    @Override
    public boolean matches(final StandardTopic otherTopic) {
        return otherTopic.matches(this);
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
}
