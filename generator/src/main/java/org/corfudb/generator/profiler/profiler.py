import matplotlib.pyplot as plt
import numpy as np

path = "/Users/vshriya/Desktop/current/CorfuDB/test/client-1529017385323.log"
output_path = "results/shriya/"
points = []
all_ops = []


### Helper Functions
def setup():
    global points
    # open file and initialize vars
    f = open(path)
    for point in f.readlines():
        points.append(point)
        if get_event_name(point) not in all_ops:
            all_ops.append(get_event_name(point))

    # sort points by start time
    points.sort(key=lambda x: get_time_stamp(x))

def get_time_stamp(entry):
    return int(entry.split()[0])


def get_thread_ID(entry):
    return int(entry.split()[1])


def get_event_name(entry):
    return entry.split()[3].split()[0]


def is_event(entry, event):
    return entry.split()[3].split()[0] == event


def nano_to_milli(time):
    return time / 1000000


### Analysis
def count_active_threads():
    '''
    How many threads are active per millisecond?
    '''
    print 1
    counts = {}  # ms interval --> set of active thread IDs
    for point in points:
        if int(nano_to_milli(get_time_stamp(point))) in counts:
            counts[int(nano_to_milli(get_time_stamp(point)))].add(get_thread_ID(point))
        else:
            counts[int(nano_to_milli(get_time_stamp(point)))] = set([get_thread_ID(point)])

    # create scatter plot
    x = []
    y = []
    for key in counts.keys():
        x += [key - min(counts.keys())]
        y += [len(counts[key])]

    plt.scatter(x, y)
    plt.title("# of Active Threads vs. Time Elapsed")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("Number of Unique Active Threads")
    plt.savefig(output_path + "count_active_threads.png", bbox_inches='tight')
    plt.clf()


def count_seq_calls():
    '''
    How many sequencer calls are made per millisecond?
    '''
    print 3
    counts = {}  # ms interval --> counts of seq calls
    for point in points:
        if get_event_name(point) == "Seq":
            if int(nano_to_milli(get_time_stamp(point))) in counts:
                counts[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts[int(nano_to_milli(get_time_stamp(point)))] = 1

    # create scatter plot
    x = []
    y = []
    for key in counts.keys():
        x += [key - min(counts.keys())]
        y += [counts[key]]

    plt.scatter(x, y)
    plt.title("# of Seq Calls vs. Time Elapsed")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("Number of Seq Calls")
    plt.savefig(output_path + "count_seq_calls.png", bbox_inches='tight')
    plt.clf()


def count_stepping():
    print 4
    counts = {}  # ms interval --> count of followBackpointers
    for point in points:
        if get_event_name(point) == "followBackpointers":
            if int(nano_to_milli(get_time_stamp(point))) in counts:
                counts[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts[int(nano_to_milli(get_time_stamp(point)))] = 1

    # create scatter plot
    x = []
    y = []
    for key in counts.keys():
        x += [key - min(counts.keys())]
        y += [counts[key]]

    plt.scatter(x, y)
    plt.title("# of followBackpointers vs. Time Elapsed")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("Number of followBackpointers")
    plt.savefig(output_path + "count_stepping.png", bbox_inches='tight')
    plt.clf()


def count_unsafe():
    print 5
    counts1 = {}  # ms interval --> count of rollbackObjectUnsafe
    for point in points:
        if get_event_name(point) == "rollbackObjectUnsafe":
            if int(nano_to_milli(get_time_stamp(point))) in counts1:
                counts1[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts1[int(nano_to_milli(get_time_stamp(point)))] = 1

    counts2 = {}  # ms interval --> count of syncObjectUnsafe
    for point in points:
        if get_event_name(point) == "syncObjectUnsafe":
            if int(nano_to_milli(get_time_stamp(point))) in counts2:
                counts2[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts2[int(nano_to_milli(get_time_stamp(point)))] = 1

    # create juxtaposed scatter plot
    x1 = []
    y1 = []
    for key in counts1.keys():
        x1 += [key - min(counts1.keys())]
        y1 += [counts1[key]]
    plt.scatter(x1, y1, s=3, color='green', alpha=0.5, label="rollbackObjUnsafe")

    x2 = []
    y2 = []
    for key in counts2.keys():
        x2 += [key - min(counts2.keys())]
        y2 += [counts2[key]]
    plt.scatter(x2, y2, s=3, alpha=0.5, label="syncObjUnsafe")

    plt.title("Comparison")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("#")
    plt.legend(loc=1)
    plt.savefig(output_path + "count_unsafe.png", bbox_inches='tight')
    plt.clf()


def count_ops_per_tx():
    print 6
    counts = {}  # thread num --> count of ops
    num_ops = []
    i = 0
    for point in points:
        if get_thread_ID(point) in counts and get_event_name(point) != "TXBegin" and get_event_name(point) != "TXEnd":
            counts[get_thread_ID(point)] += 1
        elif get_event_name(point) == "TXBegin":
            counts[get_thread_ID(point)] = 0
        elif get_event_name(point) == "TXEnd":
            i += 1
            num_ops += [counts[get_thread_ID(point)]]
            counts[get_thread_ID(point)] = 0

    print i
    # create 1-D scatter plot
    y = num_ops
    x = [0 for _ in y]
    plt.scatter(x, y, alpha=0.3)
    plt.title("Num Ops per Transaction")
    plt.ylabel("Num Ops")
    plt.savefig(output_path + "count_ops_per_tx_scatter.png", bbox_inches='tight')
    plt.clf()

    # create bar chart
    num_num_ops = {} # number --> number of times that number appears
    for n in num_ops:
        if n not in num_num_ops:
            num_num_ops[n] = 1
        else:
            num_num_ops[n] += 1

    y = num_num_ops.values()
    x = num_num_ops.keys()
    width = 1 / 1.5
    plt.bar(x, y, width, color="blue")
    plt.title("Num Ops per Transaction")
    plt.xlabel("Num Ops")
    plt.ylabel("Num Txs")
    plt.savefig(output_path + "count_ops_per_tx_bar.png", bbox_inches='tight')
    plt.clf()


def count_table_ops_per_tx():
    print 7
    counts_access = {} # thread num --> count of access ops
    counts_mutate = {} # thread num --> count of mutate ops
    num_access_ops = []
    num_mutate_ops = []
    for point in points:
        if get_event_name(point) == "TXBegin":
            counts_access[get_thread_ID(point)] = 0
            counts_mutate[get_thread_ID(point)] = 0
        elif get_event_name(point) == "TXEnd":
            num_access_ops += [counts_access[get_thread_ID(point)]]
            num_mutate_ops += [counts_mutate[get_thread_ID(point)]]
            counts_access[get_thread_ID(point)] = 0
            counts_mutate[get_thread_ID(point)] = 0
        elif "[method] containsKey" in point and get_thread_ID(point) in counts_access:
            counts_access[get_thread_ID(point)] += 1
        elif "[method] put" in point and get_thread_ID(point) in counts_mutate:
            counts_mutate[get_thread_ID(point)] += 1

    # create juxtaposed scatter plot
    x1 = num_access_ops
    y1 = [0 for _ in x1]
    plt.scatter(x1, y1, color='green', alpha=0.5, label="accessOps")

    x2 = num_mutate_ops
    y2 = [0 for _ in x2]
    plt.scatter(x2, y2, alpha=0.5, label="mutateOps")

    plt.title("Comparison")
    plt.xlabel("#")
    plt.legend(loc=1)
    plt.savefig(output_path + "count_table_ops_per_tx_scatter.png", bbox_inches='tight')
    plt.clf()

    # create double bar chart
    num_num_access_ops = {}  # number --> number of times that number appears
    for n in num_access_ops:
        if n not in num_num_access_ops:
            num_num_access_ops[n] = 1
        else:
            num_num_access_ops[n] += 1

    num_num_mutate_ops = {}  # number --> number of times that number appears
    for n in num_mutate_ops:
        if n not in num_num_mutate_ops:
            num_num_mutate_ops[n] = 1
        else:
            num_num_mutate_ops[n] += 1

    x = np.array(list(set(num_num_access_ops.keys()).union(set(num_num_mutate_ops.keys()))))
    y1 = []
    y2 = []
    for item in x:
        if item not in num_num_access_ops:
            num_num_access_ops[item] = 0
        if item not in num_num_mutate_ops:
            num_num_mutate_ops[item] = 0
        y1 += [num_num_access_ops[item]]
        y2 += [num_num_mutate_ops[item]]
    width = 0.27
    plt.bar(x, y1, width, color="blue", label="accessOps")
    plt.bar(x + width, y2, width, color="red", label="mutateOps")
    plt.legend(loc=1)
    plt.title("Num Ops per Transaction")
    plt.xlabel("Num Ops")
    plt.ylabel("Num Txs")
    plt.savefig(output_path + "count_table_ops_per_tx_bar.png", bbox_inches='tight')
    plt.clf()


def count_id_table_ops_per_tx(table_id):
    print 8
    counts_access = {} # thread num --> count of access ops
    counts_mutate = {} # thread num --> count of mutate ops
    num_access_ops = []
    num_mutate_ops = []
    for point in points:
        if get_event_name(point) == "TXBegin":
            counts_access[get_thread_ID(point)] = 0
            counts_mutate[get_thread_ID(point)] = 0
        elif get_event_name(point) == "TXEnd":
            num_access_ops += [counts_access[get_thread_ID(point)]]
            num_mutate_ops += [counts_mutate[get_thread_ID(point)]]
            counts_access[get_thread_ID(point)] = 0
            counts_mutate[get_thread_ID(point)] = 0
        elif "[method] containsKey" in point and get_thread_ID(point) in counts_access:
            if table_id in point:
                counts_access[get_thread_ID(point)] += 1
        elif "[method] put" in point and get_thread_ID(point) in counts_mutate:
            if table_id in point:
                counts_mutate[get_thread_ID(point)] += 1

    # create juxtaposed scatter plot
    x1 = num_access_ops
    y1 = [0 for _ in x1]
    plt.scatter(x1, y1, color='green', alpha=0.5, label="accessOps")

    x2 = num_mutate_ops
    y2 = [0 for _ in x2]
    plt.scatter(x2, y2, alpha=0.5, label="mutateOps")

    plt.title("Comparison")
    plt.xlabel("#")
    plt.legend(loc=1)
    plt.savefig(output_path + "count_id_table_ops_per_tx_scatter.png", bbox_inches='tight')
    plt.clf()

    # create double bar chart
    num_num_access_ops = {}  # number --> number of times that number appears
    for n in num_access_ops:
        if n not in num_num_access_ops:
            num_num_access_ops[n] = 1
        else:
            num_num_access_ops[n] += 1

    num_num_mutate_ops = {}  # number --> number of times that number appears
    for n in num_mutate_ops:
        if n not in num_num_mutate_ops:
            num_num_mutate_ops[n] = 1
        else:
            num_num_mutate_ops[n] += 1

    x = np.array(list(set(num_num_access_ops.keys()).union(set(num_num_mutate_ops.keys()))))
    y1 = []
    y2 = []
    for item in x:
        if item not in num_num_access_ops:
            num_num_access_ops[item] = 0
        if item not in num_num_mutate_ops:
            num_num_mutate_ops[item] = 0
        y1 += [num_num_access_ops[item]]
        y2 += [num_num_mutate_ops[item]]
    width = 0.27
    plt.bar(x, y1, width, color="blue", label="accessOps")
    plt.bar(x + width, y2, width, color="red", label="mutateOps")
    plt.legend(loc=1)
    plt.title("Num Ops per Transaction")
    plt.xlabel("Num Ops")
    plt.ylabel("Num Txs")
    plt.savefig(output_path + "count_id_table_ops_per_tx_bar.png", bbox_inches='tight')
    plt.clf()


def count_all_ops():
    print 9
    counts = {} # op name --> count
    for point in points:
        if get_event_name(point) not in counts:
            counts[get_event_name(point)] = 1
        else:
            counts[get_event_name(point)] += 1

    # create pie chart
    sizes = []
    labels = []
    for op in counts.keys():
        labels += [op]
        sizes += [counts[op]]
    plt.pie(sizes, labels=labels, colors=['gold', 'yellowgreen', 'lightcoral', 'lightskyblue'], autopct='%1.1f%%')
    plt.axis('equal')
    plt.savefig(output_path + "count_all_ops.png", bbox_inches='tight')
    plt.clf()


def count_all_ops_time(): # review this code - may not be 100% accurate, esp. for TXBegin portion!
    print 10
    last_ts = {} # thread # --> last time stamp
    time_count = {} # thread # --> {op name --> cumulative time taken so far}

    for point in points:
        if get_thread_ID(point) not in last_ts: # double check this edge case
            last_ts[get_thread_ID(point)] = get_time_stamp(point)
        if get_thread_ID(point) not in time_count:
            time_count[get_thread_ID(point)] = {}
        if get_event_name(point) not in time_count[get_thread_ID(point)]:
            time_count[get_thread_ID(point)][get_event_name(point)] = 0

        time_count[get_thread_ID(point)][get_event_name(point)] += get_time_stamp(point) - last_ts[get_thread_ID(point)]
        last_ts[get_thread_ID(point)] = get_time_stamp(point)

    # create pie chart
    sizes = []
    labels = []
    sum_time_count = {} # op name --> total time for op across all threads
    for thread in time_count.keys():
        for op in time_count[thread].keys():
            if op not in sum_time_count:
                sum_time_count[op] = time_count[thread][op]
            else:
                sum_time_count[op] += time_count[thread][op]
    for op in sum_time_count.keys():
        labels += [op]
        sizes += [sum_time_count[op]]
    plt.pie(sizes, labels=labels, colors=['gold', 'yellowgreen', 'lightcoral', 'lightskyblue'], autopct='%1.1f%%')
    plt.axis('equal')
    plt.savefig(output_path + "count_all_ops_time.png", bbox_inches='tight')
    plt.clf()


def count_all_ops_time_by_tx():
    print 11

    global all_ops
    last_ts = {} # thread # --> last time stamp
    in_tx = {} # thread # --> (bool) currently in tx
    time_count = {} # thread # --> {op name --> cumulative time taken so far}

    txs = [] # list of lists, txs[i] = np.array of how much time each op takes in tx i

    for point in points:
        if get_event_name(point) == "TXBegin":
            in_tx[get_thread_ID(point)] = True

        # only consider ops that are part of a tx
        if get_thread_ID(point) in in_tx and in_tx[get_thread_ID(point)] == True:
            if get_thread_ID(point) not in last_ts: # double check this edge case
                last_ts[get_thread_ID(point)] = get_time_stamp(point)
            if get_thread_ID(point) not in time_count:
                time_count[get_thread_ID(point)] = {}
            if get_event_name(point) not in time_count[get_thread_ID(point)]:
                time_count[get_thread_ID(point)][get_event_name(point)] = 0
            time_count[get_thread_ID(point)][get_event_name(point)] += get_time_stamp(point) - last_ts[get_thread_ID(point)]

        if get_event_name(point) == "TXEnd":
            # append info in time_count to final np.array for bar chart based on all_ops and indexing
            in_tx[get_thread_ID(point)] = False
            tx = np.zeros(len(all_ops))
            for i in range(len(all_ops)):
                if all_ops[i] in time_count[get_thread_ID(point)]:
                    tx[i] = time_count[get_thread_ID(point)][all_ops[i]]
            txs += [tx]
            # clear time_count for that thread
            del time_count[get_thread_ID(point)]

        # still increment time stamp
        last_ts[get_thread_ID(point)] = get_time_stamp(point)

    # create stacked bar chart
    tx_mtx = np.array([tx for tx in txs])
    tx_mtx_T = tx_mtx.T

    avg_vals = []
    for op in tx_mtx_T:
        avg_vals += [np.average(op)]

    # create pie chart
    sizes = avg_vals
    labels = all_ops
    plt.pie(sizes, labels=labels, colors=['gold', 'yellowgreen', 'lightcoral', 'lightskyblue'], autopct='%1.1f%%')
    plt.axis('equal')
    plt.savefig(output_path + "count_all_ops_time_by_tx.png", bbox_inches='tight')
    plt.clf()


def count_reads():
    print 12
    counts1 = {}  # ms interval --> count of tx reads
    for point in points:
        if get_event_name(point) == "access(tx)":
            if int(nano_to_milli(get_time_stamp(point))) in counts1:
                counts1[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts1[int(nano_to_milli(get_time_stamp(point)))] = 1

    counts2 = {}  # ms interval --> count of non tx reads
    for point in points:
        if get_event_name(point) == "access(nonTx)":
            if int(nano_to_milli(get_time_stamp(point))) in counts2:
                counts2[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts2[int(nano_to_milli(get_time_stamp(point)))] = 1

    # create juxtaposed scatter plot
    x1 = []
    y1 = []
    for key in counts1.keys():
        x1 += [key - min(counts1.keys())]
        y1 += [counts1[key]]
    plt.scatter(x1, y1, color='green', label="tx reads")

    x2 = []
    y2 = []
    for key in counts2.keys():
        x2 += [key - min(counts2.keys())]
        y2 += [counts2[key]]
    plt.scatter(x2, y2, label="non tx reads")

    plt.title("Comparison")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("#")
    plt.legend(loc=1)
    plt.savefig(output_path + "count_reads_scatter.png", bbox_inches='tight')
    plt.clf()


### Display Results
setup()
count_active_threads()
count_seq_calls()
count_stepping()
count_unsafe()
count_ops_per_tx()
count_table_ops_per_tx()
count_id_table_ops_per_tx("7c4f2940-7893-3334-a6cb-7a87bf045c0d")
count_all_ops()
count_all_ops_time()
count_all_ops_time_by_tx()
count_reads()