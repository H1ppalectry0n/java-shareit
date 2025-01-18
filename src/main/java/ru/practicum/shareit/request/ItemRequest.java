package ru.practicum.shareit.request;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    private Long id;
}
