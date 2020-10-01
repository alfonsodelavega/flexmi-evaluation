#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

if len(sys.argv) == 2:
    filename = sys.argv[1]
else:
    filename = "ecoregithub_loadtimes.csv"

c_model = "Model"
c_xmi = "XMI"
c_plain = "PlainFlexmi"
c_templates = "TemplatesFlexmi"
c_emfatic = "Emfatic"

measurements = [c_xmi, c_plain, c_templates, c_emfatic]

#%%
df = pd.read_csv(filename)
df.head()

#%%
df = df.groupby([c_model])[measurements].agg(["mean", "std", "sem"]).reset_index().head(100)
df.head()

#%%
for measurement in measurements:
    df[measurement, "ci95_hi"] = df[measurement, "mean"] + 1.96 * df[measurement, "sem"]
    df[measurement, "ci95_lo"] = df[measurement, "mean"] - 1.96 * df[measurement, "sem"]

df.head()

#%%
# remove multi-level column index and export whole dataframe
df.sort_values([(c_plain, "mean")], ascending=False, inplace=True)
df.columns = [col[0] if col[1] == "" else "_".join(col) for col in df.columns]
df.to_csv("{}_processed.csv".format(filename), index=False)


#%%
# Normalise to XMI
measurements = ["{}_mean".format(col)
                for col in [c_plain, c_templates, c_emfatic, c_xmi]]
df_norm = df[[c_model] + measurements].copy()


for col in [c_plain, c_templates, c_emfatic]:
    df_norm[col] = df_norm["{}_mean".format(col)] / df_norm["{}_mean".format(c_xmi)]

output_cols = [c_model, c_plain, c_templates, c_emfatic]
df_norm = df_norm[output_cols].copy()

df_norm.sort_values([c_plain], ascending=False, inplace=True)

df_norm.to_csv("{}_normalisedToXMI.csv".format(filename), index=False)

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

xticks = [0.75,2,3.25]

df_norm.boxplot(column=[c_emfatic, c_plain, c_templates],
           ax=ax,
           positions=xticks,
           showfliers=False)
ax.plot([1]*5, linestyle="--", color="#117733")

ax.set_ylim([0, 5])
ax.set_xlim([0, 4])

ax.set_ylabel("Load times relative to XMI")


alpha = 0.2
marker = "."
size = 30
jitter = 0.05
color="red"

x = np.random.normal(0.75, jitter, size=len(df_norm))
ax.scatter(x, df_norm[c_emfatic], color=color,
           marker=marker,alpha=alpha, s=size)

x = np.random.normal(2, jitter, size=len(df_norm))
ax.scatter(x, df_norm[c_plain], color=color,
           marker=marker,alpha=alpha, s=size)

x = np.random.normal(3.25, jitter, size=len(df_norm))
ax.scatter(x, df_norm[c_templates], color=color,
           marker=marker,alpha=alpha, s=size)


f.tight_layout()
f.savefig("{}_boxplot.pdf".format(filename), bbox_inches='tight')

#%%
f = plt.figure(figsize=(6,4))
ax = f.subplots(nrows=1, ncols=1)

alpha = 0.15
marker = "o"
size = 80

ax.scatter(x=[1]*len(df), y=df["{}_mean".format(c_xmi)]/10**6,
           marker=marker,alpha=alpha, s=size)
ax.scatter(x=[2]*len(df), y=df["{}_mean".format(c_emfatic)]/10**6,
           marker=marker,alpha=alpha, s=size)
ax.scatter(x=[3]*len(df), y=df["{}_mean".format(c_plain)]/10**6,
           marker=marker,alpha=alpha, s=size)
ax.scatter(x=[4]*len(df), y=df["{}_mean".format(c_templates)]/10**6,
           marker=marker,alpha=alpha, s=size)

ax.set_xlim([0.5, 4.5])
ax.set_xticks([1,2,3,4])
ax.set_xticklabels(["XMI", "Emfatic", "PlainFlexmi", "TemplatesFlexmi"])
plt.setp(ax.get_xticklabels(), rotation=30, horizontalalignment='right')
ax.set_ylabel("Model load times (ms)")

ax.set_yticks(range(0,91,15))

f.tight_layout()
f.savefig("{}_scatter.pdf".format(filename), bbox_inches='tight')
