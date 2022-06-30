# exercise-generator-be

## Prerequisites

### LaTeX compiler setup

In order to use this backend service, the host needs to have the LaTeX compiler package [latexmk](https://ctan.org/pkg/latexmk) installed on it's system and the executable available in its `PATH`.

> Further instructions regarding the latexmk compiler can be found in this [documentation](https://mg.readthedocs.io/latexmk.html).

For the template to work, additionally the LaTeX packages `collection-fontsrecommended` and `collection-latexextra` need to be present in your LaTeX installation.


#### Installation

We recommend installing the [TeXLive](https://www.tug.org/texlive/) package (or [MacTeX](https://www.tug.org/mactex/) for OSX users) on your system. For further instructions please follow the official instructions for your respective operating system.

After the TeXLive suite is installed, you may need, depending on if you've chosen a full installation of TeXLive or not, to install the required packages. To do so, the easiest way is to use the `tlmgr` utility which comes with every installation of TeXLive.  
Running the following command will install all necessary packages on your system (including the `latexmk` compiler):

```sh
tlmgr install collection-fontsrecommended collection-latexextra latexmk
```

Wait for the installation of the packages to finish and you should be good to go.

You can verify your installation by opening a command line and entering:

```sh
latexmk -v
```

