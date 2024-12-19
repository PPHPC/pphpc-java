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

import java.util.Collections;
import java.util.List;

/**
 * Put an agent in a cell's internal agent list synchronously and in order,
 * in a thread-safe fashion.
 *  
 * @author Nuno Fachada
 */
public class CellPutAgentSyncOrdered implements ICellPutAgentStrategy {

	/**
	 * Create a new instance of this class.
	 */
	public CellPutAgentSyncOrdered() {}

	/**
	 * Put an agent in a cell's internal agent list synchronously and in order, 
	 * in a thread-safe fashion.
	 * 
	 * @see ICellPutAgentStrategy#putAgent(List, IAgent)
	 * */
	@Override
	public void putAgent(List<IAgent> agents, IAgent agent) {
		synchronized (agents) {
			
			if (agents.size() == 0) {
			
				/* If agent list is empty, just insert current agent in the
				 * first position. */
				agents.add(agent);

			} else {
			
				/* Find index where to place agent. */
				int idx = Collections.binarySearch(agents, agent);
				
				/* Adjust index. */
				idx = idx < 0 ? -idx - 1 : idx;
			
				/* Add agent to list at the specified index, such that the
				 * agent list remains sorted. */
				agents.add(idx, agent);
			}
			
		}
	}

}
