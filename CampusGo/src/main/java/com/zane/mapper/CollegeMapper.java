package com.zane.mapper;

import com.zane.entity.College;

import java.util.List;

public interface CollegeMapper {

    College findById(Long id);

    College findByName(String name);

    List<College> findEnabled();

    List<College> findAll();

    int insert(College college);

    int update(College college);

    int delete(Long id);
}
