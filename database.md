# 数据库

## 0x01 定义
在redisServer中有个db指针，指向这个server中的所有数据库（默认16个），每个db都是一个内存map。定义如下：<br/>
```c
typedef struct redisDb {
    dict *dict;                 // 键空间，存储所有的键值对
    dict *expires;              // 存储所有的定时键
    dict *blocking_keys;        // 客户端等待获得的key
    dict *ready_keys;           // 客户端阻塞准备push的数据
    dict *watched_keys;         // 被观察的key，如果某个事务中间用到了这里的key，则终止。
    struct evictionPoolEntry *eviction_pool;    
    int id;                     // db id
    long long avg_ttl;          // 平均的ttl，统计用。
} redisDb;
```

## 0x02 键空间
对值的操作都包括先查找键，后取值。每次读写键的时候都要检查下键是否过期，做一些统计操作，如果这个键是被watch的，还要将键设置为dirty，如果脏键达到一定数量还要触发持久化操作，这个键有订阅的，还要广播通知。

## 0x03 键过期
EXPIRE、PEXPIRE、EXPIREAT、PEXPIREAT这些命令都是使用PEXPIREAT来实现的。<br/>
键的过期时间被放在expires中，key是键的名称，value是到期时间戳。<br/>
PERSIST用来删除过期时间，TTL和PTTL用来查看剩余生存时间。

## 0x04 过期键删除策略
惰性删除：当执行对该键的操作时候检测该键是否过期。<br/>
定期删除：server会定期执行serverCron函数，在这个函数里每次遍历固定数量的db，删除少量的过期键。

## 0x05 持久化和复制功能对过期键的处理策略
RDB：在SAVE或BGSAVE的时候过期键不会被写入rdb文件，未过期的键会将到期时间写入rdb文件，载入的时候主服务器会过滤已过期的键，从服务器会全部加载，等待主服务器同步新的数据过来。<br/>
AOF：在键过期后，在惰性删除或者定期删除的时候在aof文件后追加一条删除该键的日志，aof重写不会写入过期键，未过期的键会写入到期时间。<br/>
复制：从服务器并不会对过期键做任何操作，只是等待主服务器同步数据过来。

## 0x06 数据库通知
订阅频道：subscribe \_\_keyspace@0\_\_:xxx<br/>
订阅事件：subscribe \_\_keyevent@0\_\_:xxx<br/>