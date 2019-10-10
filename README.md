easy-transform-metadata
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-transform-metadata.png?branch=master)](https://travis-ci.org/DANS-KNAW/easy-transform-metadata)

SYNOPSIS
--------

    easy-transform-metadata <[--datasetId|-d]|[--list|-l]> [--transform|-t] [--output|-o]


DESCRIPTION
-----------

Tool to transform EASY metadata (dataset.xml en files.xml) into other formats


ARGUMENTS
---------

    Options:

       -d, --datasetId  <arg>   The datasetId (UUID) for which to transform the metadata
       -l, --list  <arg>        A file containing a newline separated list of datasetIds (UUID) for which to
                                transform the metadata
       -o, --output  <arg>      The directory in which to output the resultant metadata. If '-d' is used, this is
                                optional (default to stdout); if '-l' is used, this argument is mandatory.
       -t, --transform  <arg>   The file containing an XSLT to be applied to the metadata of the given dataset(s);
                                if not provided, no transformation will be performed, but the input for the transformation will be returned.
       -h, --help               Show help message
       -v, --version            Show version of this program

EXAMPLES
--------

    easy-transform-metadata -d b2149eb8-eb51-11e9-896f-6b3af1277c7b -t my-transformation.xslt
    easy-transform-metadata -l my-datasetIds.txt -t my-transformation.xslt -o transformation-output/


INSTALLATION AND CONFIGURATION
------------------------------


1. Unzip the tarball to a directory of your choice, typically `/usr/local/`
2. A new directory called easy-transform-metadata-<version> will be created
3. Add the command script to your `PATH` environment variable by creating a symbolic link to it from a directory that is
   on the path, e.g. 
   
        ln -s /usr/local/easy-transform-metadata-<version>/bin/easy-transform-metadata /usr/bin



General configuration settings can be set in `cfg/application.properties` and logging can be configured
in `cfg/logback.xml`. The available settings are explained in comments in aforementioned files.


BUILDING FROM SOURCE
--------------------

Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher

Steps:

        git clone https://github.com/DANS-KNAW/easy-transform-metadata.git
        cd easy-transform-metadata
        mvn install
