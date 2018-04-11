import cv2
import math
import numpy as np

# Resizes an image of arbitrary size into the size needed
def image_resize(filename, length, width=-1):
    if width == -1:
        width = length
    image = cv2.imread(filename) 
    # don't know how this behaves with bad filename

    # resize image
    resized = cv2.resize(image, (length, length));
    return resized

def ndarray_to_2dlist(image):
    result = []
    row = []
    length = image.shape[0]
    width = image.shape[1]
    for i in range(length):
        for j in range(width):
            add = 0
            if image[i,j,0] > 0:
                add = 1
            row.append(add)
        result.append(row)
        row = []
    return result

def print_2dlist(image):
    for i in range(len(image)):
        for j in range(len(image[0])):
            if image[i][j] == 0:
                print('_', end='')
            elif image[i][j] == 1:
                print('O', end='')
            elif image[i][j] == 2:
                print('@', end='')
            elif image[i][j] == 3:
                print('X', end='')
            else:
                print(" ")
        print("")

def sum(row):
    result = 0
    for i in row:
        result += i
    return result

def digit_segment(image):
    result = []
    length = len(image)
    width = len(image[0])
    for row in range(length):
        for col in range(width):
            if image[row][col] == 1:
                img = trace(image, row, col, length)
                print_2dlist(img)
                return
    return result

def trace(image, row, col, length):
    finished = False
    if (row == -1 and col == -1):
        return image
    else:
        lastrow, lastcol = row,col
        (newrow, newcol) = find_neighbor(image, row, col, length)
        print(" (" + str(newrow) + "," + str(newcol) + ")")
        image[lastrow][lastcol] = 2
        image[newrow][newcol] = 3
        print_2dlist(image)
        img = trace(image, newrow, newcol, length)

def find_neighbor(image, row, col, length):
    newrow = -1
    newcol = -1
    
    if (col != length-1 and image[row][col+1] == 1):
        print("right", end='')
        newrow, newcol = row, col+1
    elif (row != length-1 and col != length-1 and image[row+1][col+1] == 1):
        print("bottom right", end='')
        newrow, newcol = row+1,col+1
    elif (row != length-1 and image[row+1][col] == 1):
        print("bottom", end='')
        newrow,newcol = row+1,col
    elif (row != length-1 and col != 0 and image[row+1][col-1] == 1):
        print("bottom left", end='')
        newrow,newcol = row+1,col-1
    elif (col != 0 and image[row][col-1] == 1):
        print("left", end='')
        newrow,newcol = row,col-1
    elif (row != 0 and col != 0 and image[row-1][col-1] == 1):
        print("top left", end='')
        newrow, newcol = row-1, col-1
    elif (row != 0 and image[row-1][col] == 1):
        print("top", end='')
        newrow, newcol = row-1,col
    elif (row != 0 and col != length-1 and image[row-1][col+1] == 1):
        print("top right", end='')
        newrow, newcol = row-1,col+1
    else:
        print("Don't know what this means")
    return (newrow, newcol)

def find_outside_neighbor(image, row, col, length):
    newrow, newcol = -1, -1
    
    return (newrow, newcol)


resized = image_resize("netImages/img1.jpg",15)
resized_list = ndarray_to_2dlist(resized)
print_2dlist(resized_list)
segmented = digit_segment(resized_list)