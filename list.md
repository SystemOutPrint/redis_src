# list-链表

## 0x01 定义

```c
typedef struct list {
    listNode *head; // 链表头结点
    listNode *tail; // 链表尾节点
    void *(*dup)(void *ptr); // 节点值的复制函数
    void (*free)(void *ptr); // 节点值的释放函数
    int (*match)(void *ptr, void *key); // 节点值的比较函数
    unsigned long len; // 链表的长度
} list;
```

```c
typedef struct listIter {
    listNode *next; // 后继节点
    int direction; // 迭代方向
} listIter;
```

```c
typedef struct listNode {
    struct listNode *prev; // 前驱
    struct listNode *next; // 后继
    void *value; // 元素指针
} listNode;
```

## 0x02 效率
llen:O(1)<br/>
lpop、lpush、rpop、rpush:O(1)<br/>
lset、lrem、linsert、lindex：O(N)<br/>
ltrim:O(M+N)=O(N)<br/>

## 0x03 编码
ziplist和linkedlist<br/>
ziplist是压缩列表，类似java中的ArrayList<br/>
linkedlist是链表，类似java中的LinkedList<br/>
