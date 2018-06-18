import pdb
import os
import sys

import matplotlib.pyplot as plt
import numpy as np
import matplotlib



def collect_time_frequency(point, keyword, lst):
    if keyword in point:
        timestamp = int(point.split()[0])
        slot_index = int((timestamp - min_timestamp) / time_slot_size)
        lst[slot_index] = lst[slot_index] + 1
        
def collect_unique_threads(point, lst):
        timestamp = int(point.split()[0])
        thread_id = int(point.split()[1])
        slot_index = (int) ((timestamp - min_timestamp) / time_slot_size)
        lst[slot_index].add(thread_id)
##        print("here!!!!!")
##        for item in lst:
##            print(thread_id)

def collect_type_hist(point, keyword, lst):
    try:
       if keyword in point:
        duration_index = point.split().index("[dur]") + 1
        duration = int(point.split()[duration_index])
        lst.append(duration/1000000) 
    except ValueError:
        pdb.set_trace()
        
def collect_type_hist_for_gets(point, lst):
    try:
       if point.endswith("get\n"):
        duration_index = point.split().index("[dur]") + 1
        duration = int(point.split()[duration_index])
        lst.append(duration/1000000) 
    except ValueError:
        pdb.set_trace()


unique_thread_names = set()

def collect_unique_thread_names(points):
    for point in points:
        unique_thread_names.add(point.split()[2])

#trace_dir = sys.argv[1];
points = []


#for filename in os.listdir(trace_dir):
    #path = os.path.join(trace_dir, filename);
    #with open(path) as f:
        #for line in f:
            #points.append(line)

path = "/Users/vshriya/Desktop/current/CorfuDB/test/client-1528233734301.log"
with open(path) as f:
    for line in f:
        points.append(line)


points.sort(key=lambda x: int(x.split()[0]))


min_timestamp = (int)(points[0].split()[0])
max_timestamp = (int)(points[-1].split()[0])

# time slot size in nanoseconds
#time_slot_size = 1000000000
time_slot_size = (int) (1000000)



# collect unique thread names
collect_unique_thread_names(points)


# keyword lists

sequencer_calls = [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)
nonTxReads_calls = [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)
committed_tx =  [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)
num_threads = [None] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)
for x in range(len(num_threads)):
    num_threads[x] = set()

access_ntx = [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)
access_tx = [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)
tx_begin = [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)

routeCall_list = [0] * ((int)((max_timestamp - min_timestamp) / time_slot_size) + 1)

xs = np.arange(0, len(sequencer_calls), 1)

gets_list = []
puts_list = []
remove_list = []
keySet_list = []
containsKey_hist = []
entrySet_hist = []
getByIndex_hist = []
scanAndFilter_hist = []
sequencer_calls_hist = []
cahce_miss_hist = []
sequencer_calls_hist2 = []
accessLock_hist = []
accessLockOp_hist = []

'''
thread1 = [0] * (((max_timestamp - min_timestamp) / time_slot_size) + 1)
thread2 = [0] * (((max_timestamp - min_timestamp) / time_slot_size) + 1)
thread3 = [0] * (((max_timestamp - min_timestamp) / time_slot_size) + 1)
thread4 = [0] * (((max_timestamp - min_timestamp) / time_slot_size) + 1)
thread5 = [0] * (((max_timestamp - min_timestamp) / time_slot_size) + 1)
thread6 = [0] * (((max_timestamp - min_timestamp) / time_slot_size) + 1)
'''

for point in points:
    
    collect_time_frequency(point, "Seq", sequencer_calls)
    #collect_time_frequency(point, "routeCall", routeCall_list)
    collect_time_frequency(point, "access(nonTx)", nonTxReads_calls)
    
    '''
    collect_time_frequency(point, "http-nio-127.0.0.1-7440-exec-6", thread1)
    collect_time_frequency(point, "http-nio-127.0.0.1-7440-exec-7", thread2)
    collect_time_frequency(point, "http-nio-127.0.0.1-7440-exec-5", thread3)
    collect_time_frequency(point, "http-nio-127.0.0.1-7440-exec-10", thread4)
    collect_time_frequency(point, "http-nio-127.0.0.1-7440-exec-1", thread5)
    collect_time_frequency(point, "http-nio-127.0.0.1-7440-exec-8", thread6)    
    '''
    
    #collect_time_frequency(point, "Seq", sequencer_calls)
    collect_time_frequency(point, "TXBegin", tx_begin)
    #collect_time_frequency(point, "getConflictSetAndCommit", committed_tx)
    collect_unique_threads(point, num_threads)
    #collect_time_frequency(point, "access(tx)", access_tx)
    #collect_time_frequency(point, "access(nonTx)", access_ntx)
    #collect_time_frequency(point, "TXBegin", tx_begin)
    
    #collect_unique_threads(point, num_threads)
    #collect_time_frequency(point, "getConflictSetAndCommit", committed_tx)
    
    #collect_type_hist(point, "accessLockOp", accessLockOp_hist)
    #collect_type_hist(point, "accessLock ", accessLock_hist)
  
    '''
    collect_type_hist(point, "getByIndex", getByIndex_hist)
    collect_type_hist(point, "scanAndFilter", scanAndFilter_hist)
    collect_type_hist(point, "containsKey", containsKey_hist)
    collect_type_hist(point, "entrySet", entrySet_hist)
    collect_type_hist(point, "keySet", keySet_list)
    collect_type_hist(point, "put", puts_list)
    collect_type_hist_for_gets(point, gets_list)
    '''
    #collect_type_hist(point, "cacheLoad", cahce_miss_hist);
    collect_type_hist(point, "Seq", sequencer_calls_hist)
    #collect_type_hist(point, "1Seq", sequencer_calls_hist2)
    

sequencer_calls = [a - b for a, b in zip(sequencer_calls, routeCall_list)]

num_threads = [len(x) for x in num_threads]

def zero_to_nan(values):
    """Replace every 0 with 'nan' and return a copy."""
    return [float('nan') if x==0 else x for x in values]

num_bins = 300

#plt.hist(accessLockOp_hist, bins=num_bins, label='accessLockOp')
#plt.hist(accessLock_hist, bins=num_bins, label='accessLock')

'''
plt.hist(gets_list, bins=num_bins, label='get')
plt.hist(containsKey_hist, bins=num_bins, label='containsKey')
plt.hist(getByIndex_hist, bins=num_bins, label='getByIndex')
plt.hist(scanAndFilter_hist, bins=num_bins, label='scanAndFilter')
plt.hist(entrySet_hist, bins=num_bins, label='entrySet')
plt.hist(keySet_list, bins=num_bins, label='keySet')
plt.hist(puts_list, bins=num_bins, label='puts')
plt.hist(sequencer_calls_hist, bins=num_bins, label='Seq')
plt.yscale('log', nonposy='clip')
'''

#plt.hist(cahce_miss_hist, bins=num_bins, label='CacheMiss')
plt.hist(sequencer_calls_hist, bins=num_bins, label='Seq2')
#plt.yscale('log', nonposy='clip')

'''
plt.scatter(xs, zero_to_nan(thread1), label="t1")
plt.scatter(xs, zero_to_nan(thread2), label="t2")
plt.scatter(xs, zero_to_nan(thread3), label="t3")
plt.scatter(xs, zero_to_nan(thread4), label="t4")
plt.scatter(xs, zero_to_nan(thread5), label="t5")
plt.scatter(xs, zero_to_nan(thread6), label="t6")
'''

#plt.scatter(xs, zero_to_nan(committed_tx), label="getConflictSetAndCommit")
#plt.scatter(xs, zero_to_nan(num_threads), label="threads")
plt.scatter(xs, zero_to_nan(sequencer_calls), label="seq")
#plt.scatter(xs, zero_to_nan(nonTxReads_calls), label="nonTxReads")

#plt.scatter(xs, zero_to_nan(sequencer_calls), label="seq")
#plt.scatter(xs, zero_to_nan(committed_tx), label="getConflictSetAndCommit")
#plt.scatter(xs, zero_to_nan(num_threads), label="threads")
#plt.scatter(xs, zero_to_nan(access_tx), label="transactional reads")
#plt.scatter(xs, zero_to_nan(access_ntx), label="non-transactional reads")
#plt.scatter(xs, zero_to_nan(tx_begin), label="TXBegin")



print "hello!"
plt.legend(loc=1)
plt.ylabel('Op per ' + str((time_slot_size / 1000000)) + ' ms')
plt.show()
pdb.set_trace()
