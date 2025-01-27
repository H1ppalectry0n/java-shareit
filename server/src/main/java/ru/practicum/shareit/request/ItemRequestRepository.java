package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.requestor.id = :requestorId")
    List<ItemRequest> findByRequestorIdWithItems(long requestorId, Sort sort);
}
