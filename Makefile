
default: jar

jar: class
	jar cmf  manifest  CarCash.jar  thp/*class thp/carcash/*class

class: thp/carcash/*java thp/*java
	javac --release 8 -Xlint:deprecation thp/*java thp/carcash/*java

