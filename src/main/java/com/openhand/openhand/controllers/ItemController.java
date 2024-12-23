package com.openhand.openhand.controllers;

import com.openhand.openhand.entities.Item;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // 1. İlan Ekleme
    @PostMapping
    public ResponseEntity<Result> addItem(@RequestBody Item item) {
        Item savedItem = itemService.addItem(item);
        return ResponseEntity.ok(new Result(true, "İlan başarıyla eklendi.", savedItem));
    }

    // 2. İlan Güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Item updatedItem = itemService.updateItem(id, item);
        return ResponseEntity.ok(new Result(true, "İlan başarıyla güncellendi.", updatedItem));
    }

    // 3. İlan Silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(new Result(true, "İlan başarıyla silindi."));
    }

    // 4. Tüm İlanları Listeleme
    @GetMapping
    public ResponseEntity<Result> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(new Result(true, "Tüm ilanlar başarıyla getirildi.", items));
    }

    // 5. Kullanıcıya Göre İlanları Listeleme
    @GetMapping("/user/{userId}")
    public ResponseEntity<Result> getItemsByUser(@PathVariable Long userId) {
        List<Item> items = itemService.getItemsByUser(userId);
        return ResponseEntity.ok(new Result(true, "Kullanıcıya ait ilanlar başarıyla getirildi.", items));
    }
}

