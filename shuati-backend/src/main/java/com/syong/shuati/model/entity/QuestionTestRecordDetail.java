package com.syong.shuati.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 题目测试答题记录明细
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@TableName(value = "question_test_record_detail")
@Data
public class QuestionTestRecordDetail {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 测试记录id
     */
    private Long recordId;

    /**
     * 测试项id
     */
    private Long questionTestItemId;

    /**
     * 测试项标题快照
     */
    private String titleSnapshot;

    /**
     * 测试项选项快照
     */
    private String optionsSnapshot;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 正确答案
     */
    private String rightAnswer;

    /**
     * 是否正确
     */
    private Integer isCorrect;

    /**
     * 解析快照
     */
    private String analysisSnapshot;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
