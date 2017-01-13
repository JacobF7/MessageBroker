package core.topic;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public class SingleLevelWildcardTopic extends Topic {

    public static final String SINGLE_LEVEL_WILDCARD = "+";

    public SingleLevelWildcardTopic(final Path path) {
        super(TopicType.SINGLE_LEVEL_WILDCARD, path);
    }

    @Override
    public boolean isValid(final Path path) {
        return Collections.frequency(this.getSubtopics(), SINGLE_LEVEL_WILDCARD) == 1 && getSubtopics().contains(SINGLE_LEVEL_WILDCARD);
    }

    @Override
    public boolean matches(final SingleLevelWildcardTopic otherTopic) {
        final ArrayList<String> thisSubtopics = this.getSubtopics();
        final ArrayList<String> otherSubtopics = otherTopic.getSubtopics();

        // A match occurs if both subtopics are of the same size and the subtopics match ( "+" matches anything)
        return thisSubtopics.size() == otherSubtopics.size() &&
               otherSubtopics.stream()
                             .map(otherSubtopics::indexOf)
                             .allMatch(index -> otherSubtopics.get(index).equals(thisSubtopics.get(index)) ||
                                                thisSubtopics.get(index).equals(SINGLE_LEVEL_WILDCARD)     ||
                                                otherSubtopics.get(index).equals(SINGLE_LEVEL_WILDCARD));
    }

    @Override
    public boolean matches(final MultiLevelWildcardTopic otherTopic) {
        final int multiLevelWildcardIndex = otherTopic.getSubtopics().indexOf(MultiLevelWildcardTopic.MULTI_LEVEL_WILDCARD);

        final ArrayList<String> thisSubtopics = this.getSubtopics();
        final ArrayList<String> otherSubtopics = otherTopic.getSubtopics();

        // A match is only possible if the multi-level wildcard is located at an index which is less than the number of subtopics
        if(multiLevelWildcardIndex < thisSubtopics.size()) {

            otherSubtopics.remove(multiLevelWildcardIndex); // Remove the wildcard
            thisSubtopics.removeIf(s -> thisSubtopics.indexOf(s) >= multiLevelWildcardIndex); // Remove any topics that are beyond the index of the wildcard

            // A match occurs if both subtopics are of the same size and the subtopics match ( "+" matches anything)
            return thisSubtopics.size() == otherSubtopics.size() &&
                   otherSubtopics.stream()
                                 .map(otherSubtopics::indexOf)
                                 .allMatch(index -> otherSubtopics.get(index).equals(thisSubtopics.get(index)) ||
                                                    thisSubtopics.get(index).equals(SINGLE_LEVEL_WILDCARD));
        }

        return false;
    }

    @Override
    public boolean matches(final StandardTopic otherTopic) {
        return otherTopic.matches(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
