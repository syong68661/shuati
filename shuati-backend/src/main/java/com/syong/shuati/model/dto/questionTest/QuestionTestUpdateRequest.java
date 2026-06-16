package com.syong.shuati.model.dto.questionTest;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 更新题目测试请求
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionTestUpdateRequest implements Serializable {

    private Long id;

    private Long questionId;

    private String title;

    private String description;

    private Integer status;

    private List<QuestionTestItemDTO> itemList;

    private static final long serialVersionUID = 1L;
}
