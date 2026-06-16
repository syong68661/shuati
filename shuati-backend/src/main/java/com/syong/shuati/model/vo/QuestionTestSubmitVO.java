package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 提交题目测试结果视图
 */
@Data
public class QuestionTestSubmitVO implements Serializable {

    private Long recordId;

    private Long questionId;

    private Long questionTestId;

    private Integer score;

    private Integer rightCount;

    private Integer questionCount;

    private List<QuestionTestItemResultVO> itemResultList;

    private static final long serialVersionUID = 1L;
}
