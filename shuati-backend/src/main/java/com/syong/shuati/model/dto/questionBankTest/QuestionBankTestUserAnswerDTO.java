package com.syong.shuati.model.dto.questionBankTest;

import java.io.Serializable;
import lombok.Data;

/**
 * 题库测试用户答案
 *
 * @author <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionBankTestUserAnswerDTO implements Serializable {

    /**
     * 题目测试项 id
     */
    private Long questionTestItemId;

    /**
     * 用户答案
     */
    private String userAnswer;

    private static final long serialVersionUID = 1L;
}
