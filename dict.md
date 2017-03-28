# 字典

## 0x01 定义
```c
typedef struct dict {
    dictType *type; // 字典的函数 
    void *privdata; // 私有数据
    dictht ht[2]; // 两个哈希表（多余的用来rehash）
    long rehashidx; // rehash的进度，-1表示并未rehash
    int iterators; // 当前遍历到哪个entry
} dict;
```

```c
typedef struct dictType {
    unsigned int (*hashFunction)(const void *key); // 计算hash值的函数
    void *(*keyDup)(void *privdata, const void *key); // 复制key值的函数
    void *(*valDup)(void *privdata, const void *obj); // 复制value的函数
    int (*keyCompare)(void *privdata, const void *key1, const void *key2); // 比较key值的函数
    void (*keyDestructor)(void *privdata, void *key); // 销毁key值的函数
    void (*valDestructor)(void *privdata, void *obj); // 销毁value值的函数
} dictType;
```

```c
typedef struct dictht {
    dictEntry **table; // 哈希表的数组
    unsigned long size; // 哈希表的长度
    unsigned long sizemask; // 哈希表的掩码 sizemask = size - 1 计算:hashFunction(xx)%sizemask
    unsigned long used; // 哈希表已有的节点数量
} dictht;
```

```c
typedef struct dictEntry {
    void *key; // 这个entry的key值
    union { 
        void *val;
        uint64_t u64;
        int64_t s64;
        double d;
    } v; // 数组内容
    struct dictEntry *next; // entry的后继节点，哈希表数组的每个slot上都是个链表
} dictEntry
```

## 0x02 效率
hset、hget、hdel、hlen、hincrby：O(1)<br/>
hgetall:O(N)

## 0x03 hash算法
Murmurhash2

## 0x04 渐进式rehash
    因为哈希表允许键冲突，极端的情况哈希表数组长度为1，那哈希表就退化为链表来使用了。如果哈希数组的长度非常大，则会浪费很多内存空间，所以redis采用的方式同java的HashMap，首先为哈希表分配少量空间，然后在元素达到某一阈值的时候再进行rehash操作来扩展哈希表数组，这样就会降低键冲突的概率，还能节省内存。<br/>
    哈希表的大小一定是2的整数次幂，所以每次set元素的时候先要判断，dict->ht[0].used >= dict->ht[0].size，如果相等，则进行rehash操作。<br/>
    首先新建一个哈希表，赋值在dict->ht[1]上，大小为x（dict->ht[0] <= x <= 2^n）。然后开始遍历dict->ht[0]上的数组，将所有的键值对移到dict->ht[1]上去，每移一个键值对，将dict.rehashidx值加一。当所有键值对都移完后，释放dict->ht[0]，将dict->ht[1]设置到ht[0]的位置上去。rehash的时候查询操作都会现在ht[0]中执行，如果没有查到，再去ht[1]中执行。如果是更新操作，则要去ht[1]中执行。</br>
rehash扩展操作：如果在没有进行bgsave或bgrewriteaof的时候，负载因子为1，否则为5。<br/>
rehash收缩操作：负载因子小于0.1。


