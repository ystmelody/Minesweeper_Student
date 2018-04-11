# Written by: Justin Chung
# CS-199

import random

import sys
from ManualAI import ManualAI
from AI import AI


class World():

	# Tiles
	class __Tile():
		mine = False
		covered = True
		flag = False
		number = 0
		

	def __init__(self, filename=None, aiType=ManualAI(), verbose=False, debug=False):
		self.__colDimension = 0
		self.__rowDimension = 0
		self.__score = 0
		self.__board = None
		self.__totalMines = 0
		self.__flagsLeft = 0
		self.__coveredTiles = 0
		self.__movesMade = 0

		self.__lastUncoveredTile = None

		self.__ai = aiType

		try:
		# If file is provided, construct board based on file
			if filename != None:
				with open(filename, 'r') as file:
					self.__createBoard(file)
					firstMoveCoords = self.__getFirstMove(file)
					self.__addMines(file)
					self.__addNumbers()
					self.__coveredTiles = self.__colDimension * self.__rowDimension
					self.__flagsLeft = self.__totalMines
					self.__uncoverTile(firstMoveCoords[0]-1, firstMoveCoords[1]-1)

					
		# If file not provided, construct board using defaults
			else:
				self.__createBoard()
				self.__addMines()
				self.__addNumbers()
				firstMoveCoords = self.__getFirstMove()
				self.__coveredTiles = self.__colDimension * self.__rowDimension
				self.__flagsLeft = self.__totalMines
				self.__uncoverTile(firstMoveCoords[0], firstMoveCoords[1])

		except ValueError:
			print("Error: Cannot create board!")


	def run(self):
		""" Engine of the game """
		while (self.__coveredTiles != self.__totalMines):
			self.__printWorld()

			try: 
				action = self.__ai.getAction(self.__colDimension, self.__rowDimension, self.__coveredTiles, self.__flagsLeft, self.__lastUncoveredTile)
				if self.__checkValidAction(action):
					if self.__doMove(action):
						break
			except ValueError:
				print("Error: Invalid action!")
			except IndexError:
				print("Error: Move is out of bounds!")
		self.__handleGameover()
		self.__uncoverAll()
		self.__printWorld()
		return self.__score


	###############################################
	#				ACTIONS ON BOARD 			  #
	###############################################
	def __checkValidAction(self, actionObj):
		""" Check if move is valid, and if coordinates are valid, returning a boolean """
		move = actionObj.getMove()
		X = actionObj.getX()
		Y = actionObj.getY()
		if move in [AI.Action.LEAVE, AI.Action.UNCOVER, AI.Action.FLAG, AI.Action.UNFLAG]:
			if self.__isInBounds(X, Y):
				return True
			raise IndexError
		raise ValueError


	def __doMove(self, actionObj):
		""" Perform a move on the game board based on given action and x, y coords """
		#self.__score -= 1
		self.__movesMade += 1

		move = actionObj.getMove()
		X = actionObj.getX()
		Y = actionObj.getY()

		if move == AI.Action.LEAVE:
			print("Leaving game...")
			return True 					# Returning True means game over
		elif move == AI.Action.UNCOVER:
			if self.__board[X][Y].mine:
				print("Gameover! Uncovered a bomb!")
				return True
			self.__uncoverTile(X, Y)
		elif move == AI.Action.FLAG:
			self.__flagTile(X, Y)
		elif move == AI.Action.UNFLAG:
			self.__unflagTile(X, Y)
		return False


	#####################################################
	#			SETTING UP THE GAME BOARD   			#
	#####################################################
	def __createBoard(self, inputStream=None):
		""" Creates 2D tile array from first line of file and instantiates board instance variable """
		if inputStream:
			self.__colDimension, self.__rowDimension = [int(x) for x in inputStream.readline().split()]
			self.__board = [[self.__Tile() for i in range(self.__rowDimension)] for j in range(self.__colDimension)]
		else:
			self.__colDimension = 9		# Default sizes
			self.__rowDimension = 9		# Default size
			#self.__totalMines = 10		# Default number of mines

			self.__board = [[self.__Tile() for i in range(self.__rowDimension)] for j in range(self.__colDimension)]


	def __getFirstMove(self, inputStream=None): 
		""" Find the first move to be given to the agent, must be a "0" tile """
		if inputStream:
			startX, startY = [int(x) for x in inputStream.readline().split()]
			if startX > self.__colDimension or startX < 1 or startY > self.__rowDimension or startY < 1:
				raise ValueError('First move coordinates are invalid')
		else:
			startX = self.__randomInt(self.__colDimension)
			startY = self.__randomInt(self.__rowDimension)
			while (self.__board[startY][startX].number != 0 or self.__board[startY][startX].mine):
			#while (not self.__checkNeighboringTiles(startY, startX)):
				startX = self.__randomInt(self.__colDimension)
				startY = self.__randomInt(self.__rowDimension)
		#print(startX, startY)
		return (startY, startX)


	def __checkNeighboringTiles(self, c, r):
		""" Checks the neighboring tiles of c, r to see if there are any mines """
		""" Return the True if there are no neighboring mines, indicating a valid first move """
		print("checking")
		if self.__hasMine(c, r+1) or self.__hasMine(c, r-1) or self.__hasMine(c+1, r) or self.__hasMine(c-1,r) or self.__hasMine(c-1, r+1) or self.__hasMine(c+1, r+1) or self.__hasMine(c-1, r-1) or self.__hasMine(c+1, r-1):
			print "false"
			return False
		print "true"
		return True


	def __hasMine(self, c, r):
		""" Checks if tile has a mine """
		print("checking", c+1, r+1)
		if self.__isInBounds(c, r):
			if self.__board[c][r].mine:
				print("Mine here: ", c, r)
			return self.__board[c][r].mine


	def __addMines(self, inputStream=None):
		""" Add mines to the game board""" 
		if inputStream:
			for r, line in zip(range(self.__rowDimension - 1, -1, -1), inputStream.readlines()):
				for c, tile in zip(range(self.__colDimension), line.split()):
					if tile == "1":
						self.__addMine(c, r)
		else:
			currentMines = 0
			while currentMines < 10:	# Default number of mines is 10
				r = self.__randomInt(self.__rowDimension)
				c = self.__randomInt(self.__colDimension)
				if not self.__board[c][r].mine:
					self.__addMine(c, r)
					currentMines += 1

					
	def __addMine(self, c, r):
		""" Add mine to tile located at (c, r) and update the Tile.mine attrbute """
		self.__board[c][r].mine = True
		self.__totalMines += 1		


	def __addNumbers(self):
		""" Iterate the board and add hint numbers for each mine """
		for r in range(self.__rowDimension):
			for c in range(self.__colDimension):
				if self.__board[c][r].mine:
					self.__addHintNumber(c, r+1)
					self.__addHintNumber(c, r-1)
					self.__addHintNumber(c+1, r)
					self.__addHintNumber(c-1, r)
					self.__addHintNumber(c-1, r+1)
					self.__addHintNumber(c+1, r+1)
					self.__addHintNumber(c-1, r-1)
					self.__addHintNumber(c+1, r-1)


	def __addHintNumber(self, c, r):
		""" Increment the hint number of a tile """
		if self.__isInBounds(c, r):
			self.__board[c][r].number += 1


	def __uncoverTile(self, c, r):
		""" Uncovers a tile """
		if self.__board[c][r].covered:
			self.__coveredTiles -= 1
			self.__board[c][r].covered = False
			self.__lastUncoveredTile = (c+1, r+1)


	def __uncoverAll(self):
		""" Uncovers all tiles """
		for r in range(self.__rowDimension):
			for c in range(self.__colDimension):
				self.__uncoverTile(c, r)
		self.__coveredTiles = 0
		#self.__lastUncoveredTile = None


	def __flagTile(self, c, r):
		""" Flag a tile, coordinates adjusted to fix indexing """
		if self.__board[c][r].covered:
			if not self.__board[c][r].flag:
				self.__board[c][r].flag = True
				self.__flagsLeft -= 1


	def __unflagTile(self, c, r):
		""" Unflag a tile, coordinates adjusted to fix indexing """
		if self.__board[c][r].covered:
			if self.__board[c][r].flag:
				self.__board[c][r].flag = False
				self.__flagsLeft +=1


	def __handleGameover(self):
		for r in range(self.__rowDimension):
			for c in range(self.__colDimension):
				if not self.__board[c][r].covered and not self.__board[c][r].mine:
					print "hi"
					self.__score += 1
				if self.__board[c][r].flag and self.__board[c][r].mine and self.__board[c][r].covered:
					self.__score += 2
				if self.__board[c][r].flag and not self.__board[c][r].mine and self.__board[c][r].covered:
					self.__score -= 2
		self.__score -= self.__movesMade


	#############################################
	#			 BOARD REPRESENTATION			#
	#############################################
	def __printWorld(self):
		self.__printBoardInfo()
		self.__printActionInfo()
		self.__printAgentInfo()


	def __printBoardInfo(self):
		""" Print board for debugging """
		print("\nNumber of mines: " + str(self.__totalMines))
		print("Number of flags left: " + str(self.__flagsLeft))

		board_as_string = ""
		for r in range(self.__rowDimension - 1, -1, -1):
			print str(r+1).ljust(2) + '|',
			for c in range(self.__colDimension):
				self.__printTileInfo(c, r)
			if (r != 0):
				print '\n',

		column_label = "    "
		column_border = "   "
		for c in range(1, self.__colDimension+1):
			column_border += "---"
			column_label += str(c).ljust(3)
		print(board_as_string)
		print(column_border)
		print(column_label)


	def __printAgentInfo(self):
		""" Prints information about the board that are useful to the user """
		print("Tiles covered: " + str(self.__coveredTiles) + " | Flags left: " + str(self.__flagsLeft) + " | Last uncovered tile (number): " + str(self.__lastUncoveredTile))


	def __printActionInfo(self):
		""" Prints available actions to the user if agent is ManualAU """
		if type(self.__ai) == ManualAI:
			print("Press \"L\" to leave game\nPress \"U\" to uncover a tile\nPress \"F\" to flag a tile\nPress \"N\" to unflag a tile: ")


	def __printTileInfo(self, c, r):
		""" Checks tile attributes and prints accordingly """
		if self.__board[c][r].flag:
			print '? ',
		elif not self.__board[c][r].covered and self.__board[c][r].mine:
			print 'B ',
		elif self.__board[c][r].covered:
			print '. ',
		else:
			print str(self.__board[c][r].number) + ' ',
		

	#####################################################
	#		         HELPER FUNCTIONS					#
	#####################################################
	def __randomInt(self, limit):
		""" Return a random int within the range from 0 to limit """
		return random.randrange(limit)


	def __isInBounds(self, c, r):
		""" Returns true if given coordinates are within the boundaries of the game board """
		if c < self.__colDimension and c >= 0 and r < self.__rowDimension and r >= 0:
			return True
		return False

