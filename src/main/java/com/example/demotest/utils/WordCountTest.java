package com.example.demotest.utils;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StringUtils;

public class WordCountTest {

    private static final Integer threadCount = 8;

    private static final String filePath = "C:\\project_files\\罗马假日英文剧本.txt";

    private static String[] listStr;

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static Map<String, Integer> maps = new HashMap<>();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1024));


    static {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
            FileChannel channel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (channel.read(byteBuffer) > 0) {
                out.write(byteBuffer.array(), 0, byteBuffer.position());
                byteBuffer.clear();
            }
            String fileContent = new String(out.toByteArray(), StandardCharsets.UTF_8)
                    .replaceAll("[\\p{Punct},\r\n]", "").toLowerCase();

            listStr = fileContent.split(" ");
            randomAccessFile.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Map<String, Integer> readWork() {
        Map<String, Integer> workMap = new HashMap<>();
        int index = atomicInteger.getAndIncrement();
        int start = index * (listStr.length / threadCount);
        int end = (index + 1) * listStr.length / threadCount + 1;
        for (int i = start; i < end -1; i++) {
            String item = listStr[i];
            if (!StringUtils.isEmpty(item)) {
                if (item.length() > 1) {
                    workMap.put(item, workMap.getOrDefault(item, 0)+ 1);
                }
            }
        }
        return workMap;
    }

    public static void main(String[] args) throws Exception {
        List<Future<Map<String, Integer>>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Future<Map<String, Integer>> task = executor.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    return readWork();
                }
            });
            tasks.add(task);
        }
        for (Future<Map<String, Integer>> future: tasks) {
            Map<String, Integer> task = future.get();
            for (Map.Entry<String, Integer> entry: task.entrySet()) {
                maps.put(entry.getKey(), maps.getOrDefault(entry.getKey(), 0)+entry.getValue());
            }
        }
        maps.forEach((keys,value) -> System.out.println(keys + " " + value));
    }
}