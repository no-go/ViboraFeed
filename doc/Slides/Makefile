# NOW := $(shell date +"%c" | tr ' :' '__')
NOW := $(shell date +"%Y%m%d-%H%M")
# AUTHOR = jp
# OUTFILE = ./Slides.$(AUTHOR).$(NOW).pdf
OUTFILE = ./../Slides.pdf
TEMPLATE = _template

all:
	pandoc Readme.md --slide-level 2 -t beamer -o Readme.tex
	sed -i -e 's:\\begin{verbatim}:\\begin{lstlisting}:g' Readme.tex
	sed -i -e 's:\\end{verbatim}:\\end{lstlisting}:g' Readme.tex
	latex $(TEMPLATE).tex
	latex $(TEMPLATE).tex
	dvips -Ppdf $(TEMPLATE).dvi -t landscape
	ps2pdf $(TEMPLATE).ps
	mv $(TEMPLATE).pdf $(OUTFILE)

clean:
	rm -rf Readme.tex
	rm -rf *.ps
	rm -rf *.dvi
	rm -rf *.aux
	rm -rf *.log
	rm -rf *.nav
	rm -rf *.toc
	rm -rf *.snm
	rm -rf *.pyg
	rm -rf *.out
	rm -rf *.vrb
