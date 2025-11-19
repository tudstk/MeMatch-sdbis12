package org.example.mematch.domain.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Email Value Object Tests
class EmailTest {

    @Test
    void constructor_WithValidEmail_ShouldCreateEmail() {
        Email email = new Email("test@example.com");

        assertNotNull(email);
        assertEquals("test@example.com", email.value());
    }

    @Test
    void constructor_ShouldConvertToLowercase() {
        Email email = new Email("Test@EXAMPLE.COM");

        assertEquals("test@example.com", email.value());
    }

    @Test
    void constructor_WithInvalidEmail_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, 
            () -> new Email("@example.com"));
        assertThrows(IllegalArgumentException.class, 
            () -> new Email("test@"));
        assertThrows(IllegalArgumentException.class, 
            () -> new Email("test.example.com"));
    }

    @Test
    void constructor_WithNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Email(null));
    }

    @Test
    void equals_WithSameValue_ShouldReturnTrue() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void equals_WithDifferentValue_ShouldReturnFalse() {
        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");

        assertNotEquals(email1, email2);
    }

    @Test
    void toString_ShouldReturnValue() {
        Email email = new Email("test@example.com");

        assertEquals("test@example.com", email.toString());
    }
}

// Username Value Object Tests
class UsernameTest {

    @Test
    void constructor_WithValidUsername_ShouldCreateUsername() {
        Username username = new Username("testuser");

        assertNotNull(username);
        assertEquals("testuser", username.value());
    }

    @Test
    void constructor_WithBlankUsername_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Username(""));
        assertThrows(IllegalArgumentException.class, 
            () -> new Username("   "));
    }

    @Test
    void constructor_WithNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Username(null));
    }

    @Test
    void constructor_WithTooLongUsername_ShouldThrowException() {
        String longUsername = "a".repeat(51);
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Username(longUsername));
    }

    @Test
    void constructor_WithMaxLength_ShouldSucceed() {
        String maxUsername = "a".repeat(50);
        
        Username username = new Username(maxUsername);

        assertNotNull(username);
        assertEquals(50, username.value().length());
    }

    @Test
    void equals_WithSameValueDifferentCase_ShouldReturnTrue() {
        Username username1 = new Username("TestUser");
        Username username2 = new Username("testuser");

        assertEquals(username1, username2);
        assertEquals(username1.hashCode(), username2.hashCode());
    }

    @Test
    void equals_WithDifferentValue_ShouldReturnFalse() {
        Username username1 = new Username("user1");
        Username username2 = new Username("user2");

        assertNotEquals(username1, username2);
    }

    @Test
    void toString_ShouldReturnValue() {
        Username username = new Username("testuser");

        assertEquals("testuser", username.toString());
    }
}

// ImageUrl Value Object Tests
class ImageUrlTest {

    @Test
    void constructor_WithValidUrl_ShouldCreateImageUrl() {
        ImageUrl imageUrl = new ImageUrl("https://example.com/image.jpg");

        assertNotNull(imageUrl);
        assertEquals("https://example.com/image.jpg", imageUrl.value());
    }

    @Test
    void constructor_WithBlankUrl_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new ImageUrl(""));
        assertThrows(IllegalArgumentException.class, 
            () -> new ImageUrl("   "));
    }

    @Test
    void constructor_WithNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new ImageUrl(null));
    }

    @Test
    void equals_WithSameValue_ShouldReturnTrue() {
        ImageUrl url1 = new ImageUrl("https://example.com/image.jpg");
        ImageUrl url2 = new ImageUrl("https://example.com/image.jpg");

        assertEquals(url1, url2);
        assertEquals(url1.hashCode(), url2.hashCode());
    }

    @Test
    void equals_WithDifferentValue_ShouldReturnFalse() {
        ImageUrl url1 = new ImageUrl("https://example.com/image1.jpg");
        ImageUrl url2 = new ImageUrl("https://example.com/image2.jpg");

        assertNotEquals(url1, url2);
    }

    @Test
    void toString_ShouldReturnValue() {
        ImageUrl imageUrl = new ImageUrl("https://example.com/image.jpg");

        assertEquals("https://example.com/image.jpg", imageUrl.toString());
    }
}