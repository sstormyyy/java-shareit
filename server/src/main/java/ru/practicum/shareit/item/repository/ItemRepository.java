package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerId(Integer ownerId);

    List<Item> findByRequestIdIn(List<Integer> requestIds);

    List<Item> findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String desc);
}