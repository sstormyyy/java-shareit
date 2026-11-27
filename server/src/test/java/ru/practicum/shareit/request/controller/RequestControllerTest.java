package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    @Test
    void createDelegatesToService() {
        ItemRequestDto dto = new ItemRequestDto();
        when(requestService.create(1, dto)).thenReturn(dto);

        assertSame(dto, requestController.create(1, dto));

        verify(requestService).create(1, dto);
    }

    @Test
    void getAllByRequestorDelegatesToService() {
        List<ItemRequestWithItemsDto> requests = List.of(new ItemRequestWithItemsDto());
        when(requestService.getAllByRequestor(1)).thenReturn(requests);

        assertSame(requests, requestController.getAllByRequestor(1));

        verify(requestService).getAllByRequestor(1);
    }

    @Test
    void getAllOthersDelegatesToService() {
        List<ItemRequestDto> requests = List.of(new ItemRequestDto());
        when(requestService.getAllOthers(1)).thenReturn(requests);

        assertSame(requests, requestController.getAllOthers(1));

        verify(requestService).getAllOthers(1);
    }

    @Test
    void getByIdDelegatesToService() {
        ItemRequestWithItemsDto request = new ItemRequestWithItemsDto();
        when(requestService.getById(2)).thenReturn(request);

        assertSame(request, requestController.getById(2));

        verify(requestService).getById(2);
    }
}