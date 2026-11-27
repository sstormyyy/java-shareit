package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void createDelegatesToService() {
        ItemDto dto = new ItemDto();
        when(itemService.create(1, dto)).thenReturn(dto);

        assertSame(dto, itemController.create(1, dto));

        verify(itemService).create(1, dto);
    }

    @Test
    void updateDelegatesToService() {
        ItemDto dto = new ItemDto();
        when(itemService.update(2, 1, dto)).thenReturn(dto);

        assertSame(dto, itemController.update(1, 2, dto));

        verify(itemService).update(2, 1, dto);
    }

    @Test
    void getByIdDelegatesToService() {
        ItemDto dto = new ItemDto();
        when(itemService.getById(2, 1)).thenReturn(dto);

        assertSame(dto, itemController.getById(1, 2));

        verify(itemService).getById(2, 1);
    }

    @Test
    void getByOwnerDelegatesToService() {
        List<ItemDto> items = List.of(new ItemDto());
        when(itemService.getByOwner(1)).thenReturn(items);

        assertSame(items, itemController.getByOwner(1));

        verify(itemService).getByOwner(1);
    }

    @Test
    void searchDelegatesToService() {
        List<ItemDto> items = List.of(new ItemDto());
        when(itemService.search("дрель")).thenReturn(items);

        assertSame(items, itemController.search("дрель"));

        verify(itemService).search("дрель");
    }

    @Test
    void createCommentDelegatesToService() {
        CommentDto dto = new CommentDto();
        dto.setText("Хорошая вещь");
        when(commentService.create(1, 2, "Хорошая вещь")).thenReturn(dto);

        assertSame(dto, itemController.createComment(1, 2, dto));

        verify(commentService).create(1, 2, "Хорошая вещь");
    }
}