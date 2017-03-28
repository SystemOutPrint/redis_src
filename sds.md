# redis字符串

# 0x01 定义
```c
struct sdshdr {
    unsigned int len; 	// 已使用的字节长度 + 1(用于保存\0) = buf.length - free
    unsigned int free;  // 空闲长度
    char buf[];			// 字节数组
};
```

# 0x02 减少内存重分配
空间预分配：
newLen = newLen < 1M ? 2*oldLen + 1 : oldLen + 1M + 1
惰性空间释放:
截取字符串的时候，并没有重新分配buf，而是将多余的字节存入free中，等待下次分配。

#0x03 字符串编码
int、embstr、raw
int编码用于long型能表示的整型数据，都可以使用该编码格式。
embstr编码只限于小于等于39字节的字符串，64 - 39 = 16(redisObject) + 8(sdshdr) + 1
raw编码用于大于39字节的字符串。

#0x04 字符串共享
int编码的字符串可以共享，通过redisObject的refCount来实现。redis会在启动的时候缓存0-9999的数字用来共享。
int编码的字符串共享时间复杂度是O(1)，embstr和raw编码的字符串共享的时间复杂度是O(N)，为了效率并没有对所有字符串都使用共享功能。