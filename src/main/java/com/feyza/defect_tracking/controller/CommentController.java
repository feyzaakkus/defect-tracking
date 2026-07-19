package com.feyza.defect_tracking.controller;

import com.feyza.defect_tracking.dto.request.CommentCreateRequest;
import com.feyza.defect_tracking.dto.response.CommentResponse;
import com.feyza.defect_tracking.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody CommentCreateRequest request) {
        return commentService.createComment(request);
    }
}

