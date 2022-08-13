package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService{

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest, long userId) throws NotFoundException, ValidationException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        if (itemRequest.getDescription() == null) {
            throw new ValidationException("Отсутствует описание");
        }
        itemRequest.setRequester(user.get());
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> findAllByRequester(long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public List<ItemRequest> findAll(int from, int size) {
        return null;
    }

    @Override
    public ItemRequest findById(long id) {
        return null;
    }
}
