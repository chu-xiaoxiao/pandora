package com.zyc.pandora.domain.dto;

import lombok.Data;

/**
 * @author : zhangyuchen
 * @date : 2019/9/20 15:09
 */
@Data
public class RangeLog {
    String data;
    Long offset;
    boolean over;
}
