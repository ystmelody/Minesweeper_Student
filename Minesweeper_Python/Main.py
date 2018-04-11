# Written by: Justin Chung
# CS-199

import sys
import os
import argparse
from World import World
from AI import AI
from ManualAI import ManualAI
from RandomAI import RandomAI

def main():
	parser = argparse.ArgumentParser(description="Process command line arugments")

	#parser.add_argument("-h")						# Help
	parser.add_argument("-m", "-M", help="enable ManualAI mode", action="store_true")						# ManualAI
	parser.add_argument("-r", "-R", help="enable RandomAI mode", action="store_true")						# RandomAI
	parser.add_argument("-f", "-F", help="file or directory name", nargs=1)						# Filename
	parser.add_argument("-v", "-V", help="enable verbose mode", action="store_true")						# Verbose
	parser.add_argument("-d", "-D", help="enable debug mode", action="store_true")						# Debug

	args = parser.parse_args()

	verbose = args.v
	debug = args.d
	
	if args.m:
		world = World(aiType=ManualAI(), verbose=verbose, debug=debug)

	elif args.r:
		world = World(aiType=RandomAI(), verbose=verbose, debug=debug)

	elif not args.m and not args.r:
		world = World(aiType=MyAI(), verbose=verbose, debug=debug)

	score = world.run()
	print("Your AI scored: " + str(score))
	return


"""
	args = sys.argv

	if len(args) == 1:
		#world = World(filename="test_world.txt", aiType=ManualAI())
		world = World(aiType=ManualAI())
		score = world.run()
		print("Your AI scored: " + str(score))
		#return
"""

if __name__ == "__main__":
	main()