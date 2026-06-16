package com.syong.shuati.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class HotTagVO implements Serializable {

    private String tagName;

    private Long useCount;

    private static final long serialVersionUID = 1L;
}
