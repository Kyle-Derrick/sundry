package cn.kyle.bilibili.rename.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

@Data
@Accessors(chain = true)
public class Config {
    private File input;
    private File output;
}
