import numpy as np

param11 = np.zeros((32,1,3,3))
param21 = np.zeros((32,1))
param15 = np.zeros((7200,10))
param25 = np.zeros((1,10))
filename = "real.txt"

def parse_params():
    f = open(filename, 'r')
    line = None
    pcount = -1
    line2 = None
    line3 = None
    arrayCounter = 0
    line = f.readline()
    while ( line != '' ):
        print(line)
        if (line[:19] == "Param 1 for Layer 1"):
            print("Param 1 1")
            pcount = 11
            arrayCounter = 0
            line = f.readline()
            continue
        elif (line[:19] == "Param 2 for Layer 1"):
            print "Param 2 1"
            pcount = 21
            arrayCounter = 0
            line = f.readline()
            continue
        elif (line[:19] == "Param 1 for Layer 5"):
            print("Param 1 5")
            pcount = 15
            arrayCounter = 0
            line = f.readline()
            continue
        elif (line[:19] == "Param 2 for Layer 5"):
            print("Param 2 5")
            pcount = 25
            arrayCounter = 0
            line = f.readline()
            continue
        
        if pcount == 11:
            line2 = f.readline()
            line3 = f.readline()
            print(line2)
            print(line3)
            case11(line, line2, line3, arrayCounter)
            arrayCounter+= 1
        elif pcount == 21:
            case21(line, arrayCounter)
            arrayCounter+= 1
        elif pcount == 15:
            line2 = f.readline()
            line3 = f.readline()
            print(line2)
            print(line3)
            case15(line, line2, line3, arrayCounter)
            arrayCounter+= 1
        elif pcount == 25:
            case25(line, arrayCounter)
            arrayCounter += 1
        line = f.readline()

def case11(line, line2, line3, ac):
    firstComma = line.index(",")
    secondComma = line.index(",", firstComma+1)
    param11[ac,0,0,0] = float(line[3:firstComma])
    param11[ac,0,0,1] = float(line[firstComma+1:secondComma])
    param11[ac,0,0,2] = float(line[secondComma+1:-3])
    
    firstComma = line2.index(",")
    secondComma = line2.index(",", firstComma+1)
    param11[ac,0,1,0] = float(line2[1:firstComma])
    param11[ac,0,1,1] = float(line2[firstComma+1:secondComma])
    param11[ac,0,1,2] = float(line2[secondComma+1:-3])
    
    firstComma = line3.index(",")
    secondComma = line3.index(",", firstComma+1)
    param11[ac,0,2,0] = float(line3[1:firstComma])
    param11[ac,0,2,1] = float(line3[firstComma+1:secondComma])
    param11[ac,0,2,2] = float(line3[secondComma+1:-5])

def case21(line, ac):
    param21[ac,0] = float(line[1:-3])

def case15(line, line2, line3, ac):
    firstComma = line.index(",")
    secondComma = line.index(",", firstComma+1)
    thirdComma = line.index(",", secondComma+1)
    param15[ac,0] = float(line[3:firstComma])
    param15[ac,1] = float(line[firstComma+1:secondComma])
    param15[ac,2] = float(line[secondComma+1:thirdComma])
    param15[ac,3] = float(line[thirdComma+1:-3])
    
    firstComma = line2.index(",")
    secondComma = line2.index(",", firstComma+1)
    thirdComma = line2.index(",", secondComma+1)
    param15[ac,4] = float(line2[1:firstComma])
    param15[ac,5] = float(line2[firstComma+1:secondComma])
    param15[ac,6] = float(line2[secondComma+1:thirdComma])
    param15[ac,7] = float(line2[thirdComma+1:len(line2)-3])
    
    firstComma = line3.index(",")
    param15[ac,8] = float(line3[1:firstComma])
    param15[ac,9] = float(line3[firstComma+1:len(line3)-3])

def case25(line, ac):
    print "length of line",
    print(len(line))
    print(line)
    comma = line.index(",")
    param25[0,0] = float(line[1:comma])
    ind = 1
    nextComma = line.index(",", comma+1)
    fool = False
    while (nextComma != 101):
        param25[0,ind] = float(line[comma+1:nextComma])
        comma = nextComma
        ind += 1
        nextComma = line.index(",", comma+1)
    param25[0,8] = float(line[comma+1:nextComma])
    param25[0,9] = float(line[nextComma+1:-3])

parse_params()
print("finished parsing params")
