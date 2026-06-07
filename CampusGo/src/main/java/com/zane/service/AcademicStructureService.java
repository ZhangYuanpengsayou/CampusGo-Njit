package com.zane.service;

import com.zane.dto.ClassGroupDTO;
import com.zane.dto.MajorDTO;
import com.zane.vo.ClassGroupVO;
import com.zane.vo.MajorVO;

import java.util.List;

public interface AcademicStructureService {

    List<MajorVO> listEnabledMajors(String collegeName);

    List<MajorVO> listAllMajors();

    MajorVO createMajor(MajorDTO majorDTO);

    MajorVO updateMajor(Long id, MajorDTO majorDTO);

    List<ClassGroupVO> listEnabledClasses(String collegeName, String majorName);

    List<ClassGroupVO> listAllClasses();

    ClassGroupVO createClassGroup(ClassGroupDTO classGroupDTO);

    ClassGroupVO updateClassGroup(Long id, ClassGroupDTO classGroupDTO);

    void requireEnabledMajor(String collegeName, String majorName);

    void requireEnabledClass(String collegeName, String majorName, String className);
}
