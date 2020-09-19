package cn.kyle.tplink.management.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseParam {
    private String method;
    private Network network;
    private LoginParam login;
}
