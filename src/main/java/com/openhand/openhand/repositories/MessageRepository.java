package com.openhand.openhand.repositories;

import com.openhand.openhand.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId) OR (m.sender.userId = :reverseReceiverId AND m.receiver.userId = :reverseSenderId)")
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("reverseReceiverId") Long reverseReceiverId,
            @Param("reverseSenderId") Long reverseSenderId);

}
