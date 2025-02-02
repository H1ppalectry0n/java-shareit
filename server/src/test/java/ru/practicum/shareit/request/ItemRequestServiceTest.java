package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Profile("test")
@Transactional
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
    }

    @Test
    void createItemRequest_ShouldCreateNewRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test Description");

        ItemRequestDto result = itemRequestService.create(testUser.getId(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getRequestor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void createItemRequest_ShouldThrowNotFoundException_WhenUserNotFound() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test Description");

        assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, requestDto));
    }

    @Test
    void findAllByRequestorId_ShouldReturnRequests() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request 1");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(testUser);
        itemRequestRepository.save(request);

        List<ItemRequestDto> requests = itemRequestService.findAllByRequestorId(testUser.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request 1");
    }

    @Test
    void findAll_ShouldReturnAllRequests() {
        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setCreated(LocalDateTime.now());
        request1.setRequestor(testUser);
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setCreated(LocalDateTime.now());
        request2.setRequestor(testUser);
        itemRequestRepository.save(request2);

        List<ItemRequestDto> requests = itemRequestService.findAll();

        assertThat(requests).hasSize(2);
    }

    @Test
    void findById_ShouldReturnRequest() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request 1");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(testUser);
        itemRequestRepository.save(request);

        ItemRequestDto result = itemRequestService.findById(request.getId());

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Request 1");
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findById(999L));
    }
}
