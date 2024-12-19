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

/**
 * Wolf agent.
 * 
 * @author Nuno Fachada
 */
public class Wolf extends AbstractAgent {

	/**
	 * Create a wolf agent.
	 * 
	 * @param energy Initial wolf energy.
	 * @param params Simulation parameters.
	 */
	public Wolf(int energy, ModelParams params) {
		super(energy, params);
	}

	/**
	 * @see IAgent#getReproduceProbability()
	 */
	@Override
	public int getReproduceProbability() {
		return params.getWolvesReproduceProb();
	}

	/**
	 * @see IAgent#getReproduceThreshold()
	 */
	@Override
	public int getReproduceThreshold() {
		return params.getWolvesReproduceThreshold();
	}

	/**
	 * @see AbstractAgent#tryEat(ICell)
	 */
	@Override
	protected void tryEat(ICell cell) {

		/* Iterate over agents in this cell. */
		for (IAgent agent : cell.getAgents()) {

			/* Check if agent is sheep. */
			if (agent instanceof Sheep) {

				/* Check if sheep is alive (otherwise another wolf got to the sheep first). */
				if (agent.getEnergy() > 0) {

					/* Eat sheep... */
					agent.setEnergy(0);

					/* ...and gain energy from it. */
					this.setEnergy(this.getEnergy() + params.getWolvesGainFromFood());

					/* I can only eat one sheep, so get out of here. */
					break;

				}

			}

		}

	}

}
