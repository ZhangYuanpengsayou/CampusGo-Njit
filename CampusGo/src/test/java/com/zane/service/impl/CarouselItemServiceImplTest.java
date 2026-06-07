package com.zane.service.impl;

import com.zane.dto.CarouselItemDTO;
import com.zane.entity.CarouselItem;
import com.zane.exception.BusinessException;
import com.zane.mapper.CarouselItemMapper;
import com.zane.vo.CarouselItemVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarouselItemServiceImplTest {

    @Mock
    private CarouselItemMapper carouselItemMapper;

    @InjectMocks
    private CarouselItemServiceImpl carouselItemService;

    @Test
    void createCarouselRejectsMissingImage() {
        CarouselItemDTO dto = new CarouselItemDTO();
        dto.setTitle("首页轮播");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> carouselItemService.createCarouselItem(dto)
        );

        assertEquals("请上传轮播图图片", exception.getMessage());
    }

    @Test
    void createCarouselUsesDefaultStatusAndSortOrder() {
        CarouselItemDTO dto = new CarouselItemDTO();
        dto.setTitle("首页轮播");
        dto.setSubtitle("欢迎使用 CampusGo");
        dto.setImageUrl("/uploads/carousel/20260607/demo.jpg");

        doAnswer(invocation -> {
            CarouselItem item = invocation.getArgument(0);
            item.setId(3L);
            return 1;
        }).when(carouselItemMapper).insert(any(CarouselItem.class));
        when(carouselItemMapper.findById(3L)).thenAnswer(invocation -> buildCarousel(3L, 1, 1));

        CarouselItemVO vo = carouselItemService.createCarouselItem(dto);

        assertEquals(3L, vo.getId());
        assertEquals(1, vo.getStatus());
        assertEquals(1, vo.getSortOrder());
    }

    @Test
    void updateCarouselRejectsInvalidStatus() {
        CarouselItemDTO dto = new CarouselItemDTO();
        dto.setTitle("首页轮播");
        dto.setImageUrl("/uploads/carousel/20260607/demo.jpg");
        dto.setStatus(3);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> carouselItemService.updateCarouselItem(1L, dto)
        );

        assertEquals("轮播图状态不正确", exception.getMessage());
    }

    private CarouselItem buildCarousel(Long id, Integer sortOrder, Integer status) {
        CarouselItem item = new CarouselItem();
        item.setId(id);
        item.setTitle("首页轮播");
        item.setSubtitle("欢迎使用 CampusGo");
        item.setImageUrl("/uploads/carousel/20260607/demo.jpg");
        item.setSortOrder(sortOrder);
        item.setStatus(status);
        return item;
    }
}
