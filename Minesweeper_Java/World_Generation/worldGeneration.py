import sys

def createWorld(filename, dims=(5,8), startSq=(1,1), nBombs=10):
	"""
	Inputs: 
		dims - 2-tuple (rowDim, colDim)
		startSq - 2-tuple (startX, startY)
		nBombs - number of bombs to place
		filename - name of output file

	"""

	rowDimension, colDimension = dims
	print("row: %d col: %d" % (rowDimension, colDimension))
	# ========== Check row and column dimension here =======
	# To Do:

	startX, startY = startSq
	print("startX: %d startY: %d" % (startX, startY))
	# ========== Check startX and startY here ==============
	# To Do:
	with open(filename, 'w') as f:
		f.write("{:d} {:d}\n".format(rowDimension, colDimension))
		f.write("{:d} {:d}\n".format(startX, startY))

	# ------------------- Populate Board -------------------
	grid = [[0 for _ in range(colDimension)] for _ in range(rowDimension)]
	print(grid)

	# Set start square and its bordering squares to 0
	# To Do:


	# Populate the remainder of the grid 
	bombsToPlace = nBombs
	while bombToPlace > 0:
		# To Do:

	


if __name__ == '__main__':
	createWorld("world1.txt")