// org.example.mematch.domain.service.CommentService.java
package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Comment;
import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment);
    List<Comment> getCommentsByMemeId(Long memeId);
}
