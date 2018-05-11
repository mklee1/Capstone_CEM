import numpy as np
import sys
# from mnist import MNIST
import cv2
import math

np.set_printoptions(threshold=np.inf) # , suppress=True


















#############
# CNN STUFF #
#############

def predict_single(X, conv_W, conv_b, FC_W, FC_b):
    x1, conv_out_dim = conv_forward(X, conv_W, conv_b)
    x2 = relu_forward(x1)
    x3 = maxpool_forward(x2, conv_out_dim)
    x4 = flatten_forward(x3)
    x5 = FC_forward(x4, FC_W, FC_b)
    return np.argmax(softmax(x5), axis=1)
    
def conv_forward(X, W, b): # X has dims 1 x 28 x 28

    n_filter = 32
    h_filter = 3
    w_filter = 3
    stride = 1
    padding = 2

    d_X = 1
    h_X = 28
    w_X = 28

    n_X = X.shape[0]

    h_out = int((h_X - h_filter + 2 * padding) / stride + 1)
    w_out = int((w_X - w_filter + 2 * padding) / stride + 1)


    X_col = im2col_indices(
        X, h_filter, w_filter, stride=stride, padding=padding)
    W_row = W.reshape(n_filter, -1)

    out = W_row @ X_col + b
    out = out.reshape(n_filter, h_out, w_out, n_X)
    out = out.transpose(3, 0, 1, 2)
    out_dim = (n_filter, h_out, w_out)
    return out, out_dim

def get_im2col_indices(x_shape, field_height=3, field_width=3, padding=1, stride=1):
    # First figure out what the size of the output should be
    N, C, H, W = x_shape
    # print(N, C, H, W)
    assert (H + 2 * padding - field_height) % stride == 0
    assert (W + 2 * padding - field_height) % stride == 0
    out_height = (H + 2 * padding - field_height) / stride + 1
    out_width = (W + 2 * padding - field_width) / stride + 1

    i0 = np.repeat(np.arange(field_height,dtype='int32'), field_width)
    i0 = np.tile(i0, C)
    i1 = stride * np.repeat(np.arange(out_height,dtype='int32'), out_width)
    j0 = np.tile(np.arange(field_width), field_height * C)
    j1 = stride * np.tile(np.arange(out_width,dtype='int32'), int(out_height))
    i = i0.reshape(-1, 1) + i1.reshape(1, -1)
    j = j0.reshape(-1, 1) + j1.reshape(1, -1)

    k = np.repeat(np.arange(C,dtype='int32'), field_height * field_width).reshape(-1, 1)
    return (k, i, j)

def im2col_indices(x, field_height=3, field_width=3, padding=1, stride=1):
    """ An implementation of im2col based on some fancy indexing """
    # Zero-pad the input
    p = padding
    x_padded = np.pad(x, ((0, 0), (0, 0), (p, p), (p, p)), mode='constant')

    k, i, j = get_im2col_indices(x.shape, field_height, field_width, padding,
                               stride)

    cols = x_padded[:, k, i, j]
    (checka, checkb) = i.shape
    checkd = x.shape[0]
    for d in range(cols.shape[0]):
        for a in range(cols.shape[1]):
            for b in range(cols.shape[2]):
                if(cols[d, a, b] != x_padded[d, 0, i[a, b], j[a, b]]):
                    print(d, a, b, cols[d, i[a, b], j[a, b]], x_padded[d, 0, i[a, b], j[a, b]])
    # print("all passed!")
    C = x.shape[1]
    cols = cols.transpose(1, 2, 0).reshape(field_height * field_width * C, -1)
    return cols

def relu_forward(X):
    return np.maximum(X, 0)

def maxpool_forward(X, conv_out_dim):
    n_X = X.shape[0]

    (d_X, h_X, w_X) = conv_out_dim

    size = 2
    stride = 2

    h_out = int((h_X - size) / stride + 1)
    w_out = int((w_X - size) / stride + 1)

    X_reshaped = X.reshape(
        X.shape[0] * X.shape[1], 1, X.shape[2], X.shape[3])
    # print("X_reshaped dims", X_reshaped[13, 0, :,:])
    X_col = im2col_indices(
        X_reshaped, size, size, padding=0, stride=stride)
    # print("xc dims", X_col.shape)
    max_indexes = np.argmax(X_col, axis=0)
    # print("X_col0", X_col[0,:])
    # print("X_col1", X_col[1,:])
    # print("X_col2", X_col[2,:])
    # print("X_col3", X_col[3,:])
    
    # print("max_indexes", max_indexes.shape)
    out = X_col[max_indexes, range(max_indexes.size)]
    # print("out", out[0:100])
    # print("out dims", out.shape)
    # print("hwnd", h_out, w_out, n_X, d_X)
    out = out.reshape(h_out, w_out, n_X,
                      d_X).transpose(2, 3, 0, 1)
    return out

def flatten_forward(X):
    X_shape = X.shape
    out_shape = (X_shape[0], -1)
    out = X.ravel().reshape(out_shape)
    return out

def FC_forward(X, W, b):
    out = X @ W + b
    return out

def softmax(x):
    exp_x = np.exp(x - np.max(x, axis=1, keepdims=True))
    return exp_x / np.sum(exp_x, axis=1, keepdims=True)






##################
# IMG PROCESSING #
##################

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
            elif image[i][j] > 0:
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
                # print("End Moore tracing", bounds)
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
    # if maxDim <= 28:
    #     padToX = 28
    # elif maxDim <= 56:
    #     padToX = 56
    if maxDim <= 112:
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
    # print_2dlist(img)

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

def resize(img):
    # assume img > 25 x 25 to start
    (rows, cols) = img.shape
    new_rows = rows
    new_cols = cols
    prev_img = img
    while (new_rows != 28):
        new_rows = new_rows // 2
        new_cols = new_cols // 2
        new_img = np.zeros((new_rows, new_cols))
        for i in range(new_rows):
            for j in range(new_cols):
                new_img[i, j] = (prev_img[i*2, j*2] + prev_img[i*2, j*2+1] + prev_img[i*2+1, j*2] + prev_img[i*2+1, j*2+1]) / 4
        prev_img = new_img
    return prev_img








def main():



    # mndata = MNIST('C:/Users/charl/Documents/2017-2018/Sem2/samples', gz=True)
    # images_train, labels_train = mndata.load_training()
    # images_test, labels_test = mndata.load_testing()


    ###############
    # ARG PARSING #
    ###############

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
            # print(line)
            if (line[:19] == "Param 1 for Layer 1"):
                # print("Param 1 1")
                pcount = 11
                arrayCounter = 0
                line = f.readline()
                continue
            elif (line[:19] == "Param 2 for Layer 1"):
                # print "Param 2 1"
                pcount = 21
                arrayCounter = 0
                line = f.readline()
                continue
            elif (line[:19] == "Param 1 for Layer 5"):
                # print("Param 1 5")
                pcount = 15
                arrayCounter = 0
                line = f.readline()
                continue
            elif (line[:19] == "Param 2 for Layer 5"):
                # print("Param 2 5")
                pcount = 25
                arrayCounter = 0
                line = f.readline()
                continue
            
            if pcount == 11:
                line2 = f.readline()
                line3 = f.readline()
                # print(line2)
                # print(line3)
                case11(line, line2, line3, arrayCounter)
                arrayCounter+= 1
            elif pcount == 21:
                case21(line, arrayCounter)
                arrayCounter+= 1
            elif pcount == 15:
                line2 = f.readline()
                line3 = f.readline()
                # print(line2)
                # print(line3)
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
        param11[ac,0,0,2] = float(line[secondComma+1:-2])
        
        firstComma = line2.index(",")
        secondComma = line2.index(",", firstComma+1)
        param11[ac,0,1,0] = float(line2[1:firstComma])
        param11[ac,0,1,1] = float(line2[firstComma+1:secondComma])
        param11[ac,0,1,2] = float(line2[secondComma+1:-2])
        
        firstComma = line3.index(",")
        secondComma = line3.index(",", firstComma+1)
        param11[ac,0,2,0] = float(line3[1:firstComma])
        param11[ac,0,2,1] = float(line3[firstComma+1:secondComma])
        param11[ac,0,2,2] = float(line3[secondComma+1:-4])

    def case21(line, ac):
        param21[ac,0] = float(line[1:-2])

    def case15(line, line2, line3, ac):
        firstComma = line.index(",")
        secondComma = line.index(",", firstComma+1)
        thirdComma = line.index(",", secondComma+1)
        param15[ac,0] = float(line[1:firstComma])
        param15[ac,1] = float(line[firstComma+1:secondComma])
        param15[ac,2] = float(line[secondComma+1:thirdComma])
        param15[ac,3] = float(line[thirdComma+1:-1])
        
        firstComma = line2.index(",")
        secondComma = line2.index(",", firstComma+1)
        thirdComma = line2.index(",", secondComma+1)
        param15[ac,4] = float(line2[0:firstComma])
        param15[ac,5] = float(line2[firstComma+1:secondComma])
        param15[ac,6] = float(line2[secondComma+1:thirdComma])
        param15[ac,7] = float(line2[thirdComma+1:-1])
        
        firstComma = line3.index(",")
        param15[ac,8] = float(line3[0:firstComma])
        param15[ac,9] = float(line3[firstComma+1:-2])

    def case25(line, ac):
        # print "length of line",
        # print(len(line))
        # print(line)
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
        param25[0,9] = float(line[nextComma+1:-2])

    parse_params()
    print("finished parsing params")


    ##################
    # IMG PROCESSING #
    ##################

    imfile = "tmid_three.jpg"

    numbers = get_segments(imfile)

    for number in numbers:
        X = resize(np.array(number))
        # print_2dlist(X.tolist())
        X = X.reshape(1, 1, 28, 28)
        a = predict_single(X, param11, param21, param15, param25)
        print(a)





    # for i in range(100):
    #     X = np.array(images_train[i]).reshape(1, 1, 28, 28)
    #     a = predict_single(X, param11, param21, param15, param25)
    #     print("predicted " + str(a) + ", actual is " + str(labels_train[i]))
        

    # out_file = open(sys.argv[1], 'w')
    # out_file.write("Param " + str(1) + " for Layer " + str(1) + "\n" + str(param11) + "\n")
    # out_file.write("Param " + str(2) + " for Layer " + str(1) + "\n" + str(param21) + "\n")
    # out_file.write("Param " + str(1) + " for Layer " + str(5) + "\n" + str(param15) + "\n")
    # out_file.write("Param " + str(2) + " for Layer " + str(5) + "\n" + str(param25) + "\n")

    # X = np.arange(28 * 28).reshape(1, 1, 28, 28)
    # conv_W = np.ones((32, 1, 3, 3))
    # conv_b = np.arange(32).reshape(32, 1)
    # FC_W = np.ones((7200, 10))
    # FC_b = np.arange(10).reshape(1, 10)
    # a = predict_single(X, conv_W, conv_b, FC_W, FC_b)
    # conv_W = np.ones(32 * 3 * 3).reshape(32, 1, 3, 3)
    # conv_b = np.ones(32).reshape(32, 1)
    # conv_b = np.arange(32).reshape(32, 1)
    # FC_W = np.ones(7200 * 10).reshape(7200, 10)
    # FC_b = np.ones(10).reshape(1, 10)
    # print("a = ", a)
    # do stuff

if __name__ == "__main__":
    main()