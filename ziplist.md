# 压缩列表

## 0x01 定义

ziplist就是一个数组。<br/>
![github-03.png](/images/ziplist_01.png "github-03.png")<br/>
zlbytes代表数组总长度<br/>
zltail代表尾节点距离ziplist数组的起始地址有多少字节<br/>
zllen代表数组内的节点数量<br/>
zlend代表数组的末端0xEF<br/>

## 0x02 entry定义

![github-04.png](/images/ziplist_02.png "github-04.png")<br/>
previous_entry_content：前一个entry的长度<br/>
encoding：当前entry的编码<br/>
content：内容<br/>

## 0x03 previous_entry_content

上一个entry的长度，如果长度小于254字节，使用一个字节来表示。<br/>
如果长度大于等于254个字节，使用5个字节来表示。

## 0x04 encoding

分为int4、int8、int16、int24、int32、int64、字节数组。<br/>
int4： 0x1111xxxx<br/>
int8： 0x11111110 xxxxxxxx<br/>
int16：0x11000000 xxxxxxxx xxxxxxxx<br/>
int24：0x11100000 xxxxxxxx xxxxxxxx xxxxxxxx<br/>
int32：0x11010000 xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx<br/>
int64: 0x11xxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx...<br/>
长度小于等于63字节的字节数组：00xxxxxx<br/>
长度小于等于16383字节的字节数组：01xxxxxx xxxxxxxx<br/>
长度小于2^38字节的字节数组:10xxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx<br/>

## 0x05 连锁更新

如果前一个entry节点的长度变化导致后一个节点的previous_entry_content的长度变化引起的contents扩容，称为连锁更新。最糟糕的情况，修改第一个元素的长度，导致所有的节点都扩容，然后再删除该节点，导致所有元素都缩容。<br/>


## 0x06 效率
添加、删除、查找：O(N)