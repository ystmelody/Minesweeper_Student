from abc import ABCMeta, abstractmethod
from enum import Enum

class AI:
	__metaclass__ = ABCMeta

	class Action (Enum):
		LEAVE = 1
		UNCOVER = 2
		FLAG = 3
		UNFLAG = 4
		
	@abstractmethod
	def getAction(self, X, Y, coveredTiles, flagsLeft, lastUncoveredTile):
		pass
