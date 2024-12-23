package com.openhand.openhand.controllers;

import com.openhand.openhand.entities.ExchangeRequest;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-requests")
public class ExchangeRequestController {

    @Autowired
    private ExchangeRequestService exchangeRequestService;

    // 1. Takas Teklifi Oluşturma
    @PostMapping
    public ResponseEntity<Result> createExchangeRequest(@RequestBody ExchangeRequest exchangeRequest) {
        ExchangeRequest createdRequest = exchangeRequestService.createExchangeRequest(exchangeRequest);
        return ResponseEntity.ok(new Result(true, "Takas teklifi başarıyla oluşturuldu.", createdRequest));
    }

    // 2. Tüm Takas Tekliflerini Listeleme
    @GetMapping
    public ResponseEntity<Result> getAllExchangeRequests() {
        List<ExchangeRequest> requests = exchangeRequestService.getAllExchangeRequests();
        return ResponseEntity.ok(new Result(true, "Tüm takas teklifleri başarıyla getirildi.", requests));
    }

    // 3. Belirli Bir Kullanıcının Takas Tekliflerini Listeleme
    @GetMapping("/user/{userId}")
    public ResponseEntity<Result> getExchangeRequestsByUser(@PathVariable Long userId) {
        List<ExchangeRequest> requests = exchangeRequestService.getExchangeRequestsByUser(userId);
        return ResponseEntity.ok(new Result(true, "Kullanıcıya ait takas teklifleri başarıyla getirildi.", requests));
    }

    // 4. Takas Teklifini Kabul Etme
    @PutMapping("/{id}/accept")
    public ResponseEntity<Result> acceptExchangeRequest(@PathVariable Long id) {
        ExchangeRequest acceptedRequest = exchangeRequestService.acceptExchangeRequest(id);
        return ResponseEntity.ok(new Result(true, "Takas teklifi kabul edildi.", acceptedRequest));
    }

    // 5. Takas Teklifini Reddetme
    @PutMapping("/{id}/reject")
    public ResponseEntity<Result> rejectExchangeRequest(@PathVariable Long id) {
        ExchangeRequest rejectedRequest = exchangeRequestService.rejectExchangeRequest(id);
        return ResponseEntity.ok(new Result(true, "Takas teklifi reddedildi.", rejectedRequest));
    }
}


