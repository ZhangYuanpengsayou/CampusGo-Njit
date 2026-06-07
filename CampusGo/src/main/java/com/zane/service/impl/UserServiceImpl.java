package com.zane.service.impl;

import com.zane.common.constant.RoleConstants;
import com.zane.common.util.PasswordUtil;
import com.zane.dto.PasswordUpdateDTO;
import com.zane.dto.UserUpdateDTO;
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
import com.zane.service.UserService;
import com.zane.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final TeacherProfileMapper teacherProfileMapper;
    private final AdminProfileMapper adminProfileMapper;
    private final CollegeService collegeService;
    private final AcademicStructureService academicStructureService;
    private final DormitoryStructureService dormitoryStructureService;

    public UserServiceImpl(
            UserMapper userMapper,
            StudentProfileMapper studentProfileMapper,
            TeacherProfileMapper teacherProfileMapper,
            AdminProfileMapper adminProfileMapper,
            CollegeService collegeService,
            AcademicStructureService academicStructureService,
            DormitoryStructureService dormitoryStructureService) {
        this.userMapper = userMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.teacherProfileMapper = teacherProfileMapper;
        this.adminProfileMapper = adminProfileMapper;
        this.collegeService = collegeService;
        this.academicStructureService = academicStructureService;
        this.dormitoryStructureService = dormitoryStructureService;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = getUser(userId);
        return buildUserVO(user);
    }

    @Override
    @Transactional
    public UserVO updateCurrentUser(Long userId, UserUpdateDTO updateDTO) {
        if (updateDTO == null) {
            throw new BusinessException("修改信息不能为空");
        }

        User user = getUser(userId);
        user.setRealName(firstText(updateDTO.getRealName(), user.getRealName()));
        user.setPhone(trimToNull(updateDTO.getPhone()));
        user.setEmail(trimToNull(updateDTO.getEmail()));
        user.setCollege(resolveCollege(user, updateDTO));
        userMapper.updateProfile(user);

        updateRoleProfile(user, updateDTO);
        return getCurrentUser(userId);
    }

    private String resolveCollege(User user, UserUpdateDTO updateDTO) {
        if (RoleConstants.STUDENT.equals(user.getRole()) || RoleConstants.TEACHER.equals(user.getRole())) {
            String college = firstText(updateDTO.getCollege(), user.getCollege());
            collegeService.requireEnabledCollege(college);
            return college.trim();
        }
        return trimToNull(updateDTO.getCollege());
    }

    @Override
    public void updatePassword(Long userId, PasswordUpdateDTO updateDTO) {
        if (updateDTO == null || !StringUtils.hasText(updateDTO.getOldPassword()) || !StringUtils.hasText(updateDTO.getNewPassword())) {
            throw new BusinessException("原密码和新密码不能为空");
        }

        User user = getUser(userId);
        if (!PasswordUtil.matches(updateDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        userMapper.updatePassword(userId, PasswordUtil.encrypt(updateDTO.getNewPassword()));
    }

    private User getUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private void updateRoleProfile(User user, UserUpdateDTO updateDTO) {
        if (RoleConstants.STUDENT.equals(user.getRole())) {
            StudentProfile profile = studentProfileMapper.findByUserId(user.getId());
            if (profile != null) {
                String major = firstText(updateDTO.getMajor(), profile.getMajor());
                String className = firstText(updateDTO.getClassName(), profile.getClassName());
                String dormBuilding = firstText(updateDTO.getDormBuilding(), profile.getDormBuilding());
                String dormRoom = firstText(updateDTO.getDormRoom(), profile.getDormRoom());
                academicStructureService.requireEnabledClass(user.getCollege(), major, className);
                dormitoryStructureService.requireEnabledDormRoom(dormBuilding, dormRoom);
                profile.setMajor(major);
                profile.setClassName(className);
                profile.setDormBuilding(dormBuilding);
                profile.setDormRoom(dormRoom);
                studentProfileMapper.updateByUserId(profile);
            }
        } else if (RoleConstants.TEACHER.equals(user.getRole())) {
            TeacherProfile profile = teacherProfileMapper.findByUserId(user.getId());
            if (profile != null) {
                profile.setTitle(trimToNull(updateDTO.getTitle()));
                profile.setOffice(trimToNull(updateDTO.getOffice()));
                teacherProfileMapper.updateByUserId(profile);
            }
        } else if (RoleConstants.ADMIN.equals(user.getRole())) {
            AdminProfile profile = adminProfileMapper.findByUserId(user.getId());
            if (profile != null) {
                profile.setDepartment(trimToNull(updateDTO.getDepartment()));
                adminProfileMapper.updateByUserId(profile);
            }
        }
    }

    private UserVO buildUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setCollege(user.getCollege());

        if (RoleConstants.STUDENT.equals(user.getRole())) {
            StudentProfile profile = studentProfileMapper.findByUserId(user.getId());
            if (profile != null) {
                vo.setStudentNo(profile.getStudentNo());
                vo.setMajor(profile.getMajor());
                vo.setClassName(profile.getClassName());
                vo.setDormBuilding(profile.getDormBuilding());
                vo.setDormRoom(profile.getDormRoom());
            }
        } else if (RoleConstants.TEACHER.equals(user.getRole())) {
            TeacherProfile profile = teacherProfileMapper.findByUserId(user.getId());
            if (profile != null) {
                vo.setTeacherNo(profile.getTeacherNo());
                vo.setTitle(profile.getTitle());
                vo.setOffice(profile.getOffice());
            }
        } else if (RoleConstants.ADMIN.equals(user.getRole())) {
            AdminProfile profile = adminProfileMapper.findByUserId(user.getId());
            if (profile != null) {
                vo.setAdminNo(profile.getAdminNo());
                vo.setDepartment(profile.getDepartment());
            }
        }
        return vo;
    }

    private String firstText(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
