package com.syong.shuati.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class QuestionBankTestVO implements Serializable {

    private Long questionBankId;

    private String questionBankTitle;

    private Integer questionCount;

    private List<QuestionBankTestItemVO> itemList;

    private static final long serialVersionUID = 1L;
}
