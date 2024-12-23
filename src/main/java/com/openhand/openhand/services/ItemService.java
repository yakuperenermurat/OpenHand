package com.openhand.openhand.services;

import com.openhand.openhand.entities.Item;
import com.openhand.openhand.exceptions.ResourceNotFoundException;
import com.openhand.openhand.repositories.ItemRepository;
import com.openhand.openhand.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // 1. İlan Ekleme
    public Item addItem(Item item) {
        if (item.getTitle() == null || item.getOwner() == null) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT);
        }
        return itemRepository.save(item);
    }

    // 2. İlan Güncelleme
    public Item updateItem(Long id, Item updatedItem) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ITEM_NOT_FOUND));
        item.setTitle(updatedItem.getTitle());
        item.setDescription(updatedItem.getDescription());
        item.setImage(updatedItem.getImage());
        item.setCondition(updatedItem.getCondition());
        item.setCategory(updatedItem.getCategory());
        return itemRepository.save(item);
    }

    // 3. İlan Silme
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessages.ITEM_NOT_FOUND);
        }
        itemRepository.deleteById(id);
    }

    // 4. Tüm İlanları Listeleme
    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.ITEM_NOT_FOUND);
        }
        return items;
    }

    // 5. Kullanıcıya Göre İlanları Listeleme
    public List<Item> getItemsByUser(Long userId) {
        List<Item> items = itemRepository.findByOwnerUserId(userId);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.ITEM_NOT_FOUND);
        }
        return items;
    }
}
