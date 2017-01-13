package core.message;

import core.id.Id;
import core.utilities.LoggingUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jacobfalzon on 04/01/2017.
 */
public interface MessageGenerator {

    Message generate(Id id);

    List<MessageType> generates();

    default int getDelay() {
        return 5;
    }
}
