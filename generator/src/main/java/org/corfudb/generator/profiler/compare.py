import matplotlib
import matplotlib.pyplot as plt
import numpy as np
from os import walk

points1 = []
points2 = []
all_ops = []


### Helper Functions
def setup(path1, path2):
    global points1
    global points2
    # open files and initialize vars
    f1 = open(path1)
    for point in f1.readlines():
        points1.append(point)
        if get_event_name(point) not in all_ops:
            all_ops.append(get_event_name(point))
    f2 = open(path2)
    for point in f2.readlines():
        points2.append(point)
        if get_event_name(point) not in all_ops:
            all_ops.append(get_event_name(point))

    # sort points by start time
    points1.sort(key=lambda x: get_time_stamp(x))
    points2.sort(key=lambda x: get_time_stamp(x))

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
def compare_ops_time():
    global points1, points2, all_ops

    # Find sizes and labels for first log
    last_ts = {}  # thread # --> last time stamp
    time_count = {}  # thread # --> {op name --> cumulative time taken so far}

    for point in points1:
        if get_thread_ID(point) not in last_ts:  # double check this edge case
            last_ts[get_thread_ID(point)] = get_time_stamp(point)
        if get_thread_ID(point) not in time_count:
            time_count[get_thread_ID(point)] = {}
        if get_event_name(point) not in time_count[get_thread_ID(point)]:
            time_count[get_thread_ID(point)][get_event_name(point)] = 0

        time_count[get_thread_ID(point)][get_event_name(point)] += get_time_stamp(point) - last_ts[get_thread_ID(point)]
        last_ts[get_thread_ID(point)] = get_time_stamp(point)

    # create stacked bar chart
    sum_time_count1 = {}  # op name --> total time for op across all threads
    for thread in time_count.keys():
        for op in time_count[thread].keys():
            if op not in sum_time_count1:
                sum_time_count1[op] = time_count[thread][op]
            else:
                sum_time_count1[op] += time_count[thread][op]
    sum_time_count2 = {}  # op name --> total time for op across all threads
    for thread in time_count.keys():
        for op in time_count[thread].keys():
            if op not in sum_time_count2:
                sum_time_count2[op] = time_count[thread][op]
            else:
                sum_time_count2[op] += time_count[thread][op]

    colors = ['gold', 'yellowgreen', 'lightcoral', 'lightskyblue']
    N = 2
    ind = np.arange(N)  # the x locations for the groups
    width = 0.35  # the width of the bars: can also be len(x) sequence
    temp = tuple([0 for _ in range(N)])
    for i in range(len(all_ops)):
        op = all_ops[i]
        data = (sum_time_count1[op], sum_time_count2[op])
        plt.bar(ind, data, width, colors[i % 4], label=op, bottom=temp)
        temp = tuple(data)
    plt.ylabel('Time')
    plt.title('Comparison of Time Taken by All Ops')
    plt.xticks(ind, ('Before', 'After'))
    plt.legend(loc=1)

    # N = 5
    # menMeans = (20, 35, 30, 35, 27)
    # womenMeans = (25, 32, 34, 20, 25)
    # menStd = (2, 3, 4, 1, 2)
    # womenStd = (3, 5, 2, 3, 3)
    # ind = np.arange(N)  # the x locations for the groups
    # width = 0.35  # the width of the bars: can also be len(x) sequence
    #
    # p1 = plt.bar(ind, menMeans, width, color='#d62728', yerr=menStd)
    # p2 = plt.bar(ind, womenMeans, width,
    #              bottom=menMeans, yerr=womenStd)
    #
    # plt.ylabel('Scores')
    # plt.title('Scores by group and gender')
    # plt.xticks(ind, ('G1', 'G2', 'G3', 'G4', 'G5'))
    # plt.yticks(np.arange(0, 81, 10))
    # plt.legend((p1[0], p2[0]), ('Men', 'Women'))

    plt.savefig('results/shriya/count_all_ops_time.png', bbox_inches='tight')
    plt.clf()


### Display Results
file1 = "/Users/vshriya/Desktop/current/CorfuDB/test/client-1529017385323.log"
file2 = "/Users/vshriya/Desktop/current/CorfuDB/test/client-1529017385323.log"
setup(file1, file2)
# count_active_threads()
# count_seq_calls()
# count_stepping()
# count_unsafe()
# count_ops_per_tx()
# count_table_ops_per_tx()
# count_id_table_ops_per_tx("7c4f2940-7893-3334-a6cb-7a87bf045c0d")
# count_all_ops()
# count_all_ops_time()
# count_all_ops_time_by_tx()
# count_reads()
compare_ops_time()