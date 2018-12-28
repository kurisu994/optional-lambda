package kurisu;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author MakiseKurisu
 * @date 2018-12-28 23:14
 */
public class StreamDemo {
    public static void main(String[] args) {
        terminal();
    }

    private static void distinct() {
        List<String> l = Stream.of("a", "b", "a", "b", "c", "a", "b").distinct().collect(Collectors.toList());
        System.out.println(l);
    }

    private static void filter() {
        List l = IntStream.range(1, 10)
                .filter(i -> i % 2 == 0)
                .boxed()
                .collect(Collectors.toList());
        System.out.println(l);
    }

    private static void map() {
        //flatmap 返回新的流
        List<String> l = Stream.of("s", "abc", "aaassda").map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(l);
    }

    private static void limit() {
        List<Double> l = DoubleStream.generate(Math::random).limit(5).boxed().collect(Collectors.toList());
        System.out.println(l);
    }

    private static void peek() {
        //peek forEach 区别
        List<Long> l = Stream.of("3", "1", "8").map(Long::valueOf).peek(System.out::println).collect(Collectors.toList());
        System.out.println(l);
    }

    private static void skip() {
        List<String> l = Stream.of("3", "1", "8").skip(2L).collect(Collectors.toList());
        System.out.println(l);
    }

    private static void sorted() {
        List<String> l = Stream.of("3a", "1b", "e8", "3a", "o9").sorted((o1, o2) -> o1.equals(o2) ? 0 : -1).collect(Collectors.toList());
        System.out.println(l);
    }

    private static void terminal() {
        boolean b = Stream.of("3a", "1b", "e8", "3a", "o9").noneMatch(r -> r.equals("s"));
        long l = Stream.of("3a", "1b", "e8", "3a", "o9").count();
        Integer integer = IntStream.range(0, 12).boxed().collect(Collectors.maxBy((o1, o2) -> o1 - -1)).get();
        Integer reduce = IntStream.range(0, 12).boxed().reduce(0, (x, y) -> x - y);
        System.out.println(reduce);

    }
}
