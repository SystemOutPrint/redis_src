# �ֵ�

## 0x01 ����
```c
typedef struct dict {
    dictType *type; // �ֵ�ĺ��� 
    void *privdata; // ˽������
    dictht ht[2]; // ������ϣ�����������rehash��
    long rehashidx; // rehash�Ľ��ȣ�-1��ʾ��δrehash
    int iterators; // ��ǰ�������ĸ�entry
} dict;
```

```c
typedef struct dictType {
    unsigned int (*hashFunction)(const void *key); // ����hashֵ�ĺ���
    void *(*keyDup)(void *privdata, const void *key); // ����keyֵ�ĺ���
    void *(*valDup)(void *privdata, const void *obj); // ����value�ĺ���
    int (*keyCompare)(void *privdata, const void *key1, const void *key2); // �Ƚ�keyֵ�ĺ���
    void (*keyDestructor)(void *privdata, void *key); // ����keyֵ�ĺ���
    void (*valDestructor)(void *privdata, void *obj); // ����valueֵ�ĺ���
} dictType;
```

```c
typedef struct dictht {
    dictEntry **table; // ��ϣ�������
    unsigned long size; // ��ϣ��ĳ���
    unsigned long sizemask; // ��ϣ������� sizemask = size - 1 ����:hashFunction(xx)%sizemask
    unsigned long used; // ��ϣ�����еĽڵ�����
} dictht;
```

```c
typedef struct dictEntry {
    void *key; // ���entry��keyֵ
    union { 
        void *val;
        uint64_t u64;
        int64_t s64;
        double d;
    } v; // ��������
    struct dictEntry *next; // entry�ĺ�̽ڵ㣬��ϣ�������ÿ��slot�϶��Ǹ�����
} dictEntry
```

## 0x02 Ч��
hset��hget��hdel��hlen��hincrby��O(1)<br/>
hgetall:O(N)

## 0x03 hash�㷨
Murmurhash2

## 0x04 ����ʽrehash
    ��Ϊ��ϣ���������ͻ�����˵������ϣ�����鳤��Ϊ1���ǹ�ϣ����˻�Ϊ������ʹ���ˡ������ϣ����ĳ��ȷǳ�������˷Ѻܶ��ڴ�ռ䣬����redis���õķ�ʽͬjava��HashMap������Ϊ��ϣ����������ռ䣬Ȼ����Ԫ�شﵽĳһ��ֵ��ʱ���ٽ���rehash��������չ��ϣ�����飬�����ͻή�ͼ���ͻ�ĸ��ʣ����ܽ�ʡ�ڴ档<br/>
    ��ϣ��Ĵ�Сһ����2���������ݣ�����ÿ��setԪ�ص�ʱ����Ҫ�жϣ�dict->ht[0].used >= dict->ht[0].size�������ȣ������rehash������<br/>
    �����½�һ����ϣ����ֵ��dict->ht[1]�ϣ���СΪx��dict->ht[0] <= x <= 2^n����Ȼ��ʼ����dict->ht[0]�ϵ����飬�����еļ�ֵ���Ƶ�dict->ht[1]��ȥ��ÿ��һ����ֵ�ԣ���dict.rehashidxֵ��һ�������м�ֵ�Զ�������ͷ�dict->ht[0]����dict->ht[1]���õ�ht[0]��λ����ȥ��rehash��ʱ���ѯ������������ht[0]��ִ�У����û�в鵽����ȥht[1]��ִ�С�����Ǹ��²�������Ҫȥht[1]��ִ�С�</br>
rehash��չ�����������û�н���bgsave��bgrewriteaof��ʱ�򣬸�������Ϊ1������Ϊ5��<br/>
rehash������������������С��0.1��


