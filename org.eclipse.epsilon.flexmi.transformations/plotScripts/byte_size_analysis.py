#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

filename = "../models/0-bytes.csv"
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

# Normalise to emfatic
for column in [c_xmi, c_plain, c_templates]:
    df[column] = df[column] / df[c_emfatic]
df[c_emfatic] = 1

df.head()

#%%
f = plt.figure(figsize=(6,4))
ax = f.subplots(nrows=1, ncols=1)

xticks = [0.75,2,3.25]

df.boxplot(column=[c_xmi, c_plain, c_templates],
           ax=ax,
           positions=xticks)
ax.plot([1]*5, linestyle="--", color="#117733")

ax.set_ylim(bottom=0)
ax.set_xlim([0, 4])

ax.set_ylabel("Relative size against Emfatic")


f.tight_layout()
f.savefig("bytes_boxplot.pdf", bbox_inches='tight')