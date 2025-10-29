package org.example.mematch.domain.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ImageUrl {

    @Column(name = "image_url")
    private String value;

    protected ImageUrl() {}

    public ImageUrl(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("ImageUrl cannot be blank");
        this.value = value;
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageUrl)) return false;
        ImageUrl imageUrl = (ImageUrl) o;
        return Objects.equals(value, imageUrl.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}
