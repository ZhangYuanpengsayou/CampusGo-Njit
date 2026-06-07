package com.zane.mapper;

import com.zane.entity.AdminProfile;

public interface AdminProfileMapper {

    AdminProfile findByUserId(Long userId);

    int countByAdminNo(String adminNo);

    int insert(AdminProfile profile);

    int updateByUserId(AdminProfile profile);
}
