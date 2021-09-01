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
c_plain_xml = "PlainXMLFlexmi"
c_plain_yaml = "PlainYAMLFlexmi"
c_templates_xml = "TemplatesXMLFlexmi"
c_templates_yaml = "TemplatesYAMLFlexmi"
c_emfatic = "Emfatic"
c_hutn = "HUTN"

#%%
df = pd.read_csv(filename)
df.head()

#%%
plt.style.use('seaborn-white')

plt.rc('text', usetex=True)

plt.rcParams.update({
    "text.usetex": True,
    "font.family": "serif",
    "font.sans-serif": ["Libertine"],
    "text.latex.preamble" : r"\usepackage{libertine} \newcommand{\flexmi}{\textsc{Liquid}}"})

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
columns = [c_xmi, c_hutn, c_plain_xml, c_plain_yaml, c_templates_xml, c_templates_yaml]
for column in columns:
    df[column] = df[column] / df[c_emfatic]
df[c_emfatic] = 1

df.head()

#%%
labels_map = {c_xmi : "XMI",
              c_hutn : "HUTN",
              c_plain_xml : "Plain XML \\flexmi{}",
              c_plain_yaml : "Plain YAML \\flexmi{}",
              c_templates_xml : "Templated XML \\flexmi{}",
              c_templates_yaml : "Templated YAML \\flexmi{}"}


labels = [labels_map[m] for m in [c for c in reversed(columns)]]


f = plt.figure(figsize=(4,4))
ax = f.subplots(nrows=1, ncols=1)

yticks = [0.5,1.5,2.5,3.5,4.5,5.5]

df.boxplot(column=[c for c in reversed(columns)],
           ax=ax,
           positions=yticks,
           vert=False)
ax.vlines(1, ymin=0, ymax=6, linestyle="--", color="#117733")

ax.set_xlim([0, 7])
ax.set_ylim(bottom=0)

ax.set_xticks([0,1,2,3,4,5,6,7])

ax.set_yticklabels(labels)

ax.set_xlabel("Size (times larger)")
ax.set_title("Relative Size Against Emfatic")


# f.tight_layout()
f.savefig("{}_boxplot.pdf".format(filename), bbox_inches='tight')

#%%
df.sort_values(by=[c_xmi], ascending=False, inplace=True)
df.head(10)

#%%
# quartiles
for m in columns:
    print(df[m].describe())
    print()
