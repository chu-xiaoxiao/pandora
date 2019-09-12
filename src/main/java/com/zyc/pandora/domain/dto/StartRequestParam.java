package com.zyc.pandora.domain.dto;

import lombok.Data;

/**
 * @author : zhangyuchen
 * @date : 2019/9/12 13:51
 */
@Data
public class StartRequestParam {
    private String url;
    private String branch;
    private String project;
}
