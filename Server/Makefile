CXX       := c++
CXX_FLAGS := -std=c++2a -ggdb -pthread -Wall

BIN     := bin
SRC     := src
INCLUDE := include

LIBRARIES   :=
EXECUTABLE  := find-all-words-server.exe


all: $(BIN)/$(EXECUTABLE)

run: clean all
	clear
	./$(BIN)/$(EXECUTABLE)

$(BIN)/$(EXECUTABLE): $(SRC)/*.cpp
	$(CXX) $(CXX_FLAGS) -I$(INCLUDE) $^ -o $@ $(LIBRARIES)


clean:
	-rm $(BIN)/*