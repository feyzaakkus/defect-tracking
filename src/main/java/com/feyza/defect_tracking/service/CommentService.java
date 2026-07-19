package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.request.CommentCreateRequest;
import com.feyza.defect_tracking.dto.response.CommentResponse;
import com.feyza.defect_tracking.entity.Comment;
import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.entity.User;
import com.feyza.defect_tracking.exception.ResourceNotFoundException;
import com.feyza.defect_tracking.repository.CommentRepository;
import com.feyza.defect_tracking.repository.DefectRepository;
import com.feyza.defect_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder; // Eklendi
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final DefectRepository defectRepository;
    private final UserRepository userRepository;

    public CommentResponse createComment(CommentCreateRequest request) {
        Defect defect = defectRepository.findById(request.getDefectId())
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + request.getDefectId()));

        User currentUser = getCurrentUser();

        Comment comment = new Comment();
        comment.setDefect(defect);
        comment.setUser(currentUser);
        comment.setCommentText(request.getCommentText());
        comment.setCreatedDate(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setCommentText(comment.getCommentText());
        response.setCreatedDate(comment.getCreatedDate());

        if (comment.getDefect() != null) {
            response.setDefectId(comment.getDefect().getId());
        }

        if (comment.getUser() != null) {
            response.setCreatedByUsername(comment.getUser().getUsername());
        }

        return response;
    }
}
