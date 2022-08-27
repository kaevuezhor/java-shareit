package ru.practicum.shareit.requests.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestServiceDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest, long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        itemRequest.setRequester(user.get());
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestServiceDto> findAllByRequester(long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(r -> new ItemRequestServiceDto(r, itemRepository.findAllByRequestId(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestServiceDto> findAll(int from, int size, long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        return requestRepository.findAllByRequesterIdNot(userId, PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("created"))))
                .stream()
                .map(r -> new ItemRequestServiceDto(r, itemRepository.findAllByRequestId(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestServiceDto findById(long id, long userId) throws Throwable {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        Optional<ItemRequest> foundRequest = requestRepository.findById(id);
        if (foundRequest.isEmpty()) {
            throw new NotFoundException("Запрос " + id + " не найден");
        }
        ItemRequest request = foundRequest.get();

        return new ItemRequestServiceDto(
                request,
                itemRepository.findAllByRequestId(request.getId())
        );
    }
}
