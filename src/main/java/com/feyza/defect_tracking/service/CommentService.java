package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.entity.Comment;
import com.feyza.defect_tracking.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createComment(Comment comment) {

        if (comment.getCommentText() == null || comment.getCommentText().trim().isEmpty()) {
            throw new RuntimeException("Comment text cannot be empty.");
        }

        return commentRepository.save(comment);
    }
}