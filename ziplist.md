# ѹ���б�

## 0x01 ����

ziplist����һ�����顣<br/>
![github-03.png](/images/ziplist_01.png "github-03.png")<br/>
zlbytes���������ܳ���<br/>
zltail����β�ڵ����ziplist�������ʼ��ַ�ж����ֽ�<br/>
zllen���������ڵĽڵ�����<br/>
zlend���������ĩ��0xEF<br/>

## 0x02 entry����

![github-04.png](/images/ziplist_02.png "github-04.png")<br/>
previous_entry_content��ǰһ��entry�ĳ���<br/>
encoding����ǰentry�ı���<br/>
content������<br/>

## 0x03 previous_entry_content

��һ��entry�ĳ��ȣ��������С��254�ֽڣ�ʹ��һ���ֽ�����ʾ��<br/>
������ȴ��ڵ���254���ֽڣ�ʹ��5���ֽ�����ʾ��

## 0x04 encoding

��Ϊint4��int8��int16��int24��int32��int64���ֽ����顣<br/>
int4�� 0x1111xxxx<br/>
int8�� 0x11111110 xxxxxxxx<br/>
int16��0x11000000 xxxxxxxx xxxxxxxx<br/>
int24��0x11100000 xxxxxxxx xxxxxxxx xxxxxxxx<br/>
int32��0x11010000 xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx<br/>
int64: 0x11xxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx...<br/>
����С�ڵ���63�ֽڵ��ֽ����飺00xxxxxx<br/>
����С�ڵ���16383�ֽڵ��ֽ����飺01xxxxxx xxxxxxxx<br/>
����С��2^38�ֽڵ��ֽ�����:10xxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx<br/>

## 0x05 ��������

���ǰһ��entry�ڵ�ĳ��ȱ仯���º�һ���ڵ��previous_entry_content�ĳ��ȱ仯�����contents���ݣ���Ϊ�������¡�������������޸ĵ�һ��Ԫ�صĳ��ȣ��������еĽڵ㶼���ݣ�Ȼ����ɾ���ýڵ㣬��������Ԫ�ض����ݡ�<br/>


## 0x06 Ч��
��ӡ�ɾ�������ң�O(N)