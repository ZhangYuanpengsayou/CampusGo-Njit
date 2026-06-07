package com.zane.service.impl;

import com.zane.dto.CarouselItemDTO;
import com.zane.entity.CarouselItem;
import com.zane.exception.BusinessException;
import com.zane.mapper.CarouselItemMapper;
import com.zane.service.CarouselItemService;
import com.zane.vo.CarouselItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CarouselItemServiceImpl implements CarouselItemService {

    private static final int DEFAULT_STATUS = 1;
    private static final int DEFAULT_SORT_ORDER = 1;

    private final CarouselItemMapper carouselItemMapper;

    public CarouselItemServiceImpl(CarouselItemMapper carouselItemMapper) {
        this.carouselItemMapper = carouselItemMapper;
    }

    @Override
    public List<CarouselItemVO> listEnabledCarouselItems() {
        return carouselItemMapper.findEnabled()
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public List<CarouselItemVO> listManageCarouselItems() {
        return carouselItemMapper.findAll()
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    @Transactional
    public CarouselItemVO createCarouselItem(CarouselItemDTO carouselItemDTO) {
        validateCarouselItem(carouselItemDTO);

        CarouselItem carouselItem = new CarouselItem();
        fillCarouselItem(carouselItem, carouselItemDTO);
        carouselItemMapper.insert(carouselItem);
        return getCarouselItemVO(carouselItem.getId());
    }

    @Override
    @Transactional
    public CarouselItemVO updateCarouselItem(Long id, CarouselItemDTO carouselItemDTO) {
        validateCarouselItem(carouselItemDTO);
        getExistingCarouselItem(id);

        CarouselItem carouselItem = new CarouselItem();
        carouselItem.setId(id);
        fillCarouselItem(carouselItem, carouselItemDTO);
        carouselItemMapper.update(carouselItem);
        return getCarouselItemVO(id);
    }

    @Override
    @Transactional
    public void deleteCarouselItem(Long id) {
        getExistingCarouselItem(id);
        carouselItemMapper.delete(id);
    }

    private CarouselItemVO getCarouselItemVO(Long id) {
        return toVO(getExistingCarouselItem(id));
    }

    private CarouselItem getExistingCarouselItem(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("轮播图 ID 不正确");
        }
        CarouselItem carouselItem = carouselItemMapper.findById(id);
        if (carouselItem == null) {
            throw new BusinessException(404, "轮播图不存在");
        }
        return carouselItem;
    }

    private void validateCarouselItem(CarouselItemDTO carouselItemDTO) {
        if (carouselItemDTO == null) {
            throw new BusinessException("轮播图信息不能为空");
        }
        if (!StringUtils.hasText(carouselItemDTO.getTitle())) {
            throw new BusinessException("轮播图标题不能为空");
        }
        if (!StringUtils.hasText(carouselItemDTO.getImageUrl())) {
            throw new BusinessException("请上传轮播图图片");
        }
        if (carouselItemDTO.getTitle().trim().length() > 100) {
            throw new BusinessException("轮播图标题不能超过 100 个字符");
        }
        if (StringUtils.hasText(carouselItemDTO.getSubtitle()) && carouselItemDTO.getSubtitle().trim().length() > 300) {
            throw new BusinessException("轮播图说明不能超过 300 个字符");
        }
        Integer status = carouselItemDTO.getStatus();
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException("轮播图状态不正确");
        }
    }

    private void fillCarouselItem(CarouselItem carouselItem, CarouselItemDTO carouselItemDTO) {
        carouselItem.setTitle(carouselItemDTO.getTitle().trim());
        carouselItem.setSubtitle(trimToNull(carouselItemDTO.getSubtitle()));
        carouselItem.setImageUrl(carouselItemDTO.getImageUrl().trim());
        carouselItem.setSortOrder(carouselItemDTO.getSortOrder() == null ? DEFAULT_SORT_ORDER : carouselItemDTO.getSortOrder());
        carouselItem.setStatus(carouselItemDTO.getStatus() == null ? DEFAULT_STATUS : carouselItemDTO.getStatus());
    }

    private CarouselItemVO toVO(CarouselItem carouselItem) {
        CarouselItemVO vo = new CarouselItemVO();
        vo.setId(carouselItem.getId());
        vo.setTitle(carouselItem.getTitle());
        vo.setSubtitle(carouselItem.getSubtitle());
        vo.setImageUrl(carouselItem.getImageUrl());
        vo.setSortOrder(carouselItem.getSortOrder());
        vo.setStatus(carouselItem.getStatus());
        vo.setCreatedAt(carouselItem.getCreatedAt());
        vo.setUpdatedAt(carouselItem.getUpdatedAt());
        return vo;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
