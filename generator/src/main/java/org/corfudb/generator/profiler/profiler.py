import matplotlib.pyplot as plt
import numpy as np

input_path = "/Users/vshriya/Desktop/current/CorfuDB/test/client-1529017385323.log"
output_path = "results/shriya/"
points = []  # all data points, aka each entry in log files
all_ops = []  # all operations mentioned in log files


### Helper Functions
def setup():
    global points, all_ops

    # open file and populate global vars
    f = open(input_path)
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


def get_table_ID(entry):
    if "[id]" not in entry:
        raise TypeError("entry is not a table")
    if "[id]" in entry:
        return entry.split("[id] ")[1].split()[0]


def get_duration(entry):
    return int(entry.split("[dur] ")[1].split()[0])


def nano_to_milli(time):
    return time / 1000000


### Analysis
def count_active_threads():
    '''
    Answers the query: how many threads are active per millisecond?
    Creates a scatter plot
    '''
    print 1
    # Count active threads per ms
    counts = {}  # ms interval --> set of active thread IDs
    for point in points:
        if int(nano_to_milli(get_time_stamp(point))) in counts:
            counts[int(nano_to_milli(get_time_stamp(point)))].add(get_thread_ID(point))
        else:
            counts[int(nano_to_milli(get_time_stamp(point)))] = set([get_thread_ID(point)])

    # Create scatter plot
    x = []
    y = []
    for key in counts.keys():
        x += [key - min(counts.keys())]
        y += [len(counts[key])]

    plt.scatter(x, y)
    plt.title("Number of Active Threads vs. Time Elapsed")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("Number of Active Threads")
    plt.savefig(output_path + "count_active_threads.png", bbox_inches='tight')
    plt.clf()


def count_seq_calls():
    '''
    Answers the query: how many sequencer calls are made per millisecond?
    Creates a scatter plot
    '''
    print 2
    # Count sequencer calls per ms
    counts = {}  # ms interval --> counts of seq calls
    for point in points:
        if get_event_name(point) == "Seq":
            if int(nano_to_milli(get_time_stamp(point))) in counts:
                counts[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts[int(nano_to_milli(get_time_stamp(point)))] = 1

    # Create scatter plot
    x = []
    y = []
    for key in counts.keys():
        x += [key - min(counts.keys())]
        y += [counts[key]]

    plt.scatter(x, y)
    plt.title("Number of Seq Calls vs. Time Elapsed")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("Number of Seq Calls")
    plt.savefig(output_path + "count_seq_calls.png", bbox_inches='tight')
    plt.clf()


def count_ops_per_tx():
    '''
    Answers the query: how many operations are performed per transaction?
    Creates a bar chart
    '''
    print 3
    # Count number of ops in every transaction
    counts = {}  # thread num --> count of ops (in one tx in that thread)
    num_ops = []  # list of number of ops taken by each tx
    for point in points:
        if get_event_name(point) == "TXBegin":
            counts[get_thread_ID(point)] = 0
        elif get_event_name(point) == "TXEnd":
            num_ops += [counts[get_thread_ID(point)]]
            counts[get_thread_ID(point)] = 0
        elif get_thread_ID(point) in counts:
            counts[get_thread_ID(point)] += 1

    # Count frequency of each number of ops
    num_num_ops = {}  # number --> number of times that number appears
    for n in num_ops:
        if n not in num_num_ops:
            num_num_ops[n] = 1
        else:
            num_num_ops[n] += 1
    x = num_num_ops.keys()
    y = num_num_ops.values()

    # Create bar chart
    width = 1 / 1.5
    plt.bar(x, y, width, color="blue")
    plt.title("Num Ops per Transaction")
    plt.xlabel("Num Ops")
    plt.ylabel("Num Txs")
    plt.savefig(output_path + "count_ops_per_tx_bar.png", bbox_inches='tight')
    plt.clf()


def count_table_ops_per_tx():
    print 4
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
    print 5
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
    '''
    Answers the query: how many times was each operation called w.r.t. the total number of calls?
    Creates a pie chart
    '''
    print 6
    # Count number of times each operation was called
    counts = {}  # op name --> count
    for point in points:
        if get_event_name(point) not in counts:
            counts[get_event_name(point)] = 1
        else:
            counts[get_event_name(point)] += 1

    # Create pie chart
    plt.pie(counts.values(), labels=counts.keys(), colors=['gold', 'yellowgreen', 'lightcoral', 'lightskyblue'],
            autopct='%1.1f%%')
    plt.axis('equal')
    plt.savefig(output_path + "count_all_ops.png", bbox_inches='tight')
    plt.clf()


def count_all_ops_time():
    '''
    Answers the query: how much time did each operation take w.r.t. the total runtime?
    Creates a pie chart
    '''
    print 7
    # Count total time taken by each op
    # NOT CORRECT - double counts things in threads!!!
    counts = {}  # op name --> total time taken by this op
    for point in points:
        if get_event_name(point) not in counts:
            counts[get_event_name(point)] = get_duration(point)
        else:
            counts[get_event_name(point)] += get_duration(point)

    # Create pie chart
    plt.pie(counts.values(), labels=counts.keys(), colors=['gold', 'yellowgreen', 'lightcoral', 'lightskyblue'],
            autopct='%1.1f%%')
    plt.axis('equal')
    plt.savefig(output_path + "count_all_ops_time.png", bbox_inches='tight')
    plt.clf()


def count_all_ops_time_by_tx():
    '''
    Answers the query: how much time did each transactional operation take on average across all transactions
    w.r.t. the total runtime of all transactions?
    Creates a pie chart
    '''
    print 8
    # Count total time taken by each op w/i each tx
    # NOT CORRECT for same reason as above!
    txs = []  # list of lists, txs[i] = np.array of how much time each op takes in tx i
    in_tx = {}  # thread # --> (bool) currently in tx
    counts = {}  # thread # --> {op name --> total time taken by this op}
    for point in points:
        if get_event_name(point) == "TXBegin":
            in_tx[get_thread_ID(point)] = True

        # only consider ops that are part of a tx
        if get_thread_ID(point) in in_tx and in_tx[get_thread_ID(point)]:
            if get_event_name(point) not in counts:
                counts[get_thread_ID(point)][get_event_name(point)] = get_duration(point)
            else:
                counts[get_thread_ID(point)][get_event_name(point)] += get_duration(point)

        if get_event_name(point) == "TXEnd":
            # append info in counts to txs
            in_tx[get_thread_ID(point)] = False
            tx = np.zeros(len(all_ops))
            for i in range(len(all_ops)):
                if all_ops[i] in counts[get_thread_ID(point)]:
                    tx[i] = counts[get_thread_ID(point)][all_ops[i]]
            txs += [tx]
            # clear time_count for that thread
            del counts[get_thread_ID(point)]

    tx_mtx = np.array([tx for tx in txs])
    tx_mtx_T = tx_mtx.T  # aggregates counts s.t. each op has its own list

    avg_vals = []  # list of avg time taken by each op
    for op in tx_mtx_T:
        avg_vals += [np.average(op)]

    # create pie chart
    plt.pie(avg_vals, labels=all_ops, colors=['gold', 'yellowgreen', 'lightcoral', 'lightskyblue'], autopct='%1.1f%%')
    plt.axis('equal')
    plt.savefig(output_path + "count_all_ops_time_by_tx.png", bbox_inches='tight')
    plt.clf()


def count_reads():
    '''
    Answers the query: how many reads (both tx and non-tx) are done per millisecond?
    Creates a scatter plot, with both tx reads and non-tx reads
    '''
    print 9
    # Count tx reads
    counts_tx = {}  # ms interval --> count of tx reads
    for point in points:
        if get_event_name(point) == "access(tx)":
            if int(nano_to_milli(get_time_stamp(point))) in counts_tx:
                counts_tx[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts_tx[int(nano_to_milli(get_time_stamp(point)))] = 1

    # Count non-tx reads
    counts_non_tx = {}  # ms interval --> count of non-tx reads
    for point in points:
        if get_event_name(point) == "access(nonTx)":
            if int(nano_to_milli(get_time_stamp(point))) in counts_non_tx:
                counts_non_tx[int(nano_to_milli(get_time_stamp(point)))] += 1
            else:
                counts_non_tx[int(nano_to_milli(get_time_stamp(point)))] = 1

    # Create juxtaposed scatter plot
    x_tx = []
    y_tx = []
    for key in counts_tx.keys():
        x_tx += [key - min(counts_tx.keys())]
        y_tx += [counts_tx[key]]
    plt.scatter(x_tx, y_tx, color='green', label="tx reads")

    x_non_tx = []
    y_non_tx = []
    for key in counts_non_tx.keys():
        x_non_tx += [key - min(counts_non_tx.keys())]
        y_non_tx += [counts_non_tx[key]]
    plt.scatter(x_non_tx, y_non_tx, label="non-tx reads")

    plt.title("Tx and Non-Tx Reads vs. Time")
    plt.xlabel("Time Elapsed (ms)")
    plt.ylabel("Number of Reads")
    plt.legend(loc=1)
    plt.savefig(output_path + "count_reads_scatter.png", bbox_inches='tight')
    plt.clf()


### Display Results
setup()
count_active_threads()
count_seq_calls()
count_ops_per_tx()
count_table_ops_per_tx()
count_id_table_ops_per_tx("7c4f2940-7893-3334-a6cb-7a87bf045c0d")
count_all_ops()
count_all_ops_time()
count_all_ops_time_by_tx()
count_reads()