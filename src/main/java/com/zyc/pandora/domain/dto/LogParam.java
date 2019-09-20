package com.zyc.pandora.domain.dto;

import lombok.Data;

/**
 * @author : zhangyuchen
 * @date : 2019/9/20 14:31
 */
@Data
public class LogParam {
    private String type;
    private Long offset;
    private String project;
}
