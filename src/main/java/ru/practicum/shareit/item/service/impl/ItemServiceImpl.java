package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository requestRepository;

    @Override
    public List<Item> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text, PageRequest.of(from, size, Sort.by(Sort.Order.asc("id"))));
    }

    @Override
    public List<ItemDtoService> getAllUserItems(long userId, int from, int size) {
        List<Item> foundItems = itemRepository.findByOwner(userId, PageRequest.of(from, size, Sort.by(Sort.Order.asc("id"))));
        List<ItemDtoService> foundItemsList = new ArrayList<>();
        for (Item item : foundItems) {
            List<Booking> itemBookings = bookingRepository.findByItemIdOrderByStart(item.getId());
            List<Comment> itemComments = commentRepository.findAllByItemId(item.getId());
            foundItemsList.add(new ItemDtoService(item, itemBookings, itemComments));
        }
        return foundItemsList;
    }

    @Override
    public ItemDtoService getItem(long itemId, long userId) throws NotFoundException {
        Optional<Item> foundItem = itemRepository.findById(itemId);
        if (foundItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        List<Booking> itemBookings = bookingRepository.findByItemIdOrderByStart(itemId);
        if (foundItem.get().getOwner() != userId) {
            itemBookings = List.of();
        }
        List<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        System.out.println(commentRepository.findAll());
        return new ItemDtoService(foundItem.get(), itemBookings, itemComments);
    }

    @Override
    public Item createItem(ItemDtoCreated item, long userId) throws NotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        ItemRequest linkedRequest;
        if (item.getRequestId() == null) {
            linkedRequest = null;
        } else {
            linkedRequest = requestRepository.getReferenceById(item.getRequestId());
        }
        Item creatingItem = new Item();
        creatingItem.setName(item.getName());
        creatingItem.setDescription(item.getDescription());
        creatingItem.setAvailable(item.getAvailable());
        creatingItem.setOwner(userId);
        creatingItem.setRequest(linkedRequest);
        return itemRepository.save(creatingItem);
    }

    @Override
    public Item updateItem(long itemId, Item item, long userId) throws NotFoundException, AccessException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        return patchItem(item, itemRepository.getReferenceById(itemId));
    }

    @Override
    public void deleteItem(long itemId, long userId) throws AccessException, NotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public Comment postComment(long userId, long itemId, Comment comment) throws NotFoundException, NotBookedException {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        Optional<Item> foundItem = itemRepository.findById(itemId);
        if (foundItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        if (!isUserBookedItem(userId, itemId)) {
            throw new NotBookedException("Пользователь " + userId + " не брал в аренду предмет " + itemId);
        }
        comment.setAuthor(foundUser.get());
        comment.setItem(foundItem.get());
        return commentRepository.save(comment);
    }


    private boolean isNotOwner(long itemId, long userId) {
        return itemRepository.getReferenceById(itemId).getOwner() != userId;
    }

    private Item patchItem(Item patch, Item item) {
        if (patch.getName() != null) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        if (patch.getRequest() != null) {
            item.setRequest(patch.getRequest());
        }
        return itemRepository.save(item);
    }

    private boolean isUserBookedItem(long userId, long itemId) {
        boolean isPastBooking = bookingRepository
                .findUserPast(userId, LocalDateTime.now())
                .stream()
                .anyMatch(booking -> booking.getItem().getId() == itemId);
        boolean isCurrentBooking = bookingRepository
                .findUserCurrent(userId, LocalDateTime.now())
                .stream()
                .anyMatch(booking -> booking.getItem().getId() == itemId);
        return isPastBooking || isCurrentBooking;
    }
}
