package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(long requesterId);

    /*
    @Query("select r from ItemRequest r " +
            "where r.requester.id <> :userId")
    Page<ItemRequest> findAll(Pageable pageable, long userId);

     */

    Page<ItemRequest> findAllByRequesterIdNot(long userId, Pageable pageable);
}
