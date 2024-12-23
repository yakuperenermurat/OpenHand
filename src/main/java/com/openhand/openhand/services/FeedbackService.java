package com.openhand.openhand.services;

import com.openhand.openhand.entities.Feedback;
import com.openhand.openhand.entities.User;
import com.openhand.openhand.exceptions.ResourceNotFoundException;
import com.openhand.openhand.repositories.FeedbackRepository;
import com.openhand.openhand.repositories.UserRepository;
import com.openhand.openhand.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Geri Bildirim Gönderme
    public Feedback giveFeedback(Feedback feedback) {
        if (feedback.getGiver() == null || feedback.getReceiver() == null) {
            throw new IllegalArgumentException("Geçersiz giriş.");
        }

        Feedback savedFeedback = feedbackRepository.save(feedback);

        // Kullanıcının rating değerini güncelle
        updateUserRating(feedback.getReceiver().getUserId());

        return savedFeedback;
    }

    // 2. Belirli Bir Kullanıcıya Yapılan Geri Bildirimleri Listeleme
    public List<Feedback> getFeedbackForUser(Long userId) {
        List<Feedback> feedbacks = feedbackRepository.findByReceiverUserId(userId);
        if (feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("Geri bildirim bulunamadı.");
        }
        return feedbacks;
    }

    // Kullanıcının rating değerini güncelle
    private void updateUserRating(Long userId) {
        List<Feedback> feedbacks = feedbackRepository.findByReceiverUserId(userId);

        double averageRating = feedbacks.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı."));

        user.setRating(averageRating);
        userRepository.save(user); // Yeni rating'i kaydet
    }
}

