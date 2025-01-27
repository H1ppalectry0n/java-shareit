package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id = %d not found".formatted(userId)
        )));

        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest), null);
    }

    public List<ItemRequestDto> findAllByRequestorId(long requestorId) {
        return itemRequestRepository.findByRequestorIdWithItems(requestorId, Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(ir -> ItemRequestMapper.toDto(ir, ir.getItems())).toList();
    }

    public List<ItemRequestDto> findAll() {
        return itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(ir -> ItemRequestMapper.toDto(ir, null)).toList();
    }

    public ItemRequestDto findById(long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "ItemRequest with id = %d not found".formatted(id)
        ));
        return ItemRequestMapper.toDto(itemRequest, itemRequest.getItems());
    }

}
