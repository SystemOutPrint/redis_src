# ͬ��

## 0x01 ����ʵ��
1��master���ü����˿ڡ�<br/>
2��master��slave����socket���ӡ�<br/>
3��slave��master���͸�ping��<br/>
4�������֤��<br/>
5��slave��master���Ͷ˿ںš�<br/>
6��ͬ����<br/>
7���������<br/>

## 0x02 �ɰ渴��
ͬ������slave(�ӷ�����)��״̬������master(��������)�ĵ�ǰ״̬��<br/>
�������master��״̬�����ı��ʱ�򣬴���������master��slave��״̬�ﵽһ�¡�

### ͬ��
1��slave��master����sync��<br/>
2��masterִ��bgsave��ʹ��һ���������洢���ڿ�ʼ��д���<br/>
3��bgsaveִ����ϣ���slave����rdb�ļ���slave���ظ�rdb�ļ���<br/>
4��master���������е�����͸�slave��slaveִ�У��Ӷ��ﵽ����һ���ԡ�<br/>

### �����
1��masterִ�����<br/>
2��master��slaveͬ�������<br/>
3��slaveÿ����master����replconf ack ����ƫ������<br/>
4��slaveִ�и����<br/>

### ȱ��
slave����������master��Ҫִ��һ��bgsave�����͸�slave���룬Ч�ʺܵ͡��Ż��ķ�����Ҫ�Ƕ���������masterֻ�������ʱ���ڷ����仯������ͬ����slave������Ч�ʻ�úܶࡣ

## 0x03 �°渴��
���θ���ͬ�ɰ渴�ơ�<br/>
���߸��Ʋ��ò�����ͬ��+������ͬ����<br/>

### ������ͬ��
����master��slave������������ƫ������master���������ƻ�ѹ��������������TCP�Ļ������ڣ����clientûȷ�ϸ���ţ��Ǹ���Ŵ�����ֽ�һֱ����server�Ļ��������У�<br/>
�������ǻ�������С�����޵ģ����ﵽ���޵�ʱ����ͷŵ�������뻺��������������ʱslave���������������Լ��ĸ���ƫ����������master���ָ��ƻ��������Ҳ������ݣ��ͻᴥ��һ��������ͬ����<br/>
### ����

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
	    <td>����rdb�ļ�</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td></td>
	    <td>����rdb�ļ�</td>
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
	    <td>���ͻ���������</td>
	    <td></td>
	  </tr>
	  <tr>
	    <td></td>
	    <td>ִ������</td>
	  </tr>
	</table>
</div>
