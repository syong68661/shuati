package com.syong.shuati.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 管理端题目测试项视图
 */
@Data
public class QuestionTestItemAdminVO implements Serializable {

    private Long id;

    private String title;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String optionE;

    private String answer;

    private String analysis;

    private Integer sort;

    private static final long serialVersionUID = 1L;
}
