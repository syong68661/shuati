package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class QuestionBankTestRecordVO implements Serializable {

    private Long recordId;

    private Long questionBankId;

    private String questionBankTitle;

    private Integer score;

    private Integer rightCount;

    private Integer questionCount;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
