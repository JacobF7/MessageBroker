package broker.monitor.message;

import javax.management.MXBean;
import java.util.Map;

/**
 * Created by jacobfalzon on 09/01/2017.
 */
@MXBean
public interface MessageActivityMonitor {

    Long getMessageCount();

    Map<String, Long> getDeliveredMessageCountPerClient();

    Map<String, Long> getPublishedMessageCount();

}
