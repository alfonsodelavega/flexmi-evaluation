#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

if len(sys.argv) == 2:
    filename = sys.argv[1]
else:
    filename = "bytes.csv"

c_model = "Model"
c_xmi = "XMI"
c_plain = "PlainFlexmi"
c_templates = "TemplatesFlexmi"
c_emfatic = "Emfatic"

#%%
df = pd.read_csv(filename)
df.head()

#%%
plt.style.use('seaborn-white')

plt.rcParams.update({
    "text.usetex": True,
    "font.family": "serif",
    "text.latex.preamble" : r'\newcommand{\flexmi}{\textsc{Liquid}}'})

SMALL_SIZE = 14
MEDIUM_SIZE = 16

plt.rc('font', size=SMALL_SIZE)          # controls default text sizes
plt.rc('axes', titlesize=20)     # fontsize of the axes title
plt.rc('axes', labelsize=MEDIUM_SIZE)    # fontsize of the x and y labels
plt.rc('xtick', labelsize=SMALL_SIZE)    # fontsize of the tick labels
plt.rc('ytick', labelsize=SMALL_SIZE)    # fontsize of the tick labels
plt.rc('legend', fontsize=SMALL_SIZE)    # legend fontsize


#%%
# Normalise to emfatic
for column in [c_xmi, c_plain, c_templates]:
    df[column] = df[column] / df[c_emfatic]
df[c_emfatic] = 1

df.head()

#%%
labels_map = {c_xmi : "XMI",
              c_plain : "Plain \\flexmi{}",
              c_templates : "Templates \\flexmi{}"}

labels = [labels_map[m] for m in [c_xmi, c_plain, c_templates]]

f = plt.figure(figsize=(6,3))
ax = f.subplots(nrows=1, ncols=1)

xticks = [0.75,2,3.25]

df.boxplot(column=[c_xmi, c_plain, c_templates],
           ax=ax,
           positions=xticks)
ax.plot([1]*5, linestyle="--", color="#117733")

ax.set_xticklabels(labels)

ax.set_xlim([0, 4])
ax.set_ylim(bottom=0)

ax.set_yticks([0,1,2,3,4,5,6])

ax.set_ylabel("Size (times larger)")
ax.set_title("Relative Size Against Emfatic")


# f.tight_layout()
f.savefig("{}_boxplot.pdf".format(filename), bbox_inches='tight')

#%%
df.sort_values(by=[c_xmi], ascending=False, inplace=True)
df.head(10)

#%%
# quartiles
for m in [c_xmi, c_plain, c_templates]:
    print(df[m].describe())
    print()
