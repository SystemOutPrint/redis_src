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


## 0x03 跳跃表搜索原理
![github-01.png](/images/skiplist_01.png "github-01.png")