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

df_norm.boxplot(column=[c_plain, c_templates, c_emfatic],
           ax=ax,
           positions=xticks)
ax.plot([1]*5, linestyle="--", color="#117733")

ax.set_ylim(bottom=0)
ax.set_xlim([0, 4])

ax.set_ylabel("Relative load times against XMI")


f.tight_layout()
f.savefig("{}_boxplot.pdf".format(filename), bbox_inches='tight')
