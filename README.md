# optional-lambda
jdk1.8  optional和lambda的常用案例
## Optional类详解
### 1.类中变量:
````
EMPTY:一个空的optional实例 用来通过empty() 重置被封装对象值为空
value：一个不是空的optional实例 ，被封装对象
````

### 2.类中的public方法：
````
//获得空的optional对象  
static<T> Optional<T> empty();

//转化对象为optional包装类型，如果为空抛出异常
static <T> Optional<T> of(T value);

//转化对象为optional包装类型，为空不报异常  
static <T> Optional<T> ofNullable(T value);

//获取包装的对象  
T get();

//判断optional包装的对象是否为空，返回boolean值由！=判断  
boolean isPresent();

//判断optional包装对象是否不为空并且使用consumer接口的函数没有异常  
//Consumer函数接口是用来接受一个参数去执行，不返回值  
void ifPresent(Consumer<? super T > consumer);

//获取被包装的对象，如果为空返回参数，参数不能是lambda  
T orElse(T other);

//获取被包装的对象，如果为空返回lambda表达式的值  
T orElseGet(Supplier<? extends T> other);

//获取被包装的对象，如果为空抛出lambda表达式返回的异常  
<X extends Throwable> T orElseThrow(Supplier<?extends X> exceptionSupplier) throws X;

ps.Optional 拥有filter、map、flatmap流操作方式 具体参考Streams API 
````
## lambda 表达式
### 1、lambda简介
lambda 表达式（又被成为“闭包”或“匿名方法”）  
lambda表达式的特征在于以下语法 -   
````
parameter -> expression body
````
lambda表达式的特征 -  
- 可选类型声明 - 不需要声明参数的类型。编译器可以从参数的值推断相同的值。  
- 关于参数的可选括号 - 不需要在括号中声明单个参数。对于多个参数，需要括号。  
- 可选的花括号 - 如果正文包含单个语句，则无需在表达式正文中使用花括号。  
- 可选返回关键字 - 如果正文具有单个表达式以返回值，编译器将自动返回值。需要花括号表示表达式返回值。  
### 2、疑问
Java 8 中的 lambda 为什么要设计成这样？（为什么要一个 lambda 对应一个接口？)  
lambda 和匿名类型的关系是什么？lambda 是匿名对象的语法糖吗?  
Java 8 是如何对 lambda 进行类型推导的？它的类型推导做到了什么程度？  
Java 8 为什么要引入默认方法？  
Java 编译器如何处理 lambda？  
...
### 3、lambda与匿名类
匿名内部类最典型的一个问题就是对应用的性能有影响。  
首先，编译器会为每个匿名内部类单独生成一个类文件。这个类文件的名字都是类似ClassName$1的格式，其中，ClassName是匿名内部类所在的类名，接着是个$符号加一个数字。生成很多匿名内部类的方式是很不现实的，因为每个匿名内部类在使用之前都要被加载和验证，这个会影响应用的启动性能。  
而且类加载本身也是个很费资源的操作，它会消耗磁盘I/O，同时还需要对JAR包进行解压。如果lambda表达式都被翻译成匿名内部类来实现的话，那么对于每个lambda表达式都会生成一个新的类文件。  
每个匿名内部类都需要被加载，这将会消耗JVM meta-space（Java 8 里替代Permanent Generation）的空间。然后每个匿名内部类里的代码都被JVM编译成机器码 ，那么它们都要被存放在代码缓存区（code cache）里从而占用缓存。除此之外，这些匿名内部类都要被实例化成单独的对象。  
这样一来 ，匿名内部类的实现就会增加你的应用的内存消耗。如果在这中间加入一个缓存机制的话，是极有可能减少内存的消耗的，这就是我们想引入一个中间层来解决这个问题的动机。  

举个栗子：
```java
import java.util.function.Function;
public class AnonymousClassExample {
    Function<String, String> format = new Function<String, String>() {
        public String apply(String input){
            return Character.toUpperCase(input.charAt(0)) + input.substring(1);
        }
    };
}

```
通过```bash javap -c -v ClassName```可以看到对应Function所生成的字节码大致如下：
````
0: aload_0       
1: invokespecial #1 // Method java/lang/Object."<init>":()V
4: aload_0       
5: new           #2 // class AnonymousClassExample$1
8: dup           
9: aload_0       
10: invokespecial #3 // Method AnonymousClass$1."<init>":(LAnonymousClassExample;)V
13: putfield      #4 // Field format:Ljava/util/function/Function;
16: return 

````
- 5： 一个AnonymousClassExample$1的实例通过new操作符进行了初始化。同时将这个新创建的实例的引压入堆栈。
- 8：```dup```操作符复制了这个引用。
- 10：然后这个引用值被```invokeSpecial```消耗，作为参数来初始化匿名内部类的实例。
- 13：现在栈顶依旧包含这个实例的引用，该对象通过```putfiled```指令将初始化的新实例保存到AnonymousClassExample的format属性里
 
 把Lambda表达式转化成匿名内部类的这种实现方式对于后期实现上的优化（例如缓存方面）会有影响，因为这个实现依赖于匿名内部类的字节码生成机制。因此，我们需要一个稳定的二进制方案，这个方案也能需要能够为未来JVM采用新的实现策略提供做够的上下文信息。
 
 Java 7里新引入的invokedynamic给了提供了一个可以有效实现这种策略的途径。Lambda表达式翻译成字节码的步骤分为两步：  
- 生成一个invokedynamic调用点（call site）（也叫lambda工厂），当它被调用的时候的时候，它会返回一个由lambda转换成的Function Interface实例。
- 将lambda表达式的代码转换成一个可以通过invokespecial命令调用的函数。

再举个栗子： 
````java
import java.util.function.Function;

public class Lambda {
    Function<String, Integer> f = s -> Integer.parseInt(s);
}
````

上面的代码则会翻译成：
```
 0: aload_0
 1: invokespecial #1 // Method java/lang/Object."<init>":()V
 4: aload_0
 5: invokedynamic #2, 0 // InvokeDynamic
                  #0:apply:()Ljava/util/function/Function;
10: putfield #3 // Field f:Ljava/util/function/Function;
13: return
```
值得注意的是方法引用的编译方式略有不同，因为javac不需要生成合成方法，并且可以直接引用该方法。 
 
第二步的实现取决于lambda表达式是非捕获式（non-capturing）的还是捕获式的（capturing）。非捕获式的也就是说lambda表达式不会访问任何它外部的变量，捕获式的lambda会访问在lambda外部定义的变量。

非捕获式（non-capturing）的lambda会被去掉语法糖直接翻译成当前类里和lambda表达式有相同签名的静态函数。以上面的lambda表达式为例，它会被去糖替换成下面的方法：
```java
static Integer lambda$1(String s) {
    return Integer.parseInt(s);
}
```
注意：$1不是匿名内部类，它只是表示这段代码是由编译器生成的。  

但是捕获式（capturing）的lambda就有点复杂了，因为被捕获的变量需要和lambda的参数一起传入到lambda表达式里去执行。这种情况下的转换策略是将捕获到的变量追加到lambda表达式的参数里。我们来看一个实际的例子：  
```java
int offset = 100;
Function<String, Integer> f = s -> Integer.parseInt(s) + offset;
```
生成的代码大概如下：
```java
static Integer lambda$1(int offset, String s) {
    return Integer.parseInt(s) + offset;
}
```

不过，这个转换策略也不一定是正确的，因为invokedynamic指令本身给编译器的策略提供了很大的选择空间。例如，捕获的变量也可以放在一个数组里，也或者如果lambda表达式读取了它所在类的变量，那么生成的方法也可以是实例方法，而不是静态方法，这样就可以不需要把这些变量作为额外的参数传递给lambda了。

这种实现最大的优势就是性能有所提升。当然了，如果能够有个单一具体的数据来说明就最好了，但是这中间涉及到很多个阶段，每个阶段的耗时都不一样。  

第一个阶段就是链接阶段，这个对应上面提到的lambda工厂。如果我们和匿名内部类来对比的话，这个阶段就对应到匿名内部类本身的加载了。  

第二个阶段是从上下文里捕获变量。正如我们已经提到过，如果没有变量被捕获的话，基于lambda工厂的实现可以进一步优化来避免创建新的对象。在匿名内部类里，我们就需要创建一个新的实例了。如果要达到相同的优化效果，你需要自己手动创建一个实例对象，然后用一个静态变量来引用它。

第三个阶段是调用实际的方法。这个阶段，无论是匿名内部类还是lambda表达式都是调用相同的代码，所以这个地方性能上没有差别。对于非捕获（non-capturing）的场景，lambda表达式已经优于匿名内部类的实现了。对于捕获式（capturing）的场景，lambda表达式的实现和创建一个匿名内部类来保存变量的性能大同小异。  

到此我们可以了解到lambda表达式的实现总体上表现良好。对于匿名内部类方式需要手动优化避免对象创建的这种场景的场景（非捕获式的lambda表达式）已经被JVM进行优化了。

两者的区别：  
- 类必须实例化，而lambda不必；
- 当一个类被新建时，需要给对象分配内存；
- lambda只需要分配一次内存，它被存储在堆的永久区内；
- 对象作用于它自己的数据，而lambda不会；
- this的语义不同，匿名类this指翔的是它本身，而lambda的this则指向它所在的类
### 4、lambda详解
在上文中我们提到了类型推导，那么它到底是怎么样工作的呢？在解释这个问题前我们先来了解几个基本概念：

##### 1. 目标类型（Target typing）  
首先需要注意的是，函数式接口的名称并不是 lambda 表达式的一部分。编译器负责推导 lambda 表达式类型。它利用 lambda 表达式所在上下文 所期待的类型 进行推导，这个 被期待的类型 被称为 目标类型。lambda 表达式只能出现在目标类型为函数式接口的上下文中。  
当然，lambda 表达式对目标类型也是有要求的编译器会检查 lambda表达式的类型和目标类型的方法签名(method signature)是否一致。当且仅当下面所有条件均满足时，lambda 表达式才可以被赋给目标类型 T：  
- T 是一个函数式接口
- lambda 表达式的参数和 T 的方法参数在数量和类型上一一对应
- lambda 表达式的返回值和 T 的方法返回值相兼容（Compatible）
- lambda 表达式内所抛出的异常和 T 的方法 throws 类型相兼容
 
由于目标类型（函数式接口）已经“知道” lambda 表达式的形式参数（Formal parameter）类型，所以我们没有必要把已知类型再重复一遍。也就是说，lambda 表达式的参数类型可以从目标类型中得出：  
 ```Comparator<String> c = (s1, s2) -> s1.compareToIgnoreCase(s2);``` s1、s2编译器自动推导出类型喂String，值得一提的是当 lambda 的参数只有一个而且它的类型可以被推导得知时，该参数列表外面的括号可以被省略，实际上lambda并不是第一个拥有上下文相关的java表达式，泛型方法调用和“菱形”构造器调用也通过目标类型来进行类型推导： 
 ```
 List<String> ls = Collections.emptyList();
 List<Integer> li = Collections.emptyList();
 Map<String, Integer> m1 = new HashMap<>();
 Map<Integer, String> m2 = new HashMap<>();
 ```
##### 2. 目标类型的上下文（Contexts for target typing）
带有目标类型的上下文：
- 变量声明
- 赋值
- 返回语句
- 数组初始化器
- 方法和构造方法的参数
- lambda 表达式函数体
- 条件表达式（? :）
- 转型（Cast）表达式  

在前三个上下文（变量声明、赋值和返回语句）里，目标类型即是被赋值或被返回的类型：
```java
Comparator<String> c;
c = (String s1, String s2) -> s1.compareToIgnoreCase(s2);
public Runnable toDoLater() {
  return () -> {
    System.out.println("later");
  }
}
```

数组初始化器和赋值类似，只是这里的“变量”变成了数组元素，而类型是从数组类型中推导得知：
```java
filterFiles(
  new FileFilter[] {
    f -> f.exists(), f -> f.canRead(), f -> f.getName().startsWith("q")
  });
```

方法参数的类型推导要相对复杂些：目标类型的确认会涉及到其它两个语言特性：重载解析（Overload resolution）和参数类型推导（Type argument inference）。

重载解析会为一个给定的方法调用（method invocation）寻找最合适的方法声明（method declaration）。由于不同的声明具有不同的签名，当 lambda 表达式作为方法参数时，重载解析就会影响到 lambda 表达式的目标类型。编译器会通过它所得之的信息来做出决定。如果 lambda 表达式具有 显式类型（参数类型被显式指定），编译器就可以直接 使用lambda 表达式的返回类型；如果lambda表达式具有 隐式类型（参数类型被推导而知），重载解析则会忽略 lambda 表达式函数体而只依赖 lambda 表达式参数的数量。  

如果在解析方法声明时存在二义性（ambiguous），我们就需要利用转型（cast）或显式 lambda 表达式来提供更多的类型信息。如果 lambda 表达式的返回类型依赖于其参数的类型，那么 lambda 表达式函数体有可能可以给编译器提供额外的信息，以便其推导参数类型。  
```java
List<Person> ps = ...
Stream<String> names = ps.stream().map(p -> p.getName());
```
在上面的代码中，ps 的类型是 List<Person>，所以 ps.stream() 的返回类型是 Stream<Person>。map() 方法接收一个类型为 Function<T, R> 的函数式接口，这里 T 的类型即是 Stream 元素的类型，也就是 Person，而 R 的类型未知。由于在重载解析之后 lambda 表达式的目标类型仍然未知，我们就需要推导 R 的类型：通过对 lambda 表达式函数体进行类型检查，我们发现函数体返回 String，因此 R 的类型是 String，因而 map() 返回 Stream<String>。绝大多数情况下编译器都能解析出正确的类型，但如果碰到无法解析的情况，我们则需要：  
- 使用显式 lambda 表达式（为参数 p 提供显式类型）以提供额外的类型信息
- 把 lambda 表达式转型为 ```Function<Person, String>```
- 为泛型参数 R 提供一个实际类型。```(.<String>map(p -> p.getName()))```

lambda 表达式本身也可以为它自己的函数体提供目标类型，也就是说 lambda 表达式可以通过外部目标类型推导出其内部的返回类型，这意味着我们可以方便的编写一个返回函数的函数：  
```Supplier<Runnable> c = () -> () -> { System.out.println("hi"); };```  
最后，转型表达式（Cast expression）可以显式提供 lambda 表达式的类型，这个特性在无法确认目标类型时非常有用。除此之外，当重载的方法都拥有函数式接口时，转型可以帮助解决重载解析时出现的二义性。目标类型这个概念不仅仅适用于 lambda 表达式，泛型方法调用和“菱形”构造方法调用也可以从目标类型中受益，下面的代码在 Java SE 7 是非法的，但在 Java SE 8 中是合法的：
```java
List<String> ls = Collections.checkedList(new ArrayList<>(), String.class);
Set<Integer> si = flag ? Collections.singleton(23) : Collections.emptySet();
```

##### 3. 词法作用域（Lexical scoping）

如上文提到的在内部类中使用变量名（以及 this）非常容易出错。内部类中通过继承得到的成员（包括来自 Object 的方法）可能会把外部类的成员掩盖（shadow），此外未限定（unqualified）的 this 引用会指向内部类自己而非外部类。

相对于内部类，lambda 表达式的语义就十分简单：它不会从超类（supertype）中继承任何变量名，也不会引入一个新的作用域。lambda 表达式基于词法作用域，也就是说 lambda 表达式函数体里面的变量和它外部环境的变量具有相同的语义（也包括 lambda 表达式的形式参数）。此外，’this’ 关键字及其引用在 lambda 表达式内部和外部也拥有相同的语义。  
##### 4. 变量捕获（Variable capture）
在 Java SE 7 中，编译器对内部类中引用的外部变量（即捕获的变量）要求非常严格：如果捕获的变量没有被声明为 final 就会产生一个编译错误。我们现在放宽了这个限制——对于 lambda 表达式和内部类，我们允许在其中捕获那些符合 有效只读（Effectively final）的局部变量。

简单的说，如果一个局部变量在初始化后从未被修改过，那么它就符合有效只读的要求，换句话说，加上 final 后也不会导致编译错误的局部变量就是有效只读变量。  

对 this 的引用，以及通过 this 对未限定字段的引用和未限定方法的调用在本质上都属于使用 final 局部变量。包含此类引用的 lambda 表达式相当于捕获了 this 实例。在其它情况下，lambda 对象不会保留任何对 this 的引用。
这个特性对内存管理是一件好事：内部类实例会一直保留一个对其外部类实例的强引用，而那些没有捕获外部类成员的 lambda 表达式则不会保留对外部类实例的引用。要知道内部类的这个特性往往会造成内存泄露。  
简而言之，lambda 表达式对 值 封闭，对 变量 开放。
```java
int sum = 0;
list.forEach(e -> { sum += e.size(); });// 报错

List<Integer> aList = new List<>();
list.forEach(e -> { aList.add(e); })
```
##### 5. 方法引用（Method references）和方法引用的种类（Kinds of method references）
lambda 表达式允许我们定义一个匿名方法，并允许我们以函数式接口的方式使用它。我们也希望能够在 已有的 方法上实现同样的特性。

方法引用和 lambda 表达式拥有相同的特性（例如，它们都需要一个目标类型，并需要被转化为函数式接口的实例），不过我们并不需要为方法引用提供方法体，我们可以直接通过方法名称引用已有方法。如果我们想要调用的方法拥有一个名字，我们就可以通过它的名字直接调用它。函数式接口的方法参数对应于隐式方法调用时的参数，所以被引用方法签名可以通过放宽类型，装箱以及组织到参数数组中的方式对其参数进行操作，就像在调用实际方法一样

方法引用有很多种，它们的语法如下：
- 静态方法引用：```ClassName::methodName```
- 实例上的实例方法引用：```instanceReference::methodName```
- 超类上的实例方法引用：```super::methodName```
- 类型上的实例方法引用：```ClassName::methodName```
- 构造方法引用：```Class::new```
- 数组构造方法引用：```TypeName[]::new```

### 5、 题外话 
##### 默认方法和静态接口方法（Default and static interface methods）
Java SE 7 时代为一个已有的类库增加功能是非常困难的。具体的说，接口在发布之后就已经被定型，除非我们能够一次性更新所有该接口的实现，否则向接口添加方法就会破坏现有的接口实现。默认方法出现（之前被称为 虚拟扩展方法 或 守护方法）的目标即是解决这个问题，使得接口在发布之后仍能被逐步演化。  
默认方法 利用面向对象的方式向接口增加新的行为。它是一种新的方法：接口方法可以是 抽象的 或是 默认的。默认方法拥有其默认实现，实现接口的类型通过继承得到该默认实现（如果类型没有覆盖该默认实现）。此外，默认方法不是抽象方法，所以我们可以放心的向函数式接口里增加默认方法，而不用担心函数式接口的单抽象方法限制。  
当接口继承其它接口时，我们既可以为它所继承而来的抽象方法提供一个默认实现，也可以为它继承而来的默认方法提供一个新的实现，还可以把它继承而来的默认方法重新抽象化。  
除了默认方法，Java SE 8 还在允许在接口中定义 静态 方法。这使得我们可以从接口直接调用和它相关的辅助方法（Helper method），而不是从其它的类中调用（之前这样的类往往以对应接口的复数命名，例如 Collections）。比如，我们一般需要使用静态辅助方法生成实现 Comparator 的比较器，在Java SE 8中我们可以直接把该静态方法定义在 Comparator 接口中：  
```java
public static <T, U extends Comparable<? super U>>
    Comparator<T> comparing(Function<T, U> keyExtractor) {
  return (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
}
```
和其它方法一样，默认方法也可以被继承，大多数情况下这种继承行为和我们所期待的一致。不过，当类型或者接口的超类拥有多个具有相同签名的方法时，我们就需要一套规则来解决这个冲突：
- 类的方法（class method）声明优先于接口默认方法。无论该方法是具体的还是抽象的。
- 被其它类型所覆盖的方法会被忽略。这条规则适用于超类型共享一个公共祖先的情况。

当两个独立的默认方法相冲突或是默认方法和抽象方法相冲突时会产生编译错误。这时我们需要显式覆盖超类方法。一般来说我们会定义一个默认方法，然后在其中显式选择超类方法：
```java
interface Robot implements Artist, Gun {
  default void draw() { Artist.super.draw(); }
}
```

### 6、 lambda的问题
lambda尽管有很多的优点，但是它依旧拥有一些问题  
比如在一个需要尽量减少GC的系统上(但是事实上确没有这样)。这个实现原本是为了避免创建太多对象。它里面大量使用了lambda表达式来作为进行回调处理。不幸的是，我们有好几个回调虽然没有捕获局部变量，但是需要引用当前类的成员变量或者函数。目前来看，好像还是会导致对象的创建。下面是作为说明的实例代码：
```java
public MessageProcessor() {} 

public int processMessages() {
    return queue.read(obj -> {
        if (obj instanceof NewClient) {
            this.processNewClient((NewClient) obj);
        } 
        ...
    });
}
```
在这个项目里，内存诊断显示内存占用量排前八的地方有六个是出自这里这个模式产生的对象，占用应用总内存的60%。  
对于这个问题，我们有个很简单的解决方案。就是把这段代码抽取到构造函数里，然后用一个变量来引用调用点（call site）。下面是重写后的代码：
```java
private final Consumer<Msg> handler; 

public MessageProcessor() {
    handler = obj -> {
        if (obj instanceof NewClient) {
            this.processNewClient((NewClient) obj);
        }
        ...
    };
} 

public int processMessages() {
    return queue.read(handler);
}
```
但是使用这种方式来优化，也存在着其他问题。
1. 这里纯粹是为了性能才写这样不符合规范的代码。所以会导致可读性降低。
2. 这里也有其他内存分配的问题。你在MessageProcessor里添加了字段，导致它需要占用更多内存。同时，lambda的创建以及变量的捕获都会导致MessageProcessor的构造函数变慢。

我们之所以会有这样的方案，并不是实际有这样的场景，而是通过内存诊断才发现这个问题的，然后我们恰好有个合适的业务场景证实了这个优化的可行性。我们也只会创建一次对象，然后频繁使用lambda表达式的场景，这样的话缓存就变得非常有用了。和其他所有内存调优实践一样，科学的方法往往都是最值得推荐的。  

这个方法也适用于其他想要对lambda表达式进行调优的场景。首先尽量编写干净、简单以及函数式的代码。任何优化，例如这种抽取，都是尽量用来对付一些棘手的问题。编写需要捕获创建对象的lambda表达式并不是坏事，就像用Java代码来调用new Foo()本身就没有任何问题一样。  

这个实践也向我们建议使用lambda表达式的最佳方案就是按照常规编码习惯来用。如果lambda表达式只是用来表示小的，纯函数式的功能，那么它完全没有必要去捕获上下文的变量。就像其他所有事情一样 —— 越简单越高效。  

### 7、 实际场景
参考src里的代码


本文转自：  
[Java 8 Lambdas - A Peek Under the Hood](https://www.infoq.com/articles/Java-8-Lambdas-A-Peek-Under-the-Hood)  
[揭开Java 8 Lambda表达式的神秘面纱](https://www.jianshu.com/p/2ecf6c71d7c5)  
[深入理解Java 8 Lambda](http://lucida.me/blog/java-8-lambdas-insideout-language-features/)  
以及个人理解


