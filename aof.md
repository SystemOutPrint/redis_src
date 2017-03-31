# AOF

## 0x01 原理
__命令追加__：客户端传来的命令会先缓存再redisServer.aof_buf中。<br/>
__文件写入__：当每次serverCron执行的时候，将aof_buf中的命令写入aof文件中。<br/>
__文件同步__：根据配置选项来选择调用fsync的时机。(always、everysec、no)<br/><br/>
redis无法做到绝对的数据安全，所以更多的是将redis用作缓存。<br/>
aof文件的加载是创建一个伪客户端，然后从aof文件中一条一条读取命令，然后执行。

## 0x02 aof重写
&nbsp;&nbsp;一直aof会造成aof日志文件一直在膨胀，aof文件的大小影响起服加载速度和磁盘占用。所以当aof文件的大小达到一定的时候需要重写aof日志。AOFREWRITE首先创建了一个新的aof文件，然后遍历所有数据库中的所有键，根据数据结构的type调用相应的命令，将过期时间打包一起写入新的aof文件。<br/>
&nbsp;&nbsp;如果数据结构元素较多，一次写入会造成读取的时候客户端缓存溢出。所以aof中每条命令写入的键数不应太多（具体见REDIS_AOF_REWRITE_ITEMS_PER_CMD，默认64）。<br/>
&nbsp;&nbsp;为了不阻塞进程，所以BGREWRITEAOF使用cow进行后台aof重写，在重写期间的客户端命令会先缓存到aof的重写缓冲区中，等待重写执行完毕，再将缓冲区中的命令写入到新的aof文件中，这样就能保证新的aof文件和内存一致了。<br/>
&nbsp;&nbsp;BGREWRITEAOF执行完毕后，主进程会将新的aof文件原子替换掉旧的aof文件。