# 同步

## 0x01 复制实现
1、master设置监听端口。<br/>
2、master和slave建立socket连接。<br/>
3、slave向master发送个ping。<br/>
4、身份验证。<br/>
5、slave向master发送端口号。<br/>
6、同步。<br/>
7、命令传播。<br/>

## 0x02 旧版复制
同步：将slave(从服务器)的状态更新至master(主服务器)的当前状态。<br/>
命令传播：master的状态发生改变的时候，传播命令让master和slave的状态达到一致。

### 同步
1、slave向master发送sync。<br/>
2、master执行bgsave，使用一个缓冲区存储现在开始的写命令。<br/>
3、bgsave执行完毕，向slave发送rdb文件，slave加载该rdb文件。<br/>
4、master将缓冲区中的命令发送给slave，slave执行，从而达到最终一致性。<br/>

### 命令传播
1、master执行命令。<br/>
2、master向slave同步该命令。<br/>
3、slave每秒向master发送replconf ack 复制偏移量。<br/>
4、slave执行该命令。<br/>

### 缺点
slave断线重连后，master需要执行一次bgsave，发送给slave载入，效率很低。优化的方向主要是断线重连后，master只将在这段时间内发生变化的数据同步给slave，这样效率会好很多。

## 0x03 新版复制
初次复制同旧版复制。<br/>
断线复制采用部分重同步+完整重同步。<br/>

### 部分重同步
　　master和slave都创建个复制偏移量，master创建个复制积压缓冲区。（类似TCP的滑动窗口，如果client没确认该序号，那该序号代表的字节一直存在server的滑动窗口中）<br/>
　　但是缓冲区大小是有限的，当达到上限的时候会释放掉最早进入缓冲区的命令。如果此时slave断线重连，发送自己的复制偏移量过来，master发现复制缓冲区内找不到数据，就会触发一次完整重同步。<br/>
### 过程

<div>
	<table border="0">
	  <tr>
	    <th>master</th>
	    <th>slave</th>
	  </tr>
	  <tr>
	    <td></td>
	    <td>PSYNC ? -1</td>
	  </tr>
	  <tr>
	    <td>+FULLRESYNC runid offset</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td>BGSAVE</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td>发送rdb文件</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td></td>
	    <td>载入rdb文件</td>
	  </tr>
	  <tr>
	    <td>...</td>
	    <td>...</td>
	  </tr>
	  <tr>
	    <td></td>
	    <td>PSYNC runid offset</td>
	  </tr>
	  <tr>
	    <td>+CONTINUE</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td>发送缓冲区命令</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td></td>
	    <td>执行命令</td>
	  </tr>
	</table>
</div>
