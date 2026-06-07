package com.zane.mapper;

import com.zane.entity.TeacherProfile;

public interface TeacherProfileMapper {

    TeacherProfile findByUserId(Long userId);

    int countByTeacherNo(String teacherNo);

    int insert(TeacherProfile profile);

    int updateByUserId(TeacherProfile profile);
}
