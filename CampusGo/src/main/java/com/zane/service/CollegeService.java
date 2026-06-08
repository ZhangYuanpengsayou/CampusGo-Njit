package com.zane.service;

import com.zane.dto.CollegeDTO;
import com.zane.vo.CollegeVO;

import java.util.List;

public interface CollegeService {

    List<CollegeVO> listEnabledColleges();

    List<CollegeVO> listAllColleges();

    CollegeVO createCollege(CollegeDTO collegeDTO);

    CollegeVO updateCollege(Long id, CollegeDTO collegeDTO);

    void deleteCollege(Long id);

    void requireEnabledCollege(String collegeName);
}
