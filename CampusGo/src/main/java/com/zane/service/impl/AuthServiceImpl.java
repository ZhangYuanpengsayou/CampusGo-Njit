package com.zane.service.impl;

import com.zane.common.constant.RoleConstants;
import com.zane.common.session.LoginUser;
import com.zane.common.util.PasswordUtil;
import com.zane.dto.LoginDTO;
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
import com.zane.service.AuthService;
import com.zane.service.CollegeService;
import com.zane.service.DormitoryStructureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Set<String> ROLES = Set.of(RoleConstants.STUDENT, RoleConstants.TEACHER, RoleConstants.ADMIN);

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final TeacherProfileMapper teacherProfileMapper;
    private final AdminProfileMapper adminProfileMapper;
    private final CollegeService collegeService;
    private final AcademicStructureService academicStructureService;
    private final DormitoryStructureService dormitoryStructureService;
    private final String teacherApplicationCode;
    private final String adminApplicationCode;

    public AuthServiceImpl(
            UserMapper userMapper,
            StudentProfileMapper studentProfileMapper,
            TeacherProfileMapper teacherProfileMapper,
            AdminProfileMapper adminProfileMapper,
            CollegeService collegeService,
            AcademicStructureService academicStructureService,
            DormitoryStructureService dormitoryStructureService,
            @Value("${campusgo.register.teacher-application-code:TEACHER2026}") String teacherApplicationCode,
            @Value("${campusgo.register.admin-application-code:ADMIN2026}") String adminApplicationCode) {
        this.userMapper = userMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.teacherProfileMapper = teacherProfileMapper;
        this.adminProfileMapper = adminProfileMapper;
        this.collegeService = collegeService;
        this.academicStructureService = academicStructureService;
        this.dormitoryStructureService = dormitoryStructureService;
        this.teacherApplicationCode = teacherApplicationCode;
        this.adminApplicationCode = adminApplicationCode;
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        validateRegister(registerDTO);
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("账号已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername().trim());
        user.setPassword(PasswordUtil.encrypt(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());
        user.setRealName(registerDTO.getRealName().trim());
        user.setPhone(trimToNull(registerDTO.getPhone()));
        user.setEmail(trimToNull(registerDTO.getEmail()));
        user.setCollege(trimToNull(registerDTO.getCollege()));
        user.setStatus(1);
        userMapper.insert(user);

        insertRoleProfile(user.getId(), registerDTO);
    }

    @Override
    public LoginUser login(LoginDTO loginDTO) {
        if (loginDTO == null || !StringUtils.hasText(loginDTO.getUsername()) || !StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException("账号和密码不能为空");
        }

        User user = userMapper.findByUsername(loginDTO.getUsername().trim());
        if (user == null || !PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        return new LoginUser(user.getId(), user.getUsername(), user.getRole(), user.getRealName(), user.getCollege());
    }

    private void validateRegister(RegisterDTO registerDTO) {
        if (registerDTO == null) {
            throw new BusinessException("注册信息不能为空");
        }
        if (!StringUtils.hasText(registerDTO.getUsername())) {
            throw new BusinessException("账号不能为空");
        }
        if (!StringUtils.hasText(registerDTO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (!StringUtils.hasText(registerDTO.getRole()) || !ROLES.contains(registerDTO.getRole())) {
            throw new BusinessException("用户角色不正确");
        }
        if (!StringUtils.hasText(registerDTO.getRealName())) {
            throw new BusinessException("姓名不能为空");
        }
        validateRoleProfile(registerDTO);
    }

    private void validateRoleProfile(RegisterDTO registerDTO) {
        if (RoleConstants.STUDENT.equals(registerDTO.getRole())) {
            collegeService.requireEnabledCollege(registerDTO.getCollege());
            academicStructureService.requireEnabledClass(registerDTO.getCollege(), registerDTO.getMajor(), registerDTO.getClassName());
            dormitoryStructureService.requireEnabledDormRoom(registerDTO.getDormBuilding(), registerDTO.getDormRoom());
            if (!StringUtils.hasText(registerDTO.getStudentNo())) {
                throw new BusinessException("学号不能为空");
            }
            if (studentProfileMapper.countByStudentNo(registerDTO.getStudentNo().trim()) > 0) {
                throw new BusinessException("学号已存在");
            }
        } else if (RoleConstants.TEACHER.equals(registerDTO.getRole())) {
            collegeService.requireEnabledCollege(registerDTO.getCollege());
            validateApplicationCode(registerDTO.getApplicationCode(), teacherApplicationCode, "教师申请码不正确");
            if (!StringUtils.hasText(registerDTO.getTeacherNo())) {
                throw new BusinessException("工号不能为空");
            }
            if (teacherProfileMapper.countByTeacherNo(registerDTO.getTeacherNo().trim()) > 0) {
                throw new BusinessException("工号已存在");
            }
        } else if (RoleConstants.ADMIN.equals(registerDTO.getRole())) {
            validateApplicationCode(registerDTO.getApplicationCode(), adminApplicationCode, "管理员申请码不正确");
            if (!StringUtils.hasText(registerDTO.getAdminNo())) {
                throw new BusinessException("管理员编号不能为空");
            }
            if (adminProfileMapper.countByAdminNo(registerDTO.getAdminNo().trim()) > 0) {
                throw new BusinessException("管理员编号已存在");
            }
        }
    }

    private void validateApplicationCode(String inputCode, String expectedCode, String errorMessage) {
        if (!StringUtils.hasText(inputCode) || !inputCode.trim().equals(expectedCode)) {
            throw new BusinessException(errorMessage);
        }
    }

    private void insertRoleProfile(Long userId, RegisterDTO registerDTO) {
        if (RoleConstants.STUDENT.equals(registerDTO.getRole())) {
            StudentProfile profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setStudentNo(registerDTO.getStudentNo().trim());
            profile.setMajor(trimToNull(registerDTO.getMajor()));
            profile.setClassName(trimToNull(registerDTO.getClassName()));
            profile.setDormBuilding(trimToNull(registerDTO.getDormBuilding()));
            profile.setDormRoom(trimToNull(registerDTO.getDormRoom()));
            studentProfileMapper.insert(profile);
        } else if (RoleConstants.TEACHER.equals(registerDTO.getRole())) {
            TeacherProfile profile = new TeacherProfile();
            profile.setUserId(userId);
            profile.setTeacherNo(registerDTO.getTeacherNo().trim());
            profile.setTitle(trimToNull(registerDTO.getTitle()));
            profile.setOffice(trimToNull(registerDTO.getOffice()));
            teacherProfileMapper.insert(profile);
        } else if (RoleConstants.ADMIN.equals(registerDTO.getRole())) {
            AdminProfile profile = new AdminProfile();
            profile.setUserId(userId);
            profile.setAdminNo(registerDTO.getAdminNo().trim());
            profile.setDepartment(trimToNull(registerDTO.getDepartment()));
            adminProfileMapper.insert(profile);
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
