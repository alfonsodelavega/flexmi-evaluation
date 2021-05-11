#!/usr/bin/env python

#%%
import pandas as pd
import sys
import matplotlib.pyplot as plt
import numpy as np

if len(sys.argv) == 2:
    filename = sys.argv[1]
else:
    filename = "modelStatistics.csv"

df = pd.read_csv(filename)

#%%
for m in ["classes", "attributes", "references", "enums", "annotations"]:
    print(df[m].describe())
    print()
