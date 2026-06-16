package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class HomeStatisticsVO implements Serializable {

    private List<HotQuestionVO> hotQuestionList;

    private List<TopUserVO> topUserList;

    private List<HotTagVO> hotTagList;

    private static final long serialVersionUID = 1L;
}
