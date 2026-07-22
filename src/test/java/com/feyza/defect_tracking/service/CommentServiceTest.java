package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.request.CommentCreateRequest;
import com.feyza.defect_tracking.dto.response.CommentResponse;
import com.feyza.defect_tracking.entity.Comment;
import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.entity.User;
import com.feyza.defect_tracking.enums.Role;
import com.feyza.defect_tracking.exception.BusinessException;
import com.feyza.defect_tracking.repository.CommentRepository;
import com.feyza.defect_tracking.repository.DefectRepository;
import com.feyza.defect_tracking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private DefectRepository defectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentService commentService;

    private User currentUser;
    private Defect defect;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("tester");
        currentUser.setRole(Role.TESTER);

        defect = new Defect();
        defect.setId(10L);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should add comment successfully")
    void createComment_Success() {
        when(authentication.getName()).thenReturn("tester");
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(currentUser));
        when(defectRepository.findById(10L)).thenReturn(Optional.of(defect));

        CommentCreateRequest request = new CommentCreateRequest();
        request.setDefectId(10L);
        request.setCommentText("This is a test comment.");

        Comment savedComment = new Comment();
        savedComment.setId(100L);
        savedComment.setCommentText(request.getCommentText());
        savedComment.setDefect(defect);
        savedComment.setUser(currentUser);
        savedComment.setCreatedDate(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponse response = commentService.createComment(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("This is a test comment.", response.getCommentText());
        assertEquals("tester", response.getCreatedByUsername());
    }

    @Test
    @DisplayName("Should throw exception when comment text is empty or blank")
    void createComment_ShouldThrowException_WhenCommentTextIsBlank() {
        when(authentication.getName()).thenReturn("tester");
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(currentUser));
        when(defectRepository.findById(10L)).thenReturn(Optional.of(defect));

        CommentCreateRequest request = new CommentCreateRequest();
        request.setDefectId(10L);
        request.setCommentText("   ");

        lenient().when(commentRepository.save(any(Comment.class)))
                .thenThrow(new BusinessException("Comment text cannot be empty!"));

        assertThrows(RuntimeException.class, () -> commentService.createComment(request));
    }
}