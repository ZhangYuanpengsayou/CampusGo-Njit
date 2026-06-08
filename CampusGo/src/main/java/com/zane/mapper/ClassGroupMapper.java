package com.zane.mapper;

import com.zane.entity.ClassGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassGroupMapper {

    ClassGroup findById(Long id);

    ClassGroup findByMajorAndName(@Param("collegeName") String collegeName, @Param("majorName") String majorName, @Param("name") String name);

    List<ClassGroup> findEnabledByMajor(@Param("collegeName") String collegeName, @Param("majorName") String majorName);

    List<ClassGroup> findAll();

    int insert(ClassGroup classGroup);

    int update(ClassGroup classGroup);

    int delete(Long id);

    int deleteByMajorId(Long majorId);

    int deleteByCollegeId(Long collegeId);
}
