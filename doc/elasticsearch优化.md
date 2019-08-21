## es优化

系统主存在es上,对于es集群的优化,备份方面给下下面建议

### 一. ulimit设置
* 临时处理 
    > ulimit -n 65536
* 永久设置  
    >/etc/security/limits.conf \
    es  -  nofile  65536 \
    es soft memlock unlimited \
    es hard memlock unlimited

### 二.内存
* 1,es特别耗内存,防止内存被其他程序占用,直接设置堆内存的最大和最小值为一样大小,
最大内存可设置31G,直接使用heap中的最大和最小的内存大小-Xms31g -Xmx31g

* 2,禁用交换文件可提交es性能.
    + 临时设置  swapoff -a
    + 永久设置  
        >1./etc/fstab 注释掉任何包含 swap的内容 \
         2./etc/sysctl.conf \
         设置vm.swappiness=1 \
         添加  vm.max_map_count=655360 \
         3.生效 sysctl -p

* 3,设置内存锁定
    > elasticsearch.yml \
      bootstrap.memory_lock: true 


### 三. data,log存储路径设置
* 数据存储可设置多个目录: 
    > path.data: /mnt/data/data1,/mnt/data/data2 \
    path.logs: /mnt/data/logs

另外:es 的data文件可以直接转移到其他位置,只需要copy文件即可

### 详细步骤汇总

* 1,用户的ulimit设置
    >  /etc/security/limits.conf \
      es  -  nofile  65536 \
      es soft memlock unlimited \
      es hard memlock unlimited 
      
      
* 2,禁用文件交换
   > /etc/sysctl.conf \
   设置 vm.swappiness=1 \
   生效 sysctl -p \
   查看是否生效: sysctl vm.swappiness
   
* 3,设置内存锁定
    > elasticsearch.yml \
      bootstrap.memory_lock: true 

  
      
