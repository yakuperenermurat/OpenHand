package com.openhand.openhand.repositories;

import com.openhand.openhand.entities.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findBySenderUserIdOrReceiverUserId(Long senderUserId, Long receiverUserId);
}
