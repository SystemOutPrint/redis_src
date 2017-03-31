# redis����

## 0x01 ����
```c
typedef struct redisObject {
    unsigned type:4; // ��������
    unsigned encoding:4; // �������
    unsigned lru:REDIS_LRU_BITS; // lruʱ��������������������Ŀ���ʱ�䡣
    int refcount; // ��ǰ��������ô���
    void *ptr; // ����ָ��
} robj;
```

## 0x02 ����
REDIS_STRING<br/>
REDIS_LIST<br/>
REDIS_HASH<br/>
REDIS_SET<br/>
REDIS_ZSET<br/>

## 0x03 ����
ÿ�����͵Ķ���ı��뷽ʽ����ֹһ�֡�
REDIS_STRING�ı��뷽ʽ��REDIS_ENCODING_INT��REDIS_ENCODING_EMBSTR��REDIS_ENCODING_RAW��<br/>
REDIS_LIST�ı��뷽ʽ��REDIS_ENCODING_LINKEDLIST��REDIS_ENCODING_ZIPLIST��<br/>
REDIS_HASH�ı��뷽ʽ��REDIS_ENCODING_HT��REDIS_ENCODING_ZIPLIST��<br/>
REDIS_SET�ı��뷽ʽ��REDIS_ENCODING_INTSET��REDIS_ENCODING_HT��<br/>
REDIS_ZSET�ı��뷽ʽ��REDIS_ENCODING_SKIPLIST��REDIS_ENCODING_ZIPLIST��<br/>

## 0x04 ����ת��
ÿ��type�ı��벻ֹһ�֣�����ζ�Ŵ����Ų�ͬ����֮����໥ת����string�ı���ת����sds���������ˣ����ﲻ��׸����<br/>
��Ϊѹ���б���������������С�������list��hash��zset���͵Ķ�����������С������»�ʹ��REDIS_ENCODING_ZIPLIST���롣�����ݷǳ��ٵ�ʱ�򣬶�ѹ���б���б�������������ܲ���Ӱ�죬����ѹ���б�����������ݽṹ���ʡ�ڴ棬����ѹ���б��ر��ʺ�������С�������<br/>
����ת���Ĳ������£�<br/>
list-max-ziplist-entries������ж��ٸ�Ԫ�أ�Ĭ��512��<br/>
list-max-ziplist-value��Ԫ�ص���󳤶ȣ�Ĭ��64�ֽڡ�<br/>
hash-max-ziplist-entries������ж��ٸ���ֵ�ԣ�Ĭ��512��<br/>
hash-max-ziplist-value����ֵ�Ե���󳤶ȣ�Ĭ��64�ֽڡ�<br/>
zset-max-ziplist-entries������ж��ٸ�Ԫ�أ�Ĭ��128��<br/>
zset-max-ziplist-value��Ԫ�ص���󳤶ȣ�Ĭ��64�ֽڡ�<br/>
���set�е�Ԫ�ض�������ֵ�����Ҽ��϶�Ԫ�س��Ȳ�����512���������REDIS_ENCODING_INTSET���룬�������£�<br/>
set-max-intset-entries������ж��ٸ�Ԫ�أ�Ĭ��512��<br/>

## 0x05 ��Ծ������ʵ��
��Ծ�������ͬʱʹ����hashtable��skiplist�������ĺô����ڲ��Ҳ�����ʱ�临�ӶȽ�ΪO(1)������zrank��ʱ�临�Ӷ���Ȼ��O(N)��<br/>
��Ϊhashtable��skiplist��ʹ��ָ����ָ�����ݣ����Բ�������ɿռ��ϵ��˷ѡ�

## 0x06 �ڴ����
��redisObject��refCountΪ0��ʱ�򣬾Ϳ����ͷ�����ڴ��ˡ�

## 0x07 ������
redis��ֻ�������ַ����ܹ��������ִ���������ַ�����sharedObjectsStruct.integers�У��ͽ��ö����refcount��һ���ﵽ�������Ŀ�ġ�

## 0x08 ��תʱ��
��ǰkeyû�б�������ʱ��(��ǰʱ�� - redisObject.lru)����key���г�object idletime�κβ������ᴥ������lruֵ�����ֻ���ڷ�����������maxmemoryѡ��Ż����á�
