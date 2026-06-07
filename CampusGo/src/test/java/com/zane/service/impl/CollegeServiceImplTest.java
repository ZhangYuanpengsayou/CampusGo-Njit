package com.zane.service.impl;

import com.zane.dto.CollegeDTO;
import com.zane.entity.College;
import com.zane.exception.BusinessException;
import com.zane.mapper.CollegeMapper;
import com.zane.vo.CollegeVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollegeServiceImplTest {

    @Mock
    private CollegeMapper collegeMapper;

    @InjectMocks
    private CollegeServiceImpl collegeService;

    @Test
    void requireEnabledCollegeRejectsUnknownCollege() {
        when(collegeMapper.findByName("不存在学院")).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> collegeService.requireEnabledCollege("不存在学院")
        );

        assertEquals("请选择系统已有且启用的学院", exception.getMessage());
    }

    @Test
    void createCollegeRejectsDuplicateName() {
        CollegeDTO dto = new CollegeDTO();
        dto.setName("计算机学院");
        when(collegeMapper.findByName("计算机学院")).thenReturn(new College());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> collegeService.createCollege(dto)
        );

        assertEquals("学院名称已存在", exception.getMessage());
    }

    @Test
    void createCollegeUsesEnabledStatusByDefault() {
        CollegeDTO dto = new CollegeDTO();
        dto.setName("新学院");
        dto.setDescription("测试学院");
        when(collegeMapper.findByName("新学院")).thenReturn(null);
        doAnswer(invocation -> {
            College college = invocation.getArgument(0);
            college.setId(5L);
            return 1;
        }).when(collegeMapper).insert(any(College.class));
        when(collegeMapper.findById(5L)).thenAnswer(invocation -> {
            College college = new College();
            college.setId(5L);
            college.setName("新学院");
            college.setDescription("测试学院");
            college.setStatus(1);
            return college;
        });

        CollegeVO vo = collegeService.createCollege(dto);

        assertEquals(5L, vo.getId());
        assertEquals(1, vo.getStatus());
    }
}
