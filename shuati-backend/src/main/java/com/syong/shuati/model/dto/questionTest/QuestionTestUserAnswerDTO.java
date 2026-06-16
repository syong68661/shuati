package com.syong.shuati.model.dto.questionTest;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户提交答案 DTO
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionTestUserAnswerDTO implements Serializable {

    /**
     * 测试项 id
     */
    private Long questionTestItemId;

    /**
     * 用户答案
     */
    private String userAnswer;

    private static final long serialVersionUID = 1L;
}
