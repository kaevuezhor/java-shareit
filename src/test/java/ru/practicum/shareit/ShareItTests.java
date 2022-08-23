package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.service.BookingServiceTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Rollback
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {

	private final EntityManager em;
	private final ItemService itemService;
	private final UserService userService;
	private final BookingService bookingService;

	@Test
	void testGetUserItems() throws NotFoundException, AlreadyExistsException, ValidationException, AccessException, UnavailableException, NotOwnerException, AlreadyApprovedException, NotBookedException {
		User creatingUser = new User(
				null,
				"name",
				"e@ma.il"
		);

		userService.createUser(creatingUser);

		final AlreadyExistsException alreadyExistsException = Assertions.assertThrows(
				AlreadyExistsException.class,
				() -> userService.createUser(creatingUser)
		);

		Assertions.assertEquals("Пользователь с email " + creatingUser.getEmail() + " уже существует", alreadyExistsException.getMessage());

		ItemDtoCreated createRequestBody = new ItemDtoCreated(
				"item",
				"description",
				true,
				null
		);
		long requestHeaderUserId = 1;

		Item firstItem = itemService.createItem(createRequestBody, requestHeaderUserId);

		Assertions.assertEquals(
				new Item(
						1L,
						createRequestBody.getName(),
						createRequestBody.getDescription(),
						createRequestBody.getAvailable(),
						requestHeaderUserId,
						null
				),
				firstItem
		);

		createRequestBody = new ItemDtoCreated(
				"second item",
				"ddddddescription",
				false,
				null
		);

		Item secondItem = itemService.createItem(createRequestBody, requestHeaderUserId);

		Assertions.assertEquals(
				new Item(
						2L,
						createRequestBody.getName(),
						createRequestBody.getDescription(),
						createRequestBody.getAvailable(),
						requestHeaderUserId,
						null
				),
				secondItem
		);

		List<ItemDtoService> allUserItems = itemService.getAllUserItems(requestHeaderUserId, 0 , 2);

		Assertions.assertEquals(2, allUserItems.size());

		allUserItems = itemService.getAllUserItems(requestHeaderUserId,1,1);

		Assertions.assertEquals(1, allUserItems.size());

		Assertions.assertEquals(new ItemDtoService(secondItem, List.of(), List.of()), allUserItems.get(0));

		userService.createUser(new User(
				null,
				"nnn",
				"n@n.n"
		));

		BookingDtoCreate creatingBooking = new BookingDtoCreate(
				1,
				LocalDateTime.now().plusHours(5),
				LocalDateTime.now().plusHours(6)
		);

		bookingService.createBooking(creatingBooking, 3);

		Booking approvedBooking = bookingService.approveBooking(1, true, 1);

		allUserItems = itemService.getAllUserItems(requestHeaderUserId,0,1);

		Assertions.assertEquals(List.of(new ItemDtoService(firstItem, List.of(approvedBooking), List.of())), allUserItems);
	}

}
