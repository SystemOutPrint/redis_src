# ��������

## 0x01 ����
```c
typedef struct intset {
    uint32_t encoding; // ���뷽ʽ
    uint32_t length; // Ԫ������
    int8_t contents[]; // ����Ԫ�ص�����
} intset;
```

## 0x02 ���뷽ʽ
int16_t��int32_t��int_64_t


## 0x03 ����
����int16_t�������ڷ���int32_t�����֣�Ҳ����ζ�ű��뷽ʽ��int16_t��Ϊint32_t����ʱ��Ҫ��contents����Ϊԭ���鳤�ȵ�2����Ȼ��ԭ�����Ԫ�ض�����Ϊint32λ��

## 0x04 ����
��֧��

## 0x05 Ч��
sadd��srem��O(N)<br/>
scard��O(1)