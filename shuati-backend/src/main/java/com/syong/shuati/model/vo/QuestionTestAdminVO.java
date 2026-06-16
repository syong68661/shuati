package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 管理端题目测试视图
 */
@Data
public class QuestionTestAdminVO implements Serializable {

    private Long id;

    private Long questionId;

    private String title;

    private String description;

    private Integer status;

    private Long userId;

    private List<QuestionTestItemAdminVO> itemList;

    private static final long serialVersionUID = 1L;
}
