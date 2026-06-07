package com.zane.mapper;

import com.zane.entity.DormRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DormRoomMapper {

    DormRoom findById(Long id);

    DormRoom findByBuildingAndRoomNo(@Param("buildingName") String buildingName, @Param("roomNo") String roomNo);

    List<DormRoom> findEnabledByBuilding(String buildingName);

    List<DormRoom> findAll();

    int insert(DormRoom dormRoom);

    int update(DormRoom dormRoom);
}
