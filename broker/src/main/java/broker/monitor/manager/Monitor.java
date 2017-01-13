package broker.monitor.manager;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by jacobfalzon on 10/01/2017.
 */
public interface Monitor {

    ObjectName getObjectName() throws MalformedObjectNameException;

}
