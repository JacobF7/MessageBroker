package core.id;

import java.io.Serializable;
import java.security.SecureRandom;

/**
 * Created by jacobfalzon on 01/01/2017.
 */
public class Id implements Serializable {

    private static SecureRandom GENERATOR = new SecureRandom();

    private final String id;

    private Id(final String id) {
        this.id = id;
    }

    public static Id generate() {
        return new Id(String.valueOf(System.currentTimeMillis())  + Math.abs(GENERATOR.nextLong()));
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ID = " + id ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Id id = (Id) o;

        return getId() == null ? id.getId() == null : getId().equals(id.getId());
    }
}
