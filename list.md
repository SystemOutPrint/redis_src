# list-����

## 0x01 ����

```c
typedef struct list {
    listNode *head; // ����ͷ���
    listNode *tail; // ����β�ڵ�
    void *(*dup)(void *ptr); // �ڵ�ֵ�ĸ��ƺ���
    void (*free)(void *ptr); // �ڵ�ֵ���ͷź���
    int (*match)(void *ptr, void *key); // �ڵ�ֵ�ıȽϺ���
    unsigned long len; // ����ĳ���
} list;
```

```c
typedef struct listIter {
    listNode *next; // ��̽ڵ�
    int direction; // ��������
} listIter;
```

```c
typedef struct listNode {
    struct listNode *prev; // ǰ��
    struct listNode *next; // ���
    void *value; // Ԫ��ָ��
} listNode;
```

## 0x02 Ч��
llen:O(1)<br/>
lpop��lpush��rpop��rpush:O(1)<br/>
lset��lrem��linsert��lindex��O(N)<br/>
ltrim:O(M+N)=O(N)<br/>

## 0x03 ����
ziplist��linkedlist<br/>
ziplist��ѹ���б�����java�е�ArrayList<br/>
linkedlist����������java�е�LinkedList<br/>
