package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 用户端题目测试视图
 */
@Data
public class QuestionTestVO implements Serializable {

    private Long id;

    private Long questionId;

    private String title;

    private String description;

    private List<QuestionTestItemVO> itemList;

    private static final long serialVersionUID = 1L;
}
