easy-transform-metadata
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-transform-metadata.png?branch=master)](https://travis-ci.org/DANS-KNAW/easy-transform-metadata)

SYNOPSIS
--------

    easy-transform-metadata [-b,--bagId|-l,--list] [-t,--transform] [-o,--output]


DESCRIPTION
-----------

Tool to transform EASY metadata (dataset.xml en files.xml) into other formats


ARGUMENTS
---------

    Options:

       -b, --bagId  <arg>       The bag for which to transform the metadata
       -l, --list  <arg>        A file containing a newline separated list of bag-ids for which to transform the
                                metadata
       -o, --output  <arg>      The directory in which to output the resulting metadata. If '-b' is used, this is
                                optional (default to stdout); if '-l' is used, this argument is mandatory.
       -t, --transform  <arg>   The file containing an XSLT to be applied to the metadata of the given bags(s); if
                                not provided, no transformation will be performed, but the input for the
                                transformation will be returned.
       -h, --help               Show help message
       -v, --version            Show version of this program

EXAMPLES
--------

    easy-transform-metadata -b b2149eb8-eb51-11e9-896f-6b3af1277c7b -t my-transformation.xslt
    easy-transform-metadata -l my-bagIds.txt -t my-transformation.xslt -o transformation-output/


INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is build only as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/easy-transform-metadata `, the configuration files to `/etc/opt/dans.knaw.nl/easy-transform-metadata `,
and will install the service script for `systemd`. 

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    #!bash
    git clone https://github.com/DANS-KNAW/easy-transform-metadata .git
    cd easy-transform-metadata 
    mvn install

If the `rpm` executable is found at `/usr/local/bin/rpm`, the build profile that includes the RPM 
packaging will be activated. If `rpm` is available, but at a different path, then activate it by using
Maven's `-P` switch: `mvn -Pprm install`.