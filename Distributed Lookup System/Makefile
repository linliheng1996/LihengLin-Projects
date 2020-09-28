CXX = g++
CPPFLAGS = -g -Wall

all: serverA.cpp serverB.cpp serverC.cpp aws.cpp monitor.cpp client.cpp
	
	$(CXX) $(CPPFLAGS) serverA.cpp -o serverA
	$(CXX) $(CPPFLAGS) serverB.cpp -o serverB
	$(CXX) $(CPPFLAGS) serverC.cpp -o serverC
	$(CXX) $(CPPFLAGS) aws.cpp -o aws
	$(CXX) $(CPPFLAGS) monitor.cpp -o monitor
	$(CXX) $(CPPFLAGS) client.cpp -o client

.PHONY: serverA serverB serverC aws monitor clean

serverA:
	./serverA

serverB:
	./serverB

serverC:
	./serverC

aws:
	./aws

monitor:
	./monitor

clean:
	$(RM) serverA serverB serverC aws monitor client
	#$(RM) *.o
