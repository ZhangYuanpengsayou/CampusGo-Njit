package com.zane.service.impl;

import com.zane.common.constant.RoleConstants;
import com.zane.dto.RegisterDTO;
import com.zane.entity.AdminProfile;
import com.zane.entity.StudentProfile;
import com.zane.entity.TeacherProfile;
import com.zane.entity.User;
import com.zane.exception.BusinessException;
import com.zane.mapper.AdminProfileMapper;
import com.zane.mapper.StudentProfileMapper;
import com.zane.mapper.TeacherProfileMapper;
import com.zane.mapper.UserMapper;
import com.zane.service.AcademicStructureService;
import com.zane.service.CollegeService;
import com.zane.service.DormitoryStructureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StudentProfileMapper studentProfileMapper;

    @Mock
    private TeacherProfileMapper teacherProfileMapper;

    @Mock
    private AdminProfileMapper adminProfileMapper;

    @Mock
    private CollegeService collegeService;

    @Mock
    private AcademicStructureService academicStructureService;

    @Mock
    private DormitoryStructureService dormitoryStructureService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userMapper,
                studentProfileMapper,
                teacherProfileMapper,
                adminProfileMapper,
                collegeService,
                academicStructureService,
                dormitoryStructureService,
                "TEACHER2026",
                "ADMIN2026"
        );
    }

    @Test
    void studentRegisterDoesNotRequireApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.STUDENT);
        dto.setStudentNo("20260001");
        when(userMapper.findByUsername("student01")).thenReturn(null);
        when(studentProfileMapper.countByStudentNo("20260001")).thenReturn(0);

        authService.register(dto);

        verify(userMapper).insert(any(User.class));
        verify(studentProfileMapper).insert(any(StudentProfile.class));
    }

    @Test
    void teacherRegisterRejectsMissingApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.TEACHER);
        dto.setTeacherNo("T20260001");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(dto));

        assertEquals("教师申请码不正确", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void teacherRegisterRejectsWrongApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.TEACHER);
        dto.setTeacherNo("T20260001");
        dto.setApplicationCode("WRONG");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(dto));

        assertEquals("教师申请码不正确", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void teacherRegisterAcceptsCorrectApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.TEACHER);
        dto.setTeacherNo("T20260001");
        dto.setApplicationCode("TEACHER2026");
        when(userMapper.findByUsername("teacher01")).thenReturn(null);
        when(teacherProfileMapper.countByTeacherNo("T20260001")).thenReturn(0);

        authService.register(dto);

        verify(userMapper).insert(any(User.class));
        verify(teacherProfileMapper).insert(any(TeacherProfile.class));
    }

    @Test
    void adminRegisterRejectsMissingApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.ADMIN);
        dto.setAdminNo("A20260001");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(dto));

        assertEquals("管理员申请码不正确", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void adminRegisterRejectsWrongApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.ADMIN);
        dto.setAdminNo("A20260001");
        dto.setApplicationCode("WRONG");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(dto));

        assertEquals("管理员申请码不正确", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void adminRegisterAcceptsCorrectApplicationCode() {
        RegisterDTO dto = baseRegister(RoleConstants.ADMIN);
        dto.setAdminNo("A20260001");
        dto.setApplicationCode("ADMIN2026");
        when(userMapper.findByUsername("admin01")).thenReturn(null);
        when(adminProfileMapper.countByAdminNo("A20260001")).thenReturn(0);

        authService.register(dto);

        verify(userMapper).insert(any(User.class));
        verify(adminProfileMapper).insert(any(AdminProfile.class));
    }

    private RegisterDTO baseRegister(String role) {
        RegisterDTO dto = new RegisterDTO();
        dto.setRole(role);
        dto.setUsername(role.toLowerCase() + "01");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setRealName("测试用户");
        dto.setPhone("13800000000");
        dto.setEmail("test@example.com");
        dto.setCollege("计算机学院");
        return dto;
    }
}
