package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void createDelegatesToService() {
        BookingDto dto = new BookingDto();
        when(bookingService.create(1, dto)).thenReturn(dto);

        assertSame(dto, bookingController.create(1, dto));

        verify(bookingService).create(1, dto);
    }

    @Test
    void approveDelegatesToService() {
        BookingDto dto = new BookingDto();
        when(bookingService.approve(2, 1, true)).thenReturn(dto);

        assertSame(dto, bookingController.approve(1, 2, true));

        verify(bookingService).approve(2, 1, true);
    }

    @Test
    void getByIdDelegatesToService() {
        BookingDto dto = new BookingDto();
        when(bookingService.getById(2, 1)).thenReturn(dto);

        assertSame(dto, bookingController.getById(1, 2));

        verify(bookingService).getById(2, 1);
    }

    @Test
    void getByBookerDelegatesToService() {
        List<BookingDto> bookings = List.of(new BookingDto());
        when(bookingService.getByBooker(1, BookingState.ALL)).thenReturn(bookings);

        assertSame(bookings, bookingController.getByBooker(1, BookingState.ALL));

        verify(bookingService).getByBooker(1, BookingState.ALL);
    }

    @Test
    void getByOwnerDelegatesToService() {
        List<BookingDto> bookings = List.of(new BookingDto());
        when(bookingService.getByOwner(1, BookingState.ALL)).thenReturn(bookings);

        assertSame(bookings, bookingController.getByOwner(1, BookingState.ALL));

        verify(bookingService).getByOwner(1, BookingState.ALL);
    }
}