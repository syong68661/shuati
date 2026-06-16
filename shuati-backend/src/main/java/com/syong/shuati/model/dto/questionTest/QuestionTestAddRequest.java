package com.syong.shuati.model.dto.questionTest;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 新增题目测试请求
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionTestAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 测试项列表
     */
    private List<QuestionTestItemDTO> itemList;

    private static final long serialVersionUID = 1L;
}
