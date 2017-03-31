# redis对象

## 0x01 定义
```c
typedef struct redisObject {
    unsigned type:4; // 对象类型
    unsigned encoding:4; // 对象编码
    unsigned lru:REDIS_LRU_BITS; // lru时间戳，用来计算这个对象的空闲时间。
    int refcount; // 当前对象的引用次数
    void *ptr; // 数据指针
} robj;
```

## 0x02 类型
REDIS_STRING<br/>
REDIS_LIST<br/>
REDIS_HASH<br/>
REDIS_SET<br/>
REDIS_ZSET<br/>

## 0x03 编码
每种类型的对象的编码方式都不止一种。
REDIS_STRING的编码方式有REDIS_ENCODING_INT、REDIS_ENCODING_EMBSTR、REDIS_ENCODING_RAW。<br/>
REDIS_LIST的编码方式有REDIS_ENCODING_LINKEDLIST、REDIS_ENCODING_ZIPLIST。<br/>
REDIS_HASH的编码方式有REDIS_ENCODING_HT、REDIS_ENCODING_ZIPLIST。<br/>
REDIS_SET的编码方式有REDIS_ENCODING_INTSET、REDIS_ENCODING_HT。<br/>
REDIS_ZSET的编码方式有REDIS_ENCODING_SKIPLIST、REDIS_ENCODING_ZIPLIST。<br/>

## 0x04 编码转换
每种type的编码不止一种，就意味着存在着不同编码之间的相互转换。string的编码转换在sds里描述过了，这里不在赘述。<br/>
因为压缩列表适用于数据量很小的情况，list、hash、zset类型的对象在数据量小的情况下会使用REDIS_ENCODING_ZIPLIST编码。当数据非常少的时候，对压缩列表进行遍历并不会对性能产生影响，而且压缩列表相对其他数据结构会很省内存，所以压缩列表特别适合数据量小的情况。<br/>
编码转换的参数如下：<br/>
list-max-ziplist-entries：最多有多少个元素，默认512。<br/>
list-max-ziplist-value：元素的最大长度，默认64字节。<br/>
hash-max-ziplist-entries：最多有多少个键值对，默认512。<br/>
hash-max-ziplist-value：键值对的最大长度，默认64字节。<br/>
zset-max-ziplist-entries：最多有多少个元素，默认128。<br/>
zset-max-ziplist-value：元素的最大长度，默认64字节。<br/>
如果set中的元素都是整数值，并且集合对元素长度不超过512个，会采用REDIS_ENCODING_INTSET编码，参数如下：<br/>
set-max-intset-entries：最多有多少个元素，默认512。<br/>

## 0x05 跳跃表对象的实现
跳跃表对象中同时使用了hashtable和skiplist，这样的好处在于查找操作的时间复杂度降为O(1)，并且zrank的时间复杂度仍然是O(N)。<br/>
因为hashtable和skiplist中使用指针来指向数据，所以并不会造成空间上的浪费。

## 0x06 内存回收
当redisObject的refCount为0的时候，就可以释放这块内存了。

## 0x07 对象共享
redis中只有整型字符串能共享，当发现传入的整型字符串在sharedObjectsStruct.integers中，就将该对象的refcount加一，达到对象共享的目的。

## 0x08 空转时长
当前key没有被操作的时间(当前时间 - redisObject.lru)，对key进行除object idletime任何操作都会触发更新lru值，这个只有在服务器开启了maxmemory选项才会有用。
