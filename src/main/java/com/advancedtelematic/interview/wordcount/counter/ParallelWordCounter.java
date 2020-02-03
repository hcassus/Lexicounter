package com.advancedtelematic.interview.wordcount.counter;

import com.advancedtelematic.interview.wordcount.reader.CharacterReader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import static java.lang.Thread.sleep;

public class ParallelWordCounter {

    private ConcurrentHashMap<String, Integer> map;
    private SerialWordCounter counter;
    private ForkJoinPool pool;

    public ParallelWordCounter() {
        this.map = new ConcurrentHashMap<>();
        this.counter = new SerialWordCounter(map);
        this.pool = new ForkJoinPool();
    }

    public Map<String, Integer> countWords(CharacterReader... readers) {

        for (CharacterReader reader : readers) {
            pool.execute(fireCount(reader));
        }

        while (inProgress()) {
            Map<String, Integer> partialMap = counter.sortMap(map);
            System.out.println(partialMap);
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return counter.sortMap(map);
    }

    private Thread fireCount(CharacterReader reader) {
        return new Thread(() -> counter.countWords(reader));
    }

    private boolean inProgress() {
        return pool.getActiveThreadCount() > 0;
    }

}
