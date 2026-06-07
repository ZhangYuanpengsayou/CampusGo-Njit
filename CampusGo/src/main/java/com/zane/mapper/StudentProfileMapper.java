package com.zane.mapper;

import com.zane.entity.StudentProfile;

public interface StudentProfileMapper {

    StudentProfile findByUserId(Long userId);

    int countByStudentNo(String studentNo);

    int insert(StudentProfile profile);

    int updateByUserId(StudentProfile profile);
}
