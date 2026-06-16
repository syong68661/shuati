package com.syong.shuati.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class TopUserVO implements Serializable {

    private Long userId;

    private String userName;

    private String userAvatar;

    private Long solveCount;

    private static final long serialVersionUID = 1L;
}
