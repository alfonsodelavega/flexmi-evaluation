#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

filename = "../models/0-loadTime.csv"
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
df_agg = df.groupby([c_model])[measurements].agg(["mean", "std", "sem"]).reset_index().head(100)
df_agg.head()

#%%
for measurement in measurements:
    df_agg[measurement, "ci95_hi"] = df_agg[measurement, "mean"] + 1.96 * df_agg[measurement, "sem"]
    df_agg[measurement, "ci95_lo"] = df_agg[measurement, "mean"] - 1.96 * df_agg[measurement, "sem"]

df_agg.head()

#%%
df_agg.sort_values([(c_plain, "mean")], ascending=False, inplace=True)
df_agg.to_csv("{}.processed.csv".format(filename))
