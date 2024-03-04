package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    private static final int numberOfText = 10_000;
    private static boolean generateFinish = false;

    private static final int lenghtLine = 100_000;
    private static int countMaxA = 0;
    private static int countMaxB = 0;
    private static int countMaxC = 0;

    public static void main(String[] args) throws InterruptedException {

        Thread threadCreateText = new Thread(() -> {
            for (int i = 0; i < numberOfText; i++) {
                String text = generateText("abc", lenghtLine);
                try {
                    queueA.put(text.toString());
                    queueB.put(text.toString());
                    queueC.put(text.toString());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            generateFinish = true;
        });

        Thread threadA = new Thread(() -> {
            countMaxA = calcMax(queueA, 'a', countMaxA);
        });

        Thread threadB = new Thread(() -> {
            countMaxB = calcMax(queueB, 'b', countMaxB);
        });

        Thread threadC = new Thread(() -> {
            countMaxC = calcMax(queueC, 'c', countMaxC);
        });

        threadCreateText.start();
        threadA.start();
        threadB.start();
        threadC.start();

        threadCreateText.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("Cамое большое количество символов 'a' в строке: " + countMaxA + " шт ");
        System.out.println("Cамое большое количество символов 'b' в строке: " + countMaxB + " шт ");
        System.out.println("Cамое большое количество символов 'c' в строке: " + countMaxC + " шт ");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static int countOccurrences(String str, char ch) {
        return str.length() - str.replace(String.valueOf(ch), "").length();
    }

    private static int calcMax(BlockingQueue<String> queue, char symbol, int countMax) {
        while (true) {
            try {
                String text = queue.take();
                int count = countOccurrences(text, symbol);
                if (count > countMax) countMax = count;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (generateFinish && queue.isEmpty()) {
                break;
            }
        }
        return countMax;
    }

}