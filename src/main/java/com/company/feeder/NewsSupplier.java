package com.company.feeder;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.company.model.News;


/**
 * @author lsajjan
 *
 */
public class NewsSupplier implements Supplier<News> {

    public static final String[] HEADLINES = new String[]{
            "up", "down", "rise", "fall", "good", "bad", "success", "failure", "high", "low", "über", "unter"
    };

    private final SecureRandom random;

    private final List<Integer> headlineIndices;

    NewsSupplier() {
        this.random = new SecureRandom(String.valueOf(System.nanoTime()).getBytes());
        this.headlineIndices = IntStream
                .range(0, HEADLINES.length)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public News get() {
        final int oneFrom100 = this.random.nextInt(100);
        final int priority = oneFrom100 < 7 // 7% chance to be high prio
                ? oneFrom100 % 5 + 5
                : oneFrom100 % 5;

        Collections.shuffle(this.headlineIndices);
        return new News(priority,
                this.headlineIndices
                        .subList(0, this.random.nextInt(3) + 3)
                        .stream()
                        .map(i -> HEADLINES[i])
                        .collect(Collectors.toList()));
    }
}
