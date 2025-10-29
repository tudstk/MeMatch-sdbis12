package org.example.mematch.domain.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Username {

    @Column(name = "username", nullable = false, unique = true)
    private String value;

    protected Username() {}

    public Username(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Username cannot be blank");
        if (value.length() > 50) throw new IllegalArgumentException("Username too long");
        this.value = value;
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username)) return false;
        Username username = (Username) o;
        return Objects.equals(value.toLowerCase(), username.value.toLowerCase());
    }

    @Override
    public int hashCode() { return Objects.hash(value.toLowerCase()); }

    @Override
    public String toString() { return value; }
}
