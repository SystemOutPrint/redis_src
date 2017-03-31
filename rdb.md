# RDB

## 0x01 原理
对某一时刻的数据库状态做一个快照，持久化到硬盘中。如果没有aof文件，在启动的时候检测到rdb文件，会将数据加载入内存。SAVE是直接阻塞server进程进行持久化，而BGSAVE使用cow来实现非阻塞快照存储。因为cow是写时复制，所以最糟糕的情况下，内存中的每个数据都被修改过，这样会造成在执行BGSAVE的时候浪费50%的内存。

## 0x02 条件
	save 900 1 // 900秒内至少对数据库进行一次修改
	save 300 10 // 300秒内对数据库至少进行了10次修改
	save 60 10000 // 60秒内对数据库至少进行了10000次修改

这些条件存储在redisServer的saveparams中。<br/>
对server修改次数存储在redisServer的dirty中。<br/>
上次保存的时间存储在redisServer中的lastsave中。<br/>
在每次serverCron执行的时候就会检查下是否可以进行快照持久化操作。(BASAVE和BGREWRITEAOF不能同时进行)<br/>

## 0x03 rdb文件格式
\<REDIS\>\<db_version\>\<database\>\<EOF\>\<check_sum\><br/>
__REDIS__：常量<br/>
__db_version__：rdb文件的版本号<br/>
__database__：包含0个或任意多个数据库<br/>
__EOF__：文件结束符<br/>
__check_sum__：校验和


## 0x04 database格式
\<SELECTDB\>\<db_number\>\<key_value_pairs\><br/>
__SELECTDB__：常量代表下面要读入db的id<br/>
__db_number__：db的id<br/>
__key_value_pairs__：键值对<br/>

## 0x04 key_value_pairs格式
不带过期时间的由\<type\>\<key\>\<value\>组成。<br/>
带过期时间的由\<EXPIRETIME_MS\>\<时间戳\>\<type\>\<key\>\<value\>组成。<br/>
type值：<br/>
REDIS_RDB_TYPE_STRING<br/>
REDIS_RDB_TYPE_LIST<br/>
REDIS_RDB_TYPE_SET<br/>
REDIS_RDB_TYPE_ZSET<br/>
REDIS_RDB_TYPE_HASH<br/>
REDIS_RDB_TYPE_LIST_ZIPLIST<br/>
REDIS_RDB_TYPE_SET_INTSET<br/>
REDIS_RDB_TYPE_ZSET_ZIPLIST<br/>
REDIS_RDB_TYPE_HASH_ZIPLIST<br/><br/>

key值只能是string，而value却可以是各种类型。<br/>
REDIS_RDB_TYPE_STRING：如果字符串是整型数字，则存储成\<ENCODING\>\<integer\>。如果开启了压缩，小于等于20字节的字符串原样存储，大于20字节的字符串存储成\<REDIS_RDB_ENC_LZF\>\<compressed_len\>\<origin_len\>\<compressed_string\>，常量表示压缩算法，compressed_len表示压缩之后的字符串长度，origin_len表示压缩之前的长度，compressed_string表示压缩之后的字符串。<br/>
REDIS_RDB_TYPE_LIST：\<list_length\>\<item1\>...\<itemN\><br/>
REDIS_RDB_TYPE_SET：\<set_size\>\<elem1\>...\<elemN\><br/>
REDIS_RDB_TYPE_HASH：\<hash_size\>\<kv1\>...\<kvN\><br/>
REDIS_RDB_TYPE_ZSET：\<sorted_set_size\>\<elem1\>...\<elemN\><br/>
REDIS_RDB_TYPE_SET_INTSET：将集合对象转成一个字符串对象存储<br/>
REDIS_RDB_TYPE_*_ZIPLIST：将压缩列表转成字符串对象，存储到rdb文件中。<br/>