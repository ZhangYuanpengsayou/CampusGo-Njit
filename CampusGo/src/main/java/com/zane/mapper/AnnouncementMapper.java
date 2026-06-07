package com.zane.mapper;

import com.zane.entity.Announcement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnnouncementMapper {

    Announcement findById(Long id);

    List<Announcement> findPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    long count(@Param("keyword") String keyword);

    int insert(Announcement announcement);

    int update(Announcement announcement);

    int softDelete(Long id);
}
