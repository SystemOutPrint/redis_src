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