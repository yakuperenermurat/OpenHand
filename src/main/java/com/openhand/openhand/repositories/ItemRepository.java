package com.openhand.openhand.repositories;

import com.openhand.openhand.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerUserId(Long ownerId); // Kullanıcıya göre ilanları getir
}
