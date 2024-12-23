package com.openhand.openhand.controllers;

import com.openhand.openhand.entities.Feedback;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // 1. Geri Bildirim Gönderme
    @PostMapping
    public ResponseEntity<Result> giveFeedback(@RequestBody Feedback feedback) {
        feedbackService.giveFeedback(feedback);
        return ResponseEntity.ok(new Result(true, "Geri bildirim başarıyla gönderildi."));
    }
    // 2. Belirli Bir Kullanıcıya Yapılan Geri Bildirimleri Listeleme
    @GetMapping("/user/{userId}")
    public ResponseEntity<Result> getFeedbackForUser(@PathVariable Long userId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForUser(userId);
        return ResponseEntity.ok(new Result(true, "Geri bildirimler başarıyla listelendi."));
    }
}
