package com.zane.service.impl;

import com.zane.dto.CollegeDTO;
import com.zane.entity.College;
import com.zane.exception.BusinessException;
import com.zane.mapper.ClassGroupMapper;
import com.zane.mapper.CollegeMapper;
import com.zane.mapper.MajorMapper;
import com.zane.service.CollegeService;
import com.zane.vo.CollegeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {

    private final CollegeMapper collegeMapper;
    private final MajorMapper majorMapper;
    private final ClassGroupMapper classGroupMapper;

    public CollegeServiceImpl(CollegeMapper collegeMapper, MajorMapper majorMapper, ClassGroupMapper classGroupMapper) {
        this.collegeMapper = collegeMapper;
        this.majorMapper = majorMapper;
        this.classGroupMapper = classGroupMapper;
    }

    @Override
    public List<CollegeVO> listEnabledColleges() {
        return collegeMapper.findEnabled().stream().map(this::toVO).toList();
    }

    @Override
    public List<CollegeVO> listAllColleges() {
        return collegeMapper.findAll().stream().map(this::toVO).toList();
    }

    @Override
    @Transactional
    public CollegeVO createCollege(CollegeDTO collegeDTO) {
        validateCollege(collegeDTO, null);
        College college = new College();
        college.setName(collegeDTO.getName().trim());
        college.setDescription(trimToNull(collegeDTO.getDescription()));
        college.setStatus(collegeDTO.getStatus() == null ? 1 : collegeDTO.getStatus());
        collegeMapper.insert(college);
        return toVO(collegeMapper.findById(college.getId()));
    }

    @Override
    @Transactional
    public CollegeVO updateCollege(Long id, CollegeDTO collegeDTO) {
        College existing = getExistingCollege(id);
        validateCollege(collegeDTO, id);
        existing.setName(collegeDTO.getName().trim());
        existing.setDescription(trimToNull(collegeDTO.getDescription()));
        existing.setStatus(collegeDTO.getStatus() == null ? existing.getStatus() : collegeDTO.getStatus());
        collegeMapper.update(existing);
        return toVO(collegeMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteCollege(Long id) {
        getExistingCollege(id);
        classGroupMapper.deleteByCollegeId(id);
        majorMapper.deleteByCollegeId(id);
        collegeMapper.delete(id);
    }

    @Override
    public void requireEnabledCollege(String collegeName) {
        if (!StringUtils.hasText(collegeName)) {
            throw new BusinessException("学院不能为空");
        }
        College college = collegeMapper.findByName(collegeName.trim());
        if (college == null || college.getStatus() == null || college.getStatus() != 1) {
            throw new BusinessException("请选择系统已有且启用的学院");
        }
    }

    private College getExistingCollege(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("学院 ID 不正确");
        }
        College college = collegeMapper.findById(id);
        if (college == null) {
            throw new BusinessException(404, "学院不存在");
        }
        return college;
    }

    private void validateCollege(CollegeDTO collegeDTO, Long currentId) {
        if (collegeDTO == null) {
            throw new BusinessException("学院信息不能为空");
        }
        if (!StringUtils.hasText(collegeDTO.getName())) {
            throw new BusinessException("学院名称不能为空");
        }
        if (collegeDTO.getName().trim().length() > 100) {
            throw new BusinessException("学院名称不能超过 100 个字符");
        }
        if (collegeDTO.getStatus() != null && collegeDTO.getStatus() != 0 && collegeDTO.getStatus() != 1) {
            throw new BusinessException("学院状态不正确");
        }

        College sameNameCollege = collegeMapper.findByName(collegeDTO.getName().trim());
        if (sameNameCollege != null && (currentId == null || !sameNameCollege.getId().equals(currentId))) {
            throw new BusinessException("学院名称已存在");
        }
    }

    private CollegeVO toVO(College college) {
        CollegeVO vo = new CollegeVO();
        vo.setId(college.getId());
        vo.setName(college.getName());
        vo.setDescription(college.getDescription());
        vo.setStatus(college.getStatus());
        vo.setCreatedAt(college.getCreatedAt());
        vo.setUpdatedAt(college.getUpdatedAt());
        return vo;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
