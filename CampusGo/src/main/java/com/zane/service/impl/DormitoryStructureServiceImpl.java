package com.zane.service.impl;

import com.zane.dto.DormBuildingDTO;
import com.zane.dto.DormRoomDTO;
import com.zane.entity.DormBuilding;
import com.zane.entity.DormRoom;
import com.zane.exception.BusinessException;
import com.zane.mapper.DormBuildingMapper;
import com.zane.mapper.DormRoomMapper;
import com.zane.service.DormitoryStructureService;
import com.zane.vo.DormBuildingVO;
import com.zane.vo.DormRoomVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DormitoryStructureServiceImpl implements DormitoryStructureService {

    private static final int DEFAULT_STATUS = 1;

    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;

    public DormitoryStructureServiceImpl(DormBuildingMapper dormBuildingMapper, DormRoomMapper dormRoomMapper) {
        this.dormBuildingMapper = dormBuildingMapper;
        this.dormRoomMapper = dormRoomMapper;
    }

    @Override
    public List<DormBuildingVO> listEnabledBuildings() {
        return dormBuildingMapper.findEnabled().stream().map(this::toBuildingVO).toList();
    }

    @Override
    public List<DormBuildingVO> listAllBuildings() {
        return dormBuildingMapper.findAll().stream().map(this::toBuildingVO).toList();
    }

    @Override
    @Transactional
    public DormBuildingVO createBuilding(DormBuildingDTO dormBuildingDTO) {
        validateBuilding(dormBuildingDTO, null);
        DormBuilding building = new DormBuilding();
        fillBuilding(building, dormBuildingDTO);
        dormBuildingMapper.insert(building);
        return toBuildingVO(dormBuildingMapper.findById(building.getId()));
    }

    @Override
    @Transactional
    public DormBuildingVO updateBuilding(Long id, DormBuildingDTO dormBuildingDTO) {
        getExistingBuilding(id);
        validateBuilding(dormBuildingDTO, id);
        DormBuilding building = new DormBuilding();
        building.setId(id);
        fillBuilding(building, dormBuildingDTO);
        dormBuildingMapper.update(building);
        return toBuildingVO(dormBuildingMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        getExistingBuilding(id);
        dormRoomMapper.deleteByBuildingId(id);
        dormBuildingMapper.delete(id);
    }

    @Override
    public List<DormRoomVO> listEnabledRooms(String buildingName) {
        requireEnabledBuilding(buildingName);
        return dormRoomMapper.findEnabledByBuilding(buildingName.trim()).stream().map(this::toRoomVO).toList();
    }

    @Override
    public List<DormRoomVO> listAllRooms() {
        return dormRoomMapper.findAll().stream().map(this::toRoomVO).toList();
    }

    @Override
    @Transactional
    public DormRoomVO createRoom(DormRoomDTO dormRoomDTO) {
        validateRoom(dormRoomDTO, null);
        DormRoom room = new DormRoom();
        fillRoom(room, dormRoomDTO);
        dormRoomMapper.insert(room);
        return toRoomVO(dormRoomMapper.findById(room.getId()));
    }

    @Override
    @Transactional
    public DormRoomVO updateRoom(Long id, DormRoomDTO dormRoomDTO) {
        getExistingRoom(id);
        validateRoom(dormRoomDTO, id);
        DormRoom room = new DormRoom();
        room.setId(id);
        fillRoom(room, dormRoomDTO);
        dormRoomMapper.update(room);
        return toRoomVO(dormRoomMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        getExistingRoom(id);
        dormRoomMapper.delete(id);
    }

    @Override
    public void requireEnabledDormRoom(String buildingName, String roomNo) {
        if (!StringUtils.hasText(roomNo)) {
            throw new BusinessException("宿舍号不能为空");
        }
        requireEnabledBuilding(buildingName);
        DormRoom room = dormRoomMapper.findByBuildingAndRoomNo(buildingName.trim(), roomNo.trim());
        if (room == null || room.getStatus() == null || room.getStatus() != 1) {
            throw new BusinessException("请选择系统已有且启用的宿舍号");
        }
    }

    private void validateBuilding(DormBuildingDTO dormBuildingDTO, Long currentId) {
        if (dormBuildingDTO == null) {
            throw new BusinessException("公寓楼栋信息不能为空");
        }
        if (!StringUtils.hasText(dormBuildingDTO.getName())) {
            throw new BusinessException("公寓楼栋不能为空");
        }
        if (dormBuildingDTO.getName().trim().length() > 50) {
            throw new BusinessException("公寓楼栋不能超过 50 个字符");
        }
        validateStatus(dormBuildingDTO.getStatus(), "公寓楼栋状态不正确");
        DormBuilding sameBuilding = dormBuildingMapper.findByName(dormBuildingDTO.getName().trim());
        if (sameBuilding != null && (currentId == null || !sameBuilding.getId().equals(currentId))) {
            throw new BusinessException("公寓楼栋已存在");
        }
    }

    private void validateRoom(DormRoomDTO dormRoomDTO, Long currentId) {
        if (dormRoomDTO == null) {
            throw new BusinessException("宿舍信息不能为空");
        }
        if (!StringUtils.hasText(dormRoomDTO.getRoomNo())) {
            throw new BusinessException("宿舍号不能为空");
        }
        if (dormRoomDTO.getRoomNo().trim().length() > 50) {
            throw new BusinessException("宿舍号不能超过 50 个字符");
        }
        validateStatus(dormRoomDTO.getStatus(), "宿舍号状态不正确");
        requireEnabledBuilding(dormRoomDTO.getBuildingName());
        DormRoom sameRoom = dormRoomMapper.findByBuildingAndRoomNo(dormRoomDTO.getBuildingName().trim(), dormRoomDTO.getRoomNo().trim());
        if (sameRoom != null && (currentId == null || !sameRoom.getId().equals(currentId))) {
            throw new BusinessException("该楼栋下宿舍号已存在");
        }
    }

    private void fillBuilding(DormBuilding building, DormBuildingDTO dormBuildingDTO) {
        building.setName(dormBuildingDTO.getName().trim());
        building.setStatus(dormBuildingDTO.getStatus() == null ? DEFAULT_STATUS : dormBuildingDTO.getStatus());
    }

    private void fillRoom(DormRoom room, DormRoomDTO dormRoomDTO) {
        DormBuilding building = requireEnabledBuilding(dormRoomDTO.getBuildingName());
        room.setBuildingId(building.getId());
        room.setRoomNo(dormRoomDTO.getRoomNo().trim());
        room.setStatus(dormRoomDTO.getStatus() == null ? DEFAULT_STATUS : dormRoomDTO.getStatus());
    }

    private DormBuilding requireEnabledBuilding(String buildingName) {
        if (!StringUtils.hasText(buildingName)) {
            throw new BusinessException("公寓楼栋不能为空");
        }
        DormBuilding building = dormBuildingMapper.findByName(buildingName.trim());
        if (building == null || building.getStatus() == null || building.getStatus() != 1) {
            throw new BusinessException("请选择系统已有且启用的公寓楼栋");
        }
        return building;
    }

    private DormBuilding getExistingBuilding(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("公寓楼栋 ID 不正确");
        }
        DormBuilding building = dormBuildingMapper.findById(id);
        if (building == null) {
            throw new BusinessException(404, "公寓楼栋不存在");
        }
        return building;
    }

    private DormRoom getExistingRoom(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("宿舍号 ID 不正确");
        }
        DormRoom room = dormRoomMapper.findById(id);
        if (room == null) {
            throw new BusinessException(404, "宿舍号不存在");
        }
        return room;
    }

    private void validateStatus(Integer status, String message) {
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException(message);
        }
    }

    private DormBuildingVO toBuildingVO(DormBuilding building) {
        DormBuildingVO vo = new DormBuildingVO();
        vo.setId(building.getId());
        vo.setName(building.getName());
        vo.setStatus(building.getStatus());
        vo.setCreatedAt(building.getCreatedAt());
        vo.setUpdatedAt(building.getUpdatedAt());
        return vo;
    }

    private DormRoomVO toRoomVO(DormRoom room) {
        DormRoomVO vo = new DormRoomVO();
        vo.setId(room.getId());
        vo.setBuildingId(room.getBuildingId());
        vo.setBuildingName(room.getBuildingName());
        vo.setRoomNo(room.getRoomNo());
        vo.setStatus(room.getStatus());
        vo.setCreatedAt(room.getCreatedAt());
        vo.setUpdatedAt(room.getUpdatedAt());
        return vo;
    }
}
