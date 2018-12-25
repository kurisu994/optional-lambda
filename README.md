# optional-lambda
jdk1.8  optional和lambda的常用案例
## Optional类详解
### 1.类中变量:
````
EMPTY:一个空的optional实例 用来通过empty() 重置被封装对象值为空
value：一个不是空的optional实例 ，被封装对象
````

### 2.类中public函数：
````
//获得空的optional对象  
static<T> Optional<T> empty();

//转化对象为optional包装类型 如果为空抛出异常
static <T> Optional<T> of(T value);

//转化对象为optional包装类型 为空不报异常  
static <T> Optional<T> ofNullable(T value);

//获取包装的对象  
T get();

//判断optional包装的对象是否为空 返回boolean值 由！=判断  
boolean isPresent();

//判断optional包装对象是否不为空并且使用consumer接口的函数没有异常  
//Consumer函数接口是用来接受一个参数去执行 不返回值  
void ifPresent(Consumer<? super T > consumer);

//Optional 拥有filter、map、flatmap流操作方式  
//获取被包装的对象如果为空返回参数 参数不能是lambda  
T orElse(T other);

//获取被包装的对象 如果为空 返回lambda表达式的值  
T orElseGet(Supplier<? extends T> other);

//获取被包装的对象 如果为空 抛出lambda表达式返回的异常  
<X extends Throwable> T orElseThrow(Supplier<?extends X> exceptionSupplier) throws X;
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
### 2、lambda的历史
````
内容
````
### 3、疑问
Java 8 中的 lambda 为什么要设计成这样？（为什么要一个 lambda 对应一个接口？) 
lambda 和匿名类型的关系是什么？lambda 是匿名对象的语法糖吗?  
Java 8 是如何对 lambda 进行类型推导的？它的类型推导做到了什么程度？  
Java 8 为什么要引入默认方法？  
Java 编译器如何处理 lambda？  
...
### 4、lambda与匿名类
````
内容
````
### 5、lambda表达式的应用
````
内容
````