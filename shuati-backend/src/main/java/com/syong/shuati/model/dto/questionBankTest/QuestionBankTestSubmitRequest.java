package com.syong.shuati.model.dto.questionBankTest;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 提交题库测试请求
 *
 * @author <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionBankTestSubmitRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 测试题库用户答案列表
     */
    private List<QuestionBankTestUserAnswerDTO> answerList;

    private static final long serialVersionUID = 1L;
}
