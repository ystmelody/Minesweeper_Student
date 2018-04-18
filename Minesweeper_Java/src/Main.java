package src;

import org.apache.commons.cli.*;


public class Main {
	public static void main(String[] args) throws Exception {
		String aiType = "myai";
		boolean debug_mode = false;
	
		World world = null;
		// ------------------------- Parse Options ---------------------------
        Options options = new Options();
        
        Option help = new Option("h", "help", false, "help");
        help.setRequired(false);
        options.addOption(help);
        
        Option file = new Option("f", "file", true, "file input");
        file.setRequired(false);
        options.addOption(file);

        Option manualMode = new Option("m", "manual", false, "manual mode");
        manualMode.setRequired(false);
        options.addOption(manualMode);
        
        Option randomMode = new Option("r", "random", false, "random mode");
        randomMode.setRequired(false);
        options.addOption(randomMode);
        
        Option verbose = new Option("v", "verbose", false, "verbose mode");
        verbose.setRequired(false);
        options.addOption(help);
        
        Option debug = new Option("d", "debug", false, "debug mode");
        debug.setRequired(false);
        options.addOption(debug);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Minesweeper", options);
            System.exit(1);
        }
        
        if (cmd.hasOption("help")) {
        	formatter.printHelp("Usage", options);
        	System.exit(0);
        }
        
        // Get option values
        String filename = cmd.getOptionValue("file");
        
        // Random AI has priority over Manual AI if both flags are given
        if (cmd.hasOption("random")) {
        	aiType = "random";
        } else if (cmd.hasOption("manual")) {
        	aiType = "manual";
        }
        
        if (cmd.hasOption("debug")) {
        	debug_mode = true;
        }
        
        // DELETE THIS (for deugging only)
        aiType = "myai";
        debug_mode = true;
        filename = null;//"./worlds/world4x4_1.txt";
        
		// Create the world
		if (filename != null) {
			world = new World(filename, aiType, debug_mode);
		} else {
			System.out.print("file null");
			world = new World(null, aiType, debug_mode);
		}
		
		// run
		world.run();
	}
}
