package com.openhand.openhand.services;

import com.openhand.openhand.entities.Message;
import com.openhand.openhand.exceptions.ResourceNotFoundException;
import com.openhand.openhand.repositories.MessageRepository;
import com.openhand.openhand.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // 1. Mesaj Gönderme
    public Message sendMessage(Message message) {
        if (message.getSender() == null || message.getReceiver() == null || message.getContent().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT);
        }
        return messageRepository.save(message);
    }
    // 2. Kullanıcılar Arasındaki Mesajları Listeleme
    public List<Message> getConversation(Long senderId, Long receiverId) {
        List<Message> conversation = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(senderId, receiverId, senderId, receiverId);
        if (conversation.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.MESSAGE_NOT_FOUND);
        }
        return conversation;
    }
}
