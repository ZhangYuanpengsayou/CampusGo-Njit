package com.zane.service.impl;

import com.zane.dto.AnnouncementDTO;
import com.zane.entity.Announcement;
import com.zane.entity.User;
import com.zane.exception.BusinessException;
import com.zane.mapper.AnnouncementMapper;
import com.zane.mapper.UserMapper;
import com.zane.vo.AnnouncementVO;
import com.zane.vo.PageVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceImplTest {

    @Mock
    private AnnouncementMapper announcementMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    @Test
    void listAnnouncementsNormalizesPageParamsAndKeyword() {
        Announcement announcement = buildAnnouncement();
        when(announcementMapper.count("放假")).thenReturn(1L);
        when(announcementMapper.findPage("放假", 0, 10)).thenReturn(List.of(announcement));

        PageVO<AnnouncementVO> page = announcementService.listAnnouncements(0, 0, " 放假 ");

        assertEquals(1L, page.getTotal());
        assertEquals(1, page.getList().size());
        assertEquals("假期安排", page.getList().get(0).getTitle());
        verify(announcementMapper).findPage("放假", 0, 10);
    }

    @Test
    void createAnnouncementRejectsBlankTitle() {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setTitle(" ");
        dto.setContent("公告内容");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> announcementService.createAnnouncement(1L, dto)
        );

        assertEquals("公告标题不能为空", exception.getMessage());
    }

    @Test
    void createAnnouncementReturnsCreatedAnnouncement() {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setTitle(" 假期安排 ");
        dto.setContent(" 端午假期正常放假。 ");

        when(userMapper.findById(1L)).thenReturn(new User());
        doAnswer(invocation -> {
            Announcement announcement = invocation.getArgument(0);
            announcement.setId(10L);
            return 1;
        }).when(announcementMapper).insert(any(Announcement.class));
        when(announcementMapper.findById(10L)).thenReturn(buildAnnouncement());

        AnnouncementVO vo = announcementService.createAnnouncement(1L, dto);

        assertEquals(10L, vo.getId());
        assertEquals("假期安排", vo.getTitle());
        assertTrue(vo.getContent().contains("端午"));
        verify(announcementMapper).insert(any(Announcement.class));
    }

    private Announcement buildAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setId(10L);
        announcement.setTitle("假期安排");
        announcement.setContent("端午假期正常放假。");
        announcement.setPublisherId(1L);
        announcement.setPublisherName("系统管理员");
        announcement.setDeleted(0);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        return announcement;
    }
}
