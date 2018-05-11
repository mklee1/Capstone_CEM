import numpy as np
import sys
from mnist import MNIST

np.set_printoptions(threshold=np.inf) # , suppress=True




















def predict_single(X, conv_W, conv_b, FC_W, FC_b):
    # print("FC_W", FC_W[308,:])
    # print("conv_W dims = ", conv_W.shape)
    # print("conv_b dims = ", conv_b.shape)
    # print("FC_W dims = ", FC_W.shape)
    # print("FC_b dims = ", FC_b.shape)
    # print("x dims = ", X.shape)
    x1, conv_out_dim = conv_forward(X, conv_W, conv_b)
    # print("x1 = ", x1[0, 0, :, :])
    x2 = relu_forward(x1)
    # assert(np.array_equal(x1, x2))
    # print("x2 dims = ", x2.shape)
    x3 = maxpool_forward(x2, conv_out_dim)
    # print("x3 = ", x3[0, 0, :, :])
    # print("x3 dims = ", x3.shape)
    x4 = flatten_forward(x3)
    # print("x4 dims = ", x4.shape)
    x5 = FC_forward(x4, FC_W, FC_b)
    # print("x5 dims = ", x5.shape)
    print("x5 = ", x5)
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
    # print("X_col dims = ", X_col.shape)
    W_row = W.reshape(n_filter, -1)

    # print("W_row @ X_col", (W_row @ X_col).shape)
    # print("b", b.shape)
    out = W_row @ X_col + b
    # print("diff", out[3, :] - (W_row @ X_col)[3, :])
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
    # print("C", C)
    # print("i0 dims", i0.shape)
    i1 = stride * np.repeat(np.arange(out_height,dtype='int32'), out_width)
    j0 = np.tile(np.arange(field_width), field_height * C)
    j1 = stride * np.tile(np.arange(out_width,dtype='int32'), int(out_height))
    i = i0.reshape(-1, 1) + i1.reshape(1, -1)
    j = j0.reshape(-1, 1) + j1.reshape(1, -1)

    k = np.repeat(np.arange(C,dtype='int32'), field_height * field_width).reshape(-1, 1)
    # print("i = ", i)
    # print("j = ", j)
    # print("k = ", k)
    return (k, i, j)

def im2col_indices(x, field_height=3, field_width=3, padding=1, stride=1):
    """ An implementation of im2col based on some fancy indexing """
    # Zero-pad the input
    p = padding
    # print("x size", x.shape)
    x_padded = np.pad(x, ((0, 0), (0, 0), (p, p), (p, p)), mode='constant')
    # if (field_height == 3):
        # print("x_padded", x_padded[0, 0, :, :])

    k, i, j = get_im2col_indices(x.shape, field_height, field_width, padding,
                               stride)
    # print("x_padded", x_padded.shape)
    cols = x_padded[:, k, i, j]
    # print("k", k)
    # print("i", i.shape)
    # print("j", j.shape)
    # print("cols dim", cols.shape)
    # print("cols", cols[0,:,:])
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


def main():



    # mndata = MNIST('C:/Users/charl/Documents/2017-2018/Sem2/samples', gz=True)
    # images_train, labels_train = mndata.load_training()
    # images_test, labels_test = mndata.load_testing()




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


    imfile = "screenshot.jpg"

    numbers = parse_image(imfile)

    for number in numbers:
        X = number.reshape(1, 1, 28, 28)
        a = predict_single(X, param11, param21, param15, param25)
        print(a)

    def resize(img):
        # assume img > 25 x 25 to start
        (rows, cols) = img.shape
        new_rows = rows
        new_cols = cols
        prev_img = img
        while (new_rows != 25):
            new_rows = new_rows // 2
            new_cols = new_cols // 2
            new_img = np.zeros((new_rows, new_cols))
            for i in range(new_rows):
                for j in range(new_cols):
                    new_img[i, j] = (prev_img[i*2, j*2] + prev_img[i*2, j*2+1] + prev_img[i*2+1, j*2] + prev_img[i*2+1, j*2+1]) / 4
            prev_img = new_img
        return prev_img



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