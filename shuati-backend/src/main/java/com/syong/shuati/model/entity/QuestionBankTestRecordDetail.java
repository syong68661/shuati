package com.syong.shuati.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 题库测试记录明细
 */
@TableName(value = "question_bank_test_record_detail")
@Data
public class QuestionBankTestRecordDetail {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long recordId;

    private Long questionId;

    private Long questionTestId;

    private Long questionTestItemId;

    private String titleSnapshot;

    private String optionsSnapshot;

    private String userAnswer;

    private String rightAnswer;

    private Integer isCorrect;

    private String analysisSnapshot;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
