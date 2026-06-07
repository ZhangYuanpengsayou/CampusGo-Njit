package com.zane.service;

import com.zane.dto.CarouselItemDTO;
import com.zane.vo.CarouselItemVO;

import java.util.List;

public interface CarouselItemService {

    List<CarouselItemVO> listEnabledCarouselItems();

    List<CarouselItemVO> listManageCarouselItems();

    CarouselItemVO createCarouselItem(CarouselItemDTO carouselItemDTO);

    CarouselItemVO updateCarouselItem(Long id, CarouselItemDTO carouselItemDTO);

    void deleteCarouselItem(Long id);
}
