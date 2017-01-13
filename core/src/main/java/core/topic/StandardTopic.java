package core.topic;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public class StandardTopic extends Topic {

    public StandardTopic(final Path path) {
        super(TopicType.STANDARD, path);
    }

    @Override
    public boolean isValid(final Path path) {
        return getSubtopics().stream().noneMatch(subtopic -> subtopic.equals(SingleLevelWildcardTopic.SINGLE_LEVEL_WILDCARD) ||
                                                             subtopic.equals(MultiLevelWildcardTopic.MULTI_LEVEL_WILDCARD));
    }

    @Override
    public boolean matches(final SingleLevelWildcardTopic otherTopic) {
        final int wildcardIndex = otherTopic.getSubtopics().indexOf(SingleLevelWildcardTopic.SINGLE_LEVEL_WILDCARD);

        final ArrayList<String> thisSubtopics = this.getSubtopics();
        final ArrayList<String> otherSubtopics = otherTopic.getSubtopics();

        if(otherSubtopics.size() == thisSubtopics.size()) {
            thisSubtopics.remove(wildcardIndex);
            otherSubtopics.remove(wildcardIndex);

            return otherSubtopics.equals(thisSubtopics);
        }

        return false;
    }

    @Override
    public boolean matches(final MultiLevelWildcardTopic otherTopic) {
        final int wildcardIndex = otherTopic.getSubtopics().indexOf(MultiLevelWildcardTopic.MULTI_LEVEL_WILDCARD);

        final ArrayList<String> thisSubtopics = this.getSubtopics();
        final ArrayList<String> otherSubtopics = otherTopic.getSubtopics();

        // A match is only possible if the wildcard is located at an index which is less than the number of subtopics
        if(wildcardIndex < thisSubtopics.size()) {
            otherSubtopics.remove(wildcardIndex); // Remove the wildcard
            thisSubtopics.removeIf(s -> thisSubtopics.indexOf(s) >= wildcardIndex); // Remove any topics that are beyond the index of the wildcard

            return otherSubtopics.equals(thisSubtopics);
        }

        return false;
    }

    @Override
    public boolean matches(final StandardTopic otherTopic) {
        return otherTopic.getSubtopics().equals(this.getSubtopics());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
