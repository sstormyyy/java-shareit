package ru.practicum.shareit.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Integer requestorId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Integer requestorId);
}