package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.dto.CarouselItemDTO;
import com.zane.service.CarouselItemService;
import com.zane.vo.CarouselItemVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/carousels")
public class CarouselItemController {

    private final CarouselItemService carouselItemService;

    public CarouselItemController(CarouselItemService carouselItemService) {
        this.carouselItemService = carouselItemService;
    }

    @GetMapping
    public Result<List<CarouselItemVO>> listEnabledCarouselItems() {
        return Result.success(carouselItemService.listEnabledCarouselItems());
    }

    @GetMapping("/manage")
    @RequireRole(RoleConstants.ADMIN)
    public Result<List<CarouselItemVO>> listManageCarouselItems() {
        return Result.success(carouselItemService.listManageCarouselItems());
    }

    @PostMapping
    @RequireRole(RoleConstants.ADMIN)
    public Result<CarouselItemVO> createCarouselItem(@RequestBody CarouselItemDTO carouselItemDTO) {
        return Result.success("新增成功", carouselItemService.createCarouselItem(carouselItemDTO));
    }

    @PutMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<CarouselItemVO> updateCarouselItem(@PathVariable Long id, @RequestBody CarouselItemDTO carouselItemDTO) {
        return Result.success("修改成功", carouselItemService.updateCarouselItem(id, carouselItemDTO));
    }

    @DeleteMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> deleteCarouselItem(@PathVariable Long id) {
        carouselItemService.deleteCarouselItem(id);
        return Result.success("删除成功", null);
    }
}
