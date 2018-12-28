package kurisu;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author MakiseKurisu
 * @date 2018-12-24 19:43
 */
public class Lambda {

    private static Random random = new Random(System.currentTimeMillis());
    private static StringBuffer UNKNOWN = new StringBuffer("Unknown");

    /**
     * 比较器
     */
    private static Comparator<SomeThing> comparator = new Comparator<SomeThing>() {
        @Override
        public int compare(SomeThing o1, SomeThing o2) {
            if (o1.getPrice().equals(o2.getPrice())) {
                return o1.getQuantity() - o2.getQuantity();
            } else {
                return o1.getPrice() - o2.getPrice() > 0 ? 1 : -1;
            }
        }
    };

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        SomeOne someOne = new SomeOne();
        someOne.setGender(SomeOne.Gender.female);
        someOne.setAge(13);
        someOne.setOneThing(someOne.createThing());
        //optional(someOne);


        List<SomeThing> list = getSomeThings();
        //List<String> nameList1 = before(list);
        //List<String> nameList2 = nowLambda(list);
        groupBy(list);
        //combination(list);


        long end = System.currentTimeMillis();

        System.out.println("运行时长: " + (end - start) + "ms");
    }

    private static String tests(String a) {
        return UNKNOWN.append(a).toString();
    }

    private static void optional(SomeOne someOne) {
        /*
        以前的写法
         */
//        if (someOne != null) {
//            SomeOne.OneThing oneThing = someOne.getOneThing();
//            if (oneThing != null) {
//                if (oneThing.getThingName() != null && !"".equals(oneThing.getThingName())) {
//                    System.out.println(oneThing.getUse());
//                    return;
//                }
//            }
//        }
//        System.out.println(UNKNOWN.toString());

        /*
        java8
         */
        String use = Optional.ofNullable(someOne)
                .map(SomeOne::getOneThing)
                .filter(r -> r.getThingName() != null && !"".equals(r.getThingName()))
                .map(SomeOne.OneThing::getUse)
                .orElse(UNKNOWN.toString()); // 注意orElse接收是常量,如果是方法则会先计算返回值
//                .orElse(tests("95"));
        System.out.println(use);
        System.out.println(UNKNOWN.toString());
    }

    /**
     * 之前的写法
     *
     * @param list
     * @return
     */
    private static List<String> before(List<SomeThing> list) {
        List<SomeThing> tempList = new ArrayList<>();
        for (SomeThing someThing : list) {
            if (someThing.getPrice() - random.nextDouble() > 0 && someThing.isSale()) {
                tempList.add(someThing);
            }
        }
        tempList.sort(comparator);
        List<String> nameList = new ArrayList<>();
        for (SomeThing thing : tempList) {
            nameList.add(thing.getName());
        }
        return nameList;
    }

    /**
     * 现在的写法
     *
     * @param list
     * @return
     */
    private static List<String> nowLambda(List<SomeThing> list) {
        return list.stream().filter(r -> r.getPrice() > random.nextDouble() && r.isSale())
                .sorted((o1, o2) -> o1.getPrice().equals(o2.getPrice()) ?
                        o1.getQuantity() - o2.getQuantity()
                        : (o1.getPrice() - o2.getPrice() > 0 ? 1 : -1))
                .map(SomeThing::getName).collect(Collectors.toList());
    }

    /**
     * 分组处理
     */

    private static void groupBy(List<SomeThing> list) {
        //聚合类averagingInt、最大最小值maxBy minBy、计数counting、分组groupingBy、字符串连接joining、分区partitioningBy、汇总summarizingInt、化简reducing、转换toXXX
//        DoubleSummaryStatistics summaryStatistics = list.stream().collect(Collectors.summarizingDouble(SomeThing::getPrice)); //返回价格的统计数据 比如总价、平均数 等
//        Map<Integer, List<SomeThing>> map = list.stream().collect(Collectors.groupingBy(SomeThing::getQuantity)); //通过数量分组 返回具体每个数量的对象集合
//        Map<Boolean, List<SomeThing>> map = list.stream().collect(Collectors.groupingBy(r -> r.getQuantity() > 25));//通过具体某个条件拆分 返回满足条件的集合和不满足的集合
//        Map<String, List<SomeThing>> map = list.stream().collect(Collectors.groupingBy(SomeThing::getDealer));//
//        Map<String, Long> map = list.stream().collect(Collectors.groupingBy(SomeThing::getDealer, Collectors.counting()));//同个商家分组并统计具体数量
//        Map<String, Double> map = list.stream().collect(Collectors.groupingBy(
//                SomeThing::getDealer, Collectors.summingDouble(r -> Double.valueOf(new DecimalFormat("#.00").format(r.getPrice())))) //计算商家的价格总量
//        );
        Map<Double, List<SomeThing>> map = list.stream().collect(Collectors.groupingBy(
                r -> Double.valueOf(new DecimalFormat("#.00").format(r.getPrice())), Collectors.mapping(x -> x, Collectors.toList()))
        );
        System.out.println(map);


    }

    /**
     * 组合用法
     *
     * @param list
     */
    private static void combination(List<SomeThing> list) {
        List<String> strings = list.stream().filter(r -> r.getPrice() - 20.00 > 0).map(SomeThing::getName).collect(Collectors.toList());
        System.out.println(strings);
    }

    /**
     * 生产一组数据
     */
    private static List<SomeThing> getSomeThings() {
        List<SomeThing> list = new ArrayList<>();
        for (int i = 1; i < 501; i++) {
            list.add(new SomeThing(
                    (long) i,
                    "东西" + i,
                    Double.valueOf(new DecimalFormat("#.00").format(Math.abs(random.nextDouble() * 100))),
                    Math.abs(random.nextInt() % 50),
                    random.nextBoolean(),
                    "商家" + i % 4
            ));
        }
        return list;
    }
}
