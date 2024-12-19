/*
 * Copyright (c) 2014, 2015, Nuno Fachada
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.laseeb.pphpc;

import java.util.Random;

/**
 * Interface for PPHPC agent implementations.
 * 
 * @author Nuno Fachada
 */
public interface IAgent extends Cloneable, Comparable<IAgent> {

	/**
	 * Returns the agent energy.
	 * 
	 * @return The agent energy.
	 */
	public int getEnergy();

	/**
	 * Sets the agent energy.
	 * 
	 * @param energy Agent energy to set.
	 */
	public void setEnergy(int energy);

	/**
	 * Decrements the agent energy.
	 */
	public void decEnergy();
	
	/**
	 * Is the agent alive?
	 * 
	 * @return True if agent is alive, false otherwise.
	 */
	public boolean isAlive();

	/**
	 * Generic agent actions, consisting of:
	 * 
	 * * Try to eat.
	 * * Try to reproduce.
	 * 
	 * @param cell Cell where agent is currently in.
	 * @param rng A random number generator for the agent to use.
	 */
	public void act(ICell cell, Random rng);

	/**
	 * Returns the agent-specific reproduction threshold.
	 * 
	 * @return Agent-specific reproduction threshold.
	 */
	public int getReproduceThreshold();
	
	/**
	 * Returns the agent-specific reproduction probability.
	 * 
	 * @return Agent-specific reproduction probability.
	 */
	public int getReproduceProbability();
	
}