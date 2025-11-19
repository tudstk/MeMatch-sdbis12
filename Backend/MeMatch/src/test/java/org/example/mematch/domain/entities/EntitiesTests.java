package org.example.mematch.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// User Entity Tests
class UserTest {

    @Test
    void create_ShouldCreateUserWithValidData() {
        User user = User.create("test@example.com", "testuser", "hashedpassword");

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword", user.getPasswordHash());
        assertNull(user.getDescription());
        assertNull(user.getImageUrl());
    }

    @Test
    void updateDescription_ShouldUpdateUserDescription() {
        User user = User.create("test@example.com", "testuser", "hash");
        
        user.updateDescription("New bio");

        assertEquals("New bio", user.getDescription());
    }

    @Test
    void updateImage_ShouldUpdateUserImageUrl() {
        User user = User.create("test@example.com", "testuser", "hash");
        
        user.updateImage("https://example.com/avatar.jpg");

        assertEquals("https://example.com/avatar.jpg", user.getImageUrl());
    }
}

// Meme Entity Tests
class MemeTest {

    @Test
    void create_ShouldCreateMemeWithValidData() {
        User user = User.create("test@example.com", "testuser", "hash");
        
        Meme meme = Meme.create(user, "https://example.com/meme.jpg", "Funny caption");

        assertNotNull(meme);
        assertEquals(user, meme.getUser());
        assertEquals("https://example.com/meme.jpg", meme.getImageUrl());
        assertEquals("Funny caption", meme.getCaption());
    }

    @Test
    void create_WithNullCaption_ShouldCreateMeme() {
        User user = User.create("test@example.com", "testuser", "hash");
        
        Meme meme = Meme.create(user, "https://example.com/meme.jpg", null);

        assertNotNull(meme);
        assertNull(meme.getCaption());
    }

    @Test
    void updateCaption_ShouldUpdateMemeCaption() {
        User user = User.create("test@example.com", "testuser", "hash");
        Meme meme = Meme.create(user, "https://example.com/meme.jpg", "Original");
        
        meme.updateCaption("Updated caption");

        assertEquals("Updated caption", meme.getCaption());
    }
}

// Match Entity Tests
class MatchTest {

    @Test
    void create_WithDifferentUsers_ShouldCreateMatch() {
        User user1 = User.create("user1@example.com", "user1", "hash1");
        User user2 = User.create("user2@example.com", "user2", "hash2");

        Match match = Match.create(user1, user2);

        assertNotNull(match);
        assertEquals(user1, match.getUser1());
        assertEquals(user2, match.getUser2());
        assertFalse(match.isMatched());
    }

    @Test
    void create_WithSameUser_ShouldThrowException() {
        User user = User.create("test@example.com", "testuser", "hash");

        assertThrows(IllegalArgumentException.class,
            () -> Match.create(user, user));
    }

    @Test
    void markAsMatched_ShouldSetMatchedToTrue() {
        User user1 = User.create("user1@example.com", "user1", "hash1");
        User user2 = User.create("user2@example.com", "user2", "hash2");
        Match match = Match.create(user1, user2);

        match.markAsMatched();

        assertTrue(match.isMatched());
    }
}

// Comment Entity Tests
class CommentTest {

    @Test
    void create_ShouldCreateCommentWithValidData() {
        User user = User.create("test@example.com", "testuser", "hash");
        Meme meme = Meme.create(user, "https://example.com/meme.jpg", "Caption");

        Comment comment = Comment.create(user, meme, "Great meme!");

        assertNotNull(comment);
        assertEquals(user, comment.getUser());
        assertEquals(meme, comment.getMeme());
        assertEquals("Great meme!", comment.getContent());
    }

    @Test
    void updateContent_ShouldUpdateCommentContent() {
        User user = User.create("test@example.com", "testuser", "hash");
        Meme meme = Meme.create(user, "https://example.com/meme.jpg", "Caption");
        Comment comment = Comment.create(user, meme, "Original comment");

        comment.updateContent("Updated comment");

        assertEquals("Updated comment", comment.getContent());
    }
}

// Like Entity Tests
class LikeTest {

    @Test
    void create_ShouldCreateLikeWithValidData() {
        User user = User.create("test@example.com", "testuser", "hash");
        Meme meme = Meme.create(user, "https://example.com/meme.jpg", "Caption");

        Like like = Like.create(user, meme);

        assertNotNull(like);
        assertEquals(user, like.getUser());
        assertEquals(meme, like.getMeme());
    }
}