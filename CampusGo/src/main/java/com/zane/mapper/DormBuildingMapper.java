package com.zane.mapper;

import com.zane.entity.DormBuilding;

import java.util.List;

public interface DormBuildingMapper {

    DormBuilding findById(Long id);

    DormBuilding findByName(String name);

    List<DormBuilding> findEnabled();

    List<DormBuilding> findAll();

    int insert(DormBuilding dormBuilding);

    int update(DormBuilding dormBuilding);
}
