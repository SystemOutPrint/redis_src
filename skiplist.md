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
zadd��zrem��zscore��O(logN)<br/>
zrange��zrevrange��O(logN)<br/>




## 0x03 ��Ծ������ԭ��
![github-01.png](/images/skiplist_01.png "github-01.png")<br/>
### ���ҷ�ֵΪ6.0��Ԫ��
1��ѡȡ��߲��ͷ���(1.0, L4)<br/>
2���õ�(1.0, L4)�ĺ�̽ڵ�(7.0, L4)<br/>
3���жϷ�ֵ6.0 < 7.0<br/>
4���ص�(1.0, L4)<br/>
5��ѡ����ͬ��ֵ����һ��ڵ�(1.0, L3)<br/>
6���õ�(1.0, L3)�ĺ�̽ڵ�(4.0, L3)<br/>
7���жϷ�ֵ6.0 > 4.0<br/>
8��ѡ��(4.0, L3)<br/>
9���õ�(4.0, L3)�ĺ�̽ڵ�(7.0, L3)<br/>
10���жϷ�ֵ6.0 < 7.0<br/>
11���ص�(4.0, L3)
12��ѡ����ͬ��ֵ����һ��ڵ�(4.0, L2)<br/>
13���õ�(4.0, L2)�ĺ�̽ڵ�(7.0, L2)<br/>
14���ж�6.0 < 7.0<br/>
15���ص�(4.0, L2)
16��ѡ����ͬ��ֵ����һ��ڵ�(4.0, L1)<br/>
17���õ�(4.0, L1)�ĺ�̽ڵ�(6.0, L1)<br/>
18���ж�6.0 == 6.0<br/>
19�����
### ����ͼ����
![github-02.png](/images/skiplist_02.png "github-02.png")<br/>