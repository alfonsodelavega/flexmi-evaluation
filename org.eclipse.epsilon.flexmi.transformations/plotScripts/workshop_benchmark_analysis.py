#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

filename = "workshop_benchmark_x270.csv"
c_elems = "ModelElements"
c_xmi = "XMI"
c_flexmi = "Flexmi"

measurements = [c_xmi, c_flexmi]

#%%
df = pd.read_csv(filename)
df.head()

#%%
df_agg = df.groupby([c_elems])[measurements].agg(["mean", "std", "sem"]).reset_index().head(100)
df_agg.head()

#%%
for measurement in measurements:
    df_agg[measurement, "ci95_hi"] = df_agg[measurement, "mean"] + 1.96 * df_agg[measurement, "sem"]
    df_agg[measurement, "ci95_lo"] = df_agg[measurement, "mean"] - 1.96 * df_agg[measurement, "sem"]

df_agg.head()

#%%
df_agg.columns = [col[0] if col[1] == "" else "_".join(col) for col in df_agg.columns]
df_agg.head()

#%%
plt.style.use('seaborn-white')

plt.rc("font", family="serif")

SMALL_SIZE = 14
MEDIUM_SIZE = 16

plt.rc('font', size=SMALL_SIZE)          # controls default text sizes
plt.rc('axes', titlesize=22)     # fontsize of the axes title
plt.rc('axes', labelsize=MEDIUM_SIZE)    # fontsize of the x and y labels
plt.rc('xtick', labelsize=SMALL_SIZE)    # fontsize of the tick labels
plt.rc('ytick', labelsize=SMALL_SIZE)    # fontsize of the tick labels
plt.rc('legend', fontsize=SMALL_SIZE)    # legend fontsize

#%%
f = plt.figure(figsize=(6,4))
ax = f.subplots(nrows=1, ncols=1)

# xticks = [0.75,2,3.25]

df_agg.plot(x=c_elems, y="{}_mean".format(c_xmi), label=c_xmi,
            ax=ax, marker="o", color="#1965b0")
# ax.fill_between(x=df_agg[c_elems],
#                 y1=df_agg["{}_ci95_hi".format(c_xmi)],
#                 y2=df_agg["{}_ci95_lo".format(c_xmi)],
#                     color="#1965b0", alpha=0.1)


df_agg.plot(x=c_elems, y="{}_mean".format(c_flexmi), label=c_flexmi,
            ax=ax, marker="D", color="#dc050c")
# ax.fill_between(x=df_agg[c_elems],
#                 y2=df_agg["{}_ci95_hi".format(c_flexmi)],
#                 y1=df_agg["{}_ci95_lo".format(c_flexmi)],
#                     color="#dc050c", alpha=0.2)

ax.set_ylim([0,3000])
ax.set_xlim([0, 250000])

ax.set_xlabel("Number of model elements")
ax.set_ylabel("Loading time (ms)")


f.tight_layout()
f.savefig("workshop_benchmark_loadtimes.pdf", bbox_inches='tight')

# %%
df_agg["ratioFlexmi-XMI"] = df_agg["{}_mean".format(c_flexmi)] / df_agg["{}_mean".format(c_xmi)]
df_agg[[c_elems, "ratioFlexmi-XMI"]].head(10)
