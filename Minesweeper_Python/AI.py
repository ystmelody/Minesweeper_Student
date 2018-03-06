from abc import ABCMeta, abstractmethod

class AI:
	__metaclass__ = ABCMeta

	def __init__(self):
		self.__VALID_ACTIONS = ["L", "U", "F"]
		
	@abstractmethod
	def getAction(self):
		pass
