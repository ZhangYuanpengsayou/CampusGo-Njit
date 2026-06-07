package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.dto.DormBuildingDTO;
import com.zane.dto.DormRoomDTO;
import com.zane.service.DormitoryStructureService;
import com.zane.vo.DormBuildingVO;
import com.zane.vo.DormRoomVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DormitoryStructureController {

    private final DormitoryStructureService dormitoryStructureService;

    public DormitoryStructureController(DormitoryStructureService dormitoryStructureService) {
        this.dormitoryStructureService = dormitoryStructureService;
    }

    @GetMapping("/dorm-buildings")
    public Result<List<DormBuildingVO>> listEnabledBuildings() {
        return Result.success(dormitoryStructureService.listEnabledBuildings());
    }

    @GetMapping("/dorm-buildings/manage")
    @RequireRole(RoleConstants.ADMIN)
    public Result<List<DormBuildingVO>> listAllBuildings() {
        return Result.success(dormitoryStructureService.listAllBuildings());
    }

    @PostMapping("/dorm-buildings")
    @RequireRole(RoleConstants.ADMIN)
    public Result<DormBuildingVO> createBuilding(@RequestBody DormBuildingDTO dormBuildingDTO) {
        return Result.success("新增成功", dormitoryStructureService.createBuilding(dormBuildingDTO));
    }

    @PutMapping("/dorm-buildings/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<DormBuildingVO> updateBuilding(@PathVariable Long id, @RequestBody DormBuildingDTO dormBuildingDTO) {
        return Result.success("修改成功", dormitoryStructureService.updateBuilding(id, dormBuildingDTO));
    }

    @GetMapping("/dorm-rooms")
    public Result<List<DormRoomVO>> listEnabledRooms(@RequestParam String building) {
        return Result.success(dormitoryStructureService.listEnabledRooms(building));
    }

    @GetMapping("/dorm-rooms/manage")
    @RequireRole(RoleConstants.ADMIN)
    public Result<List<DormRoomVO>> listAllRooms() {
        return Result.success(dormitoryStructureService.listAllRooms());
    }

    @PostMapping("/dorm-rooms")
    @RequireRole(RoleConstants.ADMIN)
    public Result<DormRoomVO> createRoom(@RequestBody DormRoomDTO dormRoomDTO) {
        return Result.success("新增成功", dormitoryStructureService.createRoom(dormRoomDTO));
    }

    @PutMapping("/dorm-rooms/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<DormRoomVO> updateRoom(@PathVariable Long id, @RequestBody DormRoomDTO dormRoomDTO) {
        return Result.success("修改成功", dormitoryStructureService.updateRoom(id, dormRoomDTO));
    }
}
