package com.zane.service;

import com.zane.dto.AnnouncementDTO;
import com.zane.vo.AnnouncementVO;
import com.zane.vo.PageVO;

public interface AnnouncementService {

    PageVO<AnnouncementVO> listAnnouncements(Integer page, Integer pageSize, String keyword);

    AnnouncementVO getAnnouncement(Long id);

    AnnouncementVO createAnnouncement(Long publisherId, AnnouncementDTO announcementDTO);

    AnnouncementVO updateAnnouncement(Long id, AnnouncementDTO announcementDTO);

    void deleteAnnouncement(Long id);
}
