#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np
import sys

if len(sys.argv) == 2:
    filename = sys.argv[1]
else:
    filename = "elementCount.csv"

c_model = "Model"
c_xmi = "XMI"
c_plain_xml = "PlainXMLFlexmi"
c_plain_yaml = "PlainYAMLFlexmi"
c_templates_xml = "TemplatesXMLFlexmi"
c_templates_yaml = "TemplatesYAMLFlexmi"
c_emfatic = "Emfatic"

cols = [c_xmi, c_plain_xml, c_plain_yaml,
        c_templates_xml, c_templates_yaml, c_emfatic]

#%%
df = pd.read_csv(filename)
df.head()

#%%
def equal_elements(row):
    return row[c_xmi] == row[c_plain_xml] == row[c_plain_yaml] == row[c_templates_xml] == row[c_templates_yaml] == row[c_emfatic]

def max_distance(row):
    max_distance = 0
    for col1 in range(0, len(cols)):
        for col2 in range(col1 + 1, len(cols)):
            distance = abs(row[cols[col1]] - row[cols[col2]])
            if distance > max_distance:
                max_distance = distance
    return max_distance

df["equal"] = df.apply(equal_elements, axis=1)
df["max_distance"] = df.apply(max_distance, axis=1)
df.head()

#%%
df_distinct = df[~df["equal"]].copy()
df_distinct.sort_values(by=["max_distance"], ascending=False, inplace=True)
len(df_distinct)

#%%
def get_min_approach(row):
    min_approach = ""
    min_value = sys.maxsize
    for approach in cols:
        if row[approach] < min_value:
            min_approach = approach
            min_value = row[approach]
    return min_approach

df_distinct["min_approach"] = df_distinct.apply(get_min_approach, axis=1)

#%%
df_emfatic_issues = df_distinct[df_distinct["min_approach"] == c_emfatic]
df_emfatic_issues.to_csv("element_count_emfatic_issues.csv", index=False)

#%%
threshold = 1
df_rare = df_distinct[df_distinct["max_distance"] > threshold]
len(df_rare)

df_rare.to_csv("element_count_rare.csv", index=False)
