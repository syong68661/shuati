package com.syong.shuati.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 题目测试
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@TableName(value = "question_test")
@Data
public class QuestionTest {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long questionId;

    private String title;

    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
