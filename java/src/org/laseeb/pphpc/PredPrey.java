/*
 * Copyright (c) 2014, Nuno Fachada
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Instituto Superior Técnico nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * The PPHPC package.
 */
package org.laseeb.pphpc;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Random;

import org.uncommons.maths.random.AESCounterRNG;
import org.uncommons.maths.random.CMWC4096RNG;
import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.JavaRNG;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;
import org.uncommons.maths.random.XORShiftRNG;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Main class for multi-threaded predator-prey model.
 * @author Nuno Fachada
 */
public abstract class PredPrey {
	
	/* Enumeration for collected simulation quantities. */
	protected enum StatType { SHEEP, WOLVES, GRASS }
	
	/* Enumeration containing program errors. */
	private enum Errors {
		NONE(0), ARGS(-1), PARAMS(-2), SIM(-3), EXPORT(-4);
		private int value;
		private Errors(int value) { this.value = value; }
		public int getValue() { return this.value; }
	}
	
	/* Default parameters filename. */
	private final String paramsFileDefault = "config.txt";

	/* Default statistics output filename. */
	private final String statsFileDefault = "stats.txt";
	
	/* Interval to print current iteration. */
	@Parameter(names = "-i", description = "Interval of iterations to print current iteration")
	protected long stepPrint = 0;
	
	/* File containing simulation parameters. */
	@Parameter(names = "-p", description = "File containing simulation parameters")
	private String paramsFile = paramsFileDefault;
	
	/* File where to output simulation statistics. */
	@Parameter(names = "-s", description = "Statistics output file")
	private String statsFile = statsFileDefault;
	
	/* Seed for random number generator. */
	@Parameter(names = "-r", description = "Seed for random number generator (defaults to System.nanoTime())", 
			converter = BigIntegerConverter.class)
	private BigInteger seed = null;
	
	/* Random number generator implementation. */
	@Parameter(names = "-g", description = "Random number generator (AES, CA, CMWC, JAVA, MT or XORSHIFT)", 
			converter =  RNGTypeConverter.class)
	private RNGType rngType = RNGType.MT;
	
	/* Debug mode. */
	@Parameter(names = "-d", description = "Debug mode (show stack trace on error)", hidden = true)
	private boolean debug = false;
	
	/* Help option. */
	@Parameter(names = {"--help", "-h", "-?"}, description = "Show options", help = true)
	private boolean help;

	/* Simulation parameters. */
	protected SimParams params;
	
	/* Simulation grid. */
	protected Cell[][] grid;

	/**
	 * Perform simulation.
	 * @throws GeneralSecurityException 
	 * @throws SeedException 
	 */
	protected abstract void start() throws SeedException, GeneralSecurityException;
	
	protected abstract int getStats(StatType st, int iter);
	
	/**
	 * Export statistics to file.
	 * @param str Statistics filename.
	 * @throws IOException 
	 */
	private void export() throws IOException {
		
		FileWriter out = null;

		out = new FileWriter(this.statsFile);
		
		for (int i = 0; i <= params.getIters() ; i++) {
			out.write(this.getStats(StatType.SHEEP, i) + "\t"
					+ this.getStats(StatType.WOLVES, i) + "\t"
					+ this.getStats(StatType.GRASS, i) + "\n");
		}
		
		if (out != null) {
			out.close();
		}
	}
	
	/**
	 * Show error message or stack trace, depending on debug parameter.
	 * 
	 * @param e Exception which caused the error.
	 */
	private void errMessage(Exception e) {
		
		if (this.debug)
			e.printStackTrace();
		else
			System.err.println("An error ocurred: " + e.getMessage());
		
	}
	
	/**
	 * Run program.
	 * 
	 * @param args Command line arguments.
	 * @return Error code.
	 */
	public int doMain(String[] args) {
		
		/* Setup command line options parser. */
		JCommander parser = new JCommander(this);
		parser.setProgramName("java -cp bin" + java.io.File.pathSeparator + "lib/* " 
				+ PredPreyMulti.class.getName());
		
		/* Parse command line options. */
		try {
			parser.parse(args);
		} catch (ParameterException pe) {
			/* On parsing error, show usage and return. */
			errMessage(pe);
			parser.usage();
			return Errors.ARGS.getValue();
		}
		
		/* If help option was passed, show help and quit. */
		if (this.help) {
			parser.usage();
			return Errors.NONE.getValue();
		}
		
		/* Read parameters file. */
		try {
			this.params = new SimParams(this.paramsFile);
		} catch (IOException ioe) {
			errMessage(ioe);
			return Errors.PARAMS.getValue();
		}
		
		/* Setup seed for random number generator. */
		if (this.seed == null)
			this.seed = BigInteger.valueOf(System.nanoTime());
		
		/* Perform simulation. */
		try {
			this.start();
		} catch (Exception e) {
			errMessage(e);
			return Errors.SIM.getValue();
		}
		
		/* Export simulation results. */
		try {
			this.export();
		} catch (IOException e) {
			errMessage(e);
			return Errors.EXPORT.getValue();
		}
		
		/* Terminate with no errors. */
		return Errors.NONE.getValue();
		
	}
	
	protected Random createRNG(long modifier) throws SeedException, GeneralSecurityException {
		
		SeedGenerator seedGen = new PPSeedGenerator(modifier, this.seed);

		switch (this.rngType) {
			case AES:
				return new AESCounterRNG(seedGen);
			case CA:
				return new CellularAutomatonRNG(seedGen);
			case CMWC:
				return new CMWC4096RNG(seedGen);
			case JAVA:
				return new JavaRNG(seedGen);
			case MT:
				return new MersenneTwisterRNG(seedGen);
			case XORSHIFT: 
				return new XORShiftRNG(seedGen);
			default:
				throw new RuntimeException("Don't know this random number generator.");
		}
		
	}
	
}
