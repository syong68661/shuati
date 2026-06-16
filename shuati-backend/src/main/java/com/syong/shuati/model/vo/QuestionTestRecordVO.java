package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class QuestionTestRecordVO implements Serializable {

    private Long recordId;

    private Long questionId;

    private String questionTitle;

    private Integer score;

    private Integer rightCount;

    private Integer questionCount;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
