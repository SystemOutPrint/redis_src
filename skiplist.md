# 跳跃表

## 0x01 定义
```c
typedef struct zskiplist {
    struct zskiplistNode *header, *tail; // 跳跃表的头结点和尾节点
    unsigned long length; // 跳跃表的长度
    int level; // 跳跃表的深度
} zskiplist;
```
```c
typedef struct zskiplistNode {
    robj *obj; // 当前节点的值对象
    double score; // 当前节点的评分
    struct zskiplistNode *backward; // 当前level的前驱节点
    struct zskiplistLevel {
        struct zskiplistNode *forward; // 当前level的后继节点
        unsigned int span; // 跨度
    } level[]; // 层
} zskiplistNode;
```

## 0x02 效率
zadd、zrem、zscore：O(logN)<br/>
zrange、zrevrange：O(logN)<br/>




## 0x03 跳跃表搜索原理
![github-01.png](/images/skiplist_01.png "github-01.png")<br/>
### 查找分值为6.0的元素
1、选取最高层的头结点(1.0, L4)<br/>
2、得到(1.0, L4)的后继节点(7.0, L4)<br/>
3、判断分值6.0 < 7.0<br/>
4、回到(1.0, L4)<br/>
5、选择相同分值的下一层节点(1.0, L3)<br/>
6、得到(1.0, L3)的后继节点(4.0, L3)<br/>
7、判断分值6.0 > 4.0<br/>
8、选择(4.0, L3)<br/>
9、得到(4.0, L3)的后继节点(7.0, L3)<br/>
10、判断分值6.0 < 7.0<br/>
11、回到(4.0, L3)
12、选择相同分值的下一层节点(4.0, L2)<br/>
13、得到(4.0, L2)的后继节点(7.0, L2)<br/>
14、判断6.0 < 7.0<br/>
15、回到(4.0, L2)
16、选择相同分值的下一层节点(4.0, L1)<br/>
17、得到(4.0, L1)的后继节点(6.0, L1)<br/>
18、判断6.0 == 6.0<br/>
19、完成
### 流程图如下
![github-02.png](/images/skiplist_02.png "github-02.png")<br/>