# flexmi-evaluation

This repository contains the source code and models used for the conciseness and performance evaluation of the Flemxi syntax against state-of-the-art technologies.

## Requirements

An Eclipse installation with the latest version of Epsilon, downloadable [here](https://www.eclipse.org/epsilon/download/)

## Structure of org.eclipse.epsilon.flexmi.transformations

- `src/org.eclipse.epsilon.flexmi.transformations`: Contains the transformations to generate the compared syntaxes (e.g. Flexmi, Emfatic, HUTN) from the original Ecore models, as well as the measurement scripts of file size and loading times
- `models`: contains different datasets of models used during different stages of the evaluation. The concrete dataset where measurements were taken is `models/ammore2020-barriga`
- `models/paperExamples`: contains XML and YAML versions of all the examples of the paper
- `plotScripts`: Python scripts used to generate the plots of the paper
- `templates`: Flexmi templates for Ecore and other examples, in XML and YAML flavours
