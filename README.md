# exercise-generator-be

## Prerequisites

### LaTeX compiler setup

In order to use this backend service, the host needs to have the LaTeX compiler package [latexmk](https://mg.readthedocs.io/latexmk.html) installed on it's system and the executable available in its `PATH`.

For installation instructions for your specific environment please check out this [documentation](https://mg.readthedocs.io/latexmk.html#installation).

> **Note:** Depending on your LaTeX installation the packages `texlive-fonts-recommended` and `texlive-latex-extra` (or `collection-fontsrecommended` and `collection-latexextra` if you are using `tlmgr`) might already be installed or not. Please make sure these packages are installed on your system for the application to function.