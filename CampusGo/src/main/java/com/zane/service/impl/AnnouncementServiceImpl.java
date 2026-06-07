package com.zane.service.impl;

import com.zane.dto.AnnouncementDTO;
import com.zane.entity.Announcement;
import com.zane.exception.BusinessException;
import com.zane.mapper.AnnouncementMapper;
import com.zane.mapper.UserMapper;
import com.zane.service.AnnouncementService;
import com.zane.vo.AnnouncementVO;
import com.zane.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final AnnouncementMapper announcementMapper;
    private final UserMapper userMapper;

    public AnnouncementServiceImpl(AnnouncementMapper announcementMapper, UserMapper userMapper) {
        this.announcementMapper = announcementMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageVO<AnnouncementVO> listAnnouncements(Integer page, Integer pageSize, String keyword) {
        int currentPage = normalizePage(page);
        int size = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * size;
        String normalizedKeyword = trimToNull(keyword);

        long total = announcementMapper.count(normalizedKeyword);
        List<AnnouncementVO> list = announcementMapper.findPage(normalizedKeyword, offset, size)
                .stream()
                .map(this::toVO)
                .toList();
        return new PageVO<>(total, list);
    }

    @Override
    public AnnouncementVO getAnnouncement(Long id) {
        Announcement announcement = getExistingAnnouncement(id);
        return toVO(announcement);
    }

    @Override
    @Transactional
    public AnnouncementVO createAnnouncement(Long publisherId, AnnouncementDTO announcementDTO) {
        validateAnnouncement(announcementDTO);
        if (userMapper.findById(publisherId) == null) {
            throw new BusinessException(404, "发布人不存在");
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDTO.getTitle().trim());
        announcement.setContent(announcementDTO.getContent().trim());
        announcement.setPublisherId(publisherId);
        announcement.setDeleted(0);
        announcementMapper.insert(announcement);
        return getAnnouncement(announcement.getId());
    }

    @Override
    @Transactional
    public AnnouncementVO updateAnnouncement(Long id, AnnouncementDTO announcementDTO) {
        validateAnnouncement(announcementDTO);
        getExistingAnnouncement(id);

        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setTitle(announcementDTO.getTitle().trim());
        announcement.setContent(announcementDTO.getContent().trim());
        announcementMapper.update(announcement);
        return getAnnouncement(id);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        getExistingAnnouncement(id);
        announcementMapper.softDelete(id);
    }

    private Announcement getExistingAnnouncement(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("公告 ID 不正确");
        }
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException(404, "公告不存在");
        }
        return announcement;
    }

    private void validateAnnouncement(AnnouncementDTO announcementDTO) {
        if (announcementDTO == null) {
            throw new BusinessException("公告信息不能为空");
        }
        if (!StringUtils.hasText(announcementDTO.getTitle())) {
            throw new BusinessException("公告标题不能为空");
        }
        if (!StringUtils.hasText(announcementDTO.getContent())) {
            throw new BusinessException("公告内容不能为空");
        }
        if (announcementDTO.getTitle().trim().length() > 200) {
            throw new BusinessException("公告标题不能超过 200 个字符");
        }
    }

    private AnnouncementVO toVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(announcement.getId());
        vo.setTitle(announcement.getTitle());
        vo.setContent(announcement.getContent());
        vo.setPublisherId(announcement.getPublisherId());
        vo.setPublisherName(announcement.getPublisherName());
        vo.setCreatedAt(announcement.getCreatedAt());
        vo.setUpdatedAt(announcement.getUpdatedAt());
        return vo;
    }

    private int normalizePage(Integer page) {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
