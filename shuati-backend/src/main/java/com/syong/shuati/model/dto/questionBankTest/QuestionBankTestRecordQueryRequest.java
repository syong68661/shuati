package com.syong.shuati.model.dto.questionBankTest;

import com.syong.shuati.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询题库测试记录请求
 *
 * @author <a href="https://github.com/LightingForest">SYong</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankTestRecordQueryRequest extends PageRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    private static final long serialVersionUID = 1L;
}
