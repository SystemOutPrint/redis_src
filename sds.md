# redis�ַ���

# 0x01 ����
```c
struct sdshdr {
    unsigned int len; 	// ��ʹ�õ��ֽڳ��� + 1(���ڱ���\0) = buf.length - free
    unsigned int free;  // ���г���
    char buf[];			// �ֽ�����
};
```

# 0x02 �����ڴ��ط���
�ռ�Ԥ���䣺
newLen = newLen < 1M ? 2*oldLen + 1 : oldLen + 1M + 1
���Կռ��ͷ�:
��ȡ�ַ�����ʱ�򣬲�û�����·���buf�����ǽ�������ֽڴ���free�У��ȴ��´η��䡣