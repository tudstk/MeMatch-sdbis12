package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Comment;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.CommentService;
import org.example.mematch.infrastructure.persistence.jpa.CommentRepository;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemeRepository memeRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, 
                             MemeRepository memeRepository,
                             UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.memeRepository = memeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByMemeId(Long memeId) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));
        return commentRepository.findAll().stream()
                .filter(c -> c.getMeme().getId().equals(memeId))
                .toList();
    }

    public Comment createComment(Long userId, Long memeId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));
        Comment comment = Comment.create(user, meme, content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        commentRepository.delete(comment);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.updateContent(content);
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }
}
