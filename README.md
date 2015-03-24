RTeasy
======

RTeasy is a development environment for register transfer language which
offers functions to design and simulate a register transfer program.

As of March 2015, RTEasy has been succeeded by [Desert][desert]. We encourage everyone to
migrate over. RTEasy will receive no further updates.

Download
--------

Download the latest release here:

https://wiki.iti.uni-luebeck.de/redmine/projects/rteasy/files

How to build
------------

RTeasy uses the [Apache Maven][maven] build system. Simply perform the following steps:

```
# install build dependencies (java SDK, maven)
# clone this project
mvn install
```

How to run
----------

RTeasy is packaged as a self-contained Java jar file. You can start it by executing 
```java -jar target/RTeasy-VERSION.jar```

Developement status
-------------------

RTeasy is considered feature complete. There is no active development going on.

Bugs
----

RTeasy is considered stable and bug free. If you notice any bugs, please open a trouble ticket
via the [Github issue tracker][rteasy-github-issues].

License
-------

RTeasy was developed at the [Institute of Computer Engineering][iti], University of Luebeck, Germany.
The primary contributors were Carsten Albrecht, Carina Hauft, Hagen Schendel and Torben Schneider.
It is made open source under the terms of the BSD license.

[desert]:https://github.com/iti-luebeck/Desert
[maven]:http://maven.apache.org/
[iti]:http://www.iti.uni-luebeck.de
[rteasy-github-issues]:https://github.com/iti-luebeck/rteasy/issues
