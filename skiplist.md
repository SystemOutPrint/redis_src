# ��Ծ��

## 0x01 ����
```c
typedef struct zskiplist {
    struct zskiplistNode *header, *tail; // ��Ծ���ͷ����β�ڵ�
    unsigned long length; // ��Ծ��ĳ���
    int level; // ��Ծ������
} zskiplist;
```
```c
typedef struct zskiplistNode {
    robj *obj; // ��ǰ�ڵ��ֵ����
    double score; // ��ǰ�ڵ������
    struct zskiplistNode *backward; // ��ǰlevel��ǰ���ڵ�
    struct zskiplistLevel {
        struct zskiplistNode *forward; // ��ǰlevel�ĺ�̽ڵ�
        unsigned int span; // ���
    } level[]; // ��
} zskiplistNode;
```

## 0x02 Ч��


## 0x03 ��Ծ������ԭ��
![github-01.png](/images/skiplist_01.png "github-01.png")