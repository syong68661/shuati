package com.syong.shuati.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 题目测试项
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@TableName(value = "question_test_item")
@Data
public class QuestionTestItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long questionTestId;

    private String title;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String optionE;

    private String answer;

    private String analysis;

    private Integer sort;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
