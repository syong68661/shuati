package com.syong.shuati.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class HotQuestionVO implements Serializable {

    private Long questionId;

    private String title;

    private Long hotScore;

    private static final long serialVersionUID = 1L;
}
