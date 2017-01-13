package broker.monitor.client;

import javax.management.MXBean;

/**
 * Created by jacobfalzon on 10/01/2017.
 */
@MXBean
public interface ClientActivityMonitor {

    Integer getConnectionCount();

    Integer getTimeout();

    void setTimeout(final Integer timeout);

}
