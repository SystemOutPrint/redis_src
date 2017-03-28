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
![github-01.png](/images/skiplist_01.png "github-01.png")<br/>
### ���ҷ�ֵΪ6.0��Ԫ��
ѡȡͷ�ڵ����߲㣬1.0_L4<br/>
�õ�1.0_L4�ĺ�̽ڵ�7.0_L4<br/>
�ж�6.0 < 7.0<br/>
�ص�1.0_L4<br/>
ѡ��1.0_L3<br/>
�õ�1.0_L3�ĺ�̽ڵ�4.0_L3<br/>
�ж�6.0 > 4.0<br/>
ѡ��4.0_L3<br/>
�õ�4.0_L3�ĺ�̽ڵ�7.0_L3<br/>
�ж�6.0 < 7.0<br/>
ѡ��4.0_L2<br/>
�õ�4.0_L2�ĺ�̽ڵ�7.0_L2<br/>
�ж�6.0 < 7.0<br/>
ѡ��4.0_L1<br/>
�õ�4.0_L1�ĺ�̽ڵ�6.0_L1<br/>
�ж�6.0 == 6.0<br/>
���