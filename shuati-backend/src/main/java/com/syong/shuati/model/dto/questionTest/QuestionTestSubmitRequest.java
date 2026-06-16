package com.syong.shuati.model.dto.questionTest;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 提交题目测试请求
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionTestSubmitRequest implements Serializable {

    /**
     * 题目测试 id
     */
    private Long questionTestId;

    /**
     * 测试用户答案列表
     */
    private List<QuestionTestUserAnswerDTO> answerList;

    private static final long serialVersionUID = 1L;
}
