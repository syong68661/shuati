package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class QuestionBankTestItemVO implements Serializable {

    private Long questionId;

    private Long questionTestId;

    private Long questionTestItemId;

    private String title;

    private Integer sort;

    private Map<String, String> options = new LinkedHashMap<>();

    private static final long serialVersionUID = 1L;
}
