import pandas as pd
import matplotlib.pyplot as plt
import networkx as nx
import math

input_file = "soc-sign-bitcoinotc.csv"


data = pd.read_csv(input_file)

# find duplicate. didn't find any
# duplicate_data = data[data.duplicated(subset=["SOURCE", "TARGET"])]
# print duplicate
# print(duplicate_data.head())

# drop duplicate edges. the input file is alreay sorted by time
# data.drop_duplicates(subset=["SOURCE","TARGET"], keep='first', inplace=True)

# drop the time column
del data["TIME"]

# sort
data.sort_values(["SOURCE", "TARGET"], inplace=True)

# shift ratings to 0 - 20"
data.RATING += 10

# normalize rating
data["RATING"]=((data["RATING"]-data["RATING"].min())/(data["RATING"].max()-data["RATING"].min()))

# print(data.head())
print(data.shape)
# print(data.dtypes)
# output
data.to_csv("output.csv", index=False)

# check what node number does not exist in the graph
set1 = data["SOURCE"].unique()
set2 = data["TARGET"].unique()
set = set(set1).union(set(set2))

print("Not in the dataset:")
max = 1
count = 0
for i in range (1, 6006):
    if (i not in set):
        print(i,end=" ")
    else:
        if i > max:
            max = i
        count = count + 1
print("\nTotal nodes in the dataset = ", count)
print("Last node in the dataset = ", max)



# # distribution
#data.hist(column="RATING", grid=False,bins=21)
#plt.savefig("distribution.pdf")

n = 3 / math.sqrt(5881)

# graph
# plt.axis('off')
# graph = nx.from_pandas_edgelist(data, "SOURCE", "TARGET")
# pos=nx.spring_layout(graph, k=n, scale=1)

# nx.draw_networkx_nodes(graph, pos, node_size=0.5, node_color="red")
# nx.draw_networkx_edges(graph, pos, alpha=0.1)
# plt.savefig("network-no-legend.pdf")

# nx.draw_networkx(graph, pos,arrowsize=1, node_size=0.5, node_color="red", font_size=3)
# plt.savefig("network-legend.pdf")

