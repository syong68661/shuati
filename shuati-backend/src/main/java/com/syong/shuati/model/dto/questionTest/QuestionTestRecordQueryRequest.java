package com.syong.shuati.model.dto.questionTest;

import com.syong.shuati.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询题目测试记录请求
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionTestRecordQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
