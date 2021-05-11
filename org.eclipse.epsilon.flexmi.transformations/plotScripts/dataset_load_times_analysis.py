#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

if len(sys.argv) == 2:
    filename = sys.argv[1]
else:
    filename = "datasetloadtimes.csv"

c_xmi = "XMI"
c_plain = "PlainFlexmi"
c_templates = "TemplatesFlexmi"
c_emfatic = "Emfatic"

measurements = [c_xmi, c_emfatic, c_plain, c_templates]

#%%
df = pd.read_csv(filename)
df.head()

#%%
results = {}
for m in measurements:
    results[m, "mean"] = df[m].mean()
    results[m, "rel_std"] = df[m].std() / results[m, "mean"]
    results[m, "sem"] = df[m].sem()

    results[m, "ci95"] = 1.96 * results[m, "sem"]

    for v in ["mean", "sem", "ci95"]:
        results[m, v] = results[m, v] / 10**6 # to ms

    print(m)
    print("Mean: {}".format(results[m, "mean"]))
    print("Relative std: {}".format(results[m, "rel_std"]))
    print("95 CI error value: {}".format(results[m, "ci95"]))
    print()

#%%
# score1 is times greater than score2
def times(score1, score2):
    return score1 / score2

for m in [c_plain, c_templates, c_emfatic]:
    print("{} takes {} times the time to finish than XMI".format(
          m,
          times(results[m, "mean"], results[c_xmi, "mean"])))

print("{} takes {} times the time to finish than {}".format(
          c_templates,
          times(results[c_templates, "mean"], results[c_emfatic, "mean"]),
          c_emfatic))

#%%
plt.style.use('seaborn-white')

plt.rcParams.update({
    "text.usetex": True,
    "font.family": "serif",
    "text.latex.preamble" : r'\newcommand{\flexmi}{\textsc{Liquid}}'})

SMALL_SIZE = 14
MEDIUM_SIZE = 16

plt.rc('font', size=SMALL_SIZE)          # controls default text sizes
plt.rc('axes', titlesize=22)     # fontsize of the axes title
plt.rc('axes', labelsize=MEDIUM_SIZE)    # fontsize of the x and y labels
plt.rc('xtick', labelsize=SMALL_SIZE)    # fontsize of the tick labels
plt.rc('ytick', labelsize=SMALL_SIZE)    # fontsize of the tick labels
plt.rc('legend', fontsize=SMALL_SIZE)    # legend fontsize

#%%
y_pos = np.arange(len(measurements))
values = [results[m, "mean"] for m in measurements]
error = [results[m, "ci95"] for m in measurements]

labels_map = {c_xmi : "XMI",
              c_emfatic : "Emfatic",
              c_plain : "Plain \\flexmi{}",
              c_templates : "Templates \\flexmi{}"}

labels = [labels_map[m] for m in measurements]

blue_dark = "#1965B0"
blue_light = "#7BAFDE"

red_dark = "#DC050C"
red_light = "#EE8026"

green_dark = "#4EB265"
green_light = "#CAE0AB"

violet_dark = "#882E72"

colors = [red_dark, red_light, green_dark, blue_dark]

print(y_pos)
print(values)
print(error)
print(labels)

f = plt.figure(figsize=(6,4))
ax = f.subplots(nrows=1, ncols=1)

ax.barh(y_pos, values, xerr=error,
        capsize=8,
        align='center',
        color=colors)

ax.tick_params(axis=u'both', which=u'both',length=5)
ax.set_yticklabels(labels)
ax.set_yticks(y_pos)
# ax.invert_yaxis()  # labels read top-to-bottom
ax.set_xlabel('Total load time (ms)')

ax.set_title('Metamodels Dataset Load Times')

plt.show()

f.tight_layout()
f.savefig("{}_barh.pdf".format(filename), bbox_inches='tight')

# %%
