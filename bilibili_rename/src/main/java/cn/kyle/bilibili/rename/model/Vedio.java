package cn.kyle.bilibili.rename.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

@Data
@Accessors(chain = true)
public class Vedio {
    private String fname;
    private File file;
    private String suffix;

    public Vedio setFile(File file){
        this.file = file;
        String name = file.getName();
        this.suffix = name.substring(name.lastIndexOf("."));
        return this;
    }

    public String getName(){
        return this.fname.concat(suffix);
    }
}
