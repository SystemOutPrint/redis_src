# 整数集合

## 0x01 定义
```c
typedef struct intset {
    uint32_t encoding; // 编码方式
    uint32_t length; // 元素数量
    int8_t contents[]; // 保存元素的数组
} intset;
```

## 0x02 编码方式
int16_t、int32_t、int_64_t


## 0x03 升级
当向int16_t的数组内放入int32_t的数字，也就意味着编码方式从int16_t升为int32_t，这时需要将contents扩大为原数组长度的2倍，然后将原数组的元素都升级为int32位。

## 0x04 降级
不支持

## 0x05 效率
sadd、srem：O(N)<br/>
scard：O(1)