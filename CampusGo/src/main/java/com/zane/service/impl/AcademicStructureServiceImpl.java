package com.zane.service.impl;

import com.zane.dto.ClassGroupDTO;
import com.zane.dto.MajorDTO;
import com.zane.entity.ClassGroup;
import com.zane.entity.College;
import com.zane.entity.Major;
import com.zane.exception.BusinessException;
import com.zane.mapper.ClassGroupMapper;
import com.zane.mapper.CollegeMapper;
import com.zane.mapper.MajorMapper;
import com.zane.service.AcademicStructureService;
import com.zane.service.CollegeService;
import com.zane.vo.ClassGroupVO;
import com.zane.vo.MajorVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AcademicStructureServiceImpl implements AcademicStructureService {

    private static final int DEFAULT_STATUS = 1;

    private final MajorMapper majorMapper;
    private final ClassGroupMapper classGroupMapper;
    private final CollegeMapper collegeMapper;
    private final CollegeService collegeService;

    public AcademicStructureServiceImpl(
            MajorMapper majorMapper,
            ClassGroupMapper classGroupMapper,
            CollegeMapper collegeMapper,
            CollegeService collegeService) {
        this.majorMapper = majorMapper;
        this.classGroupMapper = classGroupMapper;
        this.collegeMapper = collegeMapper;
        this.collegeService = collegeService;
    }

    @Override
    public List<MajorVO> listEnabledMajors(String collegeName) {
        collegeService.requireEnabledCollege(collegeName);
        return majorMapper.findEnabledByCollege(collegeName.trim()).stream().map(this::toMajorVO).toList();
    }

    @Override
    public List<MajorVO> listAllMajors() {
        return majorMapper.findAll().stream().map(this::toMajorVO).toList();
    }

    @Override
    @Transactional
    public MajorVO createMajor(MajorDTO majorDTO) {
        validateMajor(majorDTO, null);
        Major major = new Major();
        fillMajor(major, majorDTO);
        majorMapper.insert(major);
        return toMajorVO(majorMapper.findById(major.getId()));
    }

    @Override
    @Transactional
    public MajorVO updateMajor(Long id, MajorDTO majorDTO) {
        getExistingMajor(id);
        validateMajor(majorDTO, id);
        Major major = new Major();
        major.setId(id);
        fillMajor(major, majorDTO);
        majorMapper.update(major);
        return toMajorVO(majorMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteMajor(Long id) {
        getExistingMajor(id);
        classGroupMapper.deleteByMajorId(id);
        majorMapper.delete(id);
    }

    @Override
    public List<ClassGroupVO> listEnabledClasses(String collegeName, String majorName) {
        requireEnabledMajor(collegeName, majorName);
        return classGroupMapper.findEnabledByMajor(collegeName.trim(), majorName.trim()).stream().map(this::toClassGroupVO).toList();
    }

    @Override
    public List<ClassGroupVO> listAllClasses() {
        return classGroupMapper.findAll().stream().map(this::toClassGroupVO).toList();
    }

    @Override
    @Transactional
    public ClassGroupVO createClassGroup(ClassGroupDTO classGroupDTO) {
        validateClassGroup(classGroupDTO, null);
        ClassGroup classGroup = new ClassGroup();
        fillClassGroup(classGroup, classGroupDTO);
        classGroupMapper.insert(classGroup);
        return toClassGroupVO(classGroupMapper.findById(classGroup.getId()));
    }

    @Override
    @Transactional
    public ClassGroupVO updateClassGroup(Long id, ClassGroupDTO classGroupDTO) {
        getExistingClassGroup(id);
        validateClassGroup(classGroupDTO, id);
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(id);
        fillClassGroup(classGroup, classGroupDTO);
        classGroupMapper.update(classGroup);
        return toClassGroupVO(classGroupMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteClassGroup(Long id) {
        getExistingClassGroup(id);
        classGroupMapper.delete(id);
    }

    @Override
    public void requireEnabledMajor(String collegeName, String majorName) {
        if (!StringUtils.hasText(majorName)) {
            throw new BusinessException("专业不能为空");
        }
        collegeService.requireEnabledCollege(collegeName);
        Major major = majorMapper.findByCollegeAndName(collegeName.trim(), majorName.trim());
        if (major == null || major.getStatus() == null || major.getStatus() != 1) {
            throw new BusinessException("请选择系统已有且启用的专业");
        }
    }

    @Override
    public void requireEnabledClass(String collegeName, String majorName, String className) {
        if (!StringUtils.hasText(className)) {
            throw new BusinessException("班级不能为空");
        }
        requireEnabledMajor(collegeName, majorName);
        ClassGroup classGroup = classGroupMapper.findByMajorAndName(collegeName.trim(), majorName.trim(), className.trim());
        if (classGroup == null || classGroup.getStatus() == null || classGroup.getStatus() != 1) {
            throw new BusinessException("请选择系统已有且启用的班级");
        }
    }

    private void validateMajor(MajorDTO majorDTO, Long currentId) {
        if (majorDTO == null) {
            throw new BusinessException("专业信息不能为空");
        }
        if (!StringUtils.hasText(majorDTO.getName())) {
            throw new BusinessException("专业名称不能为空");
        }
        if (majorDTO.getName().trim().length() > 100) {
            throw new BusinessException("专业名称不能超过 100 个字符");
        }
        validateStatus(majorDTO.getStatus(), "专业状态不正确");
        College college = getEnabledCollege(majorDTO.getCollegeName());
        Major sameMajor = majorMapper.findByCollegeAndName(college.getName(), majorDTO.getName().trim());
        if (sameMajor != null && (currentId == null || !sameMajor.getId().equals(currentId))) {
            throw new BusinessException("该学院下专业名称已存在");
        }
    }

    private void validateClassGroup(ClassGroupDTO classGroupDTO, Long currentId) {
        if (classGroupDTO == null) {
            throw new BusinessException("班级信息不能为空");
        }
        if (!StringUtils.hasText(classGroupDTO.getName())) {
            throw new BusinessException("班级名称不能为空");
        }
        if (classGroupDTO.getName().trim().length() > 100) {
            throw new BusinessException("班级名称不能超过 100 个字符");
        }
        validateStatus(classGroupDTO.getStatus(), "班级状态不正确");
        requireEnabledMajor(classGroupDTO.getCollegeName(), classGroupDTO.getMajorName());
        ClassGroup sameClass = classGroupMapper.findByMajorAndName(
                classGroupDTO.getCollegeName().trim(),
                classGroupDTO.getMajorName().trim(),
                classGroupDTO.getName().trim()
        );
        if (sameClass != null && (currentId == null || !sameClass.getId().equals(currentId))) {
            throw new BusinessException("该专业下班级名称已存在");
        }
    }

    private void fillMajor(Major major, MajorDTO majorDTO) {
        College college = getEnabledCollege(majorDTO.getCollegeName());
        major.setCollegeId(college.getId());
        major.setName(majorDTO.getName().trim());
        major.setStatus(majorDTO.getStatus() == null ? DEFAULT_STATUS : majorDTO.getStatus());
    }

    private void fillClassGroup(ClassGroup classGroup, ClassGroupDTO classGroupDTO) {
        Major major = majorMapper.findByCollegeAndName(classGroupDTO.getCollegeName().trim(), classGroupDTO.getMajorName().trim());
        classGroup.setMajorId(major.getId());
        classGroup.setName(classGroupDTO.getName().trim());
        classGroup.setStatus(classGroupDTO.getStatus() == null ? DEFAULT_STATUS : classGroupDTO.getStatus());
    }

    private College getEnabledCollege(String collegeName) {
        collegeService.requireEnabledCollege(collegeName);
        return collegeMapper.findByName(collegeName.trim());
    }

    private Major getExistingMajor(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("专业 ID 不正确");
        }
        Major major = majorMapper.findById(id);
        if (major == null) {
            throw new BusinessException(404, "专业不存在");
        }
        return major;
    }

    private ClassGroup getExistingClassGroup(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("班级 ID 不正确");
        }
        ClassGroup classGroup = classGroupMapper.findById(id);
        if (classGroup == null) {
            throw new BusinessException(404, "班级不存在");
        }
        return classGroup;
    }

    private void validateStatus(Integer status, String message) {
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException(message);
        }
    }

    private MajorVO toMajorVO(Major major) {
        MajorVO vo = new MajorVO();
        vo.setId(major.getId());
        vo.setCollegeId(major.getCollegeId());
        vo.setCollegeName(major.getCollegeName());
        vo.setName(major.getName());
        vo.setStatus(major.getStatus());
        vo.setCreatedAt(major.getCreatedAt());
        vo.setUpdatedAt(major.getUpdatedAt());
        return vo;
    }

    private ClassGroupVO toClassGroupVO(ClassGroup classGroup) {
        ClassGroupVO vo = new ClassGroupVO();
        vo.setId(classGroup.getId());
        vo.setCollegeId(classGroup.getCollegeId());
        vo.setCollegeName(classGroup.getCollegeName());
        vo.setMajorId(classGroup.getMajorId());
        vo.setMajorName(classGroup.getMajorName());
        vo.setName(classGroup.getName());
        vo.setStatus(classGroup.getStatus());
        vo.setCreatedAt(classGroup.getCreatedAt());
        vo.setUpdatedAt(classGroup.getUpdatedAt());
        return vo;
    }
}
