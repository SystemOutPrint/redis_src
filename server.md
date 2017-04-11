# ������

## 0x01 redisCommand����
```c
struct redisCommand {
    char *name;
    redisCommandProc *proc;
    int arity;
    char *sflags; /* Flags as string representation, one char per flag. */
    int flags;    /* The actual flags, obtained from the 'sflags' field. */
    /* Use a function to determine keys arguments in a command line.
     * Used for Redis Cluster redirect. */
    redisGetKeysProc *getkeys_proc;
    /* What keys should be loaded in background when calling this command? */
    int firstkey; /* The first argument that's a key (0 = no keys) */
    int lastkey;  /* The last argument that's a key */
    int keystep;  /* The step between first and last key */
    long long microseconds, calls;
};
```

## 0x02 ����ִ�й���
### �ͻ��ˣ�
1���ͻ��˷�����������ȴ��ڿͻ��˵����뻺������<br/>
2���ӻ������ڽ���������ɲ������������ݷŵ��ͻ��˵�argc��argv��<br/>
3������������ִ������ִ����Ӧ�����<br/>

### ������
1������ִ��֮ǰ���ж�cmdָ���Ƿ�Ϊ��<br/>
2���ж���������Ƿ�����<br/>
3�����������֤<br/>
4���������maxmemory���ж��ڴ�ռ�á�<br/>
5������ϴ�bgsaveʧ�ܺ󣬷�������stop-writes-on-bgsave-error���ܣ�����ִ��д������<br/>
6����ǰ�Ŀͻ���������ڶ���״̬�£����˶��ĺͽ�����������⣬��������޷�ִ�С�<br/>
7��������ڽ����������룬�޷�ִ��û��lȨ�޵����<br/>
8�����������ִ��lua�ű���ʱ������ʱ�򣬳���shutdown nosave��script kill����������޷�ִ�С�<br/>
9������ͻ�������ִ�����񣬳���exec��discard��multi��watch�����������������������С�<br/>
10�����������������moniter���ܣ��������Ὣ���ִ�е�����͵���������<br/>
11�����ݿͻ��˵Ĳ���ִ��cmd��proc������<br/>
12��������ظ����浽�ͻ��˵�buf�ڡ�<br/>
13���жϵ�ǰ�����Ƿ���Ҫ��������ѯ��־��<br/>
14������redisCommand��microseconds��calls��<br/>
15���������aof����������׷��aof�������С�<br/>
16����������㲥���ӷ�������<br/>

## 0x03 serverCron
```c
#define run_with_period(_ms_) if ((_ms_ <= 1000/server.hz) || !(server.cronloops%((_ms_)/(1000/server.hz))
```
�ú궨���α����������
```java
if (ms <= serverCron��ִ�м��) {
	run();
} else if (serverCronִ�д���%(ms/serverCron��ִ�м��) == 0) {
	run();
}
```

<br/>serverCron����ʵ�����£�
```c
int serverCron(struct aeEventLoop *eventLoop, long long id, void *clientData) {
    int j;
    REDIS_NOTUSED(eventLoop);
    REDIS_NOTUSED(id);
    REDIS_NOTUSED(clientData);

    /* Software watchdog: deliver the SIGALRM that will reach the signal
     * handler if we don't return here fast enough. */
    if (server.watchdog_period) watchdogScheduleSignal(server.watchdog_period);

    /* ����time���� */
    updateCachedTime();

	/* ÿ100�������һ��ͳ������ */
    run_with_period(100) {
        trackInstantaneousMetric(REDIS_METRIC_COMMAND,server.stat_numcommands);
        trackInstantaneousMetric(REDIS_METRIC_NET_INPUT,
                server.stat_net_input_bytes);
        trackInstantaneousMetric(REDIS_METRIC_NET_OUTPUT,
                server.stat_net_output_bytes);
    }

    /* We have just REDIS_LRU_BITS bits per object for LRU information.
     * So we use an (eventually wrapping) LRU clock.
     *
     * Note that even if the counter wraps it's not a big problem,
     * everything will still work but some object will appear younger
     * to Redis. However for this to happen a given object should never be
     * touched for all the time needed to the counter to wrap, which is
     * not likely.
     *
	 * ����lruclock����λ���롣
	 *
     * Note that you can change the resolution altering the
     * REDIS_LRU_CLOCK_RESOLUTION define. */
    server.lruclock = getLRUClock();

    /* ���·����������������ڴ��ֵ */
    if (zmalloc_used_memory() > server.stat_peak_memory)
        server.stat_peak_memory = zmalloc_used_memory();

    /* Sample the RSS here since this is a relatively slow call. */
    server.resident_set_size = zmalloc_get_rss();

    /* �ӵ���SIGTERM��׼�����ŵ�ͣ�� */
    if (server.shutdown_asap) {
        if (prepareForShutdown(0) == REDIS_OK) exit(0);
        redisLog(REDIS_WARNING,"SIGTERM received but errors trying to shut down the server, check the logs for more information");
        server.shutdown_asap = 0;
	}

    /* ÿ��5���ӡ�·ǿյ�db��Ϣ */
    run_with_period(5000) {
        for (j = 0; j < server.dbnum; j++) {
            long long size, used, vkeys;

            size = dictSlots(server.db[j].dict);
            used = dictSize(server.db[j].dict);
            vkeys = dictSize(server.db[j].expires);
            if (used || vkeys) {
                redisLog(REDIS_VERBOSE,"DB %d: %lld keys (%lld volatile) in %lld slots HT.",j,used,vkeys,size);
                /* dictPrintStats(server.dict); */
            }
        }
    }

    /* ��������ڱ���ÿ5���ӡ��client��Ϣ */
    if (!server.sentinel_mode) {
        run_with_period(5000) {
            redisLog(REDIS_VERBOSE,
                "%lu clients connected (%lu slaves), %zu bytes in use",
                listLength(server.clients)-listLength(server.slaves),
                listLength(server.slaves),
                zmalloc_used_memory());
        }
    }

    /* ����һЩ��ʱ�ͻ��˻��ߵ����»�������С */
    clientsCron();

    /* �����¹��ڼ�����db��resize��rehash���� */
    databasesCron();

    /* �����ǰӦ�ý���aof��д���ҵ�ǰû�����ڽ����е�aof��д����rdb��save�������ͽ���һ��aof��д */
    if (server.rdb_child_pid == -1 && server.aof_child_pid == -1 &&
        server.aof_rewrite_scheduled)
    {
        rewriteAppendOnlyFileBackground();
    }

    /* �����bgsave����bgrewriteaof�����Ƿ���� */
    if (server.rdb_child_pid != -1 || server.aof_child_pid != -1) {
        int statloc;
        pid_t pid;

        if ((pid = wait3(&statloc,WNOHANG,NULL)) != 0) {
            int exitcode = WEXITSTATUS(statloc);
            int bysignal = 0;

            if (WIFSIGNALED(statloc)) bysignal = WTERMSIG(statloc);

            if (pid == server.rdb_child_pid) {
                backgroundSaveDoneHandler(exitcode,bysignal);
            } else if (pid == server.aof_child_pid) {
                backgroundRewriteDoneHandler(exitcode,bysignal);
            } else {
                redisLog(REDIS_WARNING,
                    "Warning, detected child with unmatched pid: %ld",
                    (long)pid);
            }
            updateDictResizePolicy();
        }
    } else {
        /* ���û�����ڽ����е�bgrewriteaof��bgsave�����Ƿ���Խ���һ��bgsave������ */
         for (j = 0; j < server.saveparamslen; j++) {
            struct saveparam *sp = server.saveparams+j;

            /* Save if we reached the given amount of changes,
             * the given amount of seconds, and if the latest bgsave was
             * successful or if, in case of an error, at least
             * REDIS_BGSAVE_RETRY_DELAY seconds already elapsed. */
            if (server.dirty >= sp->changes &&
                server.unixtime-server.lastsave > sp->seconds &&
                (server.unixtime-server.lastbgsave_try >
                 REDIS_BGSAVE_RETRY_DELAY ||
                 server.lastbgsave_status == REDIS_OK))
            {
                redisLog(REDIS_NOTICE,"%d changes in %d seconds. Saving...",
                    sp->changes, (int)sp->seconds);
                rdbSaveBackground(server.rdb_filename);
                break;
            }
         }

         /* ����Ƿ���Խ���һ��bgrewriteaof */
         if (server.rdb_child_pid == -1 &&
             server.aof_child_pid == -1 &&
             server.aof_rewrite_perc &&
             server.aof_current_size > server.aof_rewrite_min_size)
         {
		             long long base = server.aof_rewrite_base_size ?
                            server.aof_rewrite_base_size : 1;
            long long growth = (server.aof_current_size*100/base) - 100;
            if (growth >= server.aof_rewrite_perc) {
                redisLog(REDIS_NOTICE,"Starting automatic rewriting of AOF on %lld%% growth",growth);
                rewriteAppendOnlyFileBackground();
            }
         }
    }


    /* ����Ƿ����Ƴٵ�aof fsync���������ִ�С�*/
    if (server.aof_flush_postponed_start) flushAppendOnlyFile(0);

    /* AOF write errors: in this case we have a buffer to flush as well and
     * clear the AOF error in case of success to make the DB writable again,
     * however to try every second is enough in case of 'hz' is set to
     * an higher frequency. */
    run_with_period(1000) {
        if (server.aof_last_write_status == REDIS_ERR)
            flushAppendOnlyFile(0);
    }

    /* �첽�Ĺر���Щ��Ҫ�رյĿͻ��� */
    freeClientsInAsyncFreeQueue();

    /* Clear the paused clients flag if needed. */
    clientsArePaused(); /* Don't check return value, just use the side effect. */

    /* ���ƶ�ʱ���� */
    run_with_period(1000) replicationCron();

    /* ���м�Ⱥ��ʱ���� */
    run_with_period(100) {
        if (server.cluster_enabled) clusterCron();
    }

    /* �����ڱ���ʱ�� */
    run_with_period(100) {
        if (server.sentinel_mode) sentinelTimer();
    }

    /* ������ڵ�Ǩ��socket */
    run_with_period(1000) {
	      migrateCloseTimedoutSockets();
    }

    server.cronloops++;
    return 1000/server.hz;
}
```

## 0x04 ��ʼ��������
```c
void initServer(void) {
    int j;

    signal(SIGHUP, SIG_IGN);
    signal(SIGPIPE, SIG_IGN);
	// ��ʼ���ź�handler
    setupSignalHandlers(); 

    if (server.syslog_enabled) {
        openlog(server.syslog_ident, LOG_PID | LOG_NDELAY | LOG_NOWAIT,
            server.syslog_facility);
    }

    server.pid = getpid();
    server.current_client = NULL;
    server.clients = listCreate();
    server.clients_to_close = listCreate();
    server.slaves = listCreate();
    server.monitors = listCreate();
    server.slaveseldb = -1; /* Force to emit the first SELECT command. */
    server.unblocked_clients = listCreate();
    server.ready_keys = listCreate();
    server.clients_waiting_acks = listCreate();
    server.get_ack_from_slaves = 0;
    server.clients_paused = 0;

	// ����������󣬰���0~9999��interger������ַ���
    createSharedObjects();
    adjustOpenFilesLimit();
    server.el = aeCreateEventLoop(server.maxclients+REDIS_EVENTLOOP_FDSET_INCR);
    server.db = zmalloc(sizeof(redisDb)*server.dbnum);

    /* �����Զ˿ڵļ��� */
	if (server.port != 0 &&
        listenToPort(server.port,server.ipfd,&server.ipfd_count) == REDIS_ERR)
        exit(1);

    /* ������unixsocket�ļ��� */
    if (server.unixsocket != NULL) {
        unlink(server.unixsocket); /* don't care if this fails */
        server.sofd = anetUnixServer(server.neterr,server.unixsocket,
            server.unixsocketperm, server.tcp_backlog);
        if (server.sofd == ANET_ERR) {
            redisLog(REDIS_WARNING, "Opening Unix socket: %s", server.neterr);
            exit(1);
        }
        anetNonBlock(NULL,server.sofd);
    }

    /* Abort if there are no listening sockets at all. */
    if (server.ipfd_count == 0 && server.sofd < 0) {
        redisLog(REDIS_WARNING, "Configured to not listen anywhere, exiting.");
        exit(1);
    }

    /* �������ݿ⣬Ȼ����г�ʼ�� */
    for (j = 0; j < server.dbnum; j++) {
        server.db[j].dict = dictCreate(&dbDictType,NULL);
        server.db[j].expires = dictCreate(&keyptrDictType,NULL);
        server.db[j].blocking_keys = dictCreate(&keylistDictType,NULL);
        server.db[j].ready_keys = dictCreate(&setDictType,NULL);
        server.db[j].watched_keys = dictCreate(&keylistDictType,NULL);
        server.db[j].eviction_pool = evictionPoolAlloc();
        server.db[j].id = j;
        server.db[j].avg_ttl = 0;
    }
    server.pubsub_channels = dictCreate(&keylistDictType,NULL);
    server.pubsub_patterns = listCreate();
    listSetFreeMethod(server.pubsub_patterns,freePubsubPattern);
    listSetMatchMethod(server.pubsub_patterns,listMatchPubsubPattern);
    server.cronloops = 0;
    server.rdb_child_pid = -1;
    server.aof_child_pid = -1;
    server.rdb_child_type = REDIS_RDB_CHILD_TYPE_NONE;
    aofRewriteBufferReset();
    server.aof_buf = sdsempty();
    server.lastsave = time(NULL); /* At startup we consider the DB saved. */
    server.lastbgsave_try = 0;    /* At startup we never tried to BGSAVE. */
	server.rdb_save_time_last = -1;
    server.rdb_save_time_start = -1;
    server.dirty = 0;
    resetServerStats();
    /* A few stats we don't want to reset: server startup time, and peak mem. */
    server.stat_starttime = time(NULL);
    server.stat_peak_memory = 0;
    server.resident_set_size = 0;
    server.lastbgsave_status = REDIS_OK;
    server.aof_last_write_status = REDIS_OK;
    server.aof_last_write_errno = 0;
    server.repl_good_slaves_count = 0;
    updateCachedTime();

    /* Create the serverCron() time event, that's our main way to process
     * background operations. */
    if(aeCreateTimeEvent(server.el, 1, serverCron, NULL, NULL) == AE_ERR) {
        redisPanic("Can't create the serverCron time event.");
        exit(1);
    }

    /* Create an event handler for accepting new connections in TCP and Unix
     * domain sockets. */
    for (j = 0; j < server.ipfd_count; j++) {
        if (aeCreateFileEvent(server.el, server.ipfd[j], AE_READABLE,
            acceptTcpHandler,NULL) == AE_ERR)
            {
                redisPanic(
                    "Unrecoverable error creating server.ipfd file event.");
            }
    }
    if (server.sofd > 0 && aeCreateFileEvent(server.el,server.sofd,AE_READABLE,
        acceptUnixHandler,NULL) == AE_ERR) redisPanic("Unrecoverable error creating server.sofd file event.");

    /* Open the AOF file if needed. */
    if (server.aof_state == REDIS_AOF_ON) {
        server.aof_fd = open(server.aof_filename,
                               O_WRONLY|O_APPEND|O_CREAT,0644);
        if (server.aof_fd == -1) {
            redisLog(REDIS_WARNING, "Can't open the append-only file: %s",
                strerror(errno));
            exit(1);
        }
    }

    /* 32 bit instances are limited to 4GB of address space, so if there is
	 * no explicit limit in the user provided configuration we set a limit
     * at 3 GB using maxmemory with 'noeviction' policy'. This avoids
     * useless crashes of the Redis instance for out of memory. */
    if (server.arch_bits == 32 && server.maxmemory == 0) {
        redisLog(REDIS_WARNING,"Warning: 32 bit instance detected but no memory limit set. Setting 3 GB maxmemory limit with 'noeviction' policy now.");
        server.maxmemory = 3072LL*(1024*1024); /* 3 GB */
        server.maxmemory_policy = REDIS_MAXMEMORY_NO_EVICTION;
    }

    if (server.cluster_enabled) clusterInit();
    replicationScriptCacheInit();
    scriptingInit();
    slowlogInit();
    latencyMonitorInit();
    bioInit();
}
```
