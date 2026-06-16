package com.syong.shuati.model.dto.questionTest;

import java.io.Serializable;
import lombok.Data;

/**
 * 题目测试项 DTO
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@Data
public class QuestionTestItemDTO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目 id
     */
    private String title;

    /**
     * 选项 A
     */
    private String optionA;

    /**
     * 选项 B
     */
    private String optionB;

    /**
     * 选项 C
     */
    private String optionC;

    /**
     * 选项 D
     */
    private String optionD;

    private String optionE;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 排序
     */
    private Integer sort;

    private static final long serialVersionUID = 1L;
}
