package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

/**
 * 用户端题目测试项视图
 */
@Data
public class QuestionTestItemVO implements Serializable {

    private Long id;

    private String title;

    private Integer sort;

    private Map<String, String> options = new LinkedHashMap<>();

    private static final long serialVersionUID = 1L;
}
