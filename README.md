# optional-lambda
jdk1.8  optional和lambda的常用案例
## Optional类详解
### 1.类中变量:
<code>
EMPTY:一个空的optional实例 用来通过empty() 重置被封装对象值为空

value：一个不是空的optional实例 ，被封装对象
</code>

### 2.类中public函数：
<code>
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

</code>
