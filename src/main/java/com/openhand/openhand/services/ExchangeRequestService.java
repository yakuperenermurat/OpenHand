package com.openhand.openhand.services;

import com.openhand.openhand.entities.ExchangeRequest;
import com.openhand.openhand.exceptions.ResourceNotFoundException;
import com.openhand.openhand.repositories.ExchangeRequestRepository;
import com.openhand.openhand.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRequestService {

    @Autowired
    private ExchangeRequestRepository exchangeRequestRepository;

    // 1. Takas Teklifi Oluşturma
    public ExchangeRequest createExchangeRequest(ExchangeRequest exchangeRequest) {
        if (exchangeRequest.getSender() == null || exchangeRequest.getReceiver() == null ||
                exchangeRequest.getItemOffered() == null || exchangeRequest.getItemRequested() == null) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT);
        }
        exchangeRequest.setStatus("pending");
        return exchangeRequestRepository.save(exchangeRequest);
    }

    // 2. Tüm Takas Tekliflerini Listeleme
    public List<ExchangeRequest> getAllExchangeRequests() {
        List<ExchangeRequest> requests = exchangeRequestRepository.findAll();
        if (requests.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.EXCHANGE_REQUEST_NOT_FOUND);
        }
        return requests;
    }

    // 3. Belirli Bir Kullanıcının Takas Tekliflerini Listeleme
    public List<ExchangeRequest> getExchangeRequestsByUser(Long userId) {
        List<ExchangeRequest> requests = exchangeRequestRepository.findBySenderUserIdOrReceiverUserId(userId, userId);
        if (requests.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.EXCHANGE_REQUEST_NOT_FOUND);
        }
        return requests;
    }

    // 4. Takas Teklifini Kabul Etme
    public ExchangeRequest acceptExchangeRequest(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.EXCHANGE_REQUEST_NOT_FOUND));
        exchangeRequest.setStatus("accepted");
        return exchangeRequestRepository.save(exchangeRequest);
    }

    // 5. Takas Teklifini Reddetme
    public ExchangeRequest rejectExchangeRequest(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.EXCHANGE_REQUEST_NOT_FOUND));
        exchangeRequest.setStatus("rejected");
        return exchangeRequestRepository.save(exchangeRequest);
    }
}
