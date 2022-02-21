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
c_plain_xml = "PlainXMLFlexmi"
c_plain_yaml = "PlainYAMLFlexmi"
c_templates_xml = "TemplatesXMLFlexmi"
c_templates_yaml = "TemplatesYAMLFlexmi"
c_emfatic = "Emfatic"

measurements = [c_xmi, c_emfatic, c_plain_xml, c_plain_yaml, c_templates_xml, c_templates_yaml]

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

for m in [c_plain_xml, c_plain_yaml, c_templates_xml, c_templates_yaml, c_emfatic]:
    print("{} takes {} times the time to finish than XMI".format(
          m,
          times(results[m, "mean"], results[c_xmi, "mean"])))

print()

print("{} takes {} times the time to finish than {}".format(
          c_plain_xml,
          times(results[c_plain_xml, "mean"], results[c_plain_yaml, "mean"]),
          c_plain_yaml))

print("{} takes {} times the time to finish than {}".format(
          c_templates_xml,
          times(results[c_templates_xml, "mean"], results[c_templates_yaml, "mean"]),
          c_templates_yaml))

print("{} takes {} times the time to finish than {}".format(
          c_emfatic,
          times(results[c_emfatic, "mean"], results[c_templates_yaml, "mean"]),
          c_templates_yaml))

#%%
plt.style.use('seaborn-white')

plt.rcParams.update({
    "text.usetex": True,
    "font.family": "serif",
    "font.sans-serif": ["Libertine"],
    "text.latex.preamble" : r"\usepackage{libertine} \newcommand{\flexmi}{\textsc{Flexmi}}"})

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
              c_plain_xml : "Plain XML \\flexmi{}",
              c_plain_yaml : "Plain YAML \\flexmi{}",
              c_templates_xml : "Templated XML \\flexmi{}",
              c_templates_yaml : "Templated YAML \\flexmi{}",
              c_emfatic : "Emfatic"}

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

f = plt.figure(figsize=(4,3))
ax = f.subplots(nrows=1, ncols=1)

bars = ax.barh(y_pos, values, xerr=error,
        capsize=8,
        align='center',
        color="white",
        edgecolor="black")

# hatches = ["/", "\\", "X", "XX"]
hatches = ["//////", "/////", "////", "///", "//", "/"]
# hatches = ["////", "\\\\\\", "//", "/"]
for bar, hatch in zip(bars, hatches):
    bar.set_hatch(hatch)

ax.tick_params(axis=u'both', which=u'both',length=5)
ax.set_yticklabels(labels)
ax.set_yticks(y_pos)
# ax.invert_yaxis()  # labels read top-to-bottom

ax.set_xlabel('Total load time (ms)')
ax.set_xlim([0, 5000])

ax.set_title('Ecore Models Dataset Load Times')

plt.show()

# f.tight_layout()
f.savefig("{}_barh.pdf".format(filename), bbox_inches='tight')

# %%
