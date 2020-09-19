package cn.kyle.bilibili.rename.utils;

public class StringUtils {
    public static String fileNameFilter(String fname){
        return fname.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
