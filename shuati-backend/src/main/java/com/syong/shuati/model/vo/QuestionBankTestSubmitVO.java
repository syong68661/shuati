package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class QuestionBankTestSubmitVO implements Serializable {

    private Long recordId;

    private Long questionBankId;

    private String questionBankTitle;

    private Integer score;

    private Integer rightCount;

    private Integer questionCount;

    private List<QuestionTestItemResultVO> itemResultList;

    private static final long serialVersionUID = 1L;
}
