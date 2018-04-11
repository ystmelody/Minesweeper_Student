# Written by: Justin Chung
# CS-199

import random
from AI import AI


class RandomAI ( AI ):
	__actions = [
		AI.Action.LEAVE,
		AI.Action.UNCOVER,
		AI.Action.FLAG,
		AI.Action.UNFLAG
	]

	class Action():

		def __init__(self, action, x, y):
			self.__action = action
			self.__x = x
			self.__y = y

		def getMove(self):
			return self.__action

		def getX(self):
			return self.__x

		def getY(self):
			return self.__y

	def getAction(self, X, Y, tilesCovered, flagsLeft, lastUncoveredTile, number):
		action = self.__actions[random.randrange(len(self.__actions))]
		x = random.randrange(1, X)
		y = random.randrange(1, Y)

		return self.Action(action, x, y)

	def __randomInt(self, limit):
		""" Return a random int within the range from 0 to limit """

		return random.randrange(limit)