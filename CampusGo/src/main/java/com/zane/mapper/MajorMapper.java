package com.zane.mapper;

import com.zane.entity.Major;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MajorMapper {

    Major findById(Long id);

    Major findByCollegeAndName(@Param("collegeName") String collegeName, @Param("name") String name);

    List<Major> findEnabledByCollege(String collegeName);

    List<Major> findAll();

    int insert(Major major);

    int update(Major major);
}
