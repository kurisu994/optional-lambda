# Stream 类

[来源](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)

## 简介

stream接口和其他集合的区别

1. 不存储数据。流是基于数据源的对象，它本身不存储数据元素，而是通过管道将数据源的元素传递给操作。
2. 函数式编程。流的操作不会修改数据源，例如filter不会将数据源中的数据删除。
3. 延迟操作。流的很多操作如filter,map等中间操作是延迟执行的，只有到终点操作才会将操作顺序执行。
4. 可以解绑。对于无限数量的流，有些操作是可以在有限的时间完成的，比如limit(n) 或 findFirst()，这些操作可是实现"短路"(Short-circuiting)，访问到有限的元素后就可以返回。
5. 纯消费。流的元素只能访问一次，类似Iterator，操作没有回头路，如果你想从头重新访问流的元素，对不起，你得重新生成一个新的流。  

流的操作是以管道的方式串起来的。流管道包含一个数据源，接着包含零到N个中间操作，最后以一个终点操作结束。

所有的流操作都可以串行执行或者并行执行。
除非显示地创建并行流，否则Java库中创建的都是串行流。 Collection.stream()为集合创建串行流而Collection.parallelStream()为集合创建并行流。IntStream.range(int, int)创建的是串行流。通过parallel()方法可以将串行流转换成并行流,sequential()方法将流转换成并行流。


## 创建

可以通过多种方式创建流：

1. 通过集合的stream()方法或者```parallelStream()```，比如```Arrays.asList(1,2,3).stream()```。
2. 通过```Arrays.stream(Object[])```方法, 比如```Arrays.stream(new int[]{1,2,3})```。
3. 使用流的静态方法，比如```Stream.of(Object[])```, ```IntStream.range(int, int)``` 或者 ```Stream.iterate(Object, UnaryOperator)```，如```Stream.iterate(0, n -> n * 2)```，或者```generate(Supplier s)```如```Stream.generate(Math::random)```。
4. BufferedReader.lines()从文件中获得行的流。
5. Files类的操作路径的方法，如list、find、walk等。
6. 随机数流```Random.ints()```。
7. 其它一些类提供了创建流的方法，如```BitSet.stream()```, ```Pattern.splitAsStream(java.lang.CharSequence)```, 和 ```JarFile.stream()```。
8. 更底层的使用StreamSupport，它提供了将Spliterator转换成流的方法。

## 中间操作 (intermediate operations)
- distinct   保证输出的流中包含唯一的元素，它是通过Object.equals(Object)来检查是否包含相同的元素。
- filter   返回的流中只包含满足断言(predicate)的数据。
- map   方法将流中的元素映射成另外的值，新的值类型可以和原来的元素的类型不同。
- flatmap   flatmap方法混合了map + flattern的功能，它将映射后的流的元素全部放入到一个新的流中。
- limit   limit方法指定数量的元素的流。对于串行流，这个方法是有效的，这是因为它只需返回前n个元素即可，但是对于有序的并行流，它可能花费相对较长的时间，如果你不在意有序，可以将有序并行流转换为无序的，可以提高性能。
- peek   方法方法会使用一个Consumer消费流中的元素，但是返回的流还是包含原来的流中的元素。
- sorted   sorted()将流中的元素按照自然排序方式进行排序，如果元素没有实现Comparable，则终点操作执行时会抛出java.lang.ClassCastException异常。sorted(Comparator comparator)可以指定排序的方式。
- skip   返回丢弃了前n个元素的流，如果流中的元素小于或者等于n，则返回空的流。

## 终点操作 (terminal operations)
- Match(anyMatch、allMatch、noneMatch)
- count
- collect
- find 
- min 
- max
- reduce
- forEach(forEachOrdered)
