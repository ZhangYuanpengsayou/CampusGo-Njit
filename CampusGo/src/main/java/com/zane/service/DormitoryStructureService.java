package com.zane.service;

import com.zane.dto.DormBuildingDTO;
import com.zane.dto.DormRoomDTO;
import com.zane.vo.DormBuildingVO;
import com.zane.vo.DormRoomVO;

import java.util.List;

public interface DormitoryStructureService {

    List<DormBuildingVO> listEnabledBuildings();

    List<DormBuildingVO> listAllBuildings();

    DormBuildingVO createBuilding(DormBuildingDTO dormBuildingDTO);

    DormBuildingVO updateBuilding(Long id, DormBuildingDTO dormBuildingDTO);

    void deleteBuilding(Long id);

    List<DormRoomVO> listEnabledRooms(String buildingName);

    List<DormRoomVO> listAllRooms();

    DormRoomVO createRoom(DormRoomDTO dormRoomDTO);

    DormRoomVO updateRoom(Long id, DormRoomDTO dormRoomDTO);

    void deleteRoom(Long id);

    void requireEnabledDormRoom(String buildingName, String roomNo);
}
