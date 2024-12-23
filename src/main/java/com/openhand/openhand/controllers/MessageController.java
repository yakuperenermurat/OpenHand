package com.openhand.openhand.controllers;

import com.openhand.openhand.entities.Message;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 1. Mesaj Gönderme
    @PostMapping
    public ResponseEntity<Result> sendMessage(@RequestBody Message message) {
        messageService.sendMessage(message);
        return ResponseEntity.ok(new Result(true, "Mesaj başarıyla gönderildi."));
    }
    // 2. Kullanıcılar Arasındaki Mesajları Listeleme
    @GetMapping("/conversation")
    public ResponseEntity<Result> getConversation(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        List<Message> conversation = messageService.getConversation(senderId, receiverId);
        return ResponseEntity.ok(new Result(true, "Mesajlar başarıyla listelendi."));
    }

}
