import random

from ManualAI import ManualAI


class World():

	# Tiles
	class __Tile():
		mine = False
		covered = True
		flag = False
		number = 0
		

	def __init__(self, manualAI=False, filename=None):
		self.__score = 0
		self.__numMines = 0
		#self.__numFlags = 0
		self.__gameover = False
		self.__score = 0

		if manualAI:
			self.__agent = ManualAI()

		# If file is provided
		if filename != None:
			with open(filename, 'r') as file:
				self.__colDimension, self.__rowDimension = [int(x) for x in file.readline().split()]
				self.__board = [[self.__Tile() for i in range(self.__rowDimension)] for j in range(self.__colDimension)]
				self.__addFeatures(file)
				self.__findFirst()

		# If file not provided (default)
		else:
			self.__colDimension = 25		# Default sizes
			self.__rowDimension = 25		# Default size
			self.__totalMines = 99			# Default number of mines

			self.__board = [[self.__Tile() for i in range(self.__rowDimension)] for j in range(self.__colDimension)]
			self.__addFeatures()
			self.__findFirst()

		self.__numFlags = self.__numMines


	def run(self):
		""" Engine of the game """
		
		while (not self.__gameover):
			self.__printBoard()

			try: 
				move = self.__agent.getMove()
				action = move.getAction()
				coordX = move.getX()
				coordY = move.getY()

				if action == "l":
					self.__handleGameover()
					break
				self.__doMove(action, coordX, coordY)
				
			except ValueError as error:
				print("Error: Invalid action!")
			except IndexError:
				print("Error: Move is out of bounds!")


	def __doMove(self, action, x, y):
		""" Perform a move on the game board based on given action and x, y coords """

			if action not in ["l", "u", "f"]:
				raise ValueError
			elif x < 0 or y < 0:
				raise IndexError
			else:
				if action == "f":
					self.__board[x][y].flag = True
					self.__numFlags -= 1
				elif action == "u":
					if self.__board[x][y].mine == True:
						self.__handleGameover()
					else:
						self.__score += 1
						self.__board[x][y].covered = False


#####################################################
#			SETTING UP THE GAME BOARD   			#
#####################################################
	def __addFeatures(self, open_file=None):
		""" Add mines and numbers to tiles """

		# If no file is provided (default)
		if open_file == None:
			currentMines = 0
			while currentMines < self.__totalMines:
				r = self.__randomInt(self.__rowDimension)
				c = self.__randomInt(self.__colDimension)
				if not self.__board[c][r].mine:
					self.__addMine(c, r)
					currentMines += 1

		# If file is provided
		else:
			for r, line in zip(range(self.__rowDimension - 1, -1, -1), open_file.readlines()):
				for c, tile in zip(range(self.__colDimension), line.split()):
					if tile == "1":
						self.__addMine(c, r)


	def __addMine(self, c, r):
		""" Add mine to tile located at c, r """

		# Set attributes
		self.__board[c][r].mine = True
		self.__numMines += 1		

		# Increment number values according to mines
		self.__addHintNumber(c, r+1)
		self.__addHintNumber(c, r-1)
		self.__addHintNumber(c+1, r)
		self.__addHintNumber(c-1, r)
		self.__addHintNumber(c-1, r+1)
		self.__addHintNumber(c+1, r+1)
		self.__addHintNumber(c-1, r-1)
		self.__addHintNumber(c+1, r-1)


	def __addHintNumber(self, c, r):
		""" Makes sure tile is in bounds before updating the number value
			Avoids out of bounds IndexError
		"""

		if c < self.__colDimension and c >= 0 and r < self.__rowDimension and r >= 0:
			self.__board[c][r].number += 1


	def __findFirst(self):
		""" Finds random tile with number 0 and gives it to the player """

		x = self.__randomInt(self.__colDimension)
		y = self.__randomInt(self.__rowDimension)
		while (self.__board[y][x].number != 0):
			x = self.__randomInt(self.__colDimension)
			y = self.__randomInt(self.__rowDimension)
		self.__board[y][x].number = 0
		self.__board[y][x].covered = False


#############################################
#			 BOARD REPRESENTATION			#
#############################################
	def __printBoard(self):
		""" Print board for debugging """

		print("\nNumber of mines: " + str(self.__numMines))
		print("Number of flags left: " + str(self.__numFlags))

		board_as_string = ""
		for r in range(self.__rowDimension - 1, -1, -1):
			board_as_string += str(r).ljust(2) + "|"
			for c in range(self.__colDimension):
				if not self.__board[c][r].covered:
					board_as_string += " " + str(self.__board[c][r].number) + " "
				elif self.__board[c][r].flag:
					board_as_string += " ? "
				elif self.__board[c][r].covered:
					board_as_string += " . "
				
				
				"""
				# Used for debugging
				if self.__board[c][r].mine:
					board_as_string += " B "
				else:
					board_as_string += " " + str(self.__board[c][r].number) + " "
				"""
			if (r != 0):
				board_as_string += "\n"

		column_label = "   "
		column_border = "   "
		for c in range(self.__colDimension):
			column_border += "---"
			column_label += str(c).ljust(3)
		print(board_as_string)
		print(column_border)
		print(column_label)


	def __handleGameover(self):
		""" When a mine is uncovered, end the game and reveal the entire board """
		
		print("Gameover!")

		for r in range(self.__rowDimension - 1, -1, -1):
			for c in range(self.__colDimension):
				if self.__board[c][r].mine:
					self.__board[c][r].number = "B"
				self.__board[c][r].covered = False

		self.__gameover = True
		self.__printBoard()
		print("Score: " + str(self.__score))
		

	#####################################################
	#		         HELPER FUNCTIONS					#
	#####################################################
	def __randomInt(self, limit):
		""" Return a random int within the range from 0 to limit """

		return random.randrange(limit)

if __name__ == "__main__":
	#world1 = World(manualAI=True, filename="test_world.txt")
	world2 = World(manualAI=True)
	world2.run()
