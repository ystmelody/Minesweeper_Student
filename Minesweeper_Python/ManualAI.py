from AI import AI


class ManualAI( AI ):
	
	class Action():

		def __init__(self, action, x, y):
			self.__action = action.lower()
			self.__x = x
			self.__y = y

		def getMove(self):
			""" Allow private variable action to be publicly accessible """

			return self.__action

		def getX(self):
			""" Allow private variable x to be publicly accessible """

			return self.__x

		def getY(self):
			""" Allow private variable y to be publicly accessible """

			return self.__y


	def getAction(self):
		""" Prompt user for type of action, and the coordinates of where to perform that action
			Return an Action object storing that information
		"""

		print("Press \"L\" to leave game\nPress \"U\" to uncover a tile\nPress \"F\" to flag/unflag a tile: ")
		action = raw_input("Enter an action: ").strip().lower()
		if action == "l":
			return self.Action(action, 0, 0)
		coordX = raw_input("Enter the X coordinate of the tile: ").strip()
		coordY = raw_input("Enter the Y coordinate of the tile: ").strip()

		return self.Action(action, int(coordX), int(coordY))

