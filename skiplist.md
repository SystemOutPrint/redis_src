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
![github-01.png](/images/skiplist_01.png "github-01.png")<br/>
### 查找分值为6.0的元素
选取头节点的最高层，1.0_L4<br/>
得到1.0_L4的后继节点7.0_L4<br/>
判断6.0 < 7.0<br/>
回到1.0_L4<br/>
选择1.0_L3<br/>
得到1.0_L3的后继节点4.0_L3<br/>
判断6.0 > 4.0<br/>
选择4.0_L3<br/>
得到4.0_L3的后继节点7.0_L3<br/>
判断6.0 < 7.0<br/>
选择4.0_L2<br/>
得到4.0_L2的后继节点7.0_L2<br/>
判断6.0 < 7.0<br/>
选择4.0_L1<br/>
得到4.0_L1的后继节点6.0_L1<br/>
判断6.0 == 6.0<br/>
完成