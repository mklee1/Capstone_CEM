import cv2
import math
import numpy as np

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
            else:
                print(" ")
        print("")

def num_neighbors(image, row, col):
    num_neighbors = 0
    for i in range(8):
        ind = i+1
        addrow, addcol = dirs[ind]
        nr = row + addrow
        nc = col + addcol
        if (bound(nr,nc,len(image),ind) and image[nr][nc]>=1):
            num_neighbors += 1
    return num_neighbors

def num_neighbors2(image, row, col):
    num_neighbors = 0
    for i in range(8):
        ind = i+1
        addrow, addcol = dirs[ind]
        nr = row + addrow
        nc = col + addcol
        print(nr, nc, image[nr][nc], bound(nr,nc,len(image),ind))
        if (bound(nr,nc,len(image),ind) and image[nr][nc]>=1):
            num_neighbors += 1
    return num_neighbors

def digit_segment(image, bias):
    result = []
    length = len(image)
    width = len(image[0])-bias
    print("dimensions", length, width)
    for col in range(width):
        for row in range(length):
            realCol = col + bias
            if image[row][realCol] == 1:
                print("start from", realCol, row)
                img = trace(image, row, realCol, length)
                bounds = get_bounds(img, 2)
                print(bounds)
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

def get_bounds(image, search):
    # search = 1 for clearing space, 2 for cutting
    result = []
    length = len(image)
    width = len(image[0])
    minRow = length
    minCol = width
    maxRow = 0
    maxCol = 0
    for row in range(length):
        for col in range(width):
            element = image[row][col]
            if (element == search):
                if row < minRow:
                    minRow = row
                elif row > maxRow:
                    maxRow = row

                if col < minCol:
                    minCol = col
                elif col > maxCol:
                    maxCol = col
    result = [minRow, maxRow, minCol, maxCol]
    return result

def trace(image, row, col, length, loc=1):
    newrow,newcol = row,col
    if (row == -1 and col == -1):
        return image
    else:
        lastrow, lastcol = row,col
        (newrow, newcol) = find_moore_neighbor(image, row, col, length, loc)
        if col > 21:
            print(" (" + str(newrow) + "," + str(newcol) + ")")
        image[lastrow][lastcol] = 2
        image[newrow][newcol] = 3

        try:
            # take difference in location to get backtrack direction
            (drow, dcol) = (newrow-lastrow, newcol-lastcol)
            loc = (inv_dirs[(drow,dcol)]+4) % 8 +1 # flip to get original direction
            if (col > 21):
                print_2dlist(image)
                print("start next from " + dirs1[loc])
        except:
            pass
        img = trace(image, newrow, newcol, length, loc)
    return image

dirs = {1:(-1,-1), 2:(-1,0), 3:(-1,1),
        8:(0,-1),            4:(0,1),
        7:(1,-1),  6:(1,0),  5:(1,1)}

inv_dirs = {v: k for k, v in dirs.items()}

#dirs1 for printing
dirs1 = {1:"top left....", 2:"top.........", 3: "top right...",
         4:"right.......", 5:"bottom right", 6:"bottom......",
         7:"bottom left.", 8:"left........"}

def bound(row, col, L, ind):
    if not (0 <= row and row <= L):
        return False
    if not (0 <= col and col <= L):
        return False
    return True

def bound2(row, col, L, ind):
    print(row, col, L)
    if not (0 <= row and row <= L):
        return False
    if not (0 <= col and col <= L):
        return False
    return True

def find_moore_neighbor(img, R, C, L, start):
    # print("Init: " + str(R) + " " + str(C))
    for i in range(8):
        ind = (start+i-1)%8 +1

        addrow, addcol = dirs[ind]
        nr = R + addrow
        nc = C + addcol
        nn = num_neighbors(img, nr, nc) > 1
        
        if (R == 5 and C == 25):
            print("checking " + dirs1[ind] + "..neighbor ", end="")
            print(img[nr][nc], nr, nc)
            print("nn:", num_neighbors2(img, nr, nc))
            print("dirs", dirs[ind])
        else:
            print("checking " + dirs1[ind] + "...")

        if (bound(nr,nc,L,ind) and img[nr][nc]==2):
            print("End condition for moore neighbor")
            return -1,-1
        elif (bound(nr,nc,L,ind) and img[nr][nc]==1 and nn):
            print("FOUND!")
            return nr, nc
        # print("continuing")
    return -1,-1

def remove_spacing(img):

    return result

resized = image_resize("netImages/img1.jpg",20)
resized = cv2.imread("test_segment2.jpg")
resized = ndarray_to_2dlist(resized)

bounds = get_bounds(resized, 1)
img = cut_image(resized, bounds)
print_2dlist(img)

bounds = digit_segment(img,0)
while (bounds != -1): 
    seg = cut_image(img, bounds)
    print_2dlist(seg)
    limit = bounds[3]
    bounds = digit_segment(img, limit)
    break

"""
# print(resized_list)
print_2dlist(resized_list)

# print(len(resized_list))
# print(len(resized_list[0]))
segmented = digit_segment(resized_list)
"""