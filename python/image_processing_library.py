import cv2
import math
import numpy as np

np.set_printoptions(threshold=np.inf)

# Resizes an image of arbitrary size into the size needed
def image_resize(filename, length, width=-1):
    if width == -1:
        width = length
    image = cv2.imread(filename) 
    # resize image
    resized = cv2.resize(image, (length, length));
    return resized

def ndarray_to_2dlist(image):
    # convert numpy array to 2dlist
    result = []
    row = []
    white_limit = 50
    length = image.shape[0]
    width = image.shape[1]
    for i in range(length):
        for j in range(width):
            add = 0
            if image[i,j,0] > white_limit:
                add = 1
            # add = image[i,j,0]
            row.append(add)
        result.append(row)
        row = []
    return result

def print_2dlist(image):
    # print 2d list for testing purposes
    for i in range(len(image)):
        for j in range(len(image[0])):
            if image[i][j] == 0:
                print('_', end='') # white space
            elif image[i][j] == 1:
                print('O', end='') # not visited black space
            elif image[i][j] == 2: 
                print('@', end='') # visited black space
            elif image[i][j] == 3:
                print('X', end='') # current location
            elif image[i][j] == 255:
                print('Q', end='')
            else:
                print(" ")
        print("")

def num_neighbors(image, row, col):
    dirs = {1:(-1,-1), 2:(-1,0), 3:(-1,1),
        8:(0,-1),            4:(0,1),
        7:(1,-1),  6:(1,0),  5:(1,1)}
    num_neighbors = 0
    for i in range(8):
        ind = i+1
        addrow, addcol = dirs[ind]
        nr = row + addrow
        nc = col + addcol
        if (bound(nr,nc,len(image),len(image[0]),ind) and image[nr][nc]>=1):
            num_neighbors += 1
    return num_neighbors

def digit_segment(image, bias):
    result = []
    length = len(image)
    width = len(image[0])-bias
    for col in range(width):
        for row in range(length):
            realCol = col + bias
            if image[row][realCol] == 1:
                img = trace(image, row, realCol, length, width+bias)
                bounds = get_bounds(img, bias, 2)
                print("End Moore tracing", bounds)
                return bounds
    return -1

def cut_image(img, bounds):
    minRow = bounds[0]
    maxRow = bounds[1]+1
    minCol = bounds[2]
    maxCol = bounds[3]+1
    result = []
    rows = maxRow-minRow
    cols = maxCol-minCol+4
    result.append([0]*cols)
    result.append([0]*cols)
    
    imgI, imgJ = 0, 0
    for i in range(rows):
        resI = 0
        imgI = i + minRow
        newRow = [0,0]
        for j in range(cols-4):
            imgJ = j + minCol
            newRow.append(img[imgI][imgJ])
        newRow.append(0)
        newRow.append(0)
        result.append(newRow)
    result.append([0]*cols)
    result.append([0]*cols)
    return result

def get_bounds(image, bias, search):
    # search = 1 for clearing space, 2 for cutting
    result = []
    length = len(image)
    width = len(image[0])
    minRow = length
    minCol = width
    maxRow = 0
    maxCol = 0
    for row in range(length):
        for col in range(width-bias):
            realCol = col+bias
            element = image[row][realCol]
            if (element == search):
                if row < minRow:
                    minRow = row
                elif row > maxRow:
                    maxRow = row

                if realCol < minCol:
                    minCol = realCol
                elif realCol > maxCol:
                    maxCol = realCol
    result = [minRow, maxRow, minCol, maxCol]
    return result

def trace(image, row, col, length, width, loc=1):
    dirs = {1:(-1,-1), 2:(-1,0), 3:(-1,1),
        8:(0,-1),            4:(0,1),
        7:(1,-1),  6:(1,0),  5:(1,1)}

    inv_dirs = {v: k for k, v in dirs.items()}
    newrow,newcol = row,col
    if (row == -1 and col == -1):
        return image
    else:
        lastrow, lastcol = row,col
        (newrow, newcol) = find_moore_neighbor(image, row, col, length, width, loc)
        image[lastrow][lastcol] = 2
        image[newrow][newcol] = 3

        try:
            # take difference in location to get backtrack direction
            (drow, dcol) = (newrow-lastrow, newcol-lastcol)
            loc = (inv_dirs[(drow,dcol)]+4) % 8 +1 # flip to get original direction
        except:
            pass
        img = trace(image, newrow, newcol, length, width, loc)
    return image

def bound(row, col, L, W, ind):
    if not (0 <= row and row <= L):
        return False
    if not (0 <= col and col <= W):
        return False
    return True

def find_moore_neighbor(img, R, C, L, W, start):
    dirs = {1:(-1,-1), 2:(-1,0), 3:(-1,1),
        8:(0,-1),            4:(0,1),
        7:(1,-1),  6:(1,0),  5:(1,1)}

    # print("Init: " + str(R) + " " + str(C))
    for i in range(8):
        ind = (start+i-1)%8 +1

        addrow, addcol = dirs[ind]
        nr = R + addrow
        nc = C + addcol
        nn = num_neighbors(img, nr, nc) > 1
        # print("checking " + dirs1[ind] + "...")
        if (bound(nr,nc,L,W,ind) and img[nr][nc]==2):
            return -1,-1
        elif (bound(nr,nc,L,W,ind) and img[nr][nc]==1 and nn):
            # print("FOUND!")
            return nr, nc
    return -1,-1

def thicken(image):
    height = len(image)
    width = len(image[0])
    for i in range(height):
        for j in range(width):
            if image[i][j] == 1:
                break;
    return image

def convert255(image):
    for i in range(len(image)):
        for j in range(len(image[0])):
            if image[i][j] > 0:
                image[i][j] = 255
    return image

def padArray(segment):
    maxDim = max(len(segment), len(segment[0]))
    padToX = 0
    if maxDim <= 28:
        padToX = 28
    elif maxDim <= 56:
        padToX = 56
    elif maxDim <= 112:
        padToX = 112
    elif maxDim <= 224:
        padToX = 224
    else:
        padToX = 448

    result = np.zeros((padToX, padToX))
    seg = np.array(segment)
    x_off = int((padToX - len(segment))/2)
    y_off = int((padToX - len(segment[0]))/2)
    result[x_off:seg.shape[0]+x_off, y_off:seg.shape[1]+y_off] = seg
    return result

def get_segments(filename):
    result = []
    resized = cv2.imread(filename)
    resized = ndarray_to_2dlist(resized)

    bounds = get_bounds(resized, 0, 1)
    img = cut_image(resized, bounds)
    print_2dlist(img)

    bounds = digit_segment(img,0)
    while (bounds != -1): 
        seg = cut_image(img, bounds)
        seg = convert255(seg)
        seg = padArray(seg)
        # print_2dlist(seg)

        result.append(seg)
        limit = bounds[3]+1
        bounds = digit_segment(img, limit)
    return result

segs = get_segments("test_segment2.jpg")