package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

/**
 * 单题判题结果视图
 */
@Data
public class QuestionTestItemResultVO implements Serializable {

    private Long questionTestItemId;

    private String title;

    private Map<String, String> options = new LinkedHashMap<>();

    private String userAnswer;

    private String rightAnswer;

    private Boolean isCorrect;

    private String analysis;

    private static final long serialVersionUID = 1L;
}
