package src;
import java.io.*;
import org.apache.commons.cli.*;
import java.util.*;


public class Main {
	public static void main(String[] args) {
		boolean MANUAL_AI = true;
		boolean READ_FILE = true;
	
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

        String filename = cmd.getOptionValue("file");
		// ------------------------ file input -------------------------
		if (filename != null) {
			world = new World(filename);
		} else {
			world = new World("./worlds/world6x8_1.txt");
		}
		
		// ------------------------- AI Mode: ---------------------------
		if (cmd.hasOption("m")) {
			ManualAI ai = new ManualAI();
			world.run(ai);
		}
		
		// ----------------------- Verbose Mode: ------------------------
		
		// ------------------------ Debug Mode: -------------------------
	}
}
