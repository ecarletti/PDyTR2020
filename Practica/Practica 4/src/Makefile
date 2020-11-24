dirs := Report Suma FTP
conv_dirs := Report Suma

all: clean build

build:
	$(foreach dir, $(dirs), mkdir $(dir)/classes;)
	$(foreach dir, $(conv_dirs), javac -classpath lib/jade.jar -d $(dir)/classes $(dir)/$(dir)Agent.java;)
	javac -classpath lib/jade.jar -d FTP/classes FTP/FTPAgent.java FTP/FTPCommand.java

start:
	java -cp lib/jade.jar:classes jade.Boot -gui

run-report:
	cd Report && java -cp ../lib/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:ReportAgent

run-suma:
	cd Suma && java -cp ../lib/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:SumaAgent

run-ftp:
	./FTP/run --help

clean:
	- rm -rf APDescription.txt MTPs-Main-Container.txt
	- $(foreach dir, $(dirs), rm -rf $(dir)/classes;)
