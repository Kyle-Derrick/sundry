package cn.kyle.bilibili.rename;

import cn.kyle.bilibili.rename.constant.ErrorMessage;
import cn.kyle.bilibili.rename.model.Config;
import cn.kyle.bilibili.rename.model.Vedio;
import cn.kyle.bilibili.rename.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Run {
    private final static String DEFAULT_OUTPUT_DIR = "out";

    public static void main(String[] args) {
        new Run().work(args);
    }

    private void work(String[] var){
        Config config = init(var);

        if (Objects.isNull(config)) return;
        File input = config.getInput();
        File output = config.getOutput();
        File[] files = input.listFiles(File::isDirectory);

        ExecutorService pool = Executors.newCachedThreadPool();
        AtomicInteger atomicInteger = new AtomicInteger();
        for (File file : files) {
            pool.execute(() -> {
                task(file, output, files.length, atomicInteger);
            });
        }
        pool.shutdown();
    }

    private void task(File file, File output, int size, AtomicInteger atomicInteger){
        Vedio vedio = getVedio(file);
        File outFile = new File(output.getAbsolutePath().concat(File.separator).concat(StringUtils.fileNameFilter(vedio.getName())));
        int tmp;
        try {
            FileUtils.copyFile(vedio.getFile(), outFile);
        } catch (Exception e) {
            System.err.println(ErrorMessage.COPY_FILE_ERROR.concat(outFile.getAbsolutePath()));
            e.printStackTrace();
        }
        System.out.printf("\rworking: %d/%d. ", (tmp = atomicInteger.incrementAndGet()), size);
        if (tmp == size) {
            System.out.println("\nFinished.");
            System.out.printf("output location: %s\n", output.getAbsolutePath());
            System.out.println("Please enter any key to exit... ");
            try {
                System.in.read();
            } catch (IOException e) {}
        }
    }

    private Config init(String[] var){
        File input = null, output;
        File path = new File("");
        String outDir = null;
        int len = var.length;
        //当传入没有参数
        if (len < 1){
            File[] files = path.listFiles();
            if (files != null && files.length > 0){
                input = firstDirectory(files);
            }
        } else {
            // 传入一个及以上参数
            File file = new File(var[0]);
            input = file.exists() ? file : null;
            //传入两个参数
            if (len > 1) {
                outDir = var[1];
            }
        }
        if (Objects.isNull(input)) {
            System.err.println(ErrorMessage.NOT_INPUT);
            return null;
        }
        if (Objects.isNull(outDir)) {
            try {
                JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(getDvi(input), StandardCharsets.UTF_8));
                String title = jsonObject.getString("Title");
                outDir = path.getAbsolutePath()
                        .concat(File.separator)
                        .concat(DEFAULT_OUTPUT_DIR)
                        .concat(File.separator)
                        .concat(StringUtils.fileNameFilter(title));
            } catch (Exception e) {
                System.err.println(ErrorMessage.READ_DVI_FILE_ERROR);
                e.printStackTrace();
                return null;
            }
        }
        output = new File(outDir.replaceAll("\\\\\\\\", "\\\\"));
        if (output.exists()) {
            output.delete();
        }
        output.mkdirs();

        return new Config().setInput(input).setOutput(output);
    }

    private File firstDirectory(File[] files){
        File tmp;
        for (int i = 0; i < files.length; i++) {
            tmp = files[i];
            if (!Objects.isNull(tmp) && tmp.isDirectory()){
                return tmp;
            }
        }
        return null;
    }

    private File firstFileIgnoreJudge(File[] files){
        return !Objects.isNull(files) && files.length > 0 ? files[0] : null;
    }

    private File getDvi(File path){
        File[] files = path.listFiles((file) -> file.getName().endsWith(".dvi") && file.isFile());
        return firstFileIgnoreJudge(files);
    }
    private Vedio getVedio(File path){
        Vedio vedio = new Vedio();
        for (File file : Objects.requireNonNull(path.listFiles())) {
            String fname = file.getName();
            if (fname.endsWith(".info")){
                try {
                    JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                    String fn;
                    fn = jsonObject.getString("PartNo").concat("_").concat(jsonObject.getString("PartName"));
                    vedio.setFname(fn);
                } catch (IOException e) {
                    System.err.println(ErrorMessage.READ_INFO_FILE_ERROR);
                    e.printStackTrace();
                }
            }else if (fname.endsWith(".mp4") || fname.endsWith(".flv")){
                vedio.setFile(file);
            }
        }
        return vedio;
    }
}
