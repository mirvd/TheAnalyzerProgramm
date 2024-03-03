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
            while (true) {
                try {
                    String text = queueA.take();
                    int count = countOccurrences(text, 'a');
                    if (count > countMaxA) countMaxA = count;

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (generateFinish && queueA.isEmpty()) {
                    break;
                }
            }
        });

        Thread threadB = new Thread(() -> {
            while (true) {
                try {
                    String text = queueB.take();
                    int count = countOccurrences(text, 'b');
                    if (count > countMaxB) countMaxB = count;

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (generateFinish && queueB.isEmpty()) {
                    break;
                }
            }
        });
        Thread threadC = new Thread(() -> {
            while (true) {
                try {
                    String text = queueC.take();
                    int count = countOccurrences(text, 'c');
                    if (count > countMaxC) countMaxC = count;

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (generateFinish && queueC.isEmpty()) {
                    break;
                }
            }
        });

        threadCreateText.start();
        threadA.start();
        threadB.start();
        threadC.start();

        threadCreateText.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("Cамое большим количество символов 'a' в строке: " + countMaxA + " шт ");
        System.out.println("Cамое большим количество символов 'b' в строке: " + countMaxB + " шт ");
        System.out.println("Cамое большим количество символов 'c' в строке: " + countMaxC + " шт ");

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

}