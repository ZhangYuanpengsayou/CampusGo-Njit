package com.zane.mapper;

import com.zane.entity.CarouselItem;

import java.util.List;

public interface CarouselItemMapper {

    CarouselItem findById(Long id);

    List<CarouselItem> findEnabled();

    List<CarouselItem> findAll();

    int insert(CarouselItem carouselItem);

    int update(CarouselItem carouselItem);

    int delete(Long id);
}
