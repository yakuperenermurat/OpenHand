package com.openhand.openhand.repositories;

import com.openhand.openhand.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByReceiverUserId(Long receiverUserId);
}
