package com.syong.shuati.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 题目测试答题记录
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@TableName(value = "question_test_record")
@Data
public class QuestionTestRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 题目测试id
     */
    private Long questionTestId;

    private Long userId;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 正确数量
     */
    private Integer rightCount;

    /**
     * 总数量
     */
    private Integer questionCount;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
