# Written by: Justin Chung
# CS-199

from AI import AI


class ManualAI( AI ):
	
	class Action():

		def __init__(self, action, x, y):
			self.__action = action
			self.__x = x - 1
			self.__y = y - 1

		def getMove(self):
			""" Allow private variable action to be publicly accessible """
			return self.__action

		def getX(self):
			""" Allow private variable x to be publicly accessible """
			return self.__x

		def getY(self):
			""" Allow private variable y to be publicly accessible """
			return self.__y


	def getAction(self, X, Y, tilesCovered, flagsLeft, lastUncoveredTile, number):
		""" Prompt user for type of action, and the coordinates of where to perform that action
			Return an Action object storing that information
		"""
		#print("Press \"L\" to leave game\nPress \"U\" to uncover a tile\nPress \"F\" to flag a tile\nPress \"N\" to unflag a tile: ")
	
		action = raw_input("Enter an action: ").strip().lower()
		if action == "l":
			action = AI.Action.LEAVE
			return self.Action(action, 1, 1)
		elif action == "u":
			action = AI.Action.UNCOVER
		elif action == "f":
			action = AI.Action.FLAG
		elif action == "n":
			action = AI.Action.UNFLAG
			
		coordX = raw_input("Enter the X coordinate of the tile: ").strip()
		coordY = raw_input("Enter the Y coordinate of the tile: ").strip()

		return self.Action(action, int(coordX), int(coordY))

